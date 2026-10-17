package com.industrialreforged.render.avatar;

import com.industrialreforged.screen.ScreenApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.DefaultPlayerSkin;

public class PlayerIconDefault implements IPlayerIcon {
   @Override
   public void draw(GuiGraphics graphics, float x, float y, float sx, float sy, float alpha) {
      graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
      ScreenApi.drawFace(graphics, x, y, sx, sy, 0.125F, DefaultPlayerSkin.getDefaultTexture());
   }
}
