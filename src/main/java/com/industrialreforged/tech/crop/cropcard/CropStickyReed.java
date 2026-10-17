package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropStickyReed extends IRCropCard {
   public CropStickyReed(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.STICKY_REED_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "raa1337";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(4, 2, 0, 1, 0, 1);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Reed", "Resin"};
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
      return (crop == null ? this.random.nextBoolean() : crop.getCurrentAge() <= this.getMaxAge() - 1)
         ? new ItemStack(Items.SUGAR_CANE, crop == null ? this.random.nextInt(3) : crop.getCurrentAge())
         : new ItemStack((ItemLike)IRItems.RESIN.get());
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() ? (byte)(2 - IR.random.nextInt(2)) : 0;
   }

   @Override
   public boolean onEntityCollision(ICropTile paramICropTile, Entity paramEntity) {
      return false;
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() ? 400 : 100;
   }
}
