package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.IR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;

public abstract class IRCropCard extends CropCard {
   protected final Random random = new Random();

   public IRCropCard(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public String getUnlocalizedName() {
      return "crop.industrialreforged." + this.getName();
   }

   @Override
   public String getDiscoveredBy() {
      return "IR Team";
   }

   @Override
   public List<ResourceLocation> getTexturesLocation() {
      ArrayList<ResourceLocation> arrayList = new ArrayList<>(this.getMaxAge());

      for (byte b = 1; b <= this.getMaxAge(); b++) {
         arrayList.add(IR.texture("blocks/crop/" + this.getName() + "_" + b));
      }

      return arrayList;
   }
}
