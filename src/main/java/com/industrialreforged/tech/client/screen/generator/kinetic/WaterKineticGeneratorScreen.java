package com.industrialreforged.tech.client.screen.generator.kinetic;

import com.industrialreforged.tech.blockentity.generator.kinetic.WaterKineticGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.kinetic.WaterKineticGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WaterKineticGeneratorScreen extends IRScreenBase<WaterKineticGeneratorMenu> {
   public WaterKineticGeneratorScreen(WaterKineticGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   protected void renderLabels(GuiGraphics graphics, int mx, int my) {
      graphics.drawCenteredString(this.font, this.getStatus(), this.imageWidth / 2, 51, 2157374);
      if (((WaterKineticGeneratorBlockEntity)((WaterKineticGeneratorMenu)this.menu).entity).type == WaterKineticGeneratorBlockEntity.BiomeState.INVALID) {
         graphics.drawCenteredString(this.font, Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.wrong.1"), this.imageWidth / 2, 72, 2157374);
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/water_kinetic_generator");
   }

   private Component getStatus() {
      WaterKineticGeneratorBlockEntity entity = (WaterKineticGeneratorBlockEntity)((WaterKineticGeneratorMenu)this.menu).entity;
      if (entity.type == WaterKineticGeneratorBlockEntity.BiomeState.INVALID) {
         return Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.weak");
      } else if (entity.rotorSlot.isEmpty()) {
         return Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.miss");
      } else {
         return entity.checkSpace(entity.getRotorDiameter(), true) != 0
            ? Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.space")
            : Component.translatable("text.industrialreforged.ku.generation", new Object[]{entity.getPower()});
      }
   }
}
