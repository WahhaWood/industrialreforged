package com.industrialreforged.tech.blockentity.heat;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.block.heat.HeatPipeBlock;
import com.industrialreforged.tech.blockentity.base.AbstractHeatSourceBlockEntity;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.multiblock.tank.TankValveBlockEntity;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Тепловая труба. Все соединённые трубы образуют сеть с ОДНИМ общим котлом тепла:
 * буфер сети = сумма буферов труб, поток сети = поток самой слабой трубы,
 * а рассеивание = сумма потерь всех труб (чем длиннее сеть, тем больше утечка).
 */
public class HeatPipeBlockEntity extends BaseBlockEntity implements IServerTick {
   /** Сети труб по мирам: ключ — позиция мастера (самой младшей трубы сети). */
   private static final Map<Level, Map<BlockPos, HeatNetwork>> NETWORKS = new WeakHashMap<>();

   /** Общая сеть труб: топология, общий лимит передачи и потери. */
   static final class HeatNetwork {
      final BlockPos master;
      final Set<BlockPos> pipes = new HashSet<>();
      int capacity;
      int rate;
      int loss;
      /** Последнее известное тепло сети (нужно, если мастер выгружен/сломан). */
      int heat;
      /** true — тепло сети уже распределено по другим сетям. */
      boolean absorbed;
      /** true — состав сети изменился (часть труб ушла); параметры нужно пересчитать. */
      boolean topologyDirty;
      long rateTick = Long.MIN_VALUE;
      int rateUsed;
      long lossTick = Long.MIN_VALUE;

      HeatNetwork(BlockPos master) {
         this.master = master;
      }

      /** Общий лимит передачи на тик: сколько ещё можно потратить. */
      int consumeRate(long now, int amount) {
         if (this.rateTick != now) {
            this.rateTick = now;
            this.rateUsed = 0;
         }

         int left = Math.max(0, this.rate - this.rateUsed);
         int take = Math.min(left, amount);
         this.rateUsed += take;
         return take;
      }

      void refundRate(int amount) {
         this.rateUsed = Math.max(0, this.rateUsed - amount);
      }

      /** Возвращает true, если в этот тик сеть ещё не теряла тепло. */
      boolean applyLoss(long now) {
         if (this.lossTick == now) {
            return false;
         }

         this.lossTick = now;
         return true;
      }

      @Nullable
      HeatPipeBlockEntity masterEntity(Level level) {
         return level.getBlockEntity(this.master) instanceof HeatPipeBlockEntity pipe ? pipe : null;
      }
   }

   @Nullable
   private BlockPos networkKey = null;
   /** Живое тепло сети: у мастера — источник истины, у остальных — зеркало для отладки. */
   public int heat;
   private boolean refreshConnections = true;
   /** true — сеть нужно перестроить (сменились соседи/мастер), ключ при этом сохраняем для наследования тепла. */
   private boolean networkDirty = true;

   public HeatPipeBlockEntity(BlockPos pos, BlockState state) {
      super(IRBlockEntities.HEAT_PIPE.get(), pos, state);
   }

   private int tier() {
      return this.getBlockState().getBlock() instanceof HeatPipeBlock pipe ? pipe.tier : 0;
   }

   private int rate() {
      return HeatPipeBlock.rate(this.tier());
   }

   /** Смена соседей: заставляем перестроить сеть на следующем тике (ключ сохраняем для наследования тепла). */
   public void invalidateNetwork() {
      this.networkDirty = true;
      this.refreshConnections = true;
   }

   /** Загружен ли чанк позиции прямо сейчас, без подгрузки с диска. */
   private boolean isNeighborLoaded(BlockPos pos) {
      return this.level != null
         && this.level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) != null;
   }

   @Override
   public void onLoad() {
      super.onLoad();
      this.refreshConnections = true;
   }

   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (this.refreshConnections) {
         this.refreshConnections = false;
         if (this.getBlockState().getBlock() instanceof HeatPipeBlock pipe) {
            BlockState updated = pipe.connectionState(this.getBlockState(), this.level, this.worldPosition);
            if (updated != this.getBlockState()) {
               this.level.setBlock(this.worldPosition, updated, 3);
               return;
            }
         }
      }

      HeatNetwork net = this.network();
      if (net == null) {
         return;
      }

      HeatPipeBlockEntity master = net.masterEntity(this.level);
      if (master == null) {
         // мастер выгружен или сломан — перестроим сеть на следующем тике
         this.networkDirty = true;
         return;
      }

      long now = this.level.getGameTime();
      int heatBefore = master.heat;

      // 1) забор тепла у машин и теплогенераторов — в общий котёл сети
      for (Direction dir : Direction.values()) {
         BlockPos neighborPos = this.worldPosition.relative(dir);
         // Тот же гвард, что в проводах: без проверки getBlockEntity тянет чужой
         // чанк с диска и вешает серверный поток. Нет чанка — нет соседа.
         if (!this.isNeighborLoaded(neighborPos)) {
            continue;
         }
         BlockEntity be = this.level.getBlockEntity(neighborPos);
         if (be instanceof AbstractStandardMachineBlockEntity<?> machine) {
            int allowed = net.consumeRate(now, this.rate());
            if (allowed <= 0) {
               break;
            }
            int got = machine.pullHeat(allowed);
            net.refundRate(allowed - got);
            master.heat = Math.min(net.capacity, master.heat + got);
         } else if (be instanceof com.industrialreforged.tech.api.tile.IHeatSource source) {
            int allowed = net.consumeRate(now, this.rate());
            if (allowed <= 0) {
               break;
            }
            int got = source.extractHeat(dir.getOpposite(), allowed, false);
            net.refundRate(allowed - got);
            master.heat = Math.min(net.capacity, master.heat + got);
         }
      }

      // 2) сброс тепла в резервуар-котёл (тот же общий лимит)
      for (Direction dir : Direction.values()) {
         if (master.heat <= 0) {
            break;
         }
         BlockPos neighborPos = this.worldPosition.relative(dir);
         if (!this.isNeighborLoaded(neighborPos)) {
            continue;
         }
         BlockEntity be = this.level.getBlockEntity(neighborPos);
         if (be instanceof TankValveBlockEntity tankValve) {
            int allowed = net.consumeRate(now, this.rate());
            if (allowed <= 0) {
               break;
            }
            int pushed = tankValve.pushHeat(Math.min(allowed, master.heat));
            net.refundRate(allowed - pushed);
            master.heat -= pushed;
         }
      }

      // 3) рассеивание — один раз за тик на сеть; растёт с числом труб
      if (net.applyLoss(now) && net.loss > 0 && master.heat > 0) {
         master.heat = Math.max(0, master.heat - net.loss);
      }

      // 4) мастер — источник истины: сохраняем и обновляем кэш сети
      net.heat = master.heat;
      if (this.worldPosition.equals(net.master)) {
         if (heatBefore != master.heat) {
            this.markForSave();
         }
      } else {
         this.heat = master.heat;
      }
   }

   // ------------------------------------------------------------------
   // Сети труб
   // ------------------------------------------------------------------

   @Nullable
   private HeatNetwork network() {
      Map<BlockPos, HeatNetwork> nets = NETWORKS.computeIfAbsent(this.level, level -> new HashMap<>());
      HeatNetwork net = this.networkKey == null ? null : nets.get(this.networkKey);
      if (net != null && !net.absorbed && !net.topologyDirty && !this.networkDirty && net.pipes.contains(this.worldPosition)) {
         return net;
      }

      return this.rebuildNetwork(nets);
   }

   /** Обход всех соединённых труб, выбор мастера и пересчёт параметров сети. */
   private HeatNetwork rebuildNetwork(Map<BlockPos, HeatNetwork> nets) {
      Set<BlockPos> component = new HashSet<>();
      Deque<BlockPos> queue = new ArrayDeque<>();
      component.add(this.worldPosition);
      queue.add(this.worldPosition);

      while (!queue.isEmpty()) {
         BlockPos p = queue.poll();
         for (Direction dir : Direction.values()) {
            BlockPos n = p.relative(dir);
            // Труба в выгруженном чанке не тикает — в активную сеть ей не место.
            // Догрузится — invalidateNetwork перестроит сеть с ней.
            if (!component.contains(n) && this.isNeighborLoaded(n)
               && this.level.getBlockEntity(n) instanceof HeatPipeBlockEntity) {
               component.add(n);
               queue.add(n);
            }
         }
      }

      int capacity = 0;
      int rate = Integer.MAX_VALUE;
      int loss = 0;
      BlockPos master = null;
      for (BlockPos p : component) {
         if (this.level.getBlockEntity(p) instanceof HeatPipeBlockEntity pipe) {
            int t = pipe.tier();
            capacity += HeatPipeBlock.capacity(t);
            rate = Math.min(rate, HeatPipeBlock.rate(t));
            loss += HeatPipeBlock.ambientLoss(t);
            if (master == null || p.asLong() < master.asLong()) {
               master = p;
            }
         }
      }

      if (master == null || rate == Integer.MAX_VALUE) {
         return null;
      }

      // Тепло старых сетей: при слиянии — всё, при разделении — пропорционально трубам
      Map<BlockPos, Integer> oldInComponent = new HashMap<>();
      for (BlockPos p : component) {
         if (this.level.getBlockEntity(p) instanceof HeatPipeBlockEntity pipe && pipe.networkKey != null) {
            oldInComponent.merge(pipe.networkKey, 1, Integer::sum);
         }
      }

      int inherited = 0;
      for (BlockPos key : new ArrayList<>(oldInComponent.keySet())) {
         HeatNetwork old = nets.get(key);
         if (old != null && old.absorbed) {
            continue;
         }

         int oldHeat;
         int oldSize;
         if (old != null) {
            oldHeat = old.heat;
            oldSize = Math.max(1, old.pipes.size());
         } else if (this.level.getBlockEntity(key) instanceof HeatPipeBlockEntity keyPipe) {
            oldHeat = keyPipe.heat;
            oldSize = Math.max(1, oldInComponent.get(key));
         } else {
            oldHeat = 0;
            oldSize = 1;
         }

         int inComponent = oldInComponent.get(key);
         int share = oldSize <= inComponent ? oldHeat : (int)((long)oldHeat * inComponent / oldSize);
         share = Math.min(share, oldHeat);
         inherited += share;

         if (old != null) {
            old.heat = Math.max(0, old.heat - share);
            if (old.pipes.removeAll(component)) {
               old.topologyDirty = true;
            }
            if (old.pipes.isEmpty() || old.heat <= 0) {
               old.absorbed = true;
               nets.remove(key);
            }
         }
      }

      HeatNetwork net = new HeatNetwork(master);
      net.pipes.addAll(component);
      net.capacity = capacity;
      net.rate = rate;
      net.loss = loss;
      net.heat = Math.min(Math.max(0, inherited), capacity);

      for (BlockPos p : component) {
         if (this.level.getBlockEntity(p) instanceof HeatPipeBlockEntity pipe) {
            pipe.networkKey = master;
            pipe.networkDirty = false;
            pipe.markForSave();
         }
      }

      if (this.level.getBlockEntity(master) instanceof HeatPipeBlockEntity masterPipe) {
         masterPipe.heat = net.heat;
      }

      nets.put(master, net);
      return net;
   }

   @Override
   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.heat = tag.getInt("heat");
      this.networkKey = tag.contains("network") ? BlockPos.of(tag.getLong("network")) : null;
   }

   @Override
   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putInt("heat", this.heat);
      if (this.networkKey != null) {
         tag.putLong("network", this.networkKey.asLong());
      }
   }
}
