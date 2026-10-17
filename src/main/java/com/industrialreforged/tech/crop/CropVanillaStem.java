package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public abstract class CropVanillaStem extends CropVanilla {
   public CropVanillaStem(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public List<ResourceLocation> getTexturesLocation() {
      return this.getDefaultTexturesLocation();
   }

   @Override
   public int getWeightInfluences(ICropTile paramICropTile, int paramInt1, int paramInt2, int paramInt3) {
      return (int)(paramInt1 * 1.1 + paramInt2 * 0.9 + paramInt3);
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return this.getMaxAge() - 1;
   }
}
