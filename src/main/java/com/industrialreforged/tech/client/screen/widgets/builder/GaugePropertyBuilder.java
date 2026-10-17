package com.industrialreforged.tech.client.screen.widgets.builder;

import com.industrialreforged.tech.client.screen.IRScreenBase;
import net.minecraft.resources.ResourceLocation;

public class GaugePropertyBuilder {
   private short uInner;
   private short vInner;
   private short innerWidth;
   private short innerHeight;
   private short hoverXOffset;
   private short hoverYOffset;
   private short hoverWidth;
   private short hoverHeight;
   private short bgXOffset;
   private short bgYOffset;
   private short bgWidth;
   private short bgHeight;
   private short uBgInactive;
   private short vBgInactive;
   private short uBgActive;
   private short vBgActive;
   private final boolean vertical;
   private final boolean reverse;
   private boolean smooth = true;
   private boolean heatInfo = false;
   private ResourceLocation texture = IRScreenBase.COMMON;

   private GaugePropertyBuilder(GaugePropertyBuilder.GaugeOrientation gaugeOrientation) {
      this.vertical = gaugeOrientation.vertical;
      this.reverse = gaugeOrientation.reverse;
   }

   public GaugePropertyBuilder uv(int u, int v) {
      this.uInner = toShort(u);
      this.vInner = toShort(v);
      return this;
   }

   public GaugePropertyBuilder size(int width, int height) {
      this.innerWidth = toShort(width);
      if (this.hoverWidth == 0) {
         this.hoverWidth = this.innerWidth;
      }

      this.innerHeight = toShort(height);
      if (this.hoverHeight == 0) {
         this.hoverHeight = this.innerHeight;
      }

      return this;
   }

   public GaugePropertyBuilder withHoverBorder(int n) {
      this.hoverXOffset = toShort(-n);
      this.hoverYOffset = toShort(-n);
      this.hoverWidth = toShort(this.innerWidth + 2 * n);
      this.hoverHeight = toShort(this.innerHeight + 2 * n);
      return this;
   }

   public GaugePropertyBuilder withHover(int offsetX, int offsetY, int width, int height) {
      this.hoverXOffset = toShort(offsetX);
      this.hoverYOffset = toShort(offsetY);
      this.hoverWidth = toShort(width);
      this.hoverHeight = toShort(height);
      return this;
   }

   public GaugePropertyBuilder setHover() {
      this.heatInfo = true;
      return this;
   }

   public GaugePropertyBuilder withBackground(int u, int v) {
      return this.withBackground(0, 0, this.innerWidth, this.innerHeight, u, v);
   }

   public GaugePropertyBuilder withBackground(int offsetX, int offsetY, int width, int height, int u, int v) {
      return this.withBackground(offsetX, offsetY, width, height, u, v, u, v);
   }

   public GaugePropertyBuilder withBackground(int uInactive, int vInactive, int uActive, int vActive) {
      return this.withBackground(0, 0, this.innerWidth, this.innerHeight, uInactive, vInactive, uActive, vActive);
   }

   public GaugePropertyBuilder withBackground(int offsetX, int offsetY, int width, int height, int uInactive, int vInactive, int uActive, int vActive) {
      this.bgXOffset = toShort(offsetX);
      this.bgYOffset = toShort(offsetY);
      this.bgWidth = toShort(width);
      this.bgHeight = toShort(height);
      this.uBgInactive = toShort(uInactive);
      this.vBgInactive = toShort(vInactive);
      this.uBgActive = toShort(uActive);
      this.vBgActive = toShort(vActive);
      return this;
   }

   public GaugePropertyBuilder withSmooth(boolean bl) {
      this.smooth = bl;
      return this;
   }

   public GaugePropertyBuilder withTexture(ResourceLocation resourceLocation) {
      this.texture = resourceLocation;
      return this;
   }

   public GaugeProperties build() {
      return new GaugeProperties(
         this.uInner,
         this.vInner,
         this.innerWidth,
         this.innerHeight,
         this.hoverXOffset,
         this.hoverYOffset,
         this.hoverWidth,
         this.hoverHeight,
         this.bgXOffset,
         this.bgYOffset,
         this.bgWidth,
         this.bgHeight,
         this.uBgInactive,
         this.vBgInactive,
         this.uBgActive,
         this.vBgActive,
         this.vertical,
         this.reverse,
         this.smooth,
         this.texture,
         this.heatInfo
      );
   }

   private static short toShort(int n) {
      return (short)n;
   }

   public static GaugePropertyBuilder builder(GaugePropertyBuilder.GaugeOrientation orientation) {
      return new GaugePropertyBuilder(orientation);
   }

   public enum GaugeOrientation {
      Up(true, true),
      Down(true, false),
      Left(false, true),
      Right(false, false);

      final boolean vertical;
      final boolean reverse;

      GaugeOrientation(boolean bl, boolean bl2) {
         this.vertical = bl;
         this.reverse = bl2;
      }
   }
}
