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

public class CropMelon extends CropVanillaStem {
   public CropMelon(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.MELON_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "Chao";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(2, 0, 4, 0, 2, 0);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Green", "Food", "Stem"};
   }

   @Override
   protected ItemStack getProduct() {
      return IR.random.nextInt(3) == 0 ? new ItemStack(Blocks.MELON) : new ItemStack(Items.MELON, IR.random.nextInt(4) + 2);
   }

   @Override
   protected ItemStack getSeeds() {
      return new ItemStack(Items.MELON_SEEDS, IR.random.nextInt(2) + 1);
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() - 1 ? 700 : 250;
   }
}
