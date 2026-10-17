package com.industrialreforged.tech.client.screen.widgets;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class TextLabel extends AbstractWidget {
   private final Supplier<String> textProvider;
   private final Supplier<Integer> color;
   private final boolean shadow;
   private final boolean fixedHoverWidth;
   private final boolean fixedHoverHeight;
   private final int baseX;
   private final int baseY;
   private final boolean centerX;
   private final boolean centerY;
   private BooleanSupplier enableHandler;
   private java.util.function.Supplier<String> tooltipProvider;

   public static TextLabel create(int x, int y, String string, int color, boolean shadow) {
      return create(x, y, Suppliers.ofInstance(string), color, shadow);
   }

   public static TextLabel create(int x, int y, Supplier<String> provider, int color, boolean shadow) {
      return create(x, y, provider, color, shadow, false, false);
   }

   public static TextLabel create(int x, int y, String string, int color, boolean shadow, boolean centerX, boolean centerY) {
      return create(x, y, Suppliers.ofInstance(string), color, shadow, centerX, centerY);
   }

   public static TextLabel create(int x, int y, Supplier<String> provider, int color, boolean shadow, boolean centerX, boolean centerY) {
      return create(x, y, -1, -1, provider, color, shadow, centerX, centerY);
   }

   public static TextLabel create(int x, int y, int width, int height, Supplier<String> provider, int color, boolean shadow, boolean centerX, boolean centerY) {
      return create(x, y, width, height, provider, color, shadow, 0, 0, centerX, centerY);
   }

   public static TextLabel create(
      int x, int y, int width, int height, Supplier<String> provider, int color, boolean shadow, int offsetX, int offsetY, boolean centerX, boolean centerY
   ) {
      return create(x, y, width, height, provider, Suppliers.ofInstance(color), shadow, offsetX, offsetY, centerX, centerY);
   }

   public static TextLabel createRightAligned(
      int x, int y, int width, int height, Supplier<String> provider, int color, boolean shadow, int offsetX, int offsetY, boolean centerX, boolean centerY
   ) {
      return create(x, y, width, height, provider, Suppliers.ofInstance(color), shadow, offsetX - getWidth(provider), offsetY, centerX, centerY);
   }

   public static TextLabel create(
      int x,
      int y,
      int width,
      int height,
      Supplier<String> provider,
      Supplier<Integer> supplier,
      boolean shadow,
      int offsetX,
      int offsetY,
      boolean centerX,
      boolean centerY
   ) {
      boolean fixedHoverWidth;
      if (width < 0) {
         fixedHoverWidth = false;
         width = getWidth(provider);
      } else {
         fixedHoverWidth = true;
      }

      boolean fixedHoverHeight;
      if (height < 0) {
         fixedHoverHeight = false;
         height = 8;
      } else {
         fixedHoverHeight = true;
      }

      int baseX = x + offsetX;
      int baseY = y + offsetY;
      if (centerX) {
         if (fixedHoverWidth) {
            baseX += width / 2;
         } else {
            x -= width / 2;
         }
      }

      if (centerY) {
         if (fixedHoverHeight) {
            baseY += (height + 1) / 2;
         } else {
 y -= height / 2;
         }
      }

      return new TextLabel(x, y, width, height, provider, supplier, shadow, fixedHoverWidth, fixedHoverHeight, baseX, baseY, centerX, centerY);
   }

   private TextLabel(
      int x,
      int y,
      int width,
      int height,
      Supplier<String> textProvider,
      Supplier<Integer> supplier,
      boolean shadow,
      boolean fixedHoverWidth,
      boolean fixedHoverHeight,
      int baseX,
      int baseY,
      boolean centerX,
      boolean centerY
   ) {
      super(x, y, width, height, Component.empty());
      this.textProvider = textProvider;
      this.color = supplier;
      this.shadow = shadow;
      this.fixedHoverWidth = fixedHoverWidth;
      this.fixedHoverHeight = fixedHoverHeight;
      this.baseX = baseX;
      this.baseY = baseY;
      this.centerX = centerX;
      this.centerY = centerY;
   }

   public TextLabel withEnableHandler(BooleanSupplier iEnableHandler) {
      this.enableHandler = iEnableHandler;
      return this;
   }

   public TextLabel withTooltip(String string) {
      return this.withTooltip(Suppliers.ofInstance(string));
   }

   public TextLabel withTooltip(java.util.function.Supplier<String> supplier) {
      this.tooltipProvider = supplier;
      return this;
   }

   private static int getWidth(Supplier<String> supplier) {
      String string = (String)supplier.get();
      return string.isEmpty() ? 0 : Minecraft.getInstance().font.width(string);
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (this.enableHandler == null || this.enableHandler.getAsBoolean()) {
         Font font = Minecraft.getInstance().font;
         String string = (String)this.textProvider.get();
         int n3;
         int n4;
         if (string.isEmpty()) {
            n4 = 0;
            n3 = 0;
         } else {
            n3 = font.width(string);
            n4 = 8;
         }

         int n5 = this.baseX;
         if (this.centerX) {
            n5 -= n3 / 2;
         }

         int n6 = this.baseY;
         if (this.centerY) {
            n6 -= n4 / 2;
         }

         if (!this.fixedHoverWidth) {
            this.setX(n5);
            this.width = n3;
         }

         if (!this.fixedHoverHeight) {
            this.setY(n6);
            this.height = n4;
         }

         if (!string.isEmpty()) {
            graphics.drawString(font, string, n5, n6, (Integer)this.color.get(), this.shadow);
         }

         if (this.isMouseOver(mouseX, mouseY) && this.tooltipProvider != null) {
            String tooltip = this.tooltipProvider.get();
            if (tooltip != null && !tooltip.isBlank()) {
               this.setTooltip(Tooltip.create(Component.literal(tooltip)));
            }
         }
      }
   }

   protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
   }

   public enum TextAlignment {
      Start,
      Center,
      End;

      private static final Map<String, TextLabel.TextAlignment> map = getMap();
      public final String name = this.name().toLowerCase(Locale.ENGLISH);

      public static TextLabel.TextAlignment get(String string) {
         return map.get(string);
      }

      private static Map<String, TextLabel.TextAlignment> getMap() {
         TextLabel.TextAlignment[] arrtextAlignment = values();
         HashMap<String, TextLabel.TextAlignment> hashMap = new HashMap<>(arrtextAlignment.length);

         for (TextLabel.TextAlignment textAlignment : arrtextAlignment) {
            hashMap.put(textAlignment.name, textAlignment);
         }

         return hashMap;
      }
   }
}
