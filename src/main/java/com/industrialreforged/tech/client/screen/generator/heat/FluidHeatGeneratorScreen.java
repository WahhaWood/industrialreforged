package com.industrialreforged.tech.client.screen.generator.heat;

import com.industrialreforged.tech.blockentity.generator.heat.FluidHeatGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.heat.FluidHeatGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidHeatGeneratorScreen extends IRScreenBase<FluidHeatGeneratorMenu> {
   public FluidHeatGeneratorScreen(FluidHeatGeneratorMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         FluidGauge.asNormal(this.leftPos + 70, this.topPos + 20, ((FluidHeatGeneratorBlockEntity)((FluidHeatGeneratorMenu)this.menu).entity).tank)
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      graphics.drawString(
         this.font,
         Component.translatable(
            "text.industrialreforged.tooltip.heat.exit", new Object[]{((FluidHeatGeneratorBlockEntity)((FluidHeatGeneratorMenu)this.menu).entity).getHeat()}
         ),
         96,
         33,
         5752026
      );
      graphics.drawString(
         this.font,
         Component.translatable(
            "text.industrialreforged.tooltip.heat.exit.max",
            new Object[]{((FluidHeatGeneratorBlockEntity)((FluidHeatGeneratorMenu)this.menu).entity).getMaxHeatEmittedPerTick()}
         ),
         96,
         52,
         5752026
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/fluid_heat_generator");
   }
}
