package com.industrialreforged.render.avatar;

import net.minecraft.client.gui.GuiGraphics;

public interface IPlayerIcon {
   default void draw(GuiGraphics graphics, int x, int y, int sx, int sy, float alpha) {
      this.draw(graphics, (float)x, (float)y, (float)sx, (float)sy, alpha);
   }

   void draw(GuiGraphics var1, float var2, float var3, float var4, float var5, float var6);
}
