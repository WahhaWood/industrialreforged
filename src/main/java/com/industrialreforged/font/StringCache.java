package com.industrialreforged.font;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.awt.Font;
import java.awt.Point;
import java.awt.font.GlyphVector;
import java.lang.ref.WeakReference;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Matrix4f;

public class StringCache {
   private static final int BASELINE_OFFSET = 7;
   private static final int UNDERLINE_OFFSET = 1;
   private static final int UNDERLINE_THICKNESS = 2;
   private static final int STRIKETHROUGH_OFFSET = -6;
   private static final int STRIKETHROUGH_THICKNESS = 2;
   private final GlyphCache glyphCache;
   private final int[] colorTable;
   private final WeakHashMap<StringCache.Key, StringCache.Entry> stringCache = new WeakHashMap<>();
   private final WeakHashMap<String, StringCache.Key> weakRefCache = new WeakHashMap<>();
   private final StringCache.Key lookupKey = new StringCache.Key();
   private StringCache.Glyph[][] digitGlyphs = new StringCache.Glyph[4][];
   private boolean digitGlyphsReady = false;
   private boolean antiAliasEnabled = false;
   private final Thread mainThread = Thread.currentThread();
   public float fontHeight;

   public StringCache() {
      this.glyphCache = new GlyphCache();
      this.colorTable = new int[32];

      for (int i = 0; i < 32; i++) {
         int j = (i >> 3 & 1) * 85;
         int k = (i >> 2 & 1) * 170 + j;
         int l = (i >> 1 & 1) * 170 + j;
         int i1 = (i >> 0 & 1) * 170 + j;
         if (i == 6) {
 k += 85;
         }

         if (i >= 16) {
 k /= 4;
            l /= 4;
            i1 /= 4;
         }

         this.colorTable[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | i1 & 0xFF;
      }

      this.cacheDightGlyphs();
   }

   public int getTextureId() {
      return this.glyphCache.textureName;
   }

   public void setDefaultFont(String fontName, int fontSize, boolean antiAlias) {
      this.glyphCache.setDefaultFont(fontName, fontSize, antiAlias);
      this.antiAliasEnabled = antiAlias;
      this.weakRefCache.clear();
      this.stringCache.clear();
      this.cacheDightGlyphs();
      this.updateHeight();
   }

   public Font usedFont() {
      return this.glyphCache.usedFonts.get(0);
   }

   public void setCustomFont(ResourceLocation resource, int fontSize, boolean antiAlias) throws Exception {
      this.glyphCache.setCustomFont(resource, fontSize, antiAlias);
      this.antiAliasEnabled = antiAlias;
      this.weakRefCache.clear();
      this.stringCache.clear();
      this.cacheDightGlyphs();
      this.updateHeight();
   }

   public void updateHeight() {
      float height = 0.0F;
      float minY = 2.1474836E9F;
      float maxY = -2.1474836E9F;
      this.cacheString(
         "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZzАаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя`~@#$%^&*()_-+=[]{};:'\"\\|/?.,<>№ІіЇї"
      );

      for (StringCache.Glyph g : this.cacheString("AaBbCcDdEeHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZzАаБбВвГгДдЕеЁёЖжЗзИиЙйЫы1234567890").glyphs) {
         if (g.texture.height > height) {
            height = g.texture.height;
         }

         if (g.y < minY) {
            minY = g.y;
         }

         if (g.y > maxY) {
            maxY = g.y;
         }
      }

      this.fontHeight = (maxY + height) / 2.0F - minY / 2.0F;
   }

   private void cacheDightGlyphs() {
      this.digitGlyphsReady = false;
      this.digitGlyphs[0] = this.cacheString("0123456789").glyphs;
      this.digitGlyphs[1] = this.cacheString("§l0123456789").glyphs;
      this.digitGlyphs[2] = this.cacheString("§o0123456789").glyphs;
      this.digitGlyphs[3] = this.cacheString("§l§o0123456789").glyphs;
      this.digitGlyphsReady = true;
   }

   public float renderString(Matrix4f matrix4f, String str, float startX, float startY, int initialColor, boolean shadowFlag) {
      if (str == null) {
         return 0.0F;
      }

      if (str.isEmpty()) {
         return startX;
      }

      StringCache.Entry entry = this.cacheString(str);
      startY += 7.0F;
      if ((initialColor >> 24 & 0xFF) == 0) {
         initialColor -= 16777216;
      }

      int color = shadowFlag ? (initialColor >> 24 & 0xFF) << 24 : initialColor;
      int red = ARGB32.red(color);
      int green = ARGB32.green(color);
      int blue = ARGB32.blue(color);
      int alpha = ARGB32.alpha(color);
      int boundTextureName = 0;
      if (this.antiAliasEnabled) {
         GlStateManager._enableBlend();
         GlStateManager._blendFunc(770, 771);
      }

      Tesselator tessellator = Tesselator.getInstance();
      BufferBuilder buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      int fontStyle = 0;
      int glyphIndex = 0;
      int colorIndex = 0;

      while (glyphIndex < entry.glyphs.length) {
         while (colorIndex < entry.colors.length && entry.glyphs[glyphIndex].stringIndex >= entry.colors[colorIndex].stringIndex) {
            if (!shadowFlag) {
               color = this.applyColorCode(entry.colors[colorIndex].colorCode, initialColor, false);
               red = ARGB32.red(color);
               green = ARGB32.green(color);
               blue = ARGB32.blue(color);
               alpha = ARGB32.alpha(color);
            }

            fontStyle = entry.colors[colorIndex].fontStyle;
            colorIndex++;
         }

         StringCache.Glyph glyph = entry.glyphs[glyphIndex];
         GlyphCache.Entry texture = glyph.texture;
         float glyphX = glyph.x;
         char c = str.charAt(glyph.stringIndex);
         if (c >= '0' && c <= '9') {
            int oldWidth = texture.width;
            texture = this.digitGlyphs[fontStyle][c - '0'].texture;
            int newWidth = texture.width;
            glyphX += oldWidth - newWidth >> 1;
         }

         if (boundTextureName != texture.textureName) {
            MeshData meshData = buffer.build();
            if (meshData != null) {
               BufferUploader.drawWithShader(meshData);
            }

            RenderSystem.setShaderTexture(0, texture.textureName);
            buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            boundTextureName = texture.textureName;
         }

         float x1 = startX + glyphX / 2.0F;
         float x2 = startX + (glyphX + texture.width) / 2.0F;
         float y1 = startY + glyph.y / 2.0F;
         float y2 = startY + (glyph.y + texture.height) / 2.0F;
         buffer.addVertex(matrix4f, x1, y1, 0.0F).setUv(texture.u1, texture.v1).setColor(red, green, blue, alpha);
         buffer.addVertex(matrix4f, x1, y2, 0.0F).setUv(texture.u1, texture.v2).setColor(red, green, blue, alpha);
         buffer.addVertex(matrix4f, x2, y2, 0.0F).setUv(texture.u2, texture.v2).setColor(red, green, blue, alpha);
         buffer.addVertex(matrix4f, x2, y1, 0.0F).setUv(texture.u2, texture.v1).setColor(red, green, blue, alpha);
         glyphIndex++;
      }

      MeshData meshData = buffer.build();
      if (meshData != null) {
         BufferUploader.drawWithShader(meshData);
      }

      if (entry.specialRender) {
         colorIndex = 0;
         color = initialColor;
         red = ARGB32.red(color);
         green = ARGB32.green(color);
         blue = ARGB32.blue(color);
         alpha = ARGB32.alpha(color);
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         buffer = tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         int glyphIndexx = 0;
         int colorIndexx = 0;

         while (glyphIndexx < entry.glyphs.length) {
            while (colorIndexx < entry.colors.length && entry.glyphs[glyphIndexx].stringIndex >= entry.colors[colorIndexx].stringIndex) {
               color = this.applyColorCode(entry.colors[colorIndexx].colorCode, initialColor, shadowFlag);
               red = ARGB32.red(color);
               green = ARGB32.green(color);
               blue = ARGB32.blue(color);
               alpha = ARGB32.alpha(color);
               colorIndex = entry.colors[colorIndexx].renderStyle;
               colorIndexx++;
            }

            StringCache.Glyph glyph = entry.glyphs[glyphIndexx];
            float glyphSpace = glyph.advance - glyph.texture.width;
            if ((colorIndex & 1) != 0) {
               float x1 = startX + (glyph.x - glyphSpace) / 2.0F;
               float x2 = startX + (glyph.x + glyph.advance) / 2.0F;
               float y1 = startY + 0.5F;
               float y2 = startY + 1.5F;
               buffer.addVertex(matrix4f, x1, y1, 0.0F).setColor(red, green, blue, alpha);
               buffer.addVertex(matrix4f, x1, y2, 0.0F).setColor(red, green, blue, alpha);
               buffer.addVertex(matrix4f, x2, y2, 0.0F).setColor(red, green, blue, alpha);
               buffer.addVertex(matrix4f, x2, y1, 0.0F).setColor(red, green, blue, alpha);
            }

            if ((colorIndex & 2) != 0) {
               float x1 = startX + (glyph.x - glyphSpace) / 2.0F;
               float x2 = startX + (glyph.x + glyph.advance) / 2.0F;
               float y1 = startY + -3.0F;
               float y2 = startY + -2.0F;
               buffer.addVertex(matrix4f, x1, y1, 0.0F).setColor(red, green, blue, alpha);
               buffer.addVertex(matrix4f, x1, y2, 0.0F).setColor(red, green, blue, alpha);
               buffer.addVertex(matrix4f, x2, y2, 0.0F).setColor(red, green, blue, alpha);
               buffer.addVertex(matrix4f, x2, y1, 0.0F).setColor(red, green, blue, alpha);
            }

            glyphIndexx++;
         }

         MeshData var37 = buffer.build();
         if (var37 != null) {
            BufferUploader.drawWithShader(var37);
         }
      }

      return entry.advance / 2.0F + startX;
   }

   public float getStringWidth(String str) {
      if (str != null && !str.isEmpty()) {
         StringCache.Entry entry = this.cacheString(str);
         return entry.advance / 2.0F;
      } else {
         return 0.0F;
      }
   }

   private int sizeString(String str, float width, boolean breakAtSpaces) {
      if (str != null && !str.isEmpty()) {
         width += width;
         StringCache.Glyph[] glyphs = this.cacheString(str).glyphs;
         int wsIndex = -1;
         int advance = 0;

         int index;
         for (index = 0; index < glyphs.length && advance <= width; index++) {
            if (breakAtSpaces) {
               char c = str.charAt(glyphs[index].stringIndex);
               if (c == ' ') {
                  wsIndex = index;
               } else if (c == '\n') {
                  wsIndex = index;
                  break;
               }
            }

            advance = (int)(advance + glyphs[index].advance);
         }

         if (index < glyphs.length && wsIndex != -1 && wsIndex < index) {
            index = wsIndex + 1;
         }

         return index < glyphs.length ? glyphs[index].stringIndex : str.length();
      } else {
         return 0;
      }
   }

   public float sizeStringToWidth(String str, float width) {
      return this.sizeString(str, width, true);
   }

   public String trimStringToWidth(String str, float width, boolean reverse) {
      int length = this.sizeString(str, width, false);
      str = str.substring(0, length);
      if (reverse) {
         str = new StringBuilder(str).reverse().toString();
      }

      return str;
   }

   public String trimStringToWidthSaveWords(String str, float width, boolean reverse) {
      int length = this.sizeString(str, width, true);
      str = str.substring(0, length);
      if (reverse) {
         str = new StringBuilder(str).reverse().toString();
      }

      return str;
   }

   private int applyColorCode(int colorCode, int color, boolean shadowFlag) {
      if (colorCode != -1) {
         colorCode = shadowFlag ? colorCode + 16 : colorCode;
         color = this.colorTable[colorCode] & 16777215 | color & 0xFF000000;
      }

      return color;
   }

   private StringCache.Entry cacheString(String str) {
      StringCache.Entry entry = null;
      if (this.mainThread == Thread.currentThread()) {
         this.lookupKey.str = str;
         entry = this.stringCache.get(this.lookupKey);
      }

      if (entry == null) {
         char[] text = str.toCharArray();
         entry = new StringCache.Entry();
         int length = this.stripColorCodes(entry, str, text);
         List<StringCache.Glyph> glyphList = new ArrayList<>();
         entry.advance = this.layoutBidiString(glyphList, text, 0, length, entry.colors);
         entry.glyphs = new StringCache.Glyph[glyphList.size()];
         entry.glyphs = glyphList.toArray(entry.glyphs);
         Arrays.sort(entry.glyphs);
         int colorIndex = 0;
         int shift = 0;

         for (int glyphIndex = 0; glyphIndex < entry.glyphs.length; glyphIndex++) {
            StringCache.Glyph glyph;
            for (glyph = entry.glyphs[glyphIndex];
               colorIndex < entry.colors.length && glyph.stringIndex + shift >= entry.colors[colorIndex].stringIndex;
               colorIndex++
            ) {
               shift += 2;
            }

            glyph.stringIndex += shift;
         }

         if (this.mainThread == Thread.currentThread()) {
            StringCache.Key key = new StringCache.Key();
            key.str = str;
            entry.keyRef = new WeakReference<>(key);
            this.stringCache.put(key, entry);
         }
      }

      if (this.mainThread == Thread.currentThread()) {
         StringCache.Key oldKey = entry.keyRef.get();
         if (oldKey != null) {
            this.weakRefCache.put(str, oldKey);
         }

         this.lookupKey.str = null;
      }

      return entry;
   }

   private int stripColorCodes(StringCache.Entry cacheEntry, String str, char[] text) {
      List<StringCache.ColorCode> colorList = new ArrayList<>();
      int start = 0;
      int shift = 0;
      byte fontStyle = 0;
      byte renderStyle = 0;
      byte colorCode = -1;

      int next;
      while ((next = str.indexOf(167, start)) != -1 && next + 1 < str.length()) {
         System.arraycopy(text, next - shift + 2, text, next - shift, text.length - next - 2);
         int code = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(str.charAt(next + 1)));
         switch (code) {
            case 16:
               break;
            case 17:
               fontStyle = (byte)(fontStyle | 1);
               break;
            case 18:
               renderStyle = (byte)(renderStyle | 2);
               cacheEntry.specialRender = true;
               break;
            case 19:
               renderStyle = (byte)(renderStyle | 1);
               cacheEntry.specialRender = true;
               break;
            case 20:
               fontStyle = (byte)(fontStyle | 2);
               break;
            case 21:
               fontStyle = 0;
               renderStyle = 0;
               colorCode = -1;
               break;
            default:
               if (code >= 0 && code <= 15) {
                  colorCode = (byte)code;
                  fontStyle = 0;
                  renderStyle = 0;
               }
         }

         StringCache.ColorCode entry = new StringCache.ColorCode();
         entry.stringIndex = next;
         entry.stripIndex = next - shift;
         entry.colorCode = colorCode;
         entry.fontStyle = fontStyle;
         entry.renderStyle = renderStyle;
         colorList.add(entry);
         start = next + 2;
         shift += 2;
      }

      cacheEntry.colors = new StringCache.ColorCode[colorList.size()];
      cacheEntry.colors = colorList.toArray(cacheEntry.colors);
      return text.length - shift;
   }

   private int layoutBidiString(List<StringCache.Glyph> glyphList, char[] text, int start, int limit, StringCache.ColorCode[] colors) {
      int advance = 0;
      if (!Bidi.requiresBidi(text, start, limit)) {
         return this.layoutStyle(glyphList, text, start, limit, 0, advance, colors);
      }

      Bidi bidi = new Bidi(text, start, null, 0, limit - start, -2);
      if (bidi.isRightToLeft()) {
         return this.layoutStyle(glyphList, text, start, limit, 1, advance, colors);
      }

      int runCount = bidi.getRunCount();
      byte[] levels = new byte[runCount];
      Integer[] ranges = new Integer[runCount];

      for (int index = 0; index < runCount; index++) {
         levels[index] = (byte)bidi.getRunLevel(index);
         ranges[index] = index;
      }

      Bidi.reorderVisually(levels, 0, ranges, 0, runCount);

      for (int visualIndex = 0; visualIndex < runCount; visualIndex++) {
         int logicalIndex = ranges[visualIndex];
         int layoutFlag = (bidi.getRunLevel(logicalIndex) & 1) == 1 ? 1 : 0;
         advance = this.layoutStyle(
            glyphList, text, start + bidi.getRunStart(logicalIndex), start + bidi.getRunLimit(logicalIndex), layoutFlag, advance, colors
         );
      }

      return advance;
   }

   private int layoutStyle(List<StringCache.Glyph> glyphList, char[] text, int start, int limit, int layoutFlags, int advance, StringCache.ColorCode[] colors) {
      int currentFontStyle = 0;
      int colorIndex = Arrays.binarySearch(colors, Integer.valueOf(start));
      if (colorIndex < 0) {
         colorIndex = -colorIndex - 2;
      }

      while (start < limit) {
         int next = limit;

         while (colorIndex >= 0 && colorIndex < colors.length - 1 && colors[colorIndex].stripIndex == colors[colorIndex + 1].stripIndex) {
            colorIndex++;
         }

         if (colorIndex >= 0 && colorIndex < colors.length) {
            currentFontStyle = colors[colorIndex].fontStyle;
         }

         while (++colorIndex < colors.length) {
            if (colors[colorIndex].fontStyle != currentFontStyle) {
               next = colors[colorIndex].stripIndex;
               break;
            }
         }

         advance = this.layoutString(glyphList, text, start, next, layoutFlags, advance, currentFontStyle);
         start = next;
      }

      return advance;
   }

   private int layoutString(List<StringCache.Glyph> glyphList, char[] text, int start, int limit, int layoutFlags, int advance, int style) {
      if (this.digitGlyphsReady) {
         for (int index = start; index < limit; index++) {
            if (text[index] >= '0' && text[index] <= '9') {
               text[index] = '0';
            }
         }
      }

      while (start < limit) {
         Font font = this.glyphCache.lookupFont(text, start, limit, style);
         int next = font.canDisplayUpTo(text, start, limit);
         if (next == -1) {
            next = limit;
         }

         if (next == start) {
            next++;
         }

         advance = this.layoutFont(glyphList, text, start, next, layoutFlags, advance, font);
         start = next;
      }

      return advance;
   }

   private int layoutFont(List<StringCache.Glyph> glyphList, char[] text, int start, int limit, int layoutFlags, int advance, Font font) {
      if (this.mainThread == Thread.currentThread()) {
         this.glyphCache.cacheGlyphs(font, text, start, limit, layoutFlags);
      }

      GlyphVector vector = this.glyphCache.layoutGlyphVector(font, text, start, limit, layoutFlags);
      StringCache.Glyph glyph = null;
      int numGlyphs = vector.getNumGlyphs();

      for (int index = 0; index < numGlyphs; index++) {
         Point position = vector.getGlyphPixelBounds(index, null, advance, 0.0F).getLocation();
         if (glyph != null) {
            glyph.advance = position.x - glyph.x;
         }

         glyph = new StringCache.Glyph();
         glyph.stringIndex = start + vector.getGlyphCharIndex(index);
         glyph.texture = this.glyphCache.lookupGlyph(font, vector.getGlyphCode(index));
         glyph.x = position.x;
         glyph.y = position.y;
         glyphList.add(glyph);
      }

      advance = (int)(advance + vector.getGlyphPosition(numGlyphs).getX());
      if (glyph != null) {
         glyph.advance = advance - glyph.x;
      }

      return advance;
   }

   private static class ColorCode implements Comparable<Integer> {
      public static final byte UNDERLINE = 1;
      public static final byte STRIKETHROUGH = 2;
      public int stringIndex;
      public int stripIndex;
      public byte colorCode;
      public byte fontStyle;
      public byte renderStyle;

      public int compareTo(Integer i) {
         return this.stringIndex == i ? 0 : (this.stringIndex < i ? -1 : 1);
      }
   }

   private static class Entry {
      public WeakReference<StringCache.Key> keyRef;
      public int advance;
      public StringCache.Glyph[] glyphs;
      public StringCache.ColorCode[] colors;
      public boolean specialRender;
   }

   private static class Glyph implements Comparable<StringCache.Glyph> {
      public int stringIndex;
      public GlyphCache.Entry texture;
      public float x;
      public float y;
      public float advance;

      public int compareTo(StringCache.Glyph o) {
         return this.stringIndex == o.stringIndex ? 0 : (this.stringIndex < o.stringIndex ? -1 : 1);
      }
   }

   private static class Key {
      public String str;

      @Override
      public int hashCode() {
         int code = 0;
         int length = this.str.length();
         boolean colorCode = false;

         for (int index = 0; index < length; index++) {
            char c = this.str.charAt(index);
            if (c >= '0' && c <= '9' && !colorCode) {
               c = '0';
            }

            code = code * 31 + c;
            colorCode = c == 167;
         }

         return code;
      }

      @Override
      public boolean equals(Object o) {
         if (o == null) {
            return false;
         }

         String other = o.toString();
         int length = this.str.length();
         if (length != other.length()) {
            return false;
         }

         boolean colorCode = false;

         for (int index = 0; index < length; index++) {
            char c1 = this.str.charAt(index);
            char c2 = other.charAt(index);
            if (c1 != c2 && (c1 < '0' || c1 > '9' || c2 < '0' || c2 > '9' || colorCode)) {
               return false;
            }

            colorCode = c1 == 167;
         }

         return true;
      }

      @Override
      public String toString() {
         return this.str;
      }
   }
}
