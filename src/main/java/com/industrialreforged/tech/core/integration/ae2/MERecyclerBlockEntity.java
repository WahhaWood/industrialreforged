package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import com.industrialreforged.tech.blockentity.machine.Recycler1LevelBlockEntity;
import com.industrialreforged.tech.core.recipe.RecyclerRecipe;
import com.industrialreforged.tech.core.ref.IRItems;
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
 * ME recycler ("eater"): not an autocrafting machine. The player puts samples into its GUI, and it pulls
 * those items out of the ME network down to zero, up to {@value #ITEMS_PER_TICK} per tick, at the
 * recycler's FE per item paid as AE. Scrap is exact instead of rolled: every 8th item gives one.
 */
public class MERecyclerBlockEntity extends AbstractMENodeBlockEntity {
   public static final int SAMPLES = 18;
   /** Items eaten per tick, above the crafting machines' {@link #OPERATIONS_PER_TICK}: a sample is one network extraction whatever the amount. */
   public static final int ITEMS_PER_TICK = 4096;
   /** Ticks a sample that is not in the network waits before the next search. */
   private static final int EMPTY_PAUSE = 20;
   /** AE per recycled item: the regular recycler's FE per operation at 2 FE = 1 AE (22.5). */
   public static final double AE_PER_ITEM = Recycler1LevelBlockEntity.ENERGY_CONSUME * Recycler1LevelBlockEntity.OPERATION_LENGTH / MEMachineKind.FE_PER_AE;

   public final SimpleContainer samples = new SimpleContainer(SAMPLES) {
      @Override
      public void setChanged() {
         super.setChanged();
         Arrays.fill(MERecyclerBlockEntity.this.emptyUntil, 0L); // a new sample is searched at once
         MERecyclerBlockEntity.this.saveChanges();
         MERecyclerBlockEntity.this.wake();
      }
   };
   /** Items eaten in total (shown in the GUI). */
   public long recycled;
   /** Items eaten since the last scrap, 0..7. */
   private int sinceScrap;
   /** Scrap made but not yet accepted by the network. */
   private long pendingScrap;
   /** Per sample: game tick before which it is not searched for again (it was missing from the network). */
   private final long[] emptyUntil = new long[SAMPLES];

   public MERecyclerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   int sinceScrap() {
      return this.sinceScrap;
   }

   long pendingScrap() {
      return this.pendingScrap;
   }

   @Override
   protected boolean hasWork() {
      return this.pendingScrap > 0 || !this.samples.isEmpty();
   }

   @Override
   protected TickRateModulation doWork(IGrid grid, int ticks) {
      MEStorage storage = grid.getStorageService().getInventory();
      IEnergyService energy = grid.getEnergyService();
      this.flushScrap(storage, energy);
      if (this.pendingScrap > 0) {
         return TickRateModulation.SLOWER; // no room for scrap: stop eating until there is
      }

      long budget = (long)ITEMS_PER_TICK * ticks;
      long done = 0;
      long now = this.level != null ? this.level.getGameTime() : 0L;
      for (int i = 0; i < SAMPLES && budget > 0; i++) {
         ItemStack sample = this.samples.getItem(i);
         if (!RecyclerRecipe.accepts(sample) || now < this.emptyUntil[i]) {
            continue;
         }

         AEItemKey key = AEItemKey.of(sample);
         long affordable = (long)Math.floor(energy.extractAEPower(AE_PER_ITEM * budget, Actionable.SIMULATE, PowerMultiplier.ONE) / AE_PER_ITEM + 1.0E-6);
         if (affordable <= 0) {
            break;
         }

         long eaten = StorageHelper.poweredExtraction(energy, storage, key, Math.min(budget, affordable), this.actionSource);
         if (eaten <= 0) {
            // none of it in the network: every extraction searches all of the storage, so look again later
            this.emptyUntil[i] = now + EMPTY_PAUSE;
            continue;
         }

         energy.extractAEPower(AE_PER_ITEM * eaten, Actionable.MODULATE, PowerMultiplier.ONE);
         budget -= eaten;
         done += eaten;
      }

      if (done > 0) {
         this.recycled += done;
         long total = this.sinceScrap + done;
         this.pendingScrap += total / RecyclerRecipe.SCRAP_CHANCE;
         this.sinceScrap = (int)(total % RecyclerRecipe.SCRAP_CHANCE);
         this.flushScrap(storage, energy);
         this.saveChanges();
      }

      this.lastOperations = (int)(done / ticks);
      if (done > 0 || this.pendingScrap > 0) {
         return TickRateModulation.URGENT;
      }

      // samples set but nothing to eat right now: poll, slower and slower
      return this.samples.isEmpty() ? TickRateModulation.SLEEP : TickRateModulation.SLOWER;
   }

   private void flushScrap(MEStorage storage, IEnergyService energy) {
      if (this.pendingScrap <= 0) {
         return;
      }

      long inserted = StorageHelper.poweredInsert(energy, storage, AEItemKey.of(IRItems.SCRAP.get()), this.pendingScrap, this.actionSource);
      if (inserted > 0) {
         this.pendingScrap -= inserted;
         this.saveChanges();
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
      // with slot indices: the samples keep their places across reloads
      tag.put("me_samples", ContainerHelper.saveAllItems(new CompoundTag(), this.samples.getItems(), registries));
      tag.putLong("me_recycled", this.recycled);
      tag.putInt("me_since_scrap", this.sinceScrap);
      tag.putLong("me_pending_scrap", this.pendingScrap);
   }

   @Override
   public void loadTag(CompoundTag tag, HolderLookup.Provider registries) {
      super.loadTag(tag, registries);
      this.samples.getItems().replaceAll(ignored -> ItemStack.EMPTY);
      ContainerHelper.loadAllItems(tag.getCompound("me_samples"), this.samples.getItems(), registries);
      this.recycled = tag.getLong("me_recycled");
      this.sinceScrap = tag.getInt("me_since_scrap");
      this.pendingScrap = tag.getLong("me_pending_scrap");
   }

   @Override
   public void addContentDrops(List<ItemStack> drops) {
      if (this.level != null && this.pendingScrap > 0) {
         AEItemKey.of(IRItems.SCRAP.get()).addDrops(this.pendingScrap, drops, this.level, this.worldPosition);
      }
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
      return new MERecyclerMenu(containerId, inventory, this);
   }
}
