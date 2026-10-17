package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import java.util.Collection;
import net.minecraft.world.item.ItemStack;

public class InvSlotOutput extends BasicSimpleContainer {
   public InvSlotOutput(BaseBlockEntity entity, int size) {
      this(entity, "output", size);
   }

   public InvSlotOutput(BaseBlockEntity entity, String name, int size) {
      super(entity, name, size);
      this.setCanInput(false);
   }

   public boolean canPlaceItem(int index, ItemStack itemStack) {
      return false;
   }

   public boolean canAdd(ItemStack itemStack) {
      return this.simulationAdd(itemStack);
   }

   public boolean canAdd(Collection<ItemStack> collection) {
      return this.simulationAdd(collection);
   }
}
