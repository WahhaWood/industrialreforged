package com.industrialreforged.tech.client.screen.widgets;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.client.screen.widgets.builder.GaugeProperties;
import com.industrialreforged.tech.client.screen.widgets.builder.GaugePropertyBuilder;
import com.industrialreforged.tech.core.IR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class Gauge extends AbstractWidget {
   protected final GaugeProperties properties;
   public static final GaugeProperties Fuel = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Up)
      .uv(112, 80)
      .size(13, 13)
      .withHover(0, 0, 14, 14)
      .withBackground(0, 0, 16, 16, 96, 80)
      .build();
   public static final GaugeProperties ProgressArrow = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(165, 16)
      .size(22, 15)
      .withBackground(165, 0)
      .build();
   public static final GaugeProperties ProgressCrush = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(165, 49)
      .size(21, 15)
      .withBackground(165, 32)
      .build();
   public static final GaugeProperties ProgressTriangle = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(165, 80)
      .size(22, 15)
      .withBackground(165, 64)
      .build();
   public static final GaugeProperties ProgressDrop = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(165, 112)
      .size(22, 15)
      .withBackground(165, 96)
      .build();
   public static final GaugeProperties ProgressMetalFormer = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(200, 19)
      .size(46, 9)
      .withBackground(-8, -3, 64, 16, 192, 0)
      .build();
   public static final GaugeProperties ProgressCentrifuge = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Up)
      .uv(252, 33)
      .size(3, 28)
      .withBackground(-1, -1, 5, 30, 246, 32)
      .build();
   public static final GaugeProperties HeatCentrifuge = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(225, 54)
      .size(20, 4)
      .setHover()
      .withBackground(-1, -1, 22, 6, 224, 47)
      .build();
   public static final GaugeProperties HeatNuclearReactor = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(0, 243)
      .size(100, 13)
      .withHoverBorder(1)
      .withTexture(IR.texture("screen/nuclear_reactor"))
      .build();
   public static final GaugeProperties ProgressCondenser = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(1, 185)
      .size(82, 7)
      .withHoverBorder(1)
      .withTexture(IR.texture("screen/condenser"))
      .build();
   public static final GaugeProperties HeatFermenter = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(177, 10)
      .size(40, 3)
      .withHoverBorder(1)
      .withTexture(IR.texture("screen/fermenter"))
      .build();
   public static final GaugeProperties ProgressFermenter = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(177, 1)
      .size(40, 7)
      .withHoverBorder(1)
      .withTexture(IR.texture("screen/fermenter"))
      .build();
   public static final GaugeProperties ProgressOreWasher = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(177, 118)
      .size(18, 18)
      .withTexture(IR.texture("screen/ore_washing_plant"))
      .withBackground(-1, -1, 20, 19, 102, 38)
      .build();
   public static final GaugeProperties ProgressBlockCutter = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(192, 121)
      .size(46, 19)
      .withBackground(192, 102)
      .build();

   protected Gauge(int x, int y, GaugeProperties props) {
      super(x + props.hoverXOffset, y + props.hoverYOffset, props.hoverWidth, props.hoverHeight, Component.empty());
      this.properties = props;
   }

   protected abstract float getRatio();

   protected boolean isActive(float d) {
      return d > 0.0;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      float ratio = this.getRatio();
      if (!(ratio <= 0.0) || this.properties.bgWidth > 0) {
         ScreenApi.setShaderTexture(this.properties.texture);
         float x = this.getX() - this.properties.hoverXOffset;
         float y = this.getY() - this.properties.hoverYOffset;
         boolean isMouseOver = mouseX >= x && mouseX <= x + this.properties.bgWidth && mouseY >= y && mouseY <= y + this.properties.bgHeight;
         if (this.properties.bgWidth >= 0) {
            boolean bl = this.isActive(ratio);
            ScreenApi.rect(
               graphics,
               x + this.properties.bgXOffset,
 y + this.properties.bgYOffset,
               bl ? this.properties.uBgActive : this.properties.uBgInactive,
               bl ? this.properties.vBgActive : this.properties.vBgInactive,
               this.properties.bgWidth,
               this.properties.bgHeight
            );
            if (ratio <= 0.0) {
               return;
            }
         }

         ratio = Math.min(ratio, 1.0F);
         float u = this.properties.uInner;
         float v = this.properties.vInner;
         float w = this.properties.innerWidth;
         float h = this.properties.innerHeight;
         float d8 = this.properties.vertical ? h : w;
         float d9 = ratio * d8;
         if (!this.properties.smooth) {
            d9 = Math.round(d9);
         }

         if (!(d9 <= 0.0)) {
            if (this.properties.vertical) {
               if (this.properties.reverse) {
                  v += h - d9;
 y += h - d9;
               }

 h = d9;
            } else {
               if (this.properties.reverse) {
 u += w - d9;
                  x += w - d9;
               }

 w = d9;
            }

            ScreenApi.rect(graphics, x, y, u, v, w, h);
            if (this.properties.heatInfo && isMouseOver) {
               // Раньше здесь стояло Math.round(w * 100) и жёсткое " / 2000". Но w к
        // этому моменту уже перезаписан на ширину заливки В ПИКСЕЛЯХ, так что
      // "процент" умножался на ширину шкалы, а 2000 не соответствовало ни одной
      // машине (у доменной печи потолок 50000). Шкала — переиспользуемый виджет и
      // реального максимума не знает, поэтому честный ответ здесь один: процент.
               graphics.renderTooltip(
                  Minecraft.getInstance().font, Component.translatable("text.industrialreforged.heat", new Object[]{Math.round(ratio * 100.0F) + "%"}), (int)x, (int)y
               );
            }
         }
      }
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }
}
