package com.industrialreforged.tech.client.screen.widgets;

import com.industrialreforged.screen.ScreenApi;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CustomButton extends Button<CustomButton> {
   private final ResourceLocation texture;
   private int tx;
   private int ty;
   private final int tw;
   private final int th;

   public CustomButton(int x, int y, int w, int h, @Nullable IntConsumer clickHandler) {
      this(x, y, w, h, 0, 0, null, clickHandler);
   }

   public CustomButton(int x, int y, int w, int h, int tx, int ty, ResourceLocation texture, @Nullable IntConsumer clickHandler) {
      this(x, y, w, h, tx, ty, w, h, texture, clickHandler);
   }

   public CustomButton(int x, int y, int w, int h, int tx, int ty, int tw, int th, ResourceLocation texture, @Nullable IntConsumer clickHandler) {
      super(x, y, w, h, clickHandler);
      this.texture = texture;
      this.tx = tx;
      this.ty = ty;
      this.tw = tw;
      this.th = th;
   }

   public void setTexturePos(int tx, int ty) {
      this.tx = tx;
      this.ty = ty;
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (this.texture != null) {
         ScreenApi.quad(graphics, this.texture, this.getX(), this.getY(), this.width, this.height, this.tx, this.ty, this.tw, this.th, 256, 256);
      }

      if (this.isMouseOver(mouseX, mouseY)) {
         ScreenApi.fill(graphics, this.getX(), this.getY(), this.width, this.height, -2130706433);
      }

      super.renderWidget(graphics, mouseX, mouseY, partialTick);
   }
}
