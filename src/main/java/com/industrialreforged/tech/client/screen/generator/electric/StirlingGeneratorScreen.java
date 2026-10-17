package com.industrialreforged.tech.client.screen.generator.electric;

import com.industrialreforged.tech.blockentity.generator.electric.StirlingGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.electric.StirlingGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StirlingGeneratorScreen extends IRScreenBase<StirlingGeneratorMenu> {
   public StirlingGeneratorScreen(StirlingGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.create(
            this.leftPos + 60, this.topPos + 23, ((StirlingGeneratorBlockEntity)((StirlingGeneratorMenu)this.menu).entity).energy, EnergyGauge.KineticBar
         )
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      super.renderLabels(graphics, mouseX, mouseY);
      graphics.drawCenteredString(
         this.font,
         Component.translatable(
            "text.industrialreforged.fe.generation", new Object[]{((StirlingGeneratorBlockEntity)((StirlingGeneratorMenu)this.menu).entity).getProduction()}
         ),
         this.imageWidth / 2,
         52,
         46622
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/kinetic_generator");
   }
}
