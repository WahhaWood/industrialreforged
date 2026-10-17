package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CropEmpty extends IRCropCard {
   public static final CropCard EMPTY = new CropEmpty();

   public CropEmpty() {
      super(null);
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public String getOwner() {
      return "industrialreforged";
   }

   @Override
   public String getName() {
      return "empty";
   }

   @Override
   public Block getCropBlock() {
      return Blocks.AIR;
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(0, 0, 0, 0, 0, 0);
   }

   @Override
   public int getMaxAge() {
      return 0;
   }
}
