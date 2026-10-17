package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.CropVanillaStem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CropPumpkin extends CropVanillaStem {
   public CropPumpkin(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.PUMPKIN_CROP.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(1, 0, 1, 0, 3, 1);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Orange", "Decoration", "Stem"};
   }

   @Override
   protected ItemStack getProduct() {
      return new ItemStack(Blocks.PUMPKIN);
   }

   @Override
   protected ItemStack getSeeds() {
      return new ItemStack(Items.PUMPKIN_SEEDS, IR.random.nextInt(3) + 1);
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() - 1 ? 600 : 200;
   }
}
