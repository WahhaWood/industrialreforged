package com.industrialreforged.screen.widget;

import com.industrialreforged.utils.NumberUtils;
import java.util.function.LongConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class EditLongBox extends EditBox {
   private LongConsumer consumer;
   private String defaultText = "";
   private long defaultValue = -1L;

   public EditLongBox(int x, int y, int width, int height) {
      this(Minecraft.getInstance().font, x, y, width, height);
   }

   public EditLongBox(Font font, int x, int y, int width, int height) {
      super(font, x, y, width, height, Component.empty());
      this.setFormatter((s, integer) -> {
         if (s.isEmpty()) {
            return FormattedCharSequence.forward(s, Style.EMPTY);
         }

         String format = NumberUtils.formatLong(this.getCapacity());
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

         return FormattedCharSequence.forward(result.toString(), Style.EMPTY);
      });
   }

   public void setDefaultText(String text) {
      this.defaultText = text;
   }

   public void setDefaultValue(long value) {
      this.defaultValue = value;
   }

   public void setConsumer(LongConsumer consumer) {
      this.consumer = consumer;
   }

   public void setValue(String p_146180_1_) {
      try {
         if (!p_146180_1_.equals("")) {
            Long.parseLong(p_146180_1_);
         }
      } catch (NumberFormatException e) {
         return;
      }

      super.setValue(p_146180_1_);
   }

   public void insertText(String p_146191_1_) {
      try {
         if (!p_146191_1_.equals("")) {
            Long.parseLong(p_146191_1_);
         }
      } catch (NumberFormatException e) {
         return;
      }

      super.insertText(p_146191_1_);
   }

   public void setFocused(boolean p_146195_1_) {
      if (!p_146195_1_ && this.isFocused() && this.consumer != null) {
         this.consumer.accept(this.getCapacity());
      }

      super.setFocused(p_146195_1_);
   }

   private long getCapacity() {
      String text = this.getValue();

      try {
         return text.equals("") ? this.defaultValue : Long.parseLong(text);
      } catch (NumberFormatException e) {
         super.setValue(this.defaultText);
         return this.defaultValue;
      }
   }
}
