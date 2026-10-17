package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropPotato extends IRCropCard {
   public CropPotato(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.POTATO_CROP.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(2, 0, 4, 0, 0, 2);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Yellow", "Food", "Potato"};
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() >= 9;
   }

   @Override
   public int getOptimalHarvestAge(ICropTile paramICropTile) {
      return this.getMaxAge() - 1;
   }

   @Override
   public boolean canBeHarvested(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() >= this.getMaxAge() - 1;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      if (crop != null && crop.getCurrentAge() >= this.getMaxAge() && IR.random.nextInt(20) <= 0) {
         return new ItemStack(Items.POISONOUS_POTATO);
      } else {
         return crop != null && crop.getCurrentAge() < this.getMaxAge() - 1 ? null : new ItemStack(Items.POTATO);
      }
   }
}
