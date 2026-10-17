package com.industrialreforged.tech.client.screen.widgets;

import com.industrialreforged.screen.ScreenApi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class IconButton extends Button<IconButton> {
   private final ItemStack icon;

   public IconButton(int x, int y, int w, int h, ItemLike like) {
      super(x, y, w, h, null);
      this.icon = new ItemStack(like);
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (!this.icon.isEmpty()) {
         ScreenApi.renderItem(graphics, this.icon, this.getX() + 1, this.getY() + 1, 1.0F);
      }
   }
}
