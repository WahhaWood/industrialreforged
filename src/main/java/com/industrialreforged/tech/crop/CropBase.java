package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.api.crops.ICropType;
import net.minecraft.world.level.ItemLike;

public abstract class CropBase extends IRCropCard {
   protected final ItemLike cropDrop;

   public CropBase(ICropType cropType, ItemLike cropDrop) {
      super(cropType);
      this.cropDrop = cropDrop;
   }
}
