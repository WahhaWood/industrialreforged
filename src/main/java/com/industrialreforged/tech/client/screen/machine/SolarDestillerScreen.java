package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.SolarDestillerBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.SolarDestillerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolarDestillerScreen extends IRScreenBase<SolarDestillerMenu> {
   public SolarDestillerScreen(SolarDestillerMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 184;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         FluidGauge.asPlain(this.leftPos + 37, this.topPos + 43, 53, 18, ((SolarDestillerBlockEntity)((SolarDestillerMenu)this.menu).entity).inputTank)
      );
      this.addRenderableOnly(
         FluidGauge.asPlain(this.leftPos + 115, this.topPos + 55, 17, 43, ((SolarDestillerBlockEntity)((SolarDestillerMenu)this.menu).entity).outputTank)
      );
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      if (((SolarDestillerBlockEntity)((SolarDestillerMenu)this.menu).entity).canWork()) {
         ScreenApi.rect(graphics, this.leftPos + 36, this.topPos + 26, 0.0F, 184.0F, 97.0F, 29.0F);
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/solar_destiller");
   }
}
