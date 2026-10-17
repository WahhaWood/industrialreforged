package com.industrialreforged.tech.api.crops;

import com.industrialreforged.tech.crop.data.CropData;
import net.minecraft.world.item.ItemStack;

public interface ICropSeed {
   CropData getCropFromStack(ItemStack var1);

   void incrementScannedFromStack(ItemStack var1);
}
