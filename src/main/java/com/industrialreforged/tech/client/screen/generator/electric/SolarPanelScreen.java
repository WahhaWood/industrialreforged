package com.industrialreforged.tech.client.screen.generator.electric;

import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.utils.NumberUtils;
import com.industrialreforged.tech.blockentity.generator.electric.SolarPanelBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.util.TooltipUtil;
import com.industrialreforged.tech.menu.generator.electric.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends IRScreenBase<SolarPanelMenu> {
   public SolarPanelScreen(SolarPanelMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = 194;
      this.imageHeight = 168;
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mx, int my, float pt) {
      super.renderBackground(graphics, mx, my, pt);
      if (((SolarPanelBlockEntity)((SolarPanelMenu)this.menu).entity).sunIsVisible) {
         ScreenApi.rect(
            graphics,
            this.leftPos + 24,
            this.topPos + 41,
            ((SolarPanelBlockEntity)((SolarPanelMenu)this.menu).entity).sunIsUp ? 195.0F : 210.0F,
            15.0F,
            14.0F,
            14.0F
         );
      }

      for (int i = 0; i < 4; i++) {
         ScreenApi.rect(graphics, this.leftPos + 16 + i * 18, this.topPos + 58, 233.0F, 12.0F, 18.0F, 18.0F);
      }

      // слоты модулей — правее слотов зарядки, рамка с «модульной» иконкой (233,30)
      for (int i = 0; i < 4; i++) {
         ScreenApi.rect(graphics, this.leftPos + 96 + i * 18, this.topPos + 58, 233.0F, 30.0F, 18.0F, 18.0F);
      }

      BigEnergyStorage storage = (BigEnergyStorage)((SolarPanelBlockEntity)((SolarPanelMenu)this.menu).entity).energy;
      int w = ScreenApi.energyBarWidth(24, storage);
      if (w > 0) {
         ScreenApi.rect(graphics, this.leftPos + 19, this.topPos + 26, 199.0F, 36.0F, w, 9.0F);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      graphics.drawCenteredString(
         this.font,
         Component.translatable(
            "text.industrialreforged.fe.generation", new Object[]{NumberUtils.formatToSi(((SolarPanelBlockEntity)((SolarPanelMenu)this.menu).entity).production)}
         ),
         20 + this.imageWidth / 2,
         27,
         16777215
      );
      if (this.isHovering(16, 22, 30, 15, mx, my)) {
         this.setTooltipForNextRenderPass(TooltipUtil.ratio(((SolarPanelBlockEntity)((SolarPanelMenu)this.menu).entity).energy));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/solar_generator");
   }
}
