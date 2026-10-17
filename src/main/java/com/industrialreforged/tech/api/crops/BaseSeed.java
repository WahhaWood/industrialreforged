package com.industrialreforged.tech.api.crops;

public class BaseSeed {
   public final CropCard crop;
   public int size;
   public int statGrowth;
   public int statGain;
   public int statResistance;

   public BaseSeed(CropCard paramCropCard, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
      this.crop = paramCropCard;
      this.size = paramInt1;
      this.statGrowth = paramInt2;
      this.statGain = paramInt3;
      this.statResistance = paramInt4;
   }

   @Deprecated
   public BaseSeed(CropCard paramCropCard, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
      this(paramCropCard, paramInt1, paramInt2, paramInt3, paramInt4);
   }
}
