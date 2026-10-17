package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropCocoa extends IRCropCard {
   public CropCocoa(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.COCOA_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "Notch";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(3, 1, 3, 0, 4, 0);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Brown", "Food", "Stem"};
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() <= this.getMaxAge() - 1;
   }

   @Override
   public int getWeightInfluences(ICropTile paramICropTile, int paramInt1, int paramInt2, int paramInt3) {
      return (int)(paramInt1 * 0.8 + paramInt2 * 1.3 + paramInt3 * 0.9);
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack(Items.COCOA_BEANS);
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() - 1 ? 900 : 400;
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return this.getMaxAge() - 1;
   }
}
