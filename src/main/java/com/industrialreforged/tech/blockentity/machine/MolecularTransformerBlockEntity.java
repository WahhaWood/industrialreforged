package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.tech.blockentity.base.AbstractMolecularTransformerBlockEntity;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.menu.machine.MolecularTransformerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MolecularTransformerBlockEntity extends AbstractMolecularTransformerBlockEntity<MolecularTransformerRecipe> {
   public MolecularTransformerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.MOLECULAR_TRANSFORMER.get(), blockPos, blockState, IRRecipeTypes.MOLECULAR_TRANSFORMER.get(), 1);
   }

   protected int getWorkCount(MolecularTransformerRecipe recipe) {
      ItemStack stack = this.getItem(0);
      int perOperation = Math.max(1, recipe.getIngredient().count());
      int available = stack.isEmpty() ? 0 : stack.getCount() / perOperation;
      if (available <= 0 || this.level == null) {
         return 0;
      }

      int resultCount = Math.max(1, recipe.getResultItem(this.level.registryAccess()).getCount());
      int bySlot = Math.max(1, Mth.floor(64.0F / resultCount));
      int multiplier = Math.max(1, this.upgradeSlot.getOperationsPerTick());
      return Math.min(available, Math.min(multiplier, bySlot));
   }

   protected long getRequiredEnergy(MolecularTransformerRecipe recipe) {
      return recipe.energy;
   }

   @Override
   protected boolean isValidItem(int index, ItemStack stack) {
      return this.level != null
         && this.level
            .getRecipeManager()
            .byType(IRRecipeTypes.MOLECULAR_TRANSFORMER.get())
            .stream()
            .anyMatch(holder -> ((MolecularTransformerRecipe)holder.value()).getIngredient().ingredient().test(stack));
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new MolecularTransformerMenu(containerId, playerInventory, this);
   }
}
