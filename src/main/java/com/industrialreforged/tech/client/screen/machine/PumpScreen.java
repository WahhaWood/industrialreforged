package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.PumpBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.PumpMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PumpScreen extends IRScreenBase<PumpMenu> {
   public PumpScreen(PumpMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 7, this.topPos + 27, ((PumpBlockEntity)((PumpMenu)this.menu).entity).energy));
      this.addRenderableOnly(FluidGauge.asNormal(this.leftPos + 70, this.topPos + 16, ((PumpBlockEntity)((PumpMenu)this.menu).entity).tank));
      this.addRenderableOnly(
         RatioGauge.create(this.leftPos + 36, this.topPos + 35, () -> ((PumpBlockEntity)((PumpMenu)this.menu).entity).guiProgress, Gauge.ProgressDrop)
      );
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/pump");
   }
}
