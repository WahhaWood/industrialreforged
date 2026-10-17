package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.CropVanilla;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class CropWheat extends CropVanilla {
   public CropWheat(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.WHEAT_CROP.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(1, 0, 4, 0, 0, 2);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Yellow", "Food", "Wheat"};
   }

   @Override
   public ItemStack getProduct() {
      return new ItemStack(Items.WHEAT, 1);
   }

   @Override
   public ItemStack getSeeds() {
      return new ItemStack(Items.WHEAT_SEEDS);
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return 2;
   }
}
