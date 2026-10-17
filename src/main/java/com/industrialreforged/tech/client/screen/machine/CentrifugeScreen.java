package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.CentrifugeBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.CentrifugeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CentrifugeScreen extends IRScreenBase<CentrifugeMenu> {
   public CentrifugeScreen(CentrifugeMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 11, this.topPos + 41, ((CentrifugeBlockEntity)((CentrifugeMenu)this.menu).entity).energy));
      this.addRenderableOnly(
         RatioGauge.create(
            this.leftPos + 84, this.topPos + 25, ((CentrifugeBlockEntity)((CentrifugeMenu)this.menu).entity)::getProgress, Gauge.ProgressCentrifuge
         )
      );
      this.addRenderableOnly(
         RatioGauge.create(this.leftPos + 68, this.topPos + 67, ((CentrifugeBlockEntity)((CentrifugeMenu)this.menu).entity)::getHeatRatio, Gauge.HeatCentrifuge)
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/thermal_centrifuge");
   }
}
