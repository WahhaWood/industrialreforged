package com.industrialreforged.tech.client.screen.generator.heat;

import com.industrialreforged.tech.blockentity.generator.heat.SolidHeatGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.heat.SolidHeatGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolidHeatGeneratorScreen extends IRScreenBase<SolidHeatGeneratorMenu> {
   public SolidHeatGeneratorScreen(SolidHeatGeneratorMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         RatioGauge.asFuel(
            this.leftPos + 80,
            this.topPos + 28,
            () -> ((SolidHeatGeneratorBlockEntity)((SolidHeatGeneratorMenu)this.menu).entity).fuel == 0
               ? 0.0
               : (float)((SolidHeatGeneratorBlockEntity)((SolidHeatGeneratorMenu)this.menu).entity).fuel
                  / ((SolidHeatGeneratorBlockEntity)((SolidHeatGeneratorMenu)this.menu).entity).itemFuelTime
         )
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      graphics.drawCenteredString(
         this.font, ((SolidHeatGeneratorBlockEntity)((SolidHeatGeneratorMenu)this.menu).entity).getOutputRatio(), this.imageWidth / 2, 69, 5752026
      );
      if (this.isHovering(49, 66, 79, 13, mx, my)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.heat.transfer"));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/solid_heat_generator");
   }
}
