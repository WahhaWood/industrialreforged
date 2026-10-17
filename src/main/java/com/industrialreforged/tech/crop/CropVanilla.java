package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.crop.data.CropData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CropVanilla extends IRCropCard {
   public CropVanilla(ICropType paramICropType) {
      super(paramICropType);
   }

   protected List<ResourceLocation> getDefaultTexturesLocation() {
      return super.getTexturesLocation();
   }

   @Override
   public List<ResourceLocation> getTexturesLocation() {
      ArrayList<ResourceLocation> arrayList = new ArrayList<>(this.getMaxAge());

      for (byte b = 1; b <= this.getMaxAge(); b++) {
         arrayList.add(ResourceLocation.withDefaultNamespace("blocks/" + this.getName() + "_stage_" + b));
      }

      return arrayList;
   }

   @Override
   public String getDiscoveredBy() {
      return "Notch";
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() >= 9;
   }

   protected abstract ItemStack getSeeds();

   protected abstract ItemStack getProduct();

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return this.getProduct();
   }

   @Override
   public ItemStack getSeeds(ICropTile tile) {
      CropData data = tile.getCrop();
      return data.getGain() <= 1 && data.getGrowth() <= 1 && data.getResistance() <= 1 ? this.getSeeds() : super.getSeeds(tile);
   }
}
