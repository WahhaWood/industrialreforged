package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.LiquidHeatExchangerBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.LiquidHeatExchangerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LiquidHeatExchangerScreen extends IRScreenBase<LiquidHeatExchangerMenu> {
   public LiquidHeatExchangerScreen(LiquidHeatExchangerMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 204;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         FluidGauge.asPlain(
            this.leftPos + 19, this.topPos + 47, 12, 44, ((LiquidHeatExchangerBlockEntity)((LiquidHeatExchangerMenu)this.menu).entity).inputTank
         )
      );
      this.addRenderableOnly(
         FluidGauge.asPlain(
            this.leftPos + 145, this.topPos + 47, 12, 44, ((LiquidHeatExchangerBlockEntity)((LiquidHeatExchangerMenu)this.menu).entity).outputTank
         )
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      graphics.drawCenteredString(
         this.font, ((LiquidHeatExchangerBlockEntity)((LiquidHeatExchangerMenu)this.menu).entity).getOutputRatio(), this.imageWidth / 2, 31, 5752026
      );
      if (this.isHovering(20, 28, 138, 13, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.heat.transfer"));
      }

      if (this.isHovering(46, 50, 84, 16, mouseX, mouseY) || this.isHovering(46, 72, 84, 16, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.exchanger.vent"));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/heat_source_fluid");
   }
}
