package com.industrialreforged.tech.client.screen.generator.heat;

import com.industrialreforged.tech.blockentity.generator.heat.RTHeatGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.heat.RTHeatGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RTHeatGeneratorScreen extends IRScreenBase<RTHeatGeneratorMenu> {
   public RTHeatGeneratorScreen(RTHeatGeneratorMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      graphics.drawCenteredString(
         this.font, ((RTHeatGeneratorBlockEntity)((RTHeatGeneratorMenu)this.menu).entity).getOutputRatio(), this.imageWidth / 2, 69, 5752026
      );
      if (this.isHovering(49, 66, 79, 13, mx, my)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.heat.transfer"));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/rt_heat_generator");
   }
}
