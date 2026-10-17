package com.industrialreforged.tech.client.screen.widgets;

import com.industrialreforged.tech.client.screen.widgets.builder.GaugeProperties;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class RatioGauge extends Gauge {
   private final DoubleSupplier provider;
   private Supplier<Component> supplierTooltip;
   private float lastValue = -1.0F;

   public static RatioGauge asFuel(int x, int y, DoubleSupplier provider) {
      return new RatioGauge(x, y, provider, Gauge.Fuel);
   }

   public static RatioGauge asArrow(int x, int y, DoubleSupplier provider) {
      return new RatioGauge(x, y, provider, Gauge.ProgressArrow);
   }

   public static RatioGauge create(int x, int y, DoubleSupplier provider, GaugeProperties properties) {
      return new RatioGauge(x, y, provider, properties);
   }

   public RatioGauge(int x, int y, DoubleSupplier provider, GaugeProperties properties) {
      super(x, y, properties);
      this.provider = provider;
   }

   public RatioGauge withTooltip(Supplier<Component> supplier) {
      this.supplierTooltip = supplier;
      return this;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderWidget(graphics, mouseX, mouseY, partialTick);
      if (this.supplierTooltip != null && this.isHovered() && this.isChange()) {
         this.setTooltip(Tooltip.create(this.supplierTooltip.get()));
      }
   }

   @Override
   protected float getRatio() {
      return (float)this.provider.getAsDouble();
   }

   private boolean isChange() {
      float value = this.getRatio();
      if (this.lastValue != value) {
         this.lastValue = value;
         return true;
      } else {
         return false;
      }
   }
}
