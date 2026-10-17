package com.industrialreforged.render.avatar;

import com.industrialreforged.screen.ScreenApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class PlayerIcon implements IPlayerIcon {
   private final ResourceLocation location;

   public PlayerIcon(ResourceLocation location) {
      this.location = location;
   }

   @Override
   public void draw(GuiGraphics graphics, float x, float y, float sx, float sy, float alpha) {
      graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
      ScreenApi.quad(graphics, this.location, x, y, sx, sy);
   }
}
