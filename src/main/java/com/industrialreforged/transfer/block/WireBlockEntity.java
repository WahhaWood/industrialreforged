package com.industrialreforged.transfer.block;

import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.transfer.init.GTBlockEntities;
import com.industrialreforged.transfer.render.WireRenderState;
import com.industrialreforged.transfer.util.ConnectionData;
import com.industrialreforged.transfer.util.ConnectionType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class WireBlockEntity extends BlockEntity implements IEnergyWrapper, com.industrialreforged.blockentity.api.IServerTick {
   public static final TagKey<Item> WRENCH = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));
   public final EnumMap<Direction, ConnectionData> connections = new EnumMap<>(Direction.class);
   private VoxelShape cachedCollisionShape;
   private VoxelShape cachedShape;
   private boolean neighborChange = true;
   /** Internal energy buffer, used to hop energy along a cable run. */
   private int energy;
   private final int capacity;
   private int refreshCooldown = 0;
   private static final LoadingCache<List<AABB>, VoxelShape> CACHE = CacheBuilder.newBuilder()
      .maximumSize(10000L)
      .build(new CacheLoader<List<AABB>, VoxelShape>() {
         public VoxelShape load(List<AABB> boxes) {
            if (boxes.isEmpty()) {
               return Shapes.empty();
            }

            int i = 0;
            VoxelShape shape = Shapes.create(boxes.get(i));

            while (i < boxes.size()) {
               AABB box = boxes.get(i);
               shape = Shapes.joinUnoptimized(shape, Shapes.create(box), BooleanOp.OR);
               i++;
            }

            return shape.optimize();
         }
      });
   private static final AABB[] SHAPES = new AABB[7];
   private static final AABB[] COLLISION_SHAPES = new AABB[7];

   private long budgetTick = -1L;
   private int receivedThisTick;
   private long inputAccumulator;
   private long outputAccumulator;
   private int flowTicks;
   private int inputRate;
   private int outputRate;
   @Nullable
   private Direction lastInputDirection;

   public WireBlockEntity(BlockPos pos, BlockState state) {
      super(GTBlockEntities.WIRE.get(), pos, state);
      int rate = state.getBlock() instanceof WireBlock wire ? wire.transfer : 0;
      // rate * 4 overflowed for the high tiers (wire_9 has 1G/t), which left
      // the buffer at 1000 FE and throttled everything to 1k per tick.  The clamp also has
      // to stay above the tier rate itself: output is capped by the buffer, so with the old
      // Integer.MAX_VALUE/4 the ninth tier could never pass its advertised 1G FE/t.
      this.capacity = rate < 0
         ? Integer.MAX_VALUE / 2
         : (int)Math.max(Math.min((long)rate * 4L, Integer.MAX_VALUE / 2), Math.max(1000, rate));
   }

   public ModelData getModelData() {
      WireRenderState state = new WireRenderState();
      EnumMap<Direction, ConnectionType> map = new EnumMap<>(Direction.class);

      for (Direction value : Direction.values()) {
         map.put(value, this.getOrCreate(value).type);
      }

      state.setConnectionTypes(map);
      return ModelData.builder().with(WireRenderState.PROPERTY, state).build();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   @Override
   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();

      for (Direction direction : Direction.values()) {
         tag.putByte(direction.getName(), (byte)this.getOrCreate(direction).type.ordinal());
      }

      return tag;
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      ConnectionType[] types = ConnectionType.values();

      for (Direction value : Direction.values()) {
         this.getOrCreate(value).type = types[tag.getByte(value.getName())];
      }

      this.requestModelDataUpdate();
      this.sync();
   }

   /** Keeps the per-tick intake accounting in sync with the game clock. */
   private void ensureTickBudget() {
      if (this.level == null) {
         return;
      }

      long now = this.level.getGameTime();
      if (now != this.budgetTick) {
         this.budgetTick = now;
         this.receivedThisTick = 0;
         this.lastInputDirection = null;
      }
   }

   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return new IEnergyStorage() {
         @Override
         public int receiveEnergy(int maxReceive, boolean simulate) {
            if (maxReceive <= 0) {
               return 0;
            }

            WireBlockEntity.this.ensureTickBudget();
            // the capability context points outwards, so the side that fed
            // us is the opposite one
            Direction sourceSide = direction == null ? null : direction.getOpposite();
            if (!simulate && sourceSide != null) {
               WireBlockEntity.this.lastInputDirection = sourceSide;
            }

            long rate = WireBlockEntity.this.getTransferRate();
            if (sourceSide != null) {
               // the resistor of the feeding side also caps what enters the wire
               rate = WireBlockEntity.this.getOrCreate(sourceSide).getSend(rate);
            }

            long budget = Math.max(rate - WireBlockEntity.this.receivedThisTick, 0L);
            long accepted = Math.min(
               Math.min((long)maxReceive, rate), Math.min((long)WireBlockEntity.this.capacity - WireBlockEntity.this.energy, budget)
            );
            if (accepted > 0L && !simulate) {
               WireBlockEntity.this.energy += (int)Math.min(accepted, Integer.MAX_VALUE);
               WireBlockEntity.this.receivedThisTick += (int)Math.min(accepted, Integer.MAX_VALUE);
               WireBlockEntity.this.inputAccumulator += (int)Math.min(accepted, Integer.MAX_VALUE);
               WireBlockEntity.this.setChanged();
            }

            return (int)Math.max(Math.min(accepted, Integer.MAX_VALUE), 0L);
         }

         @Override
         public int extractEnergy(int maxExtract, boolean simulate) {
            if (maxExtract <= 0) {
               return 0;
            }

            int rate = WireBlockEntity.this.getTransferRate();
            int extracted = Math.min(Math.min(maxExtract, rate), WireBlockEntity.this.energy);
            if (extracted > 0 && !simulate) {
               WireBlockEntity.this.energy -= extracted;
               WireBlockEntity.this.setChanged();
            }

            return extracted;
         }

         @Override
         public int getEnergyStored() {
            return WireBlockEntity.this.energy;
         }

         @Override
         public int getMaxEnergyStored() {
            return WireBlockEntity.this.capacity;
         }

         @Override
         public boolean canExtract() {
            return true;
         }

         @Override
         public boolean canReceive() {
            return true;
         }
      };
   }

   public int getTransferRate() {
      int rate = this.getBlock().transfer;
      return rate < 0 ? Integer.MAX_VALUE / 4 : Math.max(rate, 1);
   }

   // ------------------------------------------------------------------
   // Server side: connection detection and energy distribution
   // ------------------------------------------------------------------

   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (this.neighborChange || --this.refreshCooldown <= 0) {
         this.refreshCooldown = 10;
         this.neighborChange = false;
         this.refreshConnections();
      }

      if (++this.flowTicks >= 20) {
         this.inputRate = (int)Math.min(this.inputAccumulator / this.flowTicks, Integer.MAX_VALUE);
         this.outputRate = (int)Math.min(this.outputAccumulator / this.flowTicks, Integer.MAX_VALUE);
         this.inputAccumulator = 0;
         this.outputAccumulator = 0;
         this.flowTicks = 0;
      }

      this.pushEnergy();
   }

   private void refreshConnections() {
      boolean changed = false;

      for (Direction direction : Direction.values()) {
         ConnectionData data = this.getOrCreate(direction);
         ConnectionType type = this.detectType(direction, data);
         if (data.type != type) {
            data.type = type;
            changed = true;
         }
      }

      if (changed) {
         this.sync();
         this.setChanged();
      }
   }

   /** Загружен ли чанк соседа прямо сейчас, без подгрузки с диска. */
   private boolean isNeighborLoaded(BlockPos neighborPos) {
      return this.level != null
         && this.level.getChunkSource().getChunkNow(neighborPos.getX() >> 4, neighborPos.getZ() >> 4) != null;
   }

   private ConnectionType detectType(Direction direction, ConnectionData data) {
      BlockPos neighborPos = this.worldPosition.relative(direction);
      // Не тянем чужой чанк с диска: getBlockState/getBlockEntity ниже идут через
      // ServerChunkCache с requireChunk=true и вешают серверный поток на загрузку.
      // Нет чанка — нет соседа; догрузится — подхватим на следующем рефреше (раз в 10 тиков).
      if (!this.isNeighborLoaded(neighborPos)) {
         return ConnectionType.NONE;
      }

      BlockState neighborState = this.level.getBlockState(neighborPos);

      if (neighborState.getBlock() instanceof WireBlock) {
         if (data.transformer) {
            return ConnectionType.TRANSFORMER;
         }

         return data.resistor != null && data.resistor.value >= 0L ? ConnectionType.RESISTOR : ConnectionType.DEFAULT;
      }

      BlockEntity neighbor = this.level.getBlockEntity(neighborPos);
      if (neighbor != null) {
         IEnergyStorage storage = com.industrialreforged.utils.CapabilitiesUtils.get(
            neighbor, net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK, direction.getOpposite()
         );
         if (storage != null) {
            if (data.transformer) {
               return ConnectionType.TRANSFORMER;
            }

            return data.resistor != null && data.resistor.value >= 0L ? ConnectionType.RESISTOR : ConnectionType.BLOCK;
         }
      }

      return ConnectionType.NONE;
   }

   // ------------------------------------------------------------------
   // Единая сеть: общий пул компонента вместо ведёрок
   // ------------------------------------------------------------------
   //
   // Раньше каждый провод толкал энергию только своим соседям: дальние машины
   // мигали, энергия болталась туда-сюда между проводами, в длинной линии
   // парковались миллионы. Теперь связный кусок проводов (тир любой) — одна сеть
   // с общим пулом: раз в тик сеть мгновенно раздаёт энергию ВСЕМ своим
   // потребителям, где бы они ни стояли, поровну (water-filling).
   //
   // Правила сети:
   // - пропускная способность за тик = лимит самого слабого провода (как у
   //   тепловых труб: rate сети = минимум). Один слабый кусок душит всю сеть,
   //   но теперь равномерно, а не только downstream;
   // - ёмкость пула = сумма буферов участников; приём энергии в сеть — как раньше,
   //   через receiveEnergy каждого провода со своим потиковым бюджетом;
   // - провода проводам энергию не пихают вообще: болтанке негде взяться;
   // - резистор на стороне потребителя режет его долю (абсолютный — спрос,
   //   процентный — выдачу); трансформер на энергетику не влияет, как и раньше.
   //
   // Состояния компонента нет: состав собирается свежим BFS каждый тик, поэтому
   // классу «протухшая сеть» негде завестись. Раздачу делает первый проснувшийся
   // участник, он же отмечает в SERVED всех участников сети; остальные в этот тик
   // выходят до BFS. Раньше отметка была только у мастера, и каждый провод с
   // энергией делал полный BFS, чтобы её найти: O(N²) за тик на сеть из N проводов.

   /** Провода, чья сеть уже раздала энергию в этот тик. */
   private static final java.util.Map<Level, ServedTick> SERVED = new java.util.WeakHashMap<>();

   private static final class ServedTick {
      long tick = Long.MIN_VALUE;
      final java.util.Set<BlockPos> wires = new java.util.HashSet<>();
   }

   private void pushEnergy() {
      if (this.level == null || this.energy <= 0) {
         // В сети есть энергия — какой-нибудь её участник с energy > 0 проснётся
         // и раздаст за всех. Пустым участникам BFS не нужен.
         return;
      }

      long now = this.level.getGameTime();
      ServedTick served = SERVED.computeIfAbsent(this.level, level -> new ServedTick());
      if (served.tick != now) {
         served.tick = now;
         served.wires.clear();
      }
      if (served.wires.contains(this.worldPosition)) {
         return;
      }

      // 1) состав сети: все связные провода, только загруженные чанки
      java.util.List<WireBlockEntity> members = new java.util.ArrayList<>();
      java.util.Set<BlockPos> seen = new java.util.HashSet<>();
      java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
      queue.add(this.worldPosition);
      seen.add(this.worldPosition);
      while (!queue.isEmpty()) {
         BlockPos pos = queue.poll();
         BlockEntity at = pos.equals(this.worldPosition) ? this : this.level.getBlockEntity(pos);
         if (at instanceof WireBlockEntity wire) {
            members.add(wire);
         }
         for (Direction direction : Direction.values()) {
            BlockPos next = pos.relative(direction);
            if (seen.add(next) && this.isNeighborLoaded(next)
               && this.level.getBlockState(next).getBlock() instanceof WireBlock) {
               queue.add(next);
            }
         }
      }

      // 2) сеть обслуживается этим проводом: остальные её участники в этот тик выходят сразу
      for (WireBlockEntity wire : members) {
         served.wires.add(wire.worldPosition);
      }

      // 3) пул и лимит: сумма буферов, качает самый слабый провод
      long pool = 0L;
      int rate = Integer.MAX_VALUE;
      for (WireBlockEntity wire : members) {
         pool += wire.energy;
         rate = Math.min(rate, wire.getTransferRate());
      }
      if (pool <= 0L || rate <= 0) {
         return;
      }
      long remaining = Math.min(pool, (long)rate);

      // 4) потребители всей сети: живая capability; провода пропускаем
      java.util.List<IEnergyStorage> storages = new java.util.ArrayList<>();
      java.util.List<Long> wants = new java.util.ArrayList<>();
      java.util.List<Long> percents = new java.util.ArrayList<>();
      for (WireBlockEntity wire : members) {
         for (Direction direction : Direction.values()) {
            BlockPos neighborPos = wire.worldPosition.relative(direction);
            if (!wire.isNeighborLoaded(neighborPos)) {
               continue;
            }
            BlockEntity neighbor = wire.level.getBlockEntity(neighborPos);
            if (neighbor == null || neighbor instanceof WireBlockEntity) {
               continue;
            }
            IEnergyStorage storage = com.industrialreforged.utils.CapabilitiesUtils.get(
               neighbor, net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK, direction.getOpposite()
            );
            if (storage == null || !storage.canReceive()) {
               continue;
            }
            ConnectionData.Resistor resistor = wire.getOrCreate(direction).resistor;
            long absolute = resistor != null && resistor.mode == 0 ? Math.max(0L, resistor.value) : Long.MAX_VALUE;
            long percent = resistor != null && resistor.mode != 0 ? Math.max(0L, resistor.value) : 100L;
            int want = storage.receiveEnergy((int)Math.min(absolute, Integer.MAX_VALUE), true);
            if (want <= 0) {
               continue;
            }
            storages.add(storage);
            wants.add((long)want);
            percents.add(percent);
         }
      }

      // 5) water-filling: поровну всем голодным, остаток возвращается в общий котёл
      boolean active = true;
      while (active && remaining > 0 && !storages.isEmpty()) {
         active = false;
         int candidates = 0;
         for (long want : wants) {
            if (want > 0) {
               candidates++;
            }
         }
         if (candidates == 0) {
            break;
         }
         long share = Math.max(1L, remaining / candidates);
         for (int i = 0; i < storages.size(); i++) {
            long want = wants.get(i);
            if (want <= 0) {
               continue;
            }
            long give = Math.min(Math.min(share, want), remaining);
            give = give * Math.min(percents.get(i), 100L) / 100L;
            if (give <= 0) {
               continue;
            }
            int accepted = storages.get(i).receiveEnergy((int)Math.min(give, Integer.MAX_VALUE), false);
            if (accepted > 0) {
               wants.set(i, want - accepted);
               remaining -= accepted;
               active = true;
            }
         }
      }

      // 6) списываем розданное с участников (порядок BFS — детерминирован)
      long moved = Math.min(pool, (long)rate) - remaining;
      for (WireBlockEntity wire : members) {
         if (moved <= 0) {
            break;
         }
         int drain = (int)Math.min(moved, wire.energy);
         if (drain > 0) {
            wire.energy -= drain;
            wire.outputAccumulator += drain;
            wire.setChanged();
            moved -= drain;
         }
      }
   }

   /** Energy flowing into this wire, averaged over the last second (FE/t). */
   public int getInputRate() {
      return this.inputRate;
   }

   /** Energy leaving this wire, averaged over the last second (FE/t). */
   public int getOutputRate() {
      return this.outputRate;
   }

   // ------------------------------------------------------------------
   // Grid helpers (upgrade / break whole runs of wires)
   // ------------------------------------------------------------------

   /** All wires of the same level directly connected to this one. */
   public java.util.List<BlockPos> collectGrid() {
      java.util.List<BlockPos> grid = new java.util.ArrayList<>();
      if (this.level == null) {
         return grid;
      }

      java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
      java.util.HashSet<BlockPos> seen = new java.util.HashSet<>();
      queue.add(this.worldPosition);

      int wireLevel = this.getBlock().level;

      while (!queue.isEmpty()) {
         BlockPos pos = queue.poll();
         if (!seen.add(pos)) {
            continue;
         }

         grid.add(pos);

         for (Direction direction : Direction.values()) {
            BlockPos next = pos.relative(direction);
            BlockState state = this.level.getBlockState(next);
            if (state.getBlock() instanceof WireBlock wire && wire.level == wireLevel && !seen.contains(next)) {
               queue.add(next);
            }
         }
      }

      return grid;
   }

   /** Breaks every wire in this run, dropping the items. */
   public int breakGrid() {
      java.util.List<BlockPos> grid = this.collectGrid();
      if (this.level == null) {
         return 0;
      }

      for (BlockPos pos : grid) {
         this.level.destroyBlock(pos, true);
      }

      return grid.size();
   }

   /**
    * Upgrades the whole run to the given wire block. Old wires are returned to
    * the player, the new ones are taken from the inventory.
    *
    * @return true when the upgrade happened
    */
   public boolean upgradeGrid(net.minecraft.world.level.block.Block targetBlock, net.minecraft.world.entity.player.Player player) {
      if (!(targetBlock instanceof WireBlock targetWire) || this.level == null) {
         return false;
      }

      java.util.List<BlockPos> grid = this.collectGrid();
      int needed = grid.size();

      if (countItem(player, targetBlock.asItem()) < needed) {
         return false;
      }

      // take the new wires
      consumeItem(player, targetBlock.asItem(), needed);

      for (BlockPos pos : grid) {
         BlockState oldState = this.level.getBlockState(pos);
         BlockEntity oldEntity = this.level.getBlockEntity(pos);
         ItemStack oldWire = oldState.getBlock().asItem().getDefaultInstance();

         java.util.EnumMap<Direction, ConnectionData> saved = new java.util.EnumMap<>(Direction.class);
         if (oldEntity instanceof WireBlockEntity wireEntity) {
            saved.putAll(wireEntity.connections);
         }

         this.level.setBlock(pos, targetWire.defaultBlockState(), 3);

         if (this.level.getBlockEntity(pos) instanceof WireBlockEntity newEntity) {
            for (Direction direction : Direction.values()) {
               ConnectionData oldData = saved.get(direction);
               if (oldData != null && oldData.resistor != null) {
                  newEntity.getOrCreate(direction).resistor = oldData.resistor;
               }
            }

            newEntity.setChanged();
         }

         // return the old wire to the player
         if (!player.getInventory().add(oldWire)) {
            player.drop(oldWire, false);
         }
      }

      return true;
   }

   public static int countItem(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.Item item) {
      int count = 0;
      for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (stack.is(item)) {
            count += stack.getCount();
         }
      }
      return count;
   }

   private static void consumeItem(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.Item item, int amount) {
      for (int i = 0; i < player.getInventory().getContainerSize() && amount > 0; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (!stack.is(item)) {
            continue;
         }

         int take = Math.min(stack.getCount(), amount);
         stack.shrink(take);
         amount -= take;
      }
   }

   @Override
   protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("energy", this.energy);
      tag.putInt("capacity", this.capacity);
      this.saveMeters(tag);

      for (Direction direction : Direction.values()) {
         ConnectionData data = this.getOrCreate(direction);
         tag.putByte(direction.getName(), (byte)data.type.ordinal());
         tag.putBoolean(direction.getName() + "_transformer", data.transformer);
         if (data.resistor != null) {
            tag.putInt(direction.getName() + "_resistor_mode", data.resistor.mode);
            tag.putLong(direction.getName() + "_resistor_value", data.resistor.value);
         }
      }
   }

   @Override
   protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
      super.loadAdditional(tag, registries);
      this.energy = tag.getInt("energy");
      ConnectionType[] types = ConnectionType.values();

      for (Direction direction : Direction.values()) {
         ConnectionData data = this.getOrCreate(direction);
         data.type = types[Math.floorMod(tag.getByte(direction.getName()), types.length)];
         data.transformer = tag.getBoolean(direction.getName() + "_transformer");
         if (tag.contains(direction.getName() + "_resistor_value")) {
            data.resistor = new ConnectionData.Resistor(
               tag.getInt(direction.getName() + "_resistor_mode"), tag.getLong(direction.getName() + "_resistor_value")
            );
         }
      }
   }

   /** Flow meters are persisted so tooling (dev bridge) can read them. */
   private void saveMeters(CompoundTag tag) {
      tag.putInt("inputRate", this.inputRate);
      tag.putInt("outputRate", this.outputRate);
      tag.putInt("energy", this.energy);
   }

   public boolean isNode() {
      return this.connections.values().stream().anyMatch(data -> data.receive != null || data.transformer);
   }

   public ConnectionData getOrCreate(Direction direction) {
      return this.connections.computeIfAbsent(direction, direction1 -> new ConnectionData());
   }

   private void sync() {
      this.invalidateShapes();
      Level level = this.level;
      BlockPos pos = this.worldPosition;
      BlockState state = this.getBlockState();
      level.setBlocksDirty(pos, Blocks.AIR.defaultBlockState(), state);
      level.sendBlockUpdated(pos, Blocks.AIR.defaultBlockState(), state, 11);
      int i = 10;
      state.updateNeighbourShapes(level, pos, i, 511);
      state.updateIndirectNeighbourShapes(level, pos, i, 511);
   }

   public WireBlock getBlock() {
      return (WireBlock)this.getBlockState().getBlock();
   }

   public VoxelShape getShape() {
      if (this.cachedShape == null) {
         this.cachedShape = this.createShape(SHAPES);
      }

      return this.cachedShape != null ? this.cachedShape : Shapes.block();
   }

   public VoxelShape getCollisionShape(CollisionContext context) {
      boolean itemEntity = context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof ItemEntity;
      if (itemEntity) {
         return Shapes.block();
      }

      if (this.cachedCollisionShape == null) {
         this.cachedCollisionShape = this.createShape(COLLISION_SHAPES);
      }

      return this.cachedCollisionShape != null ? this.cachedCollisionShape : Shapes.block();
   }

   private VoxelShape createShape(AABB[] shapes) {
      List<AABB> boxes = new ArrayList<>();
      boxes.add(shapes[6]);

      for (Direction value : Direction.values()) {
         ConnectionType connectionType = this.getOrCreate(value).type;
         if (connectionType != ConnectionType.NONE && connectionType != ConnectionType.BLOCK) {
            boxes.add(shapes[value.ordinal()]);
         }
      }

      return (VoxelShape)CACHE.getUnchecked(boxes);
   }

   public void invalidateShapes() {
      this.cachedShape = null;
      this.cachedCollisionShape = null;
      this.neighborChange = true;
   }

   static {
      SHAPES[0] = new AABB(0.37, 0.0, 0.37, 0.63, 0.37, 0.63);
      SHAPES[1] = new AABB(0.37, 0.63, 0.37, 0.63, 1.0, 0.63);
      SHAPES[2] = new AABB(0.37, 0.37, 0.0, 0.63, 0.63, 0.37);
      SHAPES[3] = new AABB(0.37, 0.37, 0.63, 0.63, 0.63, 1.0);
      SHAPES[4] = new AABB(0.0, 0.37, 0.37, 0.37, 0.63, 0.63);
      SHAPES[5] = new AABB(0.63, 0.37, 0.37, 1.0, 0.63, 0.63);
      SHAPES[6] = new AABB(0.37, 0.37, 0.37, 0.63, 0.63, 0.63);
      COLLISION_SHAPES[0] = new AABB(0.375, 0.0, 0.375, 0.625, 0.375, 0.625);
      COLLISION_SHAPES[1] = new AABB(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);
      COLLISION_SHAPES[2] = new AABB(0.375, 0.375, 0.0, 0.625, 0.625, 0.375);
      COLLISION_SHAPES[3] = new AABB(0.375, 0.375, 0.625, 0.625, 0.625, 1.0);
      COLLISION_SHAPES[4] = new AABB(0.0, 0.375, 0.375, 0.375, 0.625, 0.625);
      COLLISION_SHAPES[5] = new AABB(0.625, 0.375, 0.375, 1.0, 0.625, 0.625);
      COLLISION_SHAPES[6] = new AABB(0.375, 0.375, 0.375, 0.625, 0.625, 0.625);
   }
}
