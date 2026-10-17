package com.industrialreforged.render.avatar;

import com.industrialreforged.screen.ScreenApi;
import java.util.UUID;
import net.minecraft.client.gui.GuiGraphics;

public class PlayerIconSkin implements IPlayerIcon {
   private final String username;
   private final UUID uuid;

   public PlayerIconSkin(String username, UUID uuid) {
      this.username = username;
      this.uuid = uuid;
   }

   @Override
   public void draw(GuiGraphics graphics, float x, float y, float sx, float sy, float alpha) {
      graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
      ScreenApi.drawFace(graphics, x, y, sx, sy, this.uuid, this.username);
   }
}
