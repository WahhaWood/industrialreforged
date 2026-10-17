package com.industrialreforged.screen.widget;

import com.industrialreforged.font.FontContainer;
import com.industrialreforged.font.FontType;
import com.industrialreforged.font.StringCache;
import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.screen.ScreenScale;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class FontEditBox extends EditBox {
   protected float x;
   protected float y;
   protected float w;
   protected float h;
   private boolean customFont = false;
   private FontType fontType = FontType.BoldItalicText;
   private float fontSize = 1.3F;
   private BiFunction<String, Integer, String> formatter = (s, pos) -> s;
   float alpha;

   public FontEditBox(@Nullable Component hint) {
      super(Minecraft.getInstance().font, 0, 0, 0, 0, Component.empty());
      this.setHint(hint);
   }

   public void init(float x, float y, float w, float h) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      if (!this.customFont) {
         this.fontSize = 2.0F * h / 28.277779F;
      }
   }

   public FontType getFontType() {
      return this.fontType;
   }

   public float getFontSize() {
      return this.fontSize;
   }

   public void setFont(FontType font, float fontSize) {
      this.fontType = font;
      this.fontSize = fontSize;
      this.customFont = true;
   }

   public void setFormatterLite(BiFunction<String, Integer, String> function) {
      this.formatter = function;
   }

   public boolean mouseClicked(double mx, double my, int button) {
      if (this.active && this.visible && this.isValidClickButton(button)) {
         boolean flag = this.clicked(mx, my);
         if (flag) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            if (button == 0) {
               this.onClick(mx, my);
            } else if (button == 1) {
               this.setValue("");
            }

            return true;
         }
      }

      return false;
   }

   public void onClick(double mx, double my) {
      float fontSize = ScreenScale.get(this.fontSize);
      StringCache stringCache = this.fontType.getFontContainer().getTextFont();
      String s = stringCache.trimStringToWidth(this.getValue().substring(this.displayPos), this.w / fontSize, false);
      this.moveCursorTo(stringCache.trimStringToWidth(s, ((float)mx - this.x) / fontSize, false).length() + this.displayPos, Screen.hasShiftDown());
   }

   protected boolean clicked(double mx, double my) {
      return this.active && this.visible && mx >= this.x && my >= this.y && mx <= this.x + this.w && my <= this.y + this.h;
   }

   public boolean isMouseOver(double mx, double my) {
      return this.clicked(mx, my);
   }

   protected boolean isValidClickButton(int button) {
      return button == 0 || button == 1;
   }

   public void renderWidget(@NotNull GuiGraphics graphics, int mx, int my, float pt) {
      this.update(mx, my);
      float x = this.x - ScreenScale.get(20.0F);
      float y = this.y;
      ScreenApi.fillRound(graphics, x, y, this.w, this.h, ScreenScale.get(10.0F), ARGB32.lerp(this.alpha, -14737858, -13553822));
      ScreenApi.fillBorderRound(graphics, x, y, this.w, this.h, ScreenScale.get(10.0F), ScreenScale.get(4.0F), ARGB32.lerp(this.alpha, -13553822, -16719617));
      this.renderText(graphics);
   }

   public void renderText(@NotNull GuiGraphics graphics) {
      FontContainer font = this.fontType.getFontContainer();
      float fontSize = ScreenScale.get(this.fontSize);
      int j = this.cursorPos - this.displayPos;
      int k = this.highlightPos - this.displayPos;
      String text = this.getValue();
      String s = font.getTextFont().trimStringToWidth(text.substring(this.displayPos), this.w / fontSize, false);
      boolean flag = j >= 0 && j <= s.length();
      boolean flag1 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 6L % 2L == 0L && flag;
      float l = this.x;
      float i1 = this.y + (this.h - font.height() * fontSize / 2.0F) / 2.0F;
      float j1 = l;
      if (k > s.length()) {
 k = s.length();
      }

      if (s.length() > 0) {
         String s1 = flag ? s.substring(0, j) : s;
         s1 = this.formatter.apply(s1, this.displayPos);
         j1 = this.x + font.width(s1) * fontSize;
         ScreenApi.drawString(graphics, font, s1, l, i1, fontSize, -9474919);
      }

      boolean flag2 = this.cursorPos < text.length() || text.length() >= this.getMaxLength();
      float k1 = j1;
      if (!flag) {
         k1 = j > 0 ? l + this.w : l;
      } else if (flag2) {
         k1 = j1 - 1.0F;
         j1--;
      }

      if (s.length() > 0 && flag && j < s.length()) {
         ScreenApi.drawString(graphics, font, this.formatter.apply(s.substring(j), this.cursorPos), j1, i1, fontSize, -9474919);
      }

      if (this.hint != null && s.isEmpty() && !this.isFocused()) {
         ScreenApi.drawString(graphics, font, this.hint.getString(), k1, i1, fontSize, -9474919);
      }

      if (flag1) {
         if (flag2) {
            ScreenApi.fill(graphics, k1, i1 - 1.0F, 1.0F, ScreenScale.get(25.0F), -3092272);
         } else {
            ScreenApi.drawString(graphics, font, "_", k1, i1, fontSize, -9474919);
         }
      }

      if (k != j) {
         float l1 = l + font.width(s.substring(0, k)) * fontSize;
         this.renderHighlight(graphics, k1, i1 - 1.0F, l1 - 1.0F, i1 + 1.0F + font.height());
      }
   }

   public void update(double mx, double my) {
      this.isHovered = mx >= this.x && my >= this.y && mx <= this.x + this.w && my <= this.y + this.h;
      if (this.isHovered) {
         if (this.alpha < 1.0F) {
            this.alpha = Math.min(this.alpha + 0.025F, 1.0F);
         }
      } else if (this.alpha > 0.0F) {
         this.alpha = Math.max(this.alpha - 0.025F, 0.0F);
      }
   }

   public void setHighlightPos(int p_94209_) {
      String value = this.getValue();
      int i = value.length();
      this.highlightPos = Mth.clamp(p_94209_, 0, i);
      StringCache stringCache = this.fontType.getFontContainer().getTextFont();
      if (this.displayPos > i) {
         this.displayPos = i;
      }

      float j = this.w / ScreenScale.get(this.fontSize);
      String s = stringCache.trimStringToWidth(value.substring(this.displayPos), j, false);
      int k = s.length() + this.displayPos;
      if (this.highlightPos == this.displayPos) {
         this.displayPos = this.displayPos - stringCache.trimStringToWidth(value, j, true).length();
      }

      if (this.highlightPos > k) {
         this.displayPos = this.displayPos + (this.highlightPos - k);
      } else if (this.highlightPos <= this.displayPos) {
         this.displayPos = this.displayPos - (this.displayPos - this.highlightPos);
      }

      this.displayPos = Mth.clamp(this.displayPos, 0, i);
   }

   private void renderHighlight(GuiGraphics p_281400_, float p_265338_, float p_265693_, float p_265618_, float p_265584_) {
      if (p_265338_ < p_265618_) {
         float i = p_265338_;
         p_265338_ = p_265618_;
         p_265618_ = i;
      }

      if (p_265693_ < p_265584_) {
         float j = p_265693_;
         p_265693_ = p_265584_;
         p_265584_ = j;
      }

      if (p_265618_ > this.x + this.w) {
         p_265618_ = this.x + this.w;
      }

      if (p_265338_ > this.x + this.w) {
         p_265338_ = this.x + this.w;
      }

      Matrix4f matrix4f = p_281400_.pose().last().pose();
      float f3 = ARGB32.alpha(-16776961) / 255.0F;
      float f = ARGB32.red(-16776961) / 255.0F;
      float f1 = ARGB32.green(-16776961) / 255.0F;
      float f2 = ARGB32.blue(-16776961) / 255.0F;
      VertexConsumer vertexconsumer = p_281400_.bufferSource().getBuffer(RenderType.guiTextHighlight());
      vertexconsumer.addVertex(matrix4f, p_265338_, p_265693_, 0.0F).setColor(f, f1, f2, f3);
      vertexconsumer.addVertex(matrix4f, p_265338_, p_265584_, 0.0F).setColor(f, f1, f2, f3);
      vertexconsumer.addVertex(matrix4f, p_265618_, p_265584_, 0.0F).setColor(f, f1, f2, f3);
      vertexconsumer.addVertex(matrix4f, p_265618_, p_265693_, 0.0F).setColor(f, f1, f2, f3);
      p_281400_.flush();
   }
}
