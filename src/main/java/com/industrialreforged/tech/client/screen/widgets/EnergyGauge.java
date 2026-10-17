package com.industrialreforged.tech.client.screen.widgets;

import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.tech.client.screen.widgets.builder.GaugeProperties;
import com.industrialreforged.tech.client.screen.widgets.builder.GaugePropertyBuilder;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.util.TooltipUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyGauge extends Gauge {
   private final IEnergyStorage storage;
   private long lastStorage = -1L;
   private long lastCapacity;
   public static final GaugeProperties Bar = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(132, 43)
      .size(24, 9)
      .withBackground(-4, -11, 32, 32, 128, 0)
      .build();
   public static final GaugeProperties Bolt = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Up)
      .uv(112, 64)
      .size(16, 16)
      .withBackground(0, 0, 16, 16, 96, 64)
      .build();
   public static final GaugeProperties KineticBar = GaugePropertyBuilder.builder(GaugePropertyBuilder.GaugeOrientation.Right)
      .uv(180, 18)
      .size(51, 6)
      .withTexture(IR.texture("screen/kinetic_generator"))
      .withBackground(-4, -4, 58, 14, 176, 0)
      .build();

   public static EnergyGauge asBar(int x, int y, IEnergyStorage storage) {
      return create(x, y, storage, Bar);
   }

   public static EnergyGauge asBolt(int x, int y, IEnergyStorage storage) {
      return create(x, y, storage, Bolt);
   }

   public static EnergyGauge create(int x, int y, IEnergyStorage storage, GaugeProperties properties) {
      return new EnergyGauge(x, y, storage, properties);
   }

   private EnergyGauge(int x, int y, IEnergyStorage storage, GaugeProperties properties) {
      super(x, y, properties);
      this.storage = storage;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderWidget(graphics, mouseX, mouseY, partialTick);
      if (this.active && this.isHovered() && this.isChange()) {
         this.setTooltip(Tooltip.create(TooltipUtil.ratio(this.storage)));
      }
   }

   @Override
   protected float getRatio() {
      return this.storage instanceof BigEnergyStorage s
         ? (float)s.getStored() / (float)s.getCapacity()
         : (float)this.storage.getEnergyStored() / this.storage.getMaxEnergyStored();
   }

   private boolean isChange() {
      long storage;
      long capacity;
      if (this.storage instanceof BigEnergyStorage s) {
         storage = s.getStored();
         capacity = s.getCapacity();
      } else {
         storage = this.storage.getEnergyStored();
         capacity = this.storage.getMaxEnergyStored();
      }

      if (this.lastStorage == storage && this.lastCapacity == capacity) {
         return false;
      }

      this.lastStorage = storage;
      this.lastCapacity = capacity;
      return true;
   }
}
