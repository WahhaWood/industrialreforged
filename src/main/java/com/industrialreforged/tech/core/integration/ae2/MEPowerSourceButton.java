package com.industrialreforged.tech.core.integration.ae2;

import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

/** Energy source switch of an ME machine GUI: cables only ("FE") or cables plus the network ("AE"). */
final class MEPowerSourceButton extends Button {
   private final BooleanSupplier cableOnly;

   MEPowerSourceButton(int x, int y, BooleanSupplier cableOnly, OnPress onPress) {
      super(x, y, 24, 14, Component.empty(), onPress, DEFAULT_NARRATION);
      this.cableOnly = cableOnly;
   }

   @Override
   public Component getMessage() {
      return Component.translatable(this.cableOnly.getAsBoolean() ? "gui.industrialreforged.me_power.cable" : "gui.industrialreforged.me_power.network");
   }

   @Override
   public void renderWidget(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.setTooltip(Tooltip.create(Component.translatable(this.cableOnly.getAsBoolean() ? "gui.industrialreforged.me_power.cable.tip" : "gui.industrialreforged.me_power.network.tip")));
      super.renderWidget(graphics, mouseX, mouseY, partialTick);
   }
}
