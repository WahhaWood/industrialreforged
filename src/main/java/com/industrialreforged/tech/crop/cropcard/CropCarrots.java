package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.CropVanilla;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class CropCarrots extends CropVanilla {
   public CropCarrots(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.CARROTS_CROP.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(2, 0, 4, 0, 0, 2);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Orange", "Food", "Carrots"};
   }

   @Override
   public ItemStack getProduct() {
      return new ItemStack(Items.CARROT);
   }

   @Override
   public ItemStack getSeeds() {
      return new ItemStack(Items.CARROT);
   }
}
