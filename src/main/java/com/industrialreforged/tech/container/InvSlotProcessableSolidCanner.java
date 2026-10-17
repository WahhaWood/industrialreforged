package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.machine.SolidCannerBlockEntity;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InvSlotProcessableSolidCanner extends InvSlotProcessable<SolidCannerBlockEntity, SolidCannerRecipe> {
   public InvSlotProcessableSolidCanner(SolidCannerBlockEntity canner, String name, int size) {
      super(canner, name, size, null);
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      ItemStack input = this.entity.canInputSlot.getItem();
      Predicate<SolidCannerRecipe> predicate = input.isEmpty() ? r -> r.center.test(stack) : r -> r.input.test(input) && r.center.test(stack);
      return SolidCannerRecipe.gets().stream().anyMatch(predicate);
   }

   @Nullable
   public SolidCannerRecipe process(int slot) {
      SolidCannerBlockEntity canner = this.entity;
      ItemStack input = canner.canInputSlot.getItem();
      ItemStack center = this.getItem();
      SolidCannerRecipe recipe = SolidCannerRecipe.gets().stream().filter(r -> r.matches(input, center)).findFirst().orElse(null);
      // Возвращаем настоящий рецепт, а не пустышку: иначе гейт места в выходе
      // и кламп пакета укрепления видят пустой результат и продукт сгорает.
      return recipe != null && canner.outputSlot.canAdd(recipe.result) ? recipe : null;
   }

   @Override
   public boolean assemble(int slot) {
      SolidCannerBlockEntity canner = this.entity;
      ItemStack input = canner.canInputSlot.getItem();
      ItemStack center = this.getItem();
      SolidCannerRecipe recipe = SolidCannerRecipe.gets().stream().filter(r -> r.matches(input, center)).findFirst().orElse(null);
      if (recipe == null) {
         return false;
      }

      recipe.assemble(canner.canInputSlot, canner.inputSlot, canner.outputSlot);
      return true;
   }
}
