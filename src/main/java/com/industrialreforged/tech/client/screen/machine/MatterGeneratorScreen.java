package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.utils.NumberUtils;
import com.industrialreforged.tech.blockentity.machine.MatterGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.MatterGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MatterGeneratorScreen extends IRScreenBase<MatterGeneratorMenu> {
   public String progressLabel = MsgUtils.translate("text.industrialreforged.tooltip.matter.progress");
   public String amplifierLabel = MsgUtils.translate("text.industrialreforged.tooltip.matter.amplifier");

   public MatterGeneratorScreen(MatterGeneratorMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         FluidGauge.asNormal(this.leftPos + 96, this.topPos + 22, ((MatterGeneratorBlockEntity)((MatterGeneratorMenu)this.menu).entity).tank)
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      super.renderLabels(graphics, mouseX, mouseY);
      graphics.drawString(this.font, this.progressLabel, 8, 22, 4210752, false);
      graphics.drawString(this.font, ((MatterGeneratorBlockEntity)((MatterGeneratorMenu)this.menu).entity).getProgressAsString(), 18, 31, 4210752, false);
      MatterGeneratorBlockEntity entity = (MatterGeneratorBlockEntity)((MatterGeneratorMenu)this.menu).entity;
      graphics.drawString(this.font, this.amplifierLabel, 28, 48, 4210752, false);
      graphics.drawString(this.font, NumberUtils.formatToSi(entity.amplifier) + " FE", 28, 58, 4210752, false);
   }

   public ResourceLocation getTexture() {
      return IR.texture("screen/matter_generator");
   }
}
