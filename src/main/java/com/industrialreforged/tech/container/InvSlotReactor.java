package com.industrialreforged.tech.container;

import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.tech.api.reactor.IBaseReactorComponent;
import com.industrialreforged.tech.blockentity.reactor.NuclearReactorElectricBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InvSlotReactor extends BasicSimpleContainer {
   private final NuclearReactorElectricBlockEntity reactor;

   public InvSlotReactor(NuclearReactorElectricBlockEntity reactor, String name, int size) {
      super(reactor, name, size, 1);
      this.reactor = reactor;
   }

   public boolean canPlaceItem(int index, ItemStack itemStack) {
      Item item = itemStack.getItem();
      return !itemStack.isEmpty() && index % 9 < this.reactor.getReactorSize()
         ? item instanceof IBaseReactorComponent && ((IBaseReactorComponent)item).canBePlacedIn(itemStack, this.reactor)
         : false;
   }

   public ItemStack get(int column, int row) {
      return super.getItem(row * 9 + column);
   }

   public void put(int column, int row, ItemStack itemStack) {
      super.setItem(row * 9 + column, itemStack);
   }
}
