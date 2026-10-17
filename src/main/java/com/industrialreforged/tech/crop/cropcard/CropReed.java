package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropReed extends IRCropCard {
   public CropReed(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.REED_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "Notch";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(2, 0, 0, 1, 0, 2);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Reed"};
   }

   @Override
   public int getWeightInfluences(ICropTile paramICropTile, int paramInt1, int paramInt2, int paramInt3) {
      return (int)(paramInt1 * 1.2 + paramInt2 + paramInt3 * 0.8);
   }

   @Override
   public boolean canBeHarvested(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() > 0;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack(Items.SUGAR_CANE, crop == null ? 1 : crop.getCurrentAge());
   }

   @Override
   public boolean onEntityCollision(ICropTile paramICropTile, Entity paramEntity) {
      return false;
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return 200;
   }
}
