package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.crop.IRCropBlock;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CropBaseMetalUncommon extends CropBaseMetalCommon {
   public CropBaseMetalUncommon(
      ICropType cropType,
      Supplier<IRCropBlock> cropBlock,
      String[] cropAttributes,
      Collection<TagKey<Block>> cropRootsRequirement,
      ItemLike cropDrop,
      int farmingChange
   ) {
      super(cropType, cropBlock, cropAttributes, cropRootsRequirement, cropDrop, farmingChange);
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(6, 2, 0, 0, 2, 0);
   }

   @Override
   public double dropGainChance() {
      return Math.pow(0.95, this.getProperties().tier());
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() - 1 ? 2200 : 750;
   }
}
