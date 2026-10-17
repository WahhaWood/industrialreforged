package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.crop.IRCropCard;
import com.industrialreforged.tech.crop.IRCrops;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropNetherWart extends IRCropCard {
   public CropNetherWart(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.NETHER_WART_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "Notch";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(5, 4, 2, 0, 2, 1);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Red", "Nether", "Ingredient", "Soulsand"};
   }

   @Override
   public double dropGainChance() {
      return 2.0;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack(Items.NETHER_WART, 1);
   }

   @Override
   public void tick(ICropTile paramICropTile) {
      if (paramICropTile.isBlockBelow(Blocks.SOUL_SAND)) {
         if (this.canGrow(paramICropTile)) {
            paramICropTile.getGrowth().setPoints((short)(paramICropTile.getGrowth().getPoints() + 100));
         }
      } else if (paramICropTile.isBlockBelow(Blocks.SNOW) && paramICropTile.getWorldObj().random.nextInt(300) == 0) {
         paramICropTile.setCrop(IRCrops.cropTerraWart);
      }
   }

   @Override
   public int getRootsLength(ICropTile paramICropTile) {
      return 5;
   }
}
