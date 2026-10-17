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

public class CropCoffee extends IRCropCard {
   public CropCoffee(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.COFFEE_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "Snoochy";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(7, 1, 4, 1, 2, 0);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Leaves", "Ingredient", "Beans"};
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() >= 9;
   }

   @Override
   public int getWeightInfluences(ICropTile paramICropTile, int paramInt1, int paramInt2, int paramInt3) {
      return (int)(0.4 * paramInt1 + 1.4 * paramInt2 + 1.2 * paramInt3);
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      if (paramICropTile.getCurrentAge() == this.getMaxAge() - 2) {
         return (int)(super.getGrowthDuration(paramICropTile) * 0.5);
      } else {
         return paramICropTile.getCurrentAge() == this.getMaxAge() - 3
            ? (int)(super.getGrowthDuration(paramICropTile) * 1.5)
            : super.getGrowthDuration(paramICropTile);
      }
   }

   @Override
   public boolean canBeHarvested(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() >= this.getMaxAge() - 1;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return crop != null && crop.getCurrentAge() == this.getMaxAge() - 1 ? null : new ItemStack((ItemLike)IRItems.COFFEE_BEANS.get());
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return this.getMaxAge() - 2;
   }
}
