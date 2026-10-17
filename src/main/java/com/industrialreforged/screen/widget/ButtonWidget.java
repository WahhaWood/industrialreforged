package com.industrialreforged.screen.widget;

import com.industrialreforged.screen.ScreenApi;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ButtonWidget extends AbstractWidget {
   protected boolean locked;
   protected int textureX_default;
   protected int textureX_hover;
   protected int textureX_locked;
   protected int textureY_default;
   protected int textureY_hover;
   protected int textureY_locked;
   protected int textureWidth = 200;
   protected int textureHeight = 20;
   protected int spriteWidth = 200;
   protected int spriteHeight = 20;
   protected EnumMap<ButtonWidget.State, ResourceLocation> sprite = new EnumMap<>(ButtonWidget.State.class);
   protected Consumer<List<FormattedText>> tooltips;
   protected Runnable press;

   public static ButtonWidget create(int x, int y, int width, int height) {
      return create(x, y, width, height, null);
   }

   public static ButtonWidget create(int x, int y, int width, int height, Runnable press) {
      return new ButtonWidget(x, y, width, height, press);
   }

   protected ButtonWidget(int x, int y, int width, int height, Runnable press) {
      super(x, y, width, height, Component.empty());
      this.press = press;
      this.sprite.put(ButtonWidget.State.DEFAULT, ResourceLocation.withDefaultNamespace("widget/button"));
      this.sprite.put(ButtonWidget.State.LOCKED, ResourceLocation.withDefaultNamespace("widget/button_disabled"));
      this.sprite.put(ButtonWidget.State.HOVER, ResourceLocation.withDefaultNamespace("widget/button_highlighted"));
   }

   protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (this.sprite != null) {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         ButtonWidget.State state = this.isLocked() ? ButtonWidget.State.LOCKED : (this.isHovered() ? ButtonWidget.State.HOVER : ButtonWidget.State.DEFAULT);
         ScreenApi.quad(
            graphics,
            this.sprite.get(state),
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight(),
            this.getTextureX(state),
            this.getTextureY(state),
            this.getTextureWidth(),
            this.getTextureHeight(),
            this.getSpriteWidth(),
            this.getSpriteHeight()
         );
         if (this.isHovered() && this.tooltips != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) {
               List<FormattedText> components = new ArrayList<>();
               this.tooltips.accept(components);
               mc.screen.setTooltipForNextRenderPass(Language.getInstance().getVisualOrder(components));
            }
         }
      }
   }

   public ButtonWidget onHover(Consumer<List<FormattedText>> tooltips) {
      this.tooltips = tooltips;
      return this;
   }

   public int getTextureX(ButtonWidget.State state) {
      return switch (state) {
         case LOCKED -> this.textureX_locked;
         case DEFAULT -> this.textureX_default;
         case HOVER -> this.textureX_hover;
      };
   }

   public int getTextureY(ButtonWidget.State state) {
      return switch (state) {
         case LOCKED -> this.textureY_locked;
         case DEFAULT -> this.textureY_default;
         case HOVER -> this.textureY_hover;
      };
   }

   public ButtonWidget setLockedTexturePosition(int textureX, int textureY) {
      this.textureX_locked = textureX;
      this.textureY_locked = textureY;
      return this;
   }

   public ButtonWidget setHoveredTexturePosition(int textureX, int textureY) {
      this.textureX_hover = textureX;
      this.textureY_hover = textureY;
      return this;
   }

   public ButtonWidget setDefaultTexturePosition(int textureX, int textureY) {
      this.textureX_default = textureX;
      this.textureY_default = textureY;
      return this;
   }

   public ButtonWidget setAllTexturePosition(int textureX, int textureY) {
      this.textureX_hover = this.textureX_default = this.textureX_locked = textureX;
      this.textureY_hover = this.textureY_default = this.textureY_locked = textureY;
      return this;
   }

   public ButtonWidget setSprite(@Nullable ResourceLocation sprite, int spriteWidth, int spriteHeight) {
      for (ButtonWidget.State value : ButtonWidget.State.values()) {
         this.setSprite(value, sprite, spriteWidth, spriteHeight);
      }

      return this;
   }

   public ButtonWidget setSprite(ButtonWidget.State state, @Nullable ResourceLocation sprite, int spriteWidth, int spriteHeight) {
      this.sprite.put(state, sprite);
      this.spriteWidth = spriteWidth;
      this.spriteHeight = spriteHeight;
      return this;
   }

   public ButtonWidget setTextureSize(int width, int height) {
      this.textureWidth = width;
      this.textureHeight = height;
      return this;
   }

   public int getTextureWidth() {
      return this.textureWidth;
   }

   public int getTextureHeight() {
      return this.textureHeight;
   }

   public int getSpriteWidth() {
      return this.spriteWidth;
   }

   public int getSpriteHeight() {
      return this.spriteHeight;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public boolean mouseClicked(double mx, double my, int mb) {
      return !this.isLocked() && super.mouseClicked(mx, my, mb);
   }

   protected void updateWidgetNarration(@NotNull NarrationElementOutput elementOutput) {
   }

   public void onClick(double p_93634_, double p_93635_) {
      if (this.press != null) {
         this.press.run();
      }
   }

   public ButtonWidget setLocked(boolean isLocked) {
      this.locked = isLocked;
      return this;
   }

   public enum State {
      LOCKED,
      DEFAULT,
      HOVER;
   }
}
