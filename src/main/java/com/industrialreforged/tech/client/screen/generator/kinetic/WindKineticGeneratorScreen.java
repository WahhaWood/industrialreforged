package com.industrialreforged.tech.client.screen.generator.kinetic;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.generator.kinetic.WindKineticGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.kinetic.WindKineticGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WindKineticGeneratorScreen extends IRScreenBase<WindKineticGeneratorMenu> {
   public WindKineticGeneratorScreen(WindKineticGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mx, int my, float pt) {
      if (((WindKineticGeneratorBlockEntity)((WindKineticGeneratorMenu)this.menu).entity).isRotorOverloaded()) {
         ScreenApi.rect(graphics, 44.0F, 20.0F, 176.0F, 0.0F, 30.0F, 26.0F);
         ScreenApi.rect(graphics, 102.0F, 20.0F, 176.0F, 0.0F, 30.0F, 26.0F);
         if (this.isHovering(45, 21, 28, 24, mx, my) || this.isHovering(103, 21, 28, 24, mx, my)) {
            this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.overload"));
         }
      }

      super.renderBackground(graphics, mx, my, pt);
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      graphics.drawCenteredString(this.font, this.getStatus(), this.imageWidth / 2, 51, 2157374);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/wind_kinetic_generator");
   }

   private Component getStatus() {
      WindKineticGeneratorBlockEntity entity = (WindKineticGeneratorBlockEntity)((WindKineticGeneratorMenu)this.menu).entity;
      if (!entity.hasRotor()) {
         return Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.miss");
      }

      int diameter = entity.getRotorDiameter();
      int obstructedCrossSection = entity.checkSpace(diameter * 3, false);
      if (obstructedCrossSection > 0 && obstructedCrossSection <= (diameter + 1) / 2) {
         obstructedCrossSection = 0;
      }

      if (obstructedCrossSection < 0) {
         return Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.space");
      } else {
         return !entity.isWindStrongEnough()
            ? Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.weak")
            : Component.translatable("text.industrialreforged.ku.generation", new Object[]{entity.getPower()});
      }
   }
}
