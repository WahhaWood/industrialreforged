package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class InvSlotConsumableClass extends InvSlotConsumable {
   private final Class<?> clazz;

   public InvSlotConsumableClass(BaseBlockEntity entity, String name, int size, Class<?> class_) {
      super(entity, name, size);
      this.clazz = class_;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         return stack.getItem() instanceof BlockItem ? this.clazz.isInstance(Block.byItem(stack.getItem())) : this.clazz.isInstance(stack.getItem());
      }
   }
}
