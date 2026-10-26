package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import com.industrialreforged.tech.blockentity.machine.MatterGeneratorBlockEntity;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * ME matter generator: the regular generator's recipe (4M FE per mB, scrap takes off up to 1M FE per mB) with no
 * tank and no slots. Scrap and scrap boxes are taken from the ME network as they are needed, UU matter goes into it
 * as a fluid. Energy comes from the network (AE) and from any energy cable (FE, 2 FE = 1 AE); the cables fill a buffer
 * so big that they never meet its limit.
 */
public class MEMatterGeneratorBlockEntity extends AbstractMENodeBlockEntity {
   /** AE the buffer holds: a trillion times a single ME machine's, so a cable never finds it full. */
   public static final double BUFFER_AE = 1.0E15;
   /** Effectively unlimited: the real bounds are power and network room. */
   public static final int MB_PER_TICK = Integer.MAX_VALUE;
   private static final long BOX_VALUE = MatterGeneratorBlockEntity.SCRAP_BOX_VALUE;
   private static final long SCRAP_VALUE = MatterGeneratorBlockEntity.SCRAP_VALUE;
   private static final long PRICE = MatterGeneratorBlockEntity.ENERGY_PER_MB;
   private static final long MAX_DISCOUNT = MatterGeneratorBlockEntity.MAX_DISCOUNT_PER_MB;

   /** mB made in total (shown in the GUI). */
   public long produced;
   /** FE the scrap has taken off the price in total (shown in the GUI). */
   public long saved;
   /** Discount of a scrap item that went past what the last cycle could use, FE; spent first next time. */
   private long reserve;
   /** mB made but not yet accepted by the network. */
   private long pendingMb;

   public MEMatterGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state, BUFFER_AE);
      this.cableOnly = true;
   }

   @Override
   public boolean supportsCableOnly() {
      return true;
   }

   long pendingMb() {
      return this.pendingMb;
   }

   /** Never sleeps: energy may arrive over a cable at any moment, which does not wake the node. */
   @Override
   protected boolean hasWork() {
      return true;
   }

   @Override
   protected TickRateModulation doWork(IGrid grid, int ticks) {
      MEStorage storage = grid.getStorageService().getInventory();
      IEnergyService energy = grid.getEnergyService();
      AEFluidKey matter = AEFluidKey.of(IRFluids.UU_MATTER.getFluidStill());
      this.flushMatter(storage, energy, matter);
      if (this.pendingMb > 0) {
         this.lastOperations = 0;
         return TickRateModulation.SLOWER; // no room for matter: stop making it until there is
      }

      long availableFe = (long)Math.min(4.0E18, this.availableAE(energy, 1.0E18) * MEMachineKind.FE_PER_AE);
      long budget = (long)MB_PER_TICK * ticks;
      // even with the whole discount a mB costs PRICE - MAX_DISCOUNT: no point looking at the network below that
      long byPower = Math.min(budget, availableFe / (PRICE - MAX_DISCOUNT));
      if (byPower <= 0) {
         this.lastOperations = 0;
         return TickRateModulation.SLOWER;
      }

      AEItemKey boxKey = AEItemKey.of(IRItems.SCRAP_BOX.get());
      AEItemKey scrapKey = AEItemKey.of(IRItems.SCRAP.get());
      long wanted = byPower * MAX_DISCOUNT - this.reserve;
      long boxesAvailable = wanted > 0 ? storage.extract(boxKey, ceilDiv(wanted, BOX_VALUE), Actionable.SIMULATE, this.actionSource) : 0L;
      long scrapWanted = wanted - boxesAvailable * BOX_VALUE;
      long scrapAvailable = scrapWanted > 0 ? storage.extract(scrapKey, ceilDiv(scrapWanted, SCRAP_VALUE), Actionable.SIMULATE, this.actionSource) : 0L;
      long discount = this.reserve + boxesAvailable * BOX_VALUE + scrapAvailable * SCRAP_VALUE;

      long mb = maxAffordable(availableFe, discount, byPower);
      if (mb > 0) {
         mb = Math.min(mb, storage.insert(matter, mb, Actionable.SIMULATE, this.actionSource));
      }

      if (mb <= 0) {
         this.lastOperations = 0;
         return TickRateModulation.SLOWER;
      }

      // take what this many mB can use, boxes first; a box instead of the last scrap when the scrap is not enough
      long need = Math.min(discount, mb * MAX_DISCOUNT) - this.reserve;
      long boxes = 0;
      long scrap = 0;
      if (need > 0) {
         boxes = Math.min(boxesAvailable, need / BOX_VALUE);
         scrap = ceilDiv(need - boxes * BOX_VALUE, SCRAP_VALUE);
         if (scrap > scrapAvailable && boxes < boxesAvailable) {
            boxes++;
            scrap = Math.max(0L, ceilDiv(need - boxes * BOX_VALUE, SCRAP_VALUE));
         }

         boxes = boxes > 0 ? storage.extract(boxKey, boxes, Actionable.MODULATE, this.actionSource) : 0L;
         scrap = scrap > 0 ? storage.extract(scrapKey, Math.min(scrap, scrapAvailable), Actionable.MODULATE, this.actionSource) : 0L;
      }

      long have = this.reserve + boxes * BOX_VALUE + scrap * SCRAP_VALUE;
      mb = maxAffordable(availableFe, have, mb);
      long used = Math.min(have, mb * MAX_DISCOUNT);
      this.reserve = have - used;
      long cost = mb * PRICE - used;
      if (mb > 0) {
         this.payAE(energy, cost / MEMachineKind.FE_PER_AE);
         this.produced += mb;
         this.saved += used;
         this.pendingMb += mb;
         this.flushMatter(storage, energy, matter);
      }

      this.saveChanges();
      this.lastOperations = (int)(mb / ticks);
      return mb > 0 ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
   }

   /** Most mB (at most {@code limit}) that {@code availableFe} pays for when scrap worth {@code discount} FE is at hand. */
   static long maxAffordable(long availableFe, long discount, long limit) {
      long low = 0;
      long high = limit;
      while (low < high) {
         long mid = (low + high + 1) >>> 1;
         if (mid * PRICE - Math.min(discount, mid * MAX_DISCOUNT) <= availableFe) {
            low = mid;
         } else {
            high = mid - 1;
         }
      }

      return low;
   }

   private static long ceilDiv(long a, long b) {
      return (a + b - 1) / b;
   }

   private void flushMatter(MEStorage storage, IEnergyService energy, AEFluidKey matter) {
      if (this.pendingMb <= 0) {
         return;
      }

      long inserted = this.insertResult(energy, storage, matter, this.pendingMb);
      if (inserted > 0) {
         this.pendingMb -= inserted;
         this.saveChanges();
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putLong("me_produced", this.produced);
      tag.putLong("me_saved", this.saved);
      tag.putLong("me_reserve", this.reserve);
      tag.putLong("me_pending_mb", this.pendingMb);
   }

   @Override
   public void loadTag(CompoundTag tag, HolderLookup.Provider registries) {
      super.loadTag(tag, registries);
      this.produced = tag.getLong("me_produced");
      this.saved = tag.getLong("me_saved");
      this.reserve = tag.getLong("me_reserve");
      this.pendingMb = tag.getLong("me_pending_mb");
   }

   @Override
   public void addContentDrops(List<ItemStack> drops) {
      if (this.level != null && this.reserve >= SCRAP_VALUE) {
         AEItemKey.of(IRItems.SCRAP.get()).addDrops(this.reserve / SCRAP_VALUE, drops, this.level, this.worldPosition);
      }
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
      return new MEMatterGeneratorMenu(containerId, inventory, this);
   }
}
