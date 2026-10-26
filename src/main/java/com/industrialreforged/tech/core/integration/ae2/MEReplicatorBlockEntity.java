package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import com.industrialreforged.tech.core.recipe.manager.UuRecipeManager;
import com.industrialreforged.tech.core.ref.IRFluids;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * ME replicator: no pattern storage and no tank. The player puts samples into its GUI, and it keeps making each
 * of them (any item with a scanner recipe) from the UU matter of the ME network, at the regular replicator's FE per
 * UU paid as AE. The matter is taken in whole mB; the part of the last mB a batch did not use stays in the machine.
 */
public class MEReplicatorBlockEntity extends AbstractMENodeBlockEntity {
   public static final int SAMPLES = 18;
   /** Effectively unlimited: the real bounds are power, matter and network room. */
   public static final int ITEMS_PER_TICK = Integer.MAX_VALUE;
   /** UU units in 1 mB of matter (the regular replicator drains 1 mB per 100 units). */
   public static final int UU_PER_MB = 100;
   /** FE per UU unit in the regular replicator: 512 FE per tick at 10 units per tick. */
   public static final double FE_PER_UU = 51.2;
   /** AE per UU unit, at 2 FE = 1 AE. */
   public static final double AE_PER_UU = FE_PER_UU / MEMachineKind.FE_PER_AE;
   /** Ticks a sample the network cannot afford waits before the next try. */
   private static final int EMPTY_PAUSE = 20;

   public final SimpleContainer samples = new SimpleContainer(SAMPLES) {
      @Override
      public void setChanged() {
         super.setChanged();
         Arrays.fill(MEReplicatorBlockEntity.this.emptyUntil, 0L); // a new sample is tried at once
         MEReplicatorBlockEntity.this.saveChanges();
         MEReplicatorBlockEntity.this.wake();
      }
   };
   /** Items made in total (shown in the GUI). */
   public long replicated;
   /** UU units already taken from the network but not yet spent, 0..99. */
   private long reserve;
   /** Items made but not yet accepted by the network. */
   private ItemStack pendingItem = ItemStack.EMPTY;
   private long pendingCount;
   private final long[] emptyUntil = new long[SAMPLES];

   public MEReplicatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   long pendingCount() {
      return this.pendingCount;
   }

   @Override
   protected boolean hasWork() {
      return this.pendingCount > 0 || !this.samples.isEmpty();
   }

   @Override
   protected TickRateModulation doWork(IGrid grid, int ticks) {
      MEStorage storage = grid.getStorageService().getInventory();
      IEnergyService energy = grid.getEnergyService();
      this.flushPending(storage, energy);
      if (this.pendingCount > 0) {
         this.lastOperations = 0;
         return TickRateModulation.SLOWER; // no room for the items: stop making them until there is
      }

      AEFluidKey matter = AEFluidKey.of(IRFluids.UU_MATTER.getFluidStill());
      long budget = (long)ITEMS_PER_TICK * ticks;
      long done = 0;
      long now = this.level != null ? this.level.getGameTime() : 0L;
      for (int i = 0; i < SAMPLES && budget > 0; i++) {
         ItemStack sample = this.samples.getItem(i);
         int cost = sample.isEmpty() ? -1 : UuRecipeManager.instance.getCost(this.level, sample);
         if (cost <= 0 || now < this.emptyUntil[i]) {
            continue;
         }

         double aePerItem = cost * AE_PER_UU;
         long byEnergy = (long)Math.floor(this.availableAE(energy, aePerItem * budget) / aePerItem + 1.0E-6);
         long wanted = Math.min(budget, byEnergy);
         long mbWanted = wanted <= 0 ? 0 : ceilDiv(Math.max(0L, wanted * cost - this.reserve), UU_PER_MB);
         long mbAvailable = mbWanted > 0 ? storage.extract(matter, mbWanted, Actionable.SIMULATE, this.actionSource) : 0L;
         long made = wanted <= 0 ? 0 : Math.min(wanted, (this.reserve + mbAvailable * UU_PER_MB) / cost);
         // the output must fit too: a full network stops the machine before it spends matter on items it cannot hand over
         if (made > 0) {
            made = Math.min(made, storage.insert(AEItemKey.of(sample), made, Actionable.SIMULATE, this.actionSource));
         }

         if (made <= 0) {
            this.emptyUntil[i] = now + EMPTY_PAUSE;
            continue;
         }

         long mb = ceilDiv(Math.max(0L, made * cost - this.reserve), UU_PER_MB);
         long taken = mb > 0 ? storage.extract(matter, mb, Actionable.MODULATE, this.actionSource) : 0L;
         this.reserve += taken * UU_PER_MB - made * cost;
         this.payAE(energy, aePerItem * made);
         budget -= made;
         done += made;
         this.pendingItem = sample.copyWithCount(1);
         this.pendingCount = made;
         this.flushPending(storage, energy);
         if (this.pendingCount > 0) {
            break; // the rest of the network stopped taking this item: handle it next time
         }
      }

      if (done > 0) {
         this.replicated += done;
         this.saveChanges();
      }

      this.lastOperations = (int)(done / ticks);
      if (done > 0 || this.pendingCount > 0) {
         return TickRateModulation.URGENT;
      }

      return this.samples.isEmpty() ? TickRateModulation.SLEEP : TickRateModulation.SLOWER;
   }

   private static long ceilDiv(long a, long b) {
      return (a + b - 1) / b;
   }

   private void flushPending(MEStorage storage, IEnergyService energy) {
      if (this.pendingCount <= 0) {
         return;
      }

      long inserted = this.insertResult(energy, storage, AEItemKey.of(this.pendingItem), this.pendingCount);
      if (inserted > 0) {
         this.pendingCount -= inserted;
         this.saveChanges();
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
      tag.put("me_samples", ContainerHelper.saveAllItems(new CompoundTag(), this.samples.getItems(), registries));
      tag.putLong("me_replicated", this.replicated);
      tag.putLong("me_reserve", this.reserve);
      tag.putLong("me_pending_count", this.pendingCount);
      if (this.pendingCount > 0) {
         tag.put("me_pending_item", this.pendingItem.save(registries));
      }
   }

   @Override
   public void loadTag(CompoundTag tag, HolderLookup.Provider registries) {
      super.loadTag(tag, registries);
      this.samples.getItems().replaceAll(ignored -> ItemStack.EMPTY);
      ContainerHelper.loadAllItems(tag.getCompound("me_samples"), this.samples.getItems(), registries);
      this.replicated = tag.getLong("me_replicated");
      this.reserve = tag.getLong("me_reserve");
      this.pendingCount = tag.getLong("me_pending_count");
      this.pendingItem = tag.contains("me_pending_item") ? ItemStack.parseOptional(registries, tag.getCompound("me_pending_item")) : ItemStack.EMPTY;
      if (this.pendingItem.isEmpty()) {
         this.pendingCount = 0;
      }
   }

   @Override
   public void addContentDrops(List<ItemStack> drops) {
      if (this.level != null && this.pendingCount > 0 && !this.pendingItem.isEmpty()) {
         AEItemKey.of(this.pendingItem).addDrops(this.pendingCount, drops, this.level, this.worldPosition);
      }
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
      return new MEReplicatorMenu(containerId, inventory, this);
   }
}
