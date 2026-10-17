package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.api.item.IKineticRotor;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;

public class InvSlotConsumableKineticRotor extends InvSlotConsumableClass {
   private final IKineticRotor.GearboxType type;

   public InvSlotConsumableKineticRotor(BaseBlockEntity entity, String name, int size, IKineticRotor.GearboxType gearboxType) {
      super(entity, name, size, IKineticRotor.class);
      this.type = gearboxType;
   }

   @Override
   public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
      return super.canPlaceItem(index, stack) ? ((IKineticRotor)stack.getItem()).isAcceptedType(stack, this.type) : false;
   }
}
