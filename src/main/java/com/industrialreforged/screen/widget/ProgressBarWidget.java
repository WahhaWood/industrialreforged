package com.industrialreforged.screen.widget;

import com.industrialreforged.screen.ScreenApi;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ProgressBarWidget extends AbstractWidget implements NarratableEntry {
   protected ResourceLocation texture;
   protected ProgressBarWidget.BarDirection direction = ProgressBarWidget.BarDirection.LEFT_TO_RIGHT;
   protected DoubleSupplier progressProvider;
   protected int textureX;
   protected int textureY;
   protected int textureWidth;
   protected int textureHeight;
   protected int spriteWidth = 256;
   protected int spriteHeight = 256;
   protected Consumer<List<FormattedText>> tooltips;

   public static ProgressBarWidget create(int x, int y, int width, int height) {
      return new ProgressBarWidget(x, y, width, height);
   }

   protected ProgressBarWidget(int x, int y, int width, int height) {
      super(x, y, width, height, Component.empty());
      this.textureWidth = width;
      this.textureHeight = height;
   }

   protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      float var10000 = this.getProgress();
      float __ = 1.0F;
      float min = 0.0F;
      float value = var10000;
      float clamped = value < min ? min : (value > __ ? __ : value);
      float completion = clamped;
      int direction = this.direction.ordinal();
      if (direction > 3) {
         completion = 1.0F - completion;
         direction %= 4;
      }

      int x = this.getX();
      int y = this.getY();
      int width = (int)(completion * this.width);
      int height = (int)(completion * this.height);
      ScreenApi.setShaderTexture(this.texture);
      switch (direction) {
         case 0:
            ScreenApi.rect(graphics, x, y, this.textureX, this.textureY, width, this.height, this.spriteWidth, this.spriteHeight);
            break;
         case 1:
            ScreenApi.rect(graphics, x, y, this.textureX, this.textureY, this.width, height, this.spriteWidth, this.spriteHeight);
            break;
         case 2:
            ScreenApi.rect(
               graphics,
               x + this.width - width,
 y,
               this.textureX + this.textureWidth - (int)(completion * this.textureWidth),
               this.textureY,
               width,
               this.height,
               this.spriteWidth,
               this.spriteHeight
            );
            break;
         case 3:
            ScreenApi.rect(
               graphics,
               x,
 y + this.height - height,
               this.textureX,
               this.textureY + this.textureHeight - (int)(completion * this.textureHeight),
               this.width,
               height,
               this.spriteWidth,
               this.spriteHeight
            );
      }

      if (this.isHovered() && this.tooltips != null) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.screen != null) {
            List<FormattedText> components = new ArrayList<>();
            this.tooltips.accept(components);
            mc.screen.setTooltipForNextRenderPass(Language.getInstance().getVisualOrder(components));
         }
      }
   }

   protected void updateWidgetNarration(@NotNull NarrationElementOutput elementOutput) {
   }

   public ProgressBarWidget onHover(Consumer<List<FormattedText>> tooltips) {
      this.tooltips = tooltips;
      return this;
   }

   public ProgressBarWidget setBarTexture(ResourceLocation texture) {
      if (texture == null) {
         throw new NullPointerException("texture cant be null");
      }

      this.texture = texture;
      return this;
   }

   public ProgressBarWidget setProgressProvider(DoubleSupplier progressProvider) {
      if (progressProvider == null) {
         throw new NullPointerException("progressProvider cant be null");
      }

      this.progressProvider = progressProvider;
      return this;
   }

   public float getProgress() {
      return (float)this.progressProvider.getAsDouble();
   }

   public ProgressBarWidget setTextureBarPos(int x, int y) {
      if (x >= 0 && y >= 0) {
         this.textureX = x;
         this.textureY = y;
         return this;
      } else {
         throw new IllegalArgumentException("Texture x, y cant be negative");
      }
   }

   public ProgressBarWidget setTextureBarSize(int width, int height) {
      if (width >= 0 && height >= 0) {
         this.textureWidth = width;
         this.textureHeight = height;
         return this;
      } else {
         throw new IllegalArgumentException("Texture width, height cant be negative");
      }
   }

   public ProgressBarWidget setSpriteSize(int width, int height) {
      if (width >= 0 && height >= 0) {
         this.spriteWidth = width;
         this.spriteHeight = height;
         return this;
      } else {
         throw new IllegalArgumentException("Sprite width, height cant be negative");
      }
   }

   public ProgressBarWidget setDirection(ProgressBarWidget.BarDirection direction) {
      if (direction == null) {
         throw new NullPointerException("Progressbar direction cant be null");
      }

      this.direction = direction;
      return this;
   }

   public enum BarDirection {
      LEFT_TO_RIGHT,
      UP_TO_DOWN,
      RIGHT_TO_LEFT,
      DOWN_TO_UP,
      RIGHT_TO_LEFT_INVERSE,
      DOWN_TO_UP_INVERSE,
      LEFT_TO_RIGHT_INVERSE,
      UP_TO_DOWN_INVERSE;
   }
}
