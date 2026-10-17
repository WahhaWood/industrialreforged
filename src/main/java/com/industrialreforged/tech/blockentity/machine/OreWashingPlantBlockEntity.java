package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByList;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableStandard;
import com.industrialreforged.tech.core.recipe.OreWashingRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.menu.machine.OreWashingPlantMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class OreWashingPlantBlockEntity extends AbstractStandardMachineBlockEntity<OreWashingRecipe> implements IFluidWrapper {
   public final InvSlotConsumableLiquid fluidSlot = new InvSlotConsumableLiquidByList(this, "fluid", 1, Fluids.WATER);
   public final InvSlotOutput cellSlot = new InvSlotOutput(this, "cell", 1);
   public final FluidTank fluidTank = new FluidTank(8000, true, false, stack -> stack.is(Fluids.WATER));

   public OreWashingPlantBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(16, 500, 3, IRBlockEntities.ORE_WASHING_PLANT.get(), blockPos, blockState);
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableStandard<>(this, "input", 1, IRRecipeTypes.ORE_WASHER.get());
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_EXTRACTOR_START.get();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.fluidSlot.load(tag, registries);
      this.cellSlot.load(tag, registries);
      this.fluidTank.load(registries, tag, "tank");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.fluidSlot.save(tag, registries);
      this.cellSlot.save(tag, registries);
      this.fluidTank.save(registries, tag, "tank");
   }

   @Nullable
   public OreWashingRecipe getRecipe(int index) {
      OreWashingRecipe recipe = (OreWashingRecipe)super.getRecipe(index);
      return recipe != null && this.fluidTank.getFluidAmount() >= recipe.getAmount() ? recipe : null;
   }

   /** Пакет укрепления ограничен ещё и запасом воды в баке. */
   @Override
   protected int getBatchSize(int index, OreWashingRecipe recipe) {
      int wanted = super.getBatchSize(index, recipe);
      if (recipe == null) {
         return wanted;
      }

      int byFluid = this.fluidTank.getFluidAmount() / Math.max(1, recipe.getAmount());
      return Math.max(1, Math.min(wanted, byFluid));
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new OreWashingPlantMenu(containerId, playerInventory, this);
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.fluidSlot, this.cellSlot, this.fluidTank);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.FLUID_CONSUMING,
         UpgradableProperty.STRENGTHENING,
         UpgradableProperty.SOUNDPROOFING
      );
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.fluidTank;
   }
}
