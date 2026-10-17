package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.CropmatronBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.CropmatronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CropmatronScreen extends IRScreenBase<CropmatronMenu> {
   public CropmatronScreen(CropmatronMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 192;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 152, this.topPos + 81, ((CropmatronBlockEntity)((CropmatronMenu)this.menu).entity).energy));
      this.addRenderableOnly(
         FluidGauge.asPlain(this.leftPos + 64, this.topPos + 76, 47, 24, ((CropmatronBlockEntity)((CropmatronMenu)this.menu).entity).waterTank)
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   public ResourceLocation getTexture() {
      return IR.texture("screen/cropmatron");
   }
}
