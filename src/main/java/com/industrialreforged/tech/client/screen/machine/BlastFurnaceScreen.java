package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.BlastFurnaceBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.BlastFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlastFurnaceScreen extends IRScreenBase<BlastFurnaceMenu> {
   public BlastFurnaceScreen(BlastFurnaceMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         RatioGauge.create(
               this.leftPos + 71, this.topPos + 71, ((BlastFurnaceBlockEntity)((BlastFurnaceMenu)this.menu).entity)::getGuiHeat, Gauge.HeatCentrifuge
            )
            .withTooltip(
               () -> Component.translatable(
                  "text.industrialreforged.heat", new Object[]{String.format("%.2f%%", ((BlastFurnaceBlockEntity)((BlastFurnaceMenu)this.menu).entity).getGuiHeat() * 100.0F)}
               )
            )
      );
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      BlastFurnaceBlockEntity entity = (BlastFurnaceBlockEntity)((BlastFurnaceMenu)this.menu).entity;
      if (entity.getGuiHeat() >= 1.0F) {
         ScreenApi.rect(graphics, this.leftPos + 95, this.topPos + 66, 176.0F, 8.0F, 14.0F, 14.0F);
      }

      if (entity.getGuiProgress() > 0.0F) {
         ScreenApi.rect(graphics, this.leftPos + 75, this.topPos + 34, 176.0F, 23.0F, 27.0F, 27.0F);
      }

      if (entity.isEmptyCell()) {
         ScreenApi.rect(graphics, this.leftPos + 116, this.topPos + 25, 176.0F, 79.0F, 22.0F, 22.0F);
         if (this.isHovering(117, 26, 20, 20, mouseX, mouseY)) {
            this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.blast.cell"));
         }
      }

      int i2 = (int)(entity.getGuiProgress() * 27.0F);
      if (i2 > 0) {
         ScreenApi.rect(graphics, this.leftPos + 75, this.topPos + 34 + 27 - i2, 176.0F, 51.0F, 27.0F, i2);
      }

      if (this.isHovering(75, 34, 27, 27, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(
            Component.translatable("text.industrialreforged.tooltip.matter.progress").append(String.format(" %.1f%%", entity.getGuiProgress() * 100.0F))
         );
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/blast_furnace");
   }
}
