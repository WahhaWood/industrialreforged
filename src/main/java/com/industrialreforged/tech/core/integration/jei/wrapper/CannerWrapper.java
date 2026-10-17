package com.industrialreforged.tech.core.integration.jei.wrapper;

import com.industrialreforged.tech.item.ItemCell;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

public record CannerWrapper(
   Ingredient input,
   int inputCount,
   Fluid inputFluid,
   int inputAmount,
   Ingredient center,
   int centerCount,
   ItemStack result,
   Fluid resultFluid,
   int resultAmount
) {
   public CannerWrapper(Fluid inputFluid, int inputAmount, Ingredient center, int centerCount, Fluid resultFluid, int resultAmount) {
      this(
         DataComponentIngredient.of(false, ItemCell.getCell(inputFluid, inputAmount)),
         inputAmount / 1000,
         inputFluid,
         inputAmount,
         center,
         centerCount,
         ItemCell.getCell(resultFluid, resultAmount),
         resultFluid,
         resultAmount
      );
   }
}
