package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.IRCropCard;
import com.industrialreforged.tech.crop.IRCrops;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropTerraWart extends IRCropCard {
   public CropTerraWart(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.TERRA_WART_CROP.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(5, 2, 4, 0, 3, 0);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Blue", "Aether", "Consumable", "Snow"};
   }

   @Override
   public double dropGainChance() {
      return 0.8;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack((ItemLike)IRItems.TERRA_WART.get());
   }

   @Override
   public void tick(ICropTile paramICropTile) {
      if (paramICropTile.isBlockBelow(Blocks.SNOW)) {
         if (this.canGrow(paramICropTile)) {
            paramICropTile.getGrowth().setPoints((short)(paramICropTile.getGrowth().getPoints() + 100));
         }
      } else if (paramICropTile.isBlockBelow(Blocks.SOUL_SAND) && paramICropTile.getWorldObj().random.nextInt(300) == 0) {
         paramICropTile.setCrop(IRCrops.cropNetherWart);
      }
   }

   @Override
   public int getRootsLength(ICropTile paramICropTile) {
      return 5;
   }
}
