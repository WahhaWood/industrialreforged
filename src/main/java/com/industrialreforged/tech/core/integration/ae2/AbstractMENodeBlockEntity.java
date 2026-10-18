package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.events.GridPowerStorageStateChanged;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.me.energy.StoredEnergyAmount;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Common part of the ME machines: a channel-using grid node, a private AE buffer (100M, 100G for the molecular
 * transformer) that only energy cables charge (FE, 2 FE = 1 AE) and only this machine spends, and work driven by
 * AE2's grid ticker (no block entity ticker).
 *
 * <p>Loaded only when AE2 is present: every class of this package is touched only from behind the
 * {@code ModList.isLoaded("ae2")} guard in {@code IR} (lesson of 1.0.8, see DEVNOTES).
 */
public abstract class AbstractMENodeBlockEntity extends AENetworkedBlockEntity implements IAEPowerStorage, IGridTickable, MenuProvider {
   /** Built-in buffer, AE. Not a storage of the network: only cables charge it and only the machine draws from it. */
   public static final double BUFFER_AE = 100_000_000.0;
   /** Buffer of the ME molecular transformer, AE: a single recipe of it can cost millions of AE. */
   public static final double MOLECULAR_BUFFER_AE = 100_000_000_000.0;
   /** Upper bound of operations (items) one machine handles per tick, on average. */
   public static final int OPERATIONS_PER_TICK = 1024;
   /**
    * Ticks between two work passes of a busy machine; a pass does the work of the ticks since the last one.
    * Every pass talks to the network (power, inserting results), so batching cuts that cost by this factor.
    */
   public static final int WORK_INTERVAL = 5;

   private final StoredEnergyAmount buffer;
   protected final IActionSource actionSource = IActionSource.ofMachine(this);
   /** Operations per tick during the last work pass, for the GUI. */
   public int lastOperations;

   protected AbstractMENodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      this(type, pos, state, BUFFER_AE);
   }

   protected AbstractMENodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, double bufferAe) {
      super(type, pos, state);
      this.buffer = new StoredEnergyAmount(0, bufferAe, this::emitPowerEvent);
      this.getMainNode()
         .setFlags(GridFlags.REQUIRE_CHANNEL)
         .addService(IAEPowerStorage.class, this)
         .addService(IGridTickable.class, this);
   }

   /** Does the work of {@code ticks} game ticks (1..{@value #WORK_INTERVAL}) and tells AE2 how soon to come back. */
   protected abstract TickRateModulation doWork(IGrid grid, int ticks);

   protected abstract boolean hasWork();

   /** Items held by the machine (queued inputs, undelivered outputs) that drop when it is broken. */
   public abstract void addContentDrops(List<ItemStack> drops);

   protected final void wake() {
      this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
   }

   @Override
   public TickingRequest getTickingRequest(IGridNode node) {
      return new TickingRequest(WORK_INTERVAL, 20, !this.hasWork(), WORK_INTERVAL);
   }

   @Override
   public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
      IGrid grid = node.getGrid();
      if (!this.getMainNode().isActive() || grid == null) {
         this.lastOperations = 0;
         return TickRateModulation.SLEEP;
      }

      // a wake-up after a long sleep must not turn into a huge pass: the work of one interval at most
      TickRateModulation next = this.doWork(grid, Math.max(1, Math.min(ticksSinceLastCall, WORK_INTERVAL)));
      if (next == TickRateModulation.SLEEP) {
         this.lastOperations = 0;
      }

      return next;
   }

   @Override
   public void onMainNodeStateChanged(IGridNodeListener.State reason) {
      // channel or power came back: resume a queue that went to sleep while offline
      this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
   }

   @Override
   public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putDouble("me_buffer", this.buffer.getAmount());
   }

   @Override
   public void loadTag(CompoundTag tag, HolderLookup.Provider registries) {
      super.loadTag(tag, registries);
      this.buffer.setStored(tag.getDouble("me_buffer"));
   }

   // ---- FE input: any energy cable can charge the buffer, 2 FE = 1 AE ------------------------------

   /** The buffer as a Forge energy storage: it only takes energy in. Registered for every ME node in {@link AE2Integration}. */
   public IEnergyStorage feInput() {
      return this.feInput;
   }

   /**
    * Pays {@code ae} for work: the machine's own buffer first (what the cables bring), the network for the rest.
    * Callers ask {@link #availableAE} first.
    */
   protected final void payAE(IEnergyService energy, double ae) {
      double own = Math.min(ae, this.buffer.getAmount());
      if (own > 0) {
         this.buffer.extract(own, true);
         this.saveChanges();
      }

      if (ae - own > 0) {
         energy.extractAEPower(ae - own, Actionable.MODULATE, PowerMultiplier.ONE);
      }
   }

   /** How much of {@code ae} the machine can pay now: its own buffer and what the network would give for the rest. */
   protected final double availableAE(IEnergyService energy, double ae) {
      double own = Math.min(ae, this.buffer.getAmount());
      return own + (ae - own > 0 ? energy.extractAEPower(ae - own, Actionable.SIMULATE, PowerMultiplier.ONE) : 0.0);
   }

   private final IEnergyStorage feInput = new IEnergyStorage() {
      @Override
      public int receiveEnergy(int maxReceive, boolean simulate) {
         double room = buffer.getMaximum() - buffer.getAmount();
         int accepted = (int)Math.max(0L, Math.min((long)maxReceive, (long)Math.floor(room * MEMachineKind.FE_PER_AE)));
         if (accepted > 0 && !simulate) {
            buffer.insert(accepted / MEMachineKind.FE_PER_AE, true);
            saveChanges();
         }

         return accepted;
      }

      @Override
      public int extractEnergy(int maxExtract, boolean simulate) {
         return 0;
      }

      @Override
      public int getEnergyStored() {
         return (int)Math.min(Integer.MAX_VALUE, (long)(buffer.getAmount() * MEMachineKind.FE_PER_AE));
      }

      @Override
      public int getMaxEnergyStored() {
         return (int)Math.min(Integer.MAX_VALUE, (long)(buffer.getMaximum() * MEMachineKind.FE_PER_AE));
      }

      @Override
      public boolean canExtract() {
         return false;
      }

      @Override
      public boolean canReceive() {
         return true;
      }
   };

   // ---- IAEPowerStorage: the buffer is the machine's own, the network does not see it ---------------

   @Override
   public double injectAEPower(double amt, Actionable mode) {
      double inserted = this.buffer.insert(amt, mode == Actionable.MODULATE);
      if (mode == Actionable.MODULATE && inserted > 0) {
         this.saveChanges();
      }

      return amt - inserted;
   }

   @Override
   public double extractAEPower(double amt, Actionable mode, PowerMultiplier multiplier) {
      double extracted = this.buffer.extract(multiplier.multiply(amt), mode == Actionable.MODULATE);
      if (mode == Actionable.MODULATE && extracted > 0) {
         this.saveChanges();
      }

      return multiplier.divide(extracted);
   }

   @Override
   public double getAEMaxPower() {
      return this.buffer.getMaximum();
   }

   @Override
   public double getAECurrentPower() {
      return this.buffer.getAmount();
   }

   @Override
   public boolean isAEPublicPowerStorage() {
      return false;
   }

   @Override
   public AccessRestriction getPowerFlow() {
      return AccessRestriction.READ_WRITE;
   }

   private void emitPowerEvent(GridPowerStorageStateChanged.PowerEventType type) {
      this.getMainNode().ifPresent(grid -> grid.postEvent(new GridPowerStorageStateChanged(this, type)));
   }

   @Override
   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }
}
