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

public class CropFlax extends IRCropCard {
   public CropFlax(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.FLAX_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "Eloraam";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(2, 1, 1, 2, 0, 1);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Silk", "Vine", "Addictive"};
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() >= 9;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack(Items.STRING);
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return 0;
   }
}
