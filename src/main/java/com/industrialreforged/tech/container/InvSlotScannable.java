package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.core.recipe.manager.UuRecipeManager;
import net.minecraft.world.item.ItemStack;

public class InvSlotScannable extends InvSlotConsumable {
   public InvSlotScannable(BaseBlockEntity entity, String name, int size) {
      super(entity, name, size);
      this.setMaxStackSize(1);
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return !UuRecipeManager.instance.find(this.entity.getLevel(), stack).isEmpty();
   }
}
