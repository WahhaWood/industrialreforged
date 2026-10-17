package com.industrialreforged.tech.client.screen.generator.heat;

import com.industrialreforged.tech.blockentity.generator.heat.ElectricHeatGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.heat.ElectricHeatGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectricHeatGeneratorScreen extends IRScreenBase<ElectricHeatGeneratorMenu> {
   public ElectricHeatGeneratorScreen(ElectricHeatGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.asBolt(this.leftPos + 8, this.topPos + 43, ((ElectricHeatGeneratorBlockEntity)((ElectricHeatGeneratorMenu)this.menu).entity).energy)
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      graphics.drawCenteredString(
         this.font, ((ElectricHeatGeneratorBlockEntity)((ElectricHeatGeneratorMenu)this.menu).entity).getOutputRatio(), this.imageWidth / 2, 69, 5752026
      );
      if (this.isHovering(34, 66, 109, 13, mx, my)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.heat.transfer"));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/electric_heat_generator");
   }
}
