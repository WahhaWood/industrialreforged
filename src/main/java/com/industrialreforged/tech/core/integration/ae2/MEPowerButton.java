package com.industrialreforged.tech.core.integration.ae2;

import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** On/off switch in the corner of an ME machine GUI; the label follows the machine's synced state. */
final class MEPowerButton extends Button {
   private final BooleanSupplier enabled;

   MEPowerButton(int x, int y, BooleanSupplier enabled, OnPress onPress) {
      super(x, y, 36, 14, Component.empty(), onPress, DEFAULT_NARRATION);
      this.enabled = enabled;
   }

   @Override
   public Component getMessage() {
      return Component.translatable(this.enabled.getAsBoolean() ? "gui.industrialreforged.me_matter.on" : "gui.industrialreforged.me_matter.off");
   }
}
