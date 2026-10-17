package com.industrialreforged.tech.item.armor;

import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.item.ElectricProperties;
import com.industrialreforged.tech.item.armor.base.AbstractFluidArmorItem;

public class ItemArmorCFPack extends AbstractFluidArmorItem {
   public ItemArmorCFPack() {
      super(new ElectricProperties(), IRFluids.CONSTRUCTION_FOAM.getFluidStill(), 80000);
   }
}
