package com.industrialreforged.tech.client.screen.widgets.builder;

import net.minecraft.resources.ResourceLocation;

public class GaugeProperties {
   public final short uInner;
   public final short vInner;
   public final short innerWidth;
   public final short innerHeight;
   public final short hoverXOffset;
   public final short hoverYOffset;
   public final short hoverWidth;
   public final short hoverHeight;
   public final short bgXOffset;
   public final short bgYOffset;
   public final short bgWidth;
   public final short bgHeight;
   public final short uBgInactive;
   public final short vBgInactive;
   public final short uBgActive;
   public final short vBgActive;
   public final boolean vertical;
   public final boolean reverse;
   public final boolean smooth;
   public boolean heatInfo = false;
   public final ResourceLocation texture;

   public GaugeProperties(
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6,
      int n7,
      int n8,
      int n9,
      int n10,
      int n11,
      int n12,
      int n13,
      int n14,
      int n15,
      int n16,
      boolean bl,
      boolean bl2,
      boolean bl3,
      ResourceLocation resourceLocation
   ) {
      this.uInner = (short)n;
      this.vInner = (short)n2;
      this.innerWidth = (short)n3;
      this.innerHeight = (short)n4;
      this.hoverXOffset = (short)n5;
      this.hoverYOffset = (short)n6;
      this.hoverWidth = (short)n7;
      this.hoverHeight = (short)n8;
      this.bgXOffset = (short)n9;
      this.bgYOffset = (short)n10;
      this.bgWidth = (short)n11;
      this.bgHeight = (short)n12;
      this.uBgInactive = (short)n13;
      this.vBgInactive = (short)n14;
      this.uBgActive = (short)n15;
      this.vBgActive = (short)n16;
      this.vertical = bl;
      this.reverse = bl2;
      this.smooth = bl3;
      this.texture = resourceLocation;
   }

   public GaugeProperties(
      int n,
      int n2,
      int n3,
      int n4,
      int n5,
      int n6,
      int n7,
      int n8,
      int n9,
      int n10,
      int n11,
      int n12,
      int n13,
      int n14,
      int n15,
      int n16,
      boolean bl,
      boolean bl2,
      boolean bl3,
      ResourceLocation resourceLocation,
      boolean heatInfo
   ) {
      this(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16, bl, bl2, bl3, resourceLocation);
      this.heatInfo = heatInfo;
   }
}
