package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.FluidBottlerBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.FluidBottlerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidBottlerScreen extends IRScreenBase<FluidBottlerMenu> {
   public FluidBottlerScreen(FluidBottlerMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 184;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 8, this.topPos + 34, ((FluidBottlerBlockEntity)((FluidBottlerMenu)this.menu).entity).energy));
      this.addRenderableOnly(FluidGauge.asNormal(this.leftPos + 78, this.topPos + 34, ((FluidBottlerBlockEntity)((FluidBottlerMenu)this.menu).entity).tank));
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      int n3 = Math.round(((FluidBottlerBlockEntity)((FluidBottlerMenu)this.menu).entity).getProgress() * 16.0F);
      if (n3 > 0) {
         ScreenApi.rect(graphics, this.leftPos + 61, this.topPos + 36, 198.0F, 0.0F, n3, 13.0F);
         ScreenApi.rect(graphics, this.leftPos + 61, this.topPos + 73, 198.0F, 0.0F, n3, 13.0F);
         ScreenApi.rect(graphics, this.leftPos + 99, this.topPos + 55, 198.0F, 0.0F, n3, 13.0F);
      }
   }

   public ResourceLocation getTexture() {
      return IR.texture("screen/fluid_bottler");
   }
}
