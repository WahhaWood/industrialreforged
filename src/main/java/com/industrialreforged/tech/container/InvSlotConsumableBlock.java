package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class InvSlotConsumableBlock extends InvSlotConsumable {
   public InvSlotConsumableBlock(BaseBlockEntity entity, String name, int n) {
      super(entity, name, n);
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return stack.getItem() instanceof BlockItem;
   }
}
