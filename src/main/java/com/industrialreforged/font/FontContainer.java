package com.industrialreforged.font;

import com.industrialreforged.screen.ScreenScale;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class FontContainer {
   private StringCache textFont = null;

   private FontContainer() {
   }

   public FontContainer(String fontType, int fontSize) {
      this(fontType, fontSize, null);
   }

   public FontContainer(String fontType, int fontSize, ResourceLocation resLoc) {
      this.textFont = new StringCache();
      this.textFont.setDefaultFont("Arial", (int)ScreenScale.get(fontSize), true);

      try {
         if (!fontType.isEmpty() && !fontType.equalsIgnoreCase("default") && resLoc != null) {
            this.textFont.setCustomFont(resLoc, fontSize, true);
         } else {
            this.textFont.setDefaultFont(fontType, fontSize, true);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public float height() {
      return this.textFont.fontHeight;
   }

   public float width(String text) {
      return this.textFont.getStringWidth(text);
   }

   public FontContainer copy() {
      FontContainer font = new FontContainer();
      font.textFont = this.textFont;
      return font;
   }

   public float drawStringWithShadow(GuiGraphics graphics, String text, float x, float y, int color) {
      return this.drawStringWithShadow(graphics.pose(), text, x, y, color);
   }

   public float drawString(GuiGraphics graphics, String text, float x, float y, int color) {
      return this.drawString(graphics.pose(), text, x, y, color);
   }

   public float drawStringWithShadow(PoseStack stack, String text, float x, float y, int color) {
      return this.drawStringWithShadow(stack.last().pose(), text, x, y, color);
   }

   public float drawString(PoseStack stack, String text, float x, float y, int color) {
      return this.drawString(stack.last().pose(), text, x, y, color);
   }

   public float drawStringWithShadow(Matrix4f matrix4f, String text, float x, float y, int color) {
      float l = this.textFont.renderString(matrix4f, text, x + 1.0F, y + 1.0F, color, true);
      return Math.max(l, this.textFont.renderString(matrix4f, text, x, y, color, false));
   }

   public float drawString(Matrix4f matrix4f, String text, float x, float y, int color) {
      return this.textFont.renderString(matrix4f, text, x, y, color, false);
   }

   public String getName() {
      return this.textFont.usedFont().getFontName();
   }

   public StringCache getTextFont() {
      return this.textFont;
   }

   public List<String> split(String text, float scale, float width) {
      if (text == null) {
         return Collections.emptyList();
      }

      text = text.replaceAll(String.valueOf(' '), " ");
      List<String> splitted = new ArrayList<>();
      if (text.contains("\n")) {
         int offset = 0;
         String preColor = "";

         do {
            int index = text.indexOf("\n");
            String temp = text.substring(offset, index);
            int lastColorIndex = temp.lastIndexOf("§");
            this.splitted(splitted, preColor + temp, width, scale);
            if (lastColorIndex != -1) {
               preColor = temp.substring(lastColorIndex, lastColorIndex + 2);
            }

            text = preColor + text.replaceFirst("\n", "");
            offset = index;
         } while (text.contains("\n"));

         this.splitted(splitted, preColor + text.substring(offset), width, scale);
      } else {
         this.splitted(splitted, text, width, scale);
      }

      return splitted;
   }

   private void splitted(List<String> list, String s, float width, float scale) {
      if (s.length() == 0) {
         list.add("");
      } else {
         String strColor = "";

         while (s.length() > 0) {
            String trim = this.textFont.trimStringToWidthSaveWords(s, width / scale, false);
            list.add(strColor + trim);
            int colorIndex = trim.lastIndexOf("§");
            if (colorIndex != -1) {
               strColor = trim.substring(colorIndex, colorIndex + 2);
            }

 s = s.replace(trim, "");
         }
      }
   }
}
