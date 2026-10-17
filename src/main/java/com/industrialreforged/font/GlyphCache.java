package com.industrialreforged.font;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GlyphCache {
   private static final int TEXTURE_WIDTH = 2048;
   private static final int TEXTURE_HEIGHT = 2048;
   private static final int STRING_WIDTH = 256;
   private static final int STRING_HEIGHT = 64;
   private static final int GLYPH_BORDER = 1;
   private static final Color BACK_COLOR = new Color(255, 255, 255, 0);
   private int fontSize = 18;
   private boolean antiAliasEnabled = false;
   private BufferedImage stringImage;
   private Graphics2D stringGraphics;
   private final BufferedImage glyphCacheImage = new BufferedImage(2048, 2048, 2);
   private final Graphics2D glyphCacheGraphics = this.glyphCacheImage.createGraphics();
   private final FontRenderContext fontRenderContext = this.glyphCacheGraphics.getFontRenderContext();
   private final int[] imageData = new int[4194304];
   private final IntBuffer imageBuffer = ByteBuffer.allocateDirect(16777216).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
   private final IntBuffer singleIntBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
   private final List<Font> allFonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
   protected List<Font> usedFonts = new ArrayList<>();
   int textureName;
   private final LinkedHashMap<Font, Integer> fontCache = new LinkedHashMap<>();
   private final LinkedHashMap<Long, GlyphCache.Entry> glyphCache = new LinkedHashMap<>();
   private int cachePosX = 0;
   private int cachePosY = 1;
   private int cacheLineHeight = 0;

   public GlyphCache() {
      this.glyphCacheGraphics.setBackground(BACK_COLOR);
      this.glyphCacheGraphics.setComposite(AlphaComposite.Src);
      this.allocateGlyphCacheTexture();
      this.allocateStringImage(256, 64);
      GraphicsEnvironment.getLocalGraphicsEnvironment().preferLocaleFonts();
      this.usedFonts.add(new Font("SansSerif", 0, 72));
   }

   void setDefaultFont(String name, int size, boolean antiAlias) {
      this.usedFonts.clear();
      this.usedFonts.add(new Font(name, 0, 72));
      this.fontSize = size;
      this.antiAliasEnabled = antiAlias;
      this.setRenderingHints();
   }

   void setCustomFont(ResourceLocation location, int size, boolean antiAlias) throws Exception {
      InputStream stream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(location).open();
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      Font font = Font.createFont(0, stream);
      ge.registerFont(font);
      font = font.deriveFont(0, 72.0F);
      this.usedFonts.clear();
      this.usedFonts.add(font);
      this.fontSize = size;
      this.antiAliasEnabled = antiAlias;
      this.setRenderingHints();
   }

   int fontHeight(String s) {
      Font font = this.lookupFont(s.toCharArray(), 0, s.length(), 0);
      return (int)font.getLineMetrics(s, this.fontRenderContext).getHeight();
   }

   GlyphVector layoutGlyphVector(Font font, char[] text, int start, int limit, int layoutFlags) {
      if (!this.fontCache.containsKey(font)) {
         this.fontCache.put(font, this.fontCache.size());
      }

      return font.layoutGlyphVector(this.fontRenderContext, text, start, limit, layoutFlags);
   }

   Font lookupFont(char[] text, int start, int limit, int style) {
      for (Font font : this.usedFonts) {
         if (font.canDisplayUpTo(text, start, limit) != start) {
            return font.deriveFont(style, this.fontSize);
         }
      }

      for (Font font : this.allFonts) {
         if (font.canDisplayUpTo(text, start, limit) != start) {
            this.usedFonts.add(font);
            return font.deriveFont(style, this.fontSize);
         }
      }

      Font font = this.usedFonts.get(0);
      return font.deriveFont(style, this.fontSize);
   }

   GlyphCache.Entry lookupGlyph(Font font, int glyphCode) {
      long fontKey = (long)this.fontCache.get(font).intValue() << 32;
      return this.glyphCache.get(fontKey | glyphCode);
   }

   void cacheGlyphs(Font font, char[] text, int start, int limit, int layoutFlags) {
      GlyphVector vector = this.layoutGlyphVector(font, text, start, limit, layoutFlags);
      Rectangle vectorBounds = null;
      long fontKey = (long)this.fontCache.get(font).intValue() << 32;
      int numGlyphs = vector.getNumGlyphs();
      Rectangle dirty = null;
      boolean vectorRendered = false;

      for (int index = 0; index < numGlyphs; index++) {
         int glyphCode = vector.getGlyphCode(index);
         if (!this.glyphCache.containsKey(fontKey | glyphCode)) {
            if (!vectorRendered) {
               vectorRendered = true;

               for (int i = 0; i < numGlyphs; i++) {
                  Point2D pos = vector.getGlyphPosition(i);
                  pos.setLocation(pos.getX() + 12 * i, pos.getY());
                  vector.setGlyphPosition(i, pos);
               }

               vectorBounds = vector.getPixelBounds(this.fontRenderContext, 0.0F, 0.0F);
               vectorBounds.width++;
               vectorBounds.height++;
               if (this.stringImage == null) {
                  this.allocateStringImage(vectorBounds.width, vectorBounds.height);
               } else if (vectorBounds.width > this.stringImage.getWidth() || vectorBounds.height > this.stringImage.getHeight()) {
                  int width = Math.max(vectorBounds.width, this.stringImage.getWidth());
                  int height = Math.max(vectorBounds.height, this.stringImage.getHeight());
                  this.allocateStringImage(width, height);
               }

               this.stringGraphics.clearRect(0, 0, vectorBounds.width, vectorBounds.height);
               this.stringGraphics.drawGlyphVector(vector, -vectorBounds.x, -vectorBounds.y);
            }

            Rectangle rect = vector.getGlyphPixelBounds(index, null, -vectorBounds.x, -vectorBounds.y);
            rect.x--;
            rect.y--;
            rect.height += 2;
            rect.width += 2;
            if (this.cachePosX + rect.width + 1 > 2048) {
               this.cachePosX = 1;
               this.cachePosY = this.cachePosY + this.cacheLineHeight + 1;
               this.cacheLineHeight = 0;
            }

            if (this.cachePosY + rect.height + 1 > 2048) {
               this.updateTexture(dirty);
               dirty = null;
               this.allocateGlyphCacheTexture();
               this.cachePosY = this.cachePosX = 1;
               this.cacheLineHeight = 0;
            }

            if (rect.height > this.cacheLineHeight) {
               this.cacheLineHeight = rect.height;
            }

            this.glyphCacheGraphics
               .drawImage(
                  this.stringImage,
                  this.cachePosX,
                  this.cachePosY,
                  this.cachePosX + rect.width,
                  this.cachePosY + rect.height,
                  rect.x,
                  rect.y,
                  rect.x + rect.width,
                  rect.y + rect.height,
                  null
               );
            rect.setLocation(this.cachePosX, this.cachePosY);
            GlyphCache.Entry entry = new GlyphCache.Entry();
            entry.textureName = this.textureName;
            entry.width = rect.width;
            entry.height = rect.height;
            entry.u1 = rect.x / 2048.0F;
            entry.v1 = rect.y / 2048.0F;
            entry.u2 = (rect.x + rect.width) / 2048.0F;
            entry.v2 = (rect.y + rect.height) / 2048.0F;
            this.glyphCache.put(fontKey | glyphCode, entry);
            if (dirty == null) {
               dirty = new Rectangle(this.cachePosX, this.cachePosY, rect.width, rect.height);
            } else {
               dirty.add(rect);
            }

            this.cachePosX = this.cachePosX + rect.width + 1;
         }
      }

      this.updateTexture(dirty);
   }

   private void updateTexture(Rectangle dirty) {
      if (dirty != null) {
         this.updateImageBuffer(dirty.x, dirty.y, dirty.width, dirty.height);
         GlStateManager._bindTexture(this.textureName);
         GL11.glTexSubImage2D(3553, 0, dirty.x, dirty.y, dirty.width, dirty.height, 6408, 5121, this.imageBuffer);
         GL30.glGenerateMipmap(3553);
      }
   }

   private void allocateStringImage(int width, int height) {
      this.stringImage = new BufferedImage(width, height, 2);
      this.stringGraphics = this.stringImage.createGraphics();
      this.setRenderingHints();
      this.stringGraphics.setBackground(BACK_COLOR);
      this.stringGraphics.setPaint(Color.WHITE);
   }

   private void setRenderingHints() {
      this.stringGraphics
         .setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.antiAliasEnabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
      this.stringGraphics
         .setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, this.antiAliasEnabled ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
         );
      this.stringGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
   }

   private void allocateGlyphCacheTexture() {
      this.glyphCacheGraphics.clearRect(0, 0, 2048, 2048);
      this.singleIntBuffer.clear();
      GL11.glGenTextures(this.singleIntBuffer);
      this.textureName = this.singleIntBuffer.get(0);
      this.updateImageBuffer(0, 0, 2048, 2048);
      GlStateManager._bindTexture(this.textureName);
      GlStateManager._texImage2D(3553, 0, 6408, 2048, 2048, 0, 6408, 5121, this.imageBuffer);
      GlStateManager._texParameter(3553, 10242, 33069);
      GlStateManager._texParameter(3553, 10243, 33069);
      GlStateManager._texParameter(3553, 10241, 9987);
      GlStateManager._texParameter(3553, 10240, 9729);
   }

   private void updateImageBuffer(int x, int y, int width, int height) {
      this.glyphCacheImage.getRGB(x, y, width, height, this.imageData, 0, width);

      for (int i = 0; i < width * height; i++) {
         int color = this.imageData[i];
         this.imageData[i] = color << 8 | color >>> 24;
      }

      this.imageBuffer.clear();
      this.imageBuffer.put(this.imageData);
      this.imageBuffer.flip();
   }

   static class Entry {
      public int textureName;
      public int width;
      public int height;
      public float u1;
      public float v1;
      public float u2;
      public float v2;
   }
}
