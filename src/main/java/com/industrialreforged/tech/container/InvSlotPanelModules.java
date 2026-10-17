package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelBlockEntity;
import com.industrialreforged.tech.item.solar.SolarPanelModuleItem;
import net.minecraft.world.item.ItemStack;

public class InvSlotPanelModules extends InvSlotConsumable {
   private final SolarPanelBlockEntity panel;

   public InvSlotPanelModules(SolarPanelBlockEntity panel, String name, int size) {
      super(panel, name, size);
      this.panel = panel;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof SolarPanelModuleItem;
   }

   @Override
   public void setChanged() {
      super.setChanged();
      this.panel.recalculateModules();
   }
}
