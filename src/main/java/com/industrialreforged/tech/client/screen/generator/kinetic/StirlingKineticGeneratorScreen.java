package com.industrialreforged.tech.client.screen.generator.kinetic;

import com.industrialreforged.tech.blockentity.generator.kinetic.StirlingKineticGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.kinetic.StirlingKineticGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StirlingKineticGeneratorScreen extends IRScreenBase<StirlingKineticGeneratorMenu> {
   public StirlingKineticGeneratorScreen(StirlingKineticGeneratorMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 204;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         FluidGauge.asPlain(
            this.leftPos + 19, this.topPos + 47, 12, 44, ((StirlingKineticGeneratorBlockEntity)((StirlingKineticGeneratorMenu)this.menu).entity).inputTank
         )
      );
      this.addRenderableOnly(
         FluidGauge.asPlain(
            this.leftPos + 145, this.topPos + 47, 12, 44, ((StirlingKineticGeneratorBlockEntity)((StirlingKineticGeneratorMenu)this.menu).entity).outputTank
         )
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/stirling_kinetic_generator");
   }
}
