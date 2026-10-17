package com.industrialreforged.screen.widget;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.screen.ScreenScale;
import com.industrialreforged.utils.NumberUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class FontEditLongBox extends FontEditBox {
   private final int defaultValue;
   private ResourceLocation icon;

   public FontEditLongBox(@Nullable Component focusText) {
      this(focusText, 0);
   }

   public FontEditLongBox(@Nullable Component focusText, int defaultValue) {
      super(focusText);
      this.defaultValue = defaultValue;
      this.setFormatterLite((s, integer) -> {
         if (s.isEmpty()) {
            return "";
         }

         String format = NumberUtils.formatLong(this.getLong());
         StringBuilder result = new StringBuilder();
         if (integer != this.getCursorPosition()) {
            int off = 0;
            int length = format.length();

            for (int i = 0; i < s.length(); i++) {
               char sc = s.charAt(i);
               if (i + off < length && format.charAt(i + off) == ' ') {
                  result.append(" ");
                  off++;
               }

               result.append(sc);
            }
         } else {
            int length = s.length() - 1;
            int off = format.length() - 1;

            for (int i = length; i >= 0; i--) {
               char sc = s.charAt(i);
               int pos = off - (length - i);
               if (pos >= 0 && format.charAt(pos) == ' ') {
                  result.insert(0, " ");
                  off--;
               }

               result.insert(0, sc);
               if (i == 0 && pos - 1 >= 0 && format.charAt(pos - 1) == ' ') {
                  result.insert(0, " ");
               }
            }
         }

         return result.toString();
      });
      this.setFilter(s -> {
         if (s == null) {
            return false;
         }

         try {
            if (!s.equals("")) {
               Long.parseLong(s);
            }

            return true;
         } catch (NumberFormatException e) {
            return false;
         }
      });
   }

   public void setIcon(ResourceLocation icon) {
      this.icon = icon;
   }

   @Override
   public void renderWidget(@NotNull GuiGraphics graphics, int mx, int my, float pt) {
      this.update(mx, my);
      float x = this.x - ScreenScale.get(20.0F);
      float y = this.y;
      ScreenApi.fillRound(graphics, x, y, this.w, this.h, ScreenScale.get(10.0F), ARGB32.lerp(this.alpha, -14737858, -13553822));
      ScreenApi.fillBorderRound(graphics, x, y, this.w, this.h, ScreenScale.get(10.0F), ScreenScale.get(4.0F), ARGB32.lerp(this.alpha, -13553822, -16719617));
      GL11.glEnable(3042);
      ScreenApi.quad(
         graphics,
         this.icon,
         this.x + this.w - ScreenScale.get(64.0F),
         this.y + (this.h - ScreenScale.get(32.0F)) / 2.0F,
         ScreenScale.get(32.0F),
         ScreenScale.get(32.0F)
      );
      this.renderText(graphics);
   }

   public int getInt() {
      return NumberUtils.toInt(this.getValue()).orElse(this.defaultValue);
   }

   public long getLong() {
      return NumberUtils.toLong(this.getValue()).orElse((long)this.defaultValue);
   }
}
