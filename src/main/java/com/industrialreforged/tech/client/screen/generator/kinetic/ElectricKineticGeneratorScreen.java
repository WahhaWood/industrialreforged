package com.industrialreforged.tech.client.screen.generator.kinetic;

import com.industrialreforged.tech.blockentity.generator.kinetic.ElectricKineticGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.kinetic.ElectricKineticGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectricKineticGeneratorScreen extends IRScreenBase<ElectricKineticGeneratorMenu> {
   public ElectricKineticGeneratorScreen(ElectricKineticGeneratorMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.asBolt(this.leftPos + 8, this.topPos + 43, ((ElectricKineticGeneratorBlockEntity)((ElectricKineticGeneratorMenu)this.menu).entity).energy)
      );
   }

   protected void renderTooltip(GuiGraphics graphics, int x, int y) {
      if (((ElectricKineticGeneratorMenu)this.menu).getCarried().isEmpty()
         && this.hoveredSlot != null
         && this.hoveredSlot.container == ((ElectricKineticGeneratorBlockEntity)((ElectricKineticGeneratorMenu)this.menu).entity).motors
         && !this.hoveredSlot.hasItem()) {
         graphics.renderTooltip(this.font, Component.translatable("text.industrialreforged.tooltip.kinetic.motors"), x, y);
      } else {
         super.renderTooltip(graphics, x, y);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      this.renderTitle(graphics);
      graphics.drawCenteredString(
         this.font,
         Component.translatable(
            "text.industrialreforged.ku.ratio",
            new Object[]{
               ((ElectricKineticGeneratorBlockEntity)((ElectricKineticGeneratorMenu)this.menu).entity).getPower(),
               ((ElectricKineticGeneratorBlockEntity)((ElectricKineticGeneratorMenu)this.menu).entity).getMaxPower()
            }
         ),
         this.imageWidth / 2,
         69,
         5752026
      );
      if (this.isHovering(29, 66, 119, 13, mx, my)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.kinetic.transfer"));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/electric_kinetic_generator");
   }
}
