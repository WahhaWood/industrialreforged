package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.tech.core.util.StackUtil;
import net.minecraft.world.item.ItemStack;

public class InvSlotConsumableLinked extends InvSlotConsumable {
   public final BasicSimpleContainer linkedSlot;

   public InvSlotConsumableLinked(BaseBlockEntity entity, String string, int size, BasicSimpleContainer invSlot) {
      super(entity, string, size);
      this.linkedSlot = invSlot;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      ItemStack itemStack = this.linkedSlot.getItem();
      return itemStack.isEmpty() ? false : StackUtil.checkItemEqualityStrict(itemStack, stack);
   }

   public ItemStack consumeLinked(boolean bl) {
      ItemStack itemStack = this.linkedSlot.getItem();
      if (itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      }

      int n = itemStack.getCount();
      ItemStack itemStack2 = this.consume(n, true, true);
      return !itemStack2.isEmpty() && itemStack2.getCount() == n ? this.consume(n, bl, true) : ItemStack.EMPTY;
   }
}
