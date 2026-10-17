package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelConnectorBlockEntity;
import com.industrialreforged.tech.item.SolarPanelBlockItem;
import net.minecraft.world.item.ItemStack;

public class InvSlotSolarConnector extends InvSlotConsumable {
   private final SolarPanelConnectorBlockEntity connector;

   public InvSlotSolarConnector(SolarPanelConnectorBlockEntity connector, String name, int size) {
      super(connector, name, size);
      this.connector = connector;
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof SolarPanelBlockItem;
   }

   @Override
   public void setChanged() {
      super.setChanged();
      this.connector.updatePanels();
   }
}
