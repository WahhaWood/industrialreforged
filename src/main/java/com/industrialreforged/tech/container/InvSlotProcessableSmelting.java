package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.InductionFurnaceBlockEntity;
import com.industrialreforged.tech.blockentity.machine.IronFurnaceBlockEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public class InvSlotProcessableSmelting<T extends BaseBlockEntity> extends InvSlotProcessable<T, SmeltingRecipe> {
   public InvSlotProcessableSmelting(T entity, String name, int size) {
      super(entity, name, size, RecipeType.SMELTING);
      this.setCanOutput(false);
   }

   @Override
   public boolean assemble(int slot) {
      SmeltingRecipe recipe = this.process(slot);
      if (recipe == null) {
         return false;
      }

      if (this.entity instanceof InductionFurnaceBlockEntity induction) {
         this.shrink(slot);
         induction.outputSlot.add(recipe.getResultItem(RegistryAccess.EMPTY).copy());
      } else {
         if (this.entity instanceof ElectricFurnace1LevelBlockEntity induction) {
            this.shrink(slot);
            induction.outputSlot.add(recipe.getResultItem(RegistryAccess.EMPTY).copy());
            return true;
         }

         if (this.entity instanceof IronFurnaceBlockEntity induction) {
            this.shrink(slot);
            induction.outputSlot.add(recipe.getResultItem(RegistryAccess.EMPTY).copy());
            return true;
         }
      }

      return false;
   }
}
