package com.industrialreforged.tech.multiblock.tank;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.multiblock.MultiblockValidationResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Клапан резервуара-котла. Резервуар хранит воду и пар раздельно и кипятит воду теплом,
 * которое приносят тепловые трубы: 1 мБ воды + 100 тепла → 100 мБ пара.
 * Режим клапана переключается ключом: вход (принимает воду/пар) или выход (отдаёт пар).
 */
public class TankValveBlockEntity extends BaseBlockEntity implements IFluidWrapper, IFluidHandler, IServerTick {
   private static final IFluidHandler EMPTY_HANDLER = EmptyFluidHandler.INSTANCE;

   /** Максимум тепла в резервуаре. */
   public static final int HEAT_CAPACITY = 10000;
   /** Тепло на 1 мБ воды. */
   public static final int HEAT_PER_MB = 100;
   /** Пара с 1 мБ воды. */
   public static final int STEAM_PER_MB = 100;

   /** Собранные клапаны по мирам: поломка стенки находит свой танк без скана округи. */
   private static final Map<Level, Set<BlockPos>> FORMED_VALVES = new WeakHashMap<>();

   private static void registerValve(Level level, BlockPos pos) {
      FORMED_VALVES.computeIfAbsent(level, l -> new HashSet<>()).add(pos.immutable());
   }

   private static void unregisterValve(@Nullable Level level, BlockPos pos) {
      if (level == null) {
         return;
      }
      Set<BlockPos> set = FORMED_VALVES.get(level);
      if (set != null) {
         set.remove(pos);
      }
   }

   /** Распускает все собранные танки мира, в корпус которых попала точка (поломка стенки). */
   public static void disbandTanksContaining(Level level, BlockPos pos) {
      Set<BlockPos> valves = FORMED_VALVES.get(level);
      if (valves == null || valves.isEmpty()) {
         return;
      }
      for (BlockPos valvePos : new ArrayList<>(valves)) {
         if (!(level.getBlockEntity(valvePos) instanceof TankValveBlockEntity valve) || !valve.isFormed()) {
            unregisterValve(level, valvePos);
            continue;
         }
         if (valve.minPos != null && valve.maxPos != null
            && pos.getX() >= valve.minPos.getX() && pos.getX() <= valve.maxPos.getX()
            && pos.getY() >= valve.minPos.getY() && pos.getY() <= valve.maxPos.getY()
            && pos.getZ() >= valve.minPos.getZ() && pos.getZ() <= valve.maxPos.getZ()) {
            valve.disbandStructure();
         }
      }
   }

   private boolean formed = false;
   @Nullable
   private BlockPos masterPos = null;
   private int capacity = 0;
   private int innerVolume = 0;
   @Nullable
   private BlockPos minPos = null;
   @Nullable
   private BlockPos maxPos = null;
   private int heat = 0;
   /** Позиции всех клапанов структуры (у мастера) — для раздачи пара из выходных. */
   private final List<BlockPos> structureValves = new ArrayList<>();
   /** Уровень воды/пара в промилле ёмкости — синхронизируется клиенту для рендера жидкости. */
   private int displayWaterFill = 0;
   private int displaySteamFill = 0;

   public final FluidTank waterTank = new FluidTank(0, stack ->
      stack.is(Fluids.WATER) || stack.is(IRFluids.DISTILLED_WATER.getFluidStill())
   );
   public final FluidTank steamTank = new FluidTank(0, stack ->
      stack.is(IRFluids.STEAM.getFluidStill())
   );

   public TankValveBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.TANK_VALVE.get(), blockPos, blockState);
   }

   public boolean isFormed() {
      return this.formed;
   }

   public boolean isMaster() {
      return this.formed && (this.masterPos == null || this.masterPos.equals(this.worldPosition));
   }

   @Nullable
   public TankValveBlockEntity getMasterEntity() {
      if (!this.formed || this.level == null) {
         return null;
      }
      if (this.masterPos == null || this.masterPos.equals(this.worldPosition)) {
         return this;
      }
      if (this.level.getBlockEntity(this.masterPos) instanceof TankValveBlockEntity master) {
         return master;
      }
      return null;
   }

   @Nullable
   private TankValveBlockEntity masterOrNull() {
      TankValveBlockEntity master = this.getMasterEntity();
      return master != null && master.isFormed() ? master : null;
   }

   /** Режим клапана: вход/выход пара или воды. */
   public TankValveBlock.TankValveMode mode() {
      BlockState state = this.getBlockState();
      return state.hasProperty(TankValveBlock.MODE) ? state.getValue(TankValveBlock.MODE) : TankValveBlock.TankValveMode.WATER_IN;
   }

   private FluidTank tankAt(int index) {
      return index == 0 ? this.waterTank : this.steamTank;
   }

   /** Тепло от тепловых труб (принимает мастер-клапан). Возвращает фактически принятое. */
   public int pushHeat(int amount) {
      TankValveBlockEntity master = this.masterOrNull();
      if (master == null) {
         return 0;
      }

      int accepted = Math.min(amount, HEAT_CAPACITY - master.heat);
      if (accepted > 0) {
         master.heat += accepted;
         master.markForSave();
      }

      return accepted;
   }

   public int getHeat() {
      TankValveBlockEntity master = this.masterOrNull();
      return master == null ? this.heat : master.heat;
   }

   /** Кипячение: 1 мБ воды + 100 тепла → 100 мБ пара. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide || !this.isMaster()) {
         return;
      }

      if (this.heat >= HEAT_PER_MB) {
         int water = this.waterTank.getFluidAmount();

         if (water > 0) {
            int byHeat = this.heat / HEAT_PER_MB;
            int bySpace = this.steamTank.getSpace() / STEAM_PER_MB;
            int n = Math.min(Math.min(byHeat, water), bySpace);

            if (n > 0) {
               this.waterTank.drainForce(n, FluidAction.EXECUTE);
               this.steamTank.fillForce(new FluidStack(IRFluids.STEAM.getFluidStill(), n * STEAM_PER_MB), FluidAction.EXECUTE);
               this.heat -= n * HEAT_PER_MB;
               this.markForSave();
            }
         }
      }

      // Выходные клапаны отдают пар соседям (турбина/конденсатор) — работает и без труб
      this.pushSteamToNeighbors();
      this.syncFluidLevel();
   }

   /** Пересчитывает и при необходимости отправляет клиенту уровень жидкости (для рендера в резервуаре). */
   private void syncFluidLevel() {
      int capacity = Math.max(1, this.capacity);
      int waterFill = Math.min(1000, (int)Math.min(1000L, this.waterTank.getFluidAmount() * 1000L / capacity));
      int steamFill = Math.min(1000, (int)Math.min(1000L, this.steamTank.getFluidAmount() * 1000L / capacity));
      if (Math.abs(waterFill - this.displayWaterFill) >= 2 || Math.abs(steamFill - this.displaySteamFill) >= 2) {
         this.displayWaterFill = waterFill;
         this.displaySteamFill = steamFill;
         this.markForUpdate();
      }
   }

   /** Уровень воды в промилле ёмкости (для клиентского рендера). */
   public int getDisplayWaterFill() {
      return this.displayWaterFill;
   }

   /** Уровень пара в промилле ёмкости (для клиентского рендера). */
   public int getDisplaySteamFill() {
      return this.displaySteamFill;
   }

   /** Нижний угол оболочки структуры (для клиентского рендера жидкости). */
   @Nullable
   public BlockPos getMinPos() {
      return this.minPos;
   }

   /** Верхний угол оболочки структуры (для клиентского рендера жидкости). */
   @Nullable
   public BlockPos getMaxPos() {
      return this.maxPos;
   }

   /** Раздача пара из выходных клапанов в соседние FluidHandler'ы (до 2000 мБ/тик на клапан). */
   private void pushSteamToNeighbors() {
      if (this.steamTank.isEmpty() || this.structureValves.isEmpty() || this.level == null) {
         return;
      }

      for (BlockPos vPos : this.structureValves) {
         if (this.steamTank.isEmpty()) {
            break;
         }

         if (!(this.level.getBlockEntity(vPos) instanceof TankValveBlockEntity valve)
            || valve.mode() != TankValveBlock.TankValveMode.STEAM_OUT) {
            continue;
         }

         int budget = Math.min(2000, this.steamTank.getFluidAmount());
         if (budget <= 0) {
            break;
         }

         FluidStack stack = new FluidStack(IRFluids.STEAM.getFluidStill(), budget);
         int moved = com.industrialreforged.tech.core.util.LiquidUtil.distribute(valve, stack, FluidAction.EXECUTE);
         if (moved > 0) {
            this.steamTank.drainForce(moved, FluidAction.EXECUTE);
            this.markForSave();
         }
      }
   }

   // ------------------------------------------------------------------
   // Внешний FluidHandler: правила зависят от режима клапана
   // ------------------------------------------------------------------

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.masterOrNull() == null ? EMPTY_HANDLER : this;
   }

   @Override
   public int getTanks() {
      return 2;
   }

   @Override
   public FluidStack getFluidInTank(int tank) {
      TankValveBlockEntity master = this.masterOrNull();
      return master == null ? FluidStack.EMPTY : master.tankAt(tank).getFluid();
   }

   @Override
   public int getTankCapacity(int tank) {
      TankValveBlockEntity master = this.masterOrNull();
      return master == null ? 0 : master.tankAt(tank).getCapacity();
   }

   @Override
   public boolean isFluidValid(int tank, FluidStack stack) {
      return this.tankAt(tank).isFluidValid(stack);
   }

   @Override
   public int fill(FluidStack resource, FluidAction action) {
      if (resource.isEmpty()) {
         return 0;
      }

      TankValveBlock.TankValveMode mode = this.mode();
      if (!mode.isInput()) {
         return 0;
      }

      TankValveBlockEntity master = this.masterOrNull();
      if (master == null) {
         return 0;
      }

      if (mode == TankValveBlock.TankValveMode.STEAM_IN) {
         return resource.is(IRFluids.STEAM.getFluidStill())
            ? master.steamTank.fill(resource, action)
            : 0;
      }

      // WATER_IN
      return resource.is(Fluids.WATER) || resource.is(IRFluids.DISTILLED_WATER.getFluidStill())
         ? master.waterTank.fill(resource, action)
         : 0;
   }

   @Override
   public FluidStack drain(FluidStack resource, FluidAction action) {
      if (resource.isEmpty()) {
         return FluidStack.EMPTY;
      }

      TankValveBlockEntity master = this.masterOrNull();
      if (master == null) {
         return FluidStack.EMPTY;
      }

      TankValveBlock.TankValveMode mode = this.mode();
      if (mode == TankValveBlock.TankValveMode.STEAM_OUT) {
         return master.steamTank.drain(resource, action);
      }
      if (mode == TankValveBlock.TankValveMode.WATER_OUT) {
         return master.waterTank.drain(resource, action);
      }

      return FluidStack.EMPTY;
   }

   @Override
   public FluidStack drain(int maxDrain, FluidAction action) {
      TankValveBlockEntity master = this.masterOrNull();
      if (master == null) {
         return FluidStack.EMPTY;
      }

      TankValveBlock.TankValveMode mode = this.mode();
      if (mode == TankValveBlock.TankValveMode.STEAM_OUT) {
         return master.steamTank.drain(maxDrain, action);
      }
      if (mode == TankValveBlock.TankValveMode.WATER_OUT) {
         return master.waterTank.drain(maxDrain, action);
      }

      return FluidStack.EMPTY;
   }

   public Component getStatusMessage() {
      TankValveBlockEntity master = this.masterOrNull();
      int cap = master == null ? this.capacity : master.capacity;
      int water = master == null ? 0 : master.waterTank.getFluidAmount();
      int steam = master == null ? 0 : master.steamTank.getFluidAmount();
      int currentHeat = master == null ? 0 : master.heat;
      return Component.translatable(
         "text.industrialreforged.multiblock.tank.status.info",
         Component.translatable(this.mode().getTranslationKey()),
         String.format("%,d", water),
         String.format("%,d", cap),
         String.format("%,d", steam),
         String.format("%,d", currentHeat),
         String.format("%,d", HEAT_CAPACITY)
      );
   }

   public void setFormed(boolean formed, @Nullable BlockPos masterPos, int capacity, int innerVolume, @Nullable BlockPos minPos, @Nullable BlockPos maxPos) {
      this.formed = formed;
      this.masterPos = masterPos;
      this.capacity = capacity;
      this.innerVolume = innerVolume;
      this.minPos = minPos;
      this.maxPos = maxPos;
      if (masterPos != null && masterPos.equals(this.worldPosition)) {
         this.waterTank.setCapacity(capacity);
         this.steamTank.setCapacity(capacity);
      }
      if (formed) {
         registerValve(this.level, this.worldPosition);
      } else {
         unregisterValve(this.level, this.worldPosition);
      }
      if (this.level != null && !this.level.isClientSide) {
         ActiveBlock.setActive(this, formed);
      }
      this.markForSave();
      this.markForUpdate();
   }

   public void disbandStructure() {
      if (!this.formed) {
         return;
      }
      TankValveBlockEntity master = this.getMasterEntity();
      this.formed = false;
      this.masterPos = null;
      this.structureValves.clear();
      unregisterValve(this.level, this.worldPosition);
      if (this.level != null && !this.level.isClientSide) {
         ActiveBlock.setActive(this, false);
      }
      this.markForSave();
      this.markForUpdate();

      if (master != null && master != this && master.isFormed()) {
         master.disbandStructure();
      }

      if (this.minPos != null && this.maxPos != null && this.level != null) {
         for (int x = this.minPos.getX(); x <= this.maxPos.getX(); x++) {
            for (int y = this.minPos.getY(); y <= this.maxPos.getY(); y++) {
               for (int z = this.minPos.getZ(); z <= this.maxPos.getZ(); z++) {
                  BlockPos p = new BlockPos(x, y, z);
                  ActiveBlock.setActive(this.level, p, false);
                  if (!p.equals(this.worldPosition) && this.level.getBlockEntity(p) instanceof TankValveBlockEntity child) {
                     child.setFormed(false, null, 0, 0, null, null);
                  }
                  if (this.level.getBlockEntity(p) instanceof TankControllerBlockEntity controller) {
                     controller.setFormed(false, null, 0, 0, null, null);
                  }
               }
            }
         }
      }
   }

   public boolean validateStructure(@Nullable Player player) {
      if (this.level == null || this.level.isClientSide) {
         return false;
      }
      MultiblockValidationResult result = MultiblockTankValidator.validateWithFallback(this.level, this.worldPosition);
      if (result.isSuccess()) {
         this.applyValidation(result, this.worldPosition, player);
         return true;
      } else {
         if (this.formed) {
            this.disbandStructure();
         }
         if (player != null) {
            player.sendSystemMessage(result.getMessage());
         }
         return false;
      }
   }

   /**
    * Помечает всю структуру собранной: мастер-клапан получает буфер, все клапаны и контроллеры —
    * ссылку на мастера, оболочка — активную текстуру. Вызывается и клапаном, и контроллером.
    */
   public void applyValidation(MultiblockValidationResult result, BlockPos master, @Nullable Player player) {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      this.structureValves.clear();
      this.structureValves.addAll(result.getValves());
      for (BlockPos vPos : result.getValves()) {
         if (this.level.getBlockEntity(vPos) instanceof TankValveBlockEntity vBE) {
            vBE.setFormed(true, master, result.getCapacity(), result.getInnerVolume(), result.getMinPos(), result.getMaxPos());
         }
      }
      for (BlockPos p : result.getAllBlocks()) {
         if (this.level.getBlockEntity(p) instanceof TankControllerBlockEntity controller) {
            controller.setFormed(true, master, result.getCapacity(), result.getInnerVolume(), result.getMinPos(), result.getMaxPos());
         }
      }
      for (BlockPos wallPos : result.getAllBlocks()) {
         ActiveBlock.setActive(this.level, wallPos, true);
      }
      if (player != null) {
         player.sendSystemMessage(result.getMessage());
      }
   }

   public int getCapacity() {
      TankValveBlockEntity master = this.getMasterEntity();
      return master != null ? master.capacity : this.capacity;
   }

   public int getInnerVolume() {
      TankValveBlockEntity master = this.getMasterEntity();
      return master != null ? master.innerVolume : this.innerVolume;
   }

   @Override
   public void onLoad() {
      super.onLoad();
      // Страховка: при загрузке чанка level уже задан, даже если в loadAdditional его ещё не было
      if (this.formed) {
         registerValve(this.level, this.worldPosition);
      }
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.formed = tag.getBoolean("formed");
      this.capacity = tag.getInt("capacity");
      this.innerVolume = tag.getInt("innerVolume");
      if (tag.contains("masterPos")) {
         this.masterPos = BlockPos.of(tag.getLong("masterPos"));
      } else {
         this.masterPos = null;
      }
      if (tag.contains("minPos")) {
         this.minPos = BlockPos.of(tag.getLong("minPos"));
      } else {
         this.minPos = null;
      }
      if (tag.contains("maxPos")) {
         this.maxPos = BlockPos.of(tag.getLong("maxPos"));
      } else {
         this.maxPos = null;
      }
      this.heat = tag.getInt("heat");
      this.displayWaterFill = tag.getInt("displayWaterFill");
      this.displaySteamFill = tag.getInt("displaySteamFill");
      this.structureValves.clear();
      for (long packed : tag.getLongArray("valves")) {
         this.structureValves.add(BlockPos.of(packed));
      }
      if (this.capacity > 0) {
         this.waterTank.setCapacity(this.capacity);
         this.steamTank.setCapacity(this.capacity);
      }
      this.waterTank.load(registries, tag, "waterTank");
      this.steamTank.load(registries, tag, "steamTank");
      // Совместимость со старым одиночным танком
      if (tag.contains("tank") && this.waterTank.isEmpty() && this.steamTank.isEmpty()) {
         FluidStack legacy = FluidStack.parseOptional(registries, tag.getCompound("tank"));
         if (!legacy.isEmpty()) {
            if (legacy.is(IRFluids.STEAM.getFluidStill())) {
               this.steamTank.fillForce(legacy, FluidAction.EXECUTE);
            } else {
               this.waterTank.fillForce(legacy, FluidAction.EXECUTE);
            }
         }
      }
      if (this.isMaster() && this.capacity > 0) {
         this.waterTank.setCapacity(this.capacity);
         this.steamTank.setCapacity(this.capacity);
      }
      if (this.formed) {
         registerValve(this.level, this.worldPosition);
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putBoolean("formed", this.formed);
      tag.putInt("capacity", this.capacity);
      tag.putInt("innerVolume", this.innerVolume);
      if (this.masterPos != null) {
         tag.putLong("masterPos", this.masterPos.asLong());
      }
      if (this.minPos != null) {
         tag.putLong("minPos", this.minPos.asLong());
      }
      if (this.maxPos != null) {
         tag.putLong("maxPos", this.maxPos.asLong());
      }
      tag.putInt("heat", this.heat);
      tag.putInt("displayWaterFill", this.displayWaterFill);
      tag.putInt("displaySteamFill", this.displaySteamFill);
      long[] valveArray = new long[this.structureValves.size()];
      for (int i = 0; i < valveArray.length; i++) {
         valveArray[i] = this.structureValves.get(i).asLong();
      }
      tag.putLongArray("valves", valveArray);
      this.waterTank.save(registries, tag, "waterTank");
      this.steamTank.save(registries, tag, "steamTank");
   }

   // ------------------------------------------------------------------
   // Синхронизация с клиентом
   //
   // markForUpdate() шлёт sendBlockUpdated — это обновление БЛОКА. Данные
   // block entity идут отдельным пакетом, и ChunkHolder зовёт для этого
   // getUpdatePacket(), который в ванилле возвращает null. Без переопределения
   // markForUpdate() молча ничего не отправляет: сервер thinks всё хорошо, а
   // клиентская копия вечно formed=false с пустыми границами. Рендер жидкости
   // на этом молча выходил по раннему return.
   //
   // Шлём только то, что нужно клиенту для отрисовки: полная saveAdditional()
   // тащила бы за собой содержимое обоих танков и список всех клапанов структуры.
   // ------------------------------------------------------------------

   @Override
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putBoolean("formed", this.formed);
      tag.putInt("capacity", this.capacity);
      if (this.masterPos != null) {
         tag.putLong("masterPos", this.masterPos.asLong());
      }
      if (this.minPos != null) {
         tag.putLong("minPos", this.minPos.asLong());
      }
      if (this.maxPos != null) {
         tag.putLong("maxPos", this.maxPos.asLong());
      }
      tag.putInt("heat", this.heat);
      tag.putInt("displayWaterFill", this.displayWaterFill);
      tag.putInt("displaySteamFill", this.displaySteamFill);
      return tag;
   }

   @Override
   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   @Override
   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.formed = tag.getBoolean("formed");
      this.capacity = tag.getInt("capacity");
      this.masterPos = tag.contains("masterPos") ? BlockPos.of(tag.getLong("masterPos")) : null;
      this.minPos = tag.contains("minPos") ? BlockPos.of(tag.getLong("minPos")) : null;
      this.maxPos = tag.contains("maxPos") ? BlockPos.of(tag.getLong("maxPos")) : null;
      this.heat = tag.getInt("heat");
      this.displayWaterFill = tag.getInt("displayWaterFill");
      this.displaySteamFill = tag.getInt("displaySteamFill");
   }
}
