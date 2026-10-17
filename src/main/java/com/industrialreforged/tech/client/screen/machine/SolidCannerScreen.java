package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.SolidCannerBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.SolidCannerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolidCannerScreen extends IRScreenBase<SolidCannerMenu> {
   public SolidCannerScreen(SolidCannerMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 184;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 7, this.topPos + 45, ((SolidCannerBlockEntity)((SolidCannerMenu)this.menu).entity).energy));
      this.addRenderableOnly(
         RatioGauge.asArrow(this.leftPos + 89, this.topPos + 36, ((SolidCannerBlockEntity)((SolidCannerMenu)this.menu).entity)::getProgress)
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/solid_canner");
   }
}
