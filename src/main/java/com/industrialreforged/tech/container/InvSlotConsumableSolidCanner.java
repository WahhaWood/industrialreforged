package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.machine.SolidCannerBlockEntity;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

public class InvSlotConsumableSolidCanner extends InvSlotConsumableLiquid {
   private final SolidCannerBlockEntity canner;

   public InvSlotConsumableSolidCanner(SolidCannerBlockEntity canner, String name, int size) {
      super(canner, name, size);
      this.canner = canner;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      ItemStack center = this.canner.inputSlot.getItem();
      Predicate<SolidCannerRecipe> predicate = center.isEmpty() ? r -> r.input.test(stack) : r -> r.center.test(center) && r.input.test(stack);
      return SolidCannerRecipe.gets().stream().anyMatch(predicate);
   }
}
