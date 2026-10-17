package com.industrialreforged.tech.item.tool;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemScannerAdv extends ItemScanner {
   public ItemScannerAdv(Properties properties) {
      super(4000000, 2048, properties);
   }

   @Override
   public int startLayerScan(ItemStack itemStack) {
      return this.use(itemStack, 1000, false) ? this.getScanRange() / 2 : 0;
   }

   @Override
   public int getScanRange() {
      return 12;
   }
}
