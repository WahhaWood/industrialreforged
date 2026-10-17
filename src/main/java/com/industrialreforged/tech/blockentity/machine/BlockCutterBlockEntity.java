package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.api.item.IBlockCuttingBlade;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableClass;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotProcessableStandard;
import com.industrialreforged.tech.core.recipe.BlockCutterRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.BlockCutterMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockCutterBlockEntity extends AbstractStandardMachineBlockEntity<BlockCutterRecipe> {
   public final InvSlotConsumableClass cutterSlot = new InvSlotConsumableClass(this, "cutterInputSlot", 1, IBlockCuttingBlade.class);

   public BlockCutterBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(48, 900, 1, IRBlockEntities.BLOCK_CUTTER.get(), blockPos, blockState);
   }

   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACERATOR_START.get();
   }

   @Override
   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_MACHINE_INTERRUPT.get();
   }

   @Override
   protected InvSlotProcessable<?, ?> createInputSlot(int outputs) {
      return new InvSlotProcessableStandard<>(this, "input", 1, IRRecipeTypes.BLOCK_CUTTER.get());
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.cutterSlot.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.cutterSlot.save(tag, registries);
   }

   @Nullable
   public BlockCutterRecipe getRecipe(int slot) {
      if (this.cutterSlot.isEmpty()) {
         return null;
      }

      BlockCutterRecipe recipe = (BlockCutterRecipe)super.getRecipe(slot);
      if (recipe == null) {
         return null;
      }

      ItemStack itemStack = this.cutterSlot.getItem();
      IBlockCuttingBlade cuttingBlade = (IBlockCuttingBlade)itemStack.getItem();
      return recipe.hardness > cuttingBlade.getHardness(itemStack) ? null : recipe;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new BlockCutterMenu(containerId, playerInventory, this);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.STRENGTHENING,
         UpgradableProperty.SOUNDPROOFING
      );
   }
}
