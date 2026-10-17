package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropWeed extends IRCropCard {
   public CropWeed(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.WEED_CROP.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(0, 0, 0, 1, 0, 5);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Weed", "Bad"};
   }

   @Override
   public int getOptimalHarvestAge(ICropTile paramICropTile) {
      return 1;
   }

   @Override
   public boolean onLeftClick(ICropTile paramICropTile, Player paramPlayer) {
      return false;
   }

   @Override
   public boolean canBeHarvested(ICropTile paramICropTile) {
      return false;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return ItemStack.EMPTY;
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return 300;
   }

   @Override
   public boolean onEntityCollision(ICropTile paramICropTile, Entity paramEntity) {
      return false;
   }
}
