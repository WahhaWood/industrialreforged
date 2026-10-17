package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropRedWheat extends IRCropCard {
   public CropRedWheat(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.RED_WHEAT_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "raa1337";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(6, 3, 0, 0, 2, 0);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Red", "Redstone", "Wheat"};
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() <= 10 && paramICropTile.getLightLevel() >= 5;
   }

   @Override
   public double dropGainChance() {
      return 0.5;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      if (crop == null) {
         return this.random.nextInt(3000) < 5 ? new ItemStack(Items.REDSTONE) : (this.random.nextInt(200) < 25 ? new ItemStack(Items.WHEAT) : ItemStack.EMPTY);
      }

      BlockPos blockPos = crop.getPosition();
      return crop.getWorldObj().getBestNeighborSignal(blockPos) <= 0 && !crop.getWorldObj().random.nextBoolean()
         ? new ItemStack(Items.WHEAT, 1)
         : new ItemStack(Items.REDSTONE, 1);
   }

   @Override
   public boolean isRedstoneSignalEmitter(ICropTile paramICropTile) {
      return true;
   }

   @Override
   public int getEmittedRedstoneSignal(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() ? 15 : 0;
   }

   @Override
   public int getEmittedLight(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() ? 7 : 0;
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return 600;
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return 1;
   }
}
