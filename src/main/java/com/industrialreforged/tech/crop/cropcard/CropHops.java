package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropHops extends IRCropCard {
   public CropHops(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.HOPS_CROP.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(5, 2, 2, 0, 1, 1);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Green", "Ingredient", "Wheat"};
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return 600;
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() >= 9;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack((ItemLike)IRItems.HOPS.get());
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return 2;
   }
}
