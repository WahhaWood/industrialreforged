package com.industrialreforged.tech.client.screen.generator.kinetic;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.generator.kinetic.SteamKineticGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.kinetic.SteamKineticGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SteamKineticGeneratorScreen extends IRScreenBase<SteamKineticGeneratorMenu> {
   public SteamKineticGeneratorScreen(SteamKineticGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(FluidGauge.asPlain(75, 21, 26, 26, ((SteamKineticGeneratorBlockEntity)((SteamKineticGeneratorMenu)this.menu).entity).waterTank));
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mx, int my, float pt) {
      SteamKineticGeneratorBlockEntity entity = (SteamKineticGeneratorBlockEntity)((SteamKineticGeneratorMenu)this.menu).entity;
      if (entity.hasTurbine()) {
         if (entity.isVentingSteam()) {
            ScreenApi.rect(graphics, 36.0F, 20.0F, 176.0F, 0.0F, 30.0F, 26.0F);
            if (this.isHovering(37, 21, 28, 24, mx, my)) {
               this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.heat.turbine.venting"));
            }
         }

         if (entity.isThrottled()) {
            ScreenApi.rect(graphics, 110.0F, 20.0F, 176.0F, 0.0F, 30.0F, 26.0F);
            if (this.isHovering(111, 21, 28, 24, mx, my)) {
               this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.heat.turbine.condensatio"));
            }
         }
      }

      super.renderBackground(graphics, mx, my, pt);
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      SteamKineticGeneratorBlockEntity entity = (SteamKineticGeneratorBlockEntity)((SteamKineticGeneratorMenu)this.menu).entity;
      graphics.drawCenteredString(
         this.font,
         Component.translatable(
            !entity.hasTurbine()
               ? "text.industrialreforged.tooltip.heat.turbine.miss"
               : (
                  entity.isTurbineBlockedByWater()
                     ? "text.industrialreforged.tooltip.heat.turbine.filled"
                     : (ActiveBlock.isActive(entity) ? "text.industrialreforged.tooltip.heat.turbine.active" : "text.industrialreforged.tooltip.heat.turbine.waiting")
               )
         ),
         this.imageWidth / 2,
         54,
         entity.hasTurbine() && !entity.isTurbineBlockedByWater() ? 2157374 : 14946604
      );
      if (entity.hasTurbine() && !entity.isTurbineBlockedByWater()) {
         graphics.drawCenteredString(
            this.font, Component.translatable("text.industrialreforged.ku.generation", new Object[]{entity.getPower()}), this.imageWidth / 2, 71, 2157374
         );
      }
   }

   protected void renderTooltip(GuiGraphics graphics, int x, int y) {
      if (((SteamKineticGeneratorMenu)this.menu).getCarried().isEmpty()
         && this.hoveredSlot != null
         && this.hoveredSlot.container == ((SteamKineticGeneratorBlockEntity)((SteamKineticGeneratorMenu)this.menu).entity).turbineSlot
         && !this.hoveredSlot.hasItem()) {
         graphics.renderTooltip(this.font, Component.translatable("text.industrialreforged.tooltip.heat.turbine.slot"), x, y);
      } else {
         super.renderTooltip(graphics, x, y);
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/steam_kinetic_generator");
   }
}
