package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.energy.IAEPowerStorage;
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

/**
 * Common part of the ME machines: a channel-using grid node, a 100M AE buffer that the network sees as
 * one of its power storages, and work driven by AE2's grid ticker (no block entity ticker).
 *
 * <p>Loaded only when AE2 is present: every class of this package is touched only from behind the
 * {@code ModList.isLoaded("ae2")} guard in {@code IR} (lesson of 1.0.8, see DEVNOTES).
 */
public abstract class AbstractMENodeBlockEntity extends AENetworkedBlockEntity implements IAEPowerStorage, IGridTickable, MenuProvider {
   /** Built-in buffer, AE. Charged by the network like an energy cell and drained by it too. */
   public static final double BUFFER_AE = 100_000_000.0;
   /** Upper bound of operations (items) one machine handles per tick, on average. */
   public static final int OPERATIONS_PER_TICK = 1024;
   /**
    * Ticks between two work passes of a busy machine; a pass does the work of the ticks since the last one.
    * Every pass talks to the network (power, inserting results), so batching cuts that cost by this factor.
    */
   public static final int WORK_INTERVAL = 5;

   private final StoredEnergyAmount buffer = new StoredEnergyAmount(0, BUFFER_AE, this::emitPowerEvent);
   protected final IActionSource actionSource = IActionSource.ofMachine(this);
   /** Operations per tick during the last work pass, for the GUI. */
   public int lastOperations;

   protected AbstractMENodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
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

   // ---- IAEPowerStorage: the buffer is a public storage of the network --------------------------

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
      return true;
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
