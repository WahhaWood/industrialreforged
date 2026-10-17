package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.utils.ContainerUtils;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.util.ItemMoveHelper;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager.CachedCheck;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMolecularTransformerBlockEntity<T extends com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe>
   extends AbstractMachineBlockEntity
   implements IUpgradableBlock,
   IEnergyWrapper,
   IContainerWrapper,
   RecipeInput {
   /** Minimum buffer, FE: 100G, so a big batch is not limited by the size of the recipe. */
   public static final long BUFFER_FE = 100_000_000_000L;
   public final CountingEnergyStorage energy = new CountingEnergyStorage(0L);
   public final BasicSimpleContainer inputSlot;
   public final InvSlotOutput outputSlot;
   public final InvSlotUpgrade upgradeSlot;
   public RecipeHolder<T> recipe;
   public int count;
   /** Measured input of the last second, in FE/t (shown live in the GUI). */
   public long energyPerTick;
   public long energyLeft;
   private long receivedAccumulator;
   private int receivedTicks;
   private final RecipeType<T> recipeType;
   private final CachedCheck<RecipeInput, T> quickCheck;
   private CombinedInvWrapper wrapper;

   public AbstractMolecularTransformerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState, RecipeType<T> recipeType, int inputSize) {
      super(type, blockPos, blockState);
      this.inputSlot = ContainerUtils.simple(this, "input", inputSize, this::isValidItem);
      this.inputSlot.setCanOutput(false);
      this.outputSlot = new InvSlotOutput(this, 1);
      this.upgradeSlot = new InvSlotUpgrade(this, 4);
      this.upgradeSlot.addListener(container -> this.changeRecipe());
      this.recipeType = recipeType;
      this.quickCheck = RecipeManager.createCheck(recipeType);
   }

   protected abstract int getWorkCount(T var1);

   protected abstract long getRequiredEnergy(T var1);

   /**
    * The transformer accumulates the full energy cost of a batch first and then
    * performs every operation of the batch at once.  {@code energyLeft} is the
    * amount still missing, which is what the GUI shows as progress.
    */
   @Override
   public void onLoad() {
      super.onLoad();
      this.upgradeSlot.setChanged();
   }

   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      ItemMoveHelper.tick(this.level, this.worldPosition, this.upgradeSlot, this.inputSlot, this.outputSlot);

      if (this.recipe == null) {
         this.findRecipe();
      } else if (this.energy.getStored() == 0L) {
         int newCount = this.getWorkCount((T)this.recipe.value());
         if (newCount > 0 && newCount != this.count) {
            ItemStack result = ((T)this.recipe.value()).getResultItem(this.level.registryAccess());
            if (!result.isEmpty() && this.outputSlot.canAdd(result.copyWithCount(result.getCount() * newCount))) {
               this.count = newCount;
               long total = this.getRequiredEnergy((T)this.recipe.value()) * (long)newCount;
               this.energyLeft = total;
               this.setEnergy(total);
               this.markForSave();
            }
         }
      }

      if (this.recipe != null) {
         long total = this.getRequiredEnergy((T)this.recipe.value()) * (long)Math.max(1, this.count);
         long stored = this.energy.getStored();
         this.energyLeft = Math.max(0L, total - stored);

         if (stored >= total && this.finishRecipe()) {
            this.recipe = null;
            this.count = 0;
            this.energyLeft = 0L;
            // keep what was paid beyond the batch cost: the buffer is bigger than one batch
            this.energy.setStored(Math.max(0L, stored - total));
            this.setEnergy(0L);
            this.markForSave();
            ItemMoveHelper.moveNow(this.level, this.worldPosition, this.upgradeSlot, this.inputSlot, this.outputSlot);
         } else if (stored >= total && !this.hasBatchInput()) {
            // The batch can never complete now (input pulled), so give the buffer back
            // instead of waiting for a recipe that is no longer achievable.
            this.clearRecipe();
            this.markForSave();
         }
      }

      // Measure what actually arrives instead of showing the configured rate:
      // the value in the GUI then keeps updating while the machine charges.
      // (Buffer deltas are useless here: a charged wire fills and finishes a
      // batch inside one tick, so the buffer is always back to 0 when we look.)
      this.receivedAccumulator += this.energy.drainReceived();

      if (++this.receivedTicks >= 20) {
         this.energyPerTick = this.receivedAccumulator / this.receivedTicks;
         this.receivedAccumulator = 0L;
         this.receivedTicks = 0;
      }
   }

   private void findRecipe() {
      if (this.level == null || this.getItem(0).isEmpty()) {
         this.freezeBuffer();
         return;
      }

      RecipeHolder<T> holder = this.quickCheck.getRecipeFor(this, this.level).orElse(null);
      if (holder == null) {
         this.freezeBuffer();
         return;
      }

      int operations = this.getWorkCount((T)holder.value());
      if (operations <= 0) {
         this.freezeBuffer();
         return;
      }

      ItemStack result = ((T)holder.value()).getResultItem(this.level.registryAccess());
      if (result.isEmpty() || !this.outputSlot.canAdd(result.copyWithCount(result.getCount() * operations))) {
         this.freezeBuffer();
         return;
      }

      this.recipe = holder;
      this.count = operations;
      long total = this.getRequiredEnergy((T)holder.value()) * (long)operations;
      this.energyLeft = total;
      this.setEnergy(total);
      this.markForSave();
   }

   /** True while the input slot still holds everything the running batch needs. */
   private boolean hasBatchInput() {
      if (this.recipe == null) {
         return false;
      }

      int required = Math.max(1, ((T)this.recipe.value()).getIngredient().count()) * Math.max(1, this.count);
      return this.inputSlot.getItem(0).getCount() >= required;
   }

   /**
    * Drops the running batch.  Without this the machine kept a locked buffer forever when
    * the player pulled the input out mid-charge: the recipe was never cleared, so it never
    * re-entered findRecipe() and sat at 100 % progress doing nothing until it was broken.
    */
   private void clearRecipe() {
      this.recipe = null;
      this.count = 0;
      this.energyLeft = 0L;
      this.freezeBuffer();
   }

   /** Holds on to whatever was paid instead of deleting it, and stops taking more in. */
   private void freezeBuffer() {
      this.setEnergy(this.energy.getStored());
   }

   /** Consumes the batch from the input slot and inserts the results. */
   private boolean finishRecipe() {
      T recipe = (T)this.recipe.value();
      ItemStack result = recipe.getResultItem(this.level.registryAccess()).copy();
      if (result.isEmpty()) {
         return false;
      }

      int perOperation = Math.max(1, recipe.getIngredient().count());
      int required = perOperation * Math.max(1, this.count);
      ItemStack input = this.inputSlot.getItem(0);
      if (input.getCount() < required) {
         return false;
      }

      ItemStack batch = result.copyWithCount(result.getCount() * Math.max(1, this.count));
      if (!this.outputSlot.canAdd(batch)) {
         return false;
      }

      ItemStack remaining = input.copyWithCount(input.getCount() - required);
      this.inputSlot.setItem(0, remaining);
      this.outputSlot.add(batch);
      return true;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.energy.load(tag, registries);
      this.inputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      if (tag.contains("recipeId")) {
         this.recipe = IR.findRecipeFromId(this.recipeType, ResourceLocation.tryParse(tag.getString("recipeId")));
         this.count = tag.getInt("multiplier");
         this.energyLeft = tag.getLong("energyLeft");
         // Capacity is the cost of the whole batch; the old build restored it from the
         // *remaining* energy, which left the buffer over-full and blocked every push.
         this.setEnergy(
            this.recipe != null
               ? this.getRequiredEnergy((T)this.recipe.value()) * (long)Math.max(1, this.count)
               : 0L
         );
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.energy.save(tag, registries);
      this.inputSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putLong("receivedPerTick", this.energyPerTick);

      if (this.recipe != null) {
         tag.putString("recipeId", this.recipe.id().toString());
         tag.putInt("multiplier", this.count);
         tag.putLong("energyLeft", this.energyLeft);
      }
   }

   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return this.energy;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.outputSlot.createWrapper()});
         }
         return this.wrapper;
      }

      return switch (direction) {
         case UP -> this.inputSlot.createWrapper();
         case DOWN -> this.outputSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> {
            if (this.wrapper == null) {
               this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.outputSlot.createWrapper()});
            }
            yield this.wrapper;
         }
      };
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.STRENGTHENING);
   }

   /**
    * Upgrade slot listener.  InvSlotUpgrade.setChanged() rewrites the capacity from the
    * default one, which is 0 here, so taking any upgrade out mid-charge left the batch
    * with a zero buffer: the wire could not push the rest and the craft froze forever.
    * Put the batch capacity back (or keep the frozen buffer when idle).
    */
   private void changeRecipe() {
      if (this.recipe != null) {
         this.setEnergy(this.getRequiredEnergy((T)this.recipe.value()) * (long)Math.max(1, this.count));
      } else {
         this.freezeBuffer();
      }
   }

   /**
    * Capacity follows the cost of the running batch, while the input itself has
    * no limit at all (same as the matter generator): whatever the wire offers in
    * a tick is accepted.
    */
   private void setEnergy(long value) {
      long capacity = Math.max(value, BUFFER_FE);
      this.energy.setCapacity(capacity);
      if (this.energy.getStored() > capacity) {
         this.energy.setStored(capacity);
      }

      this.energy.setReceive(Long.MAX_VALUE / 4);
   }

   public ItemStack getItem(int index) {
      return this.inputSlot.getItem(index);
   }

   public int size() {
      return this.inputSlot.getContainerSize();
   }

   protected boolean isValidItem(int index, ItemStack stack) {
      return true;
   }

   /** Counts energy pushed in from the outside (capability/network). */
   public static class CountingEnergyStorage extends BigEnergyStorage {
      private long received;

      public CountingEnergyStorage(long capacity) {
         super(capacity);
      }

      // NOTE: only the long variant counts.  BigEnergyStorage.receiveEnergy(int)
      // delegates here, so overriding both doubled every measurement (the wire
      // showed 650k/t while the GUI claimed 1.3M/t).
      @Override
      public long receiveEnergy(long maxReceive, boolean simulate) {
         long accepted = super.receiveEnergy(maxReceive, simulate);
         if (!simulate && accepted > 0L) {
            this.received += accepted;
         }

         return accepted;
      }

      /** Returns the received amount since the last call and resets it. */
      public long drainReceived() {
         long value = this.received;
         this.received = 0L;
         return value;
      }
   }
}
