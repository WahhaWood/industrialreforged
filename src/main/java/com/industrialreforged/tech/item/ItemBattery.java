package com.industrialreforged.tech.item;

import com.industrialreforged.item.energy.BasicEnergyItem;
import net.minecraft.world.item.Item.Properties;

public class ItemBattery extends BasicEnergyItem {
   public ItemBattery(int capacity, int energyTick, boolean chargeInventory, Properties properties) {
      super(properties, capacity, energyTick);
   }

   public ItemBattery(int capacity, int energyTick, Properties properties) {
      super(properties, capacity, energyTick);
   }
}
