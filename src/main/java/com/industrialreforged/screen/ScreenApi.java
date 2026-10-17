package com.industrialreforged.screen;

import com.industrialreforged.font.EnumStringRenderType;
import com.industrialreforged.font.FontContainer;
import com.industrialreforged.font.FontType;
import com.industrialreforged.core.AsyncTexture;
import com.industrialreforged.core.ClientShaders;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.logging.LogUtils;
import com.industrialreforged.energy.BigEnergyStorage;
import com.mojang.math.Axis;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

public class ScreenApi {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<UUID, Pair<ResourceLocation, Float>> CACHE = new WeakHashMap<>(1);
   private static final Minecraft minecraft = Minecraft.getInstance();
   private static final Map<ResourceLocation, ResourceLocation> dynamicTextures = new HashMap<>();

   public static ResourceLocation toDynamicTexture(ResourceLocation loc) {
      return dynamicTextures.computeIfAbsent(loc, location -> {
         Resource resource = (Resource)minecraft.getResourceManager().getResource(location).orElse(null);
         if (resource == null) {
            return (ResourceLocation)location;
         }

         try (InputStream stream = resource.open()) {
            return minecraft.getTextureManager().register("gravity_dynamic_texture", new DynamicTexture(NativeImage.read(stream)));
         } catch (IOException e) {
            LOGGER.error("Register dymamic texture error", e);
            return (ResourceLocation)location;
         }
      });
   }

   public static void renderFluidTank(GuiGraphics graphics, FluidTank tank, int x, int y, int width, int height) {
      renderFluidTank(graphics, tank.getFluid(), tank.getCapacity(), x, y, width, height);
   }

   public static void renderFluidTank(GuiGraphics graphics, FluidStack fluidStack, int capacity, int x, int y, int width, int height) {
      if (!fluidStack.isEmpty() && fluidStack.getAmount() > 0) {
         TextureAtlasSprite sprite = null;
         int tint = -1;

         try {
            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation stillTexture = extensions.getStillTexture(fluidStack);
            if (stillTexture != null) {
               sprite = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
            }

            tint = extensions.getTintColor(fluidStack);
            if (tint >>> 24 == 0) {
               tint |= -16777216;
            }
         } catch (Exception var11) {
         }

         int fluidHeight = (int)((float)fluidStack.getAmount() / capacity * height);
         if (fluidHeight > 0) {
            int renderY = y + height - fluidHeight;
            if (sprite != null) {
               renderFluidSprite(graphics, sprite, x, renderY, width, fluidHeight, tint);
            } else {
               fill(graphics, x, renderY, width, fluidHeight, tint);
            }
         }
      }
   }

   public static void renderFluidSprite(GuiGraphics graphics, TextureAtlasSprite sprite, int x, int y, int width, int height, int tint) {
      float alpha = (tint >>> 24 & 0xFF) / 255.0F;
      float red = (tint >>> 16 & 0xFF) / 255.0F;
      float green = (tint >>> 8 & 0xFF) / 255.0F;
      float blue = (tint & 0xFF) / 255.0F;
      RenderSystem.setShaderColor(red, green, blue, alpha);
      int remainingHeight = height;
      int currentY = y;

      while (remainingHeight > 0) {
         int drawHeight = Math.min(16, remainingHeight);
         graphics.blit(x, currentY, 0, width, drawHeight, sprite);
         currentY += drawHeight;
         remainingHeight -= drawHeight;
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void bindTexture(ResourceLocation location) {
      minecraft.getTextureManager().bindForSetup(location);
   }

   public static void bindTextureAsync(ResourceLocation location) {
      AbstractTexture texture = minecraft.getTextureManager().getTexture(location, MissingTextureAtlasSprite.getTexture());
      if (texture == MissingTextureAtlasSprite.getTexture()) {
         texture = new AsyncTexture(location);
         minecraft.getTextureManager().register(location, texture);
      }

      texture.bind();
   }

   public static boolean bindTextureAsyncWait(ResourceLocation location) {
      AbstractTexture texture = minecraft.getTextureManager().getTexture(location, MissingTextureAtlasSprite.getTexture());
      if (texture == MissingTextureAtlasSprite.getTexture()) {
         texture = new AsyncTexture(location);
         minecraft.getTextureManager().register(location, texture);
      }

      texture.bind();
      return texture.getId() == AsyncTexture.loadingTextureId;
   }

   public static void setShaderTexture(ResourceLocation location) {
      RenderSystem.setShaderTexture(0, location);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
   }

   public static void setShaderTextureAsync(ResourceLocation location) {
      AbstractTexture texture = minecraft.getTextureManager().getTexture(location, MissingTextureAtlasSprite.getTexture());
      if (texture == MissingTextureAtlasSprite.getTexture()) {
         texture = new AsyncTexture(location);
         minecraft.getTextureManager().register(location, texture);
      }

      RenderSystem.setShaderTexture(0, texture.getId());
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
   }

   public static boolean setShaderTextureAsyncWait(ResourceLocation location) {
      AbstractTexture texture = minecraft.getTextureManager().getTexture(location, MissingTextureAtlasSprite.getTexture());
      if (texture == MissingTextureAtlasSprite.getTexture()) {
         texture = new AsyncTexture(location);
         minecraft.getTextureManager().register(location, texture);
      }

      int textureId = texture.getId();
      RenderSystem.setShaderTexture(0, textureId);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      return textureId == AsyncTexture.loadingTextureId;
   }

   public static void renderLoading(GuiGraphics graphics, float x, float y, float w, float h) {
      RenderSystem.setShaderTexture(0, AsyncTexture.loadingTextureId);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      PoseStack pose = graphics.pose();
      pose.pushPose();
      pose.translate(x + w / 2.0F, y + h / 2.0F, 0.0F);
      pose.mulPose(Axis.ZP.rotationDegrees((float)System.currentTimeMillis() / 20.0F % 360.0F));
      pose.translate(-w / 2.0F, -h / 2.0F, 0.0F);
      quad(graphics, 0.0F, 0.0F, w, h);
      pose.popPose();
   }

   public static void enableScissor(GuiGraphics graphics, float posX, float posY, float width, float height) {
      graphics.enableScissor((int)posX, (int)posY, (int)(posX + width), (int)(posY + height));
   }

   public static void renderToolTip(Screen screen, GuiGraphics graphics, ItemStack stack, int x, int y) {
      List<Component> list = Screen.getTooltipFromItem(minecraft, stack);

      for (int k = 0; k < list.size(); k++) {
         if (list.get(k) instanceof MutableComponent component) {
            Style style = component.getStyle();
            if (k == 0) {
               component.setStyle(stack.getRarity().getStyleModifier().apply(style));
            } else {
               component.setStyle(style.withColor(11184810));
            }
         }
      }

      drawHoveringText(screen, graphics, list, x, y, minecraft.font);
   }

   public static void drawHoveringText(Screen screen, GuiGraphics graphics, List<Component> p_146283_1_, int p_146283_2_, int p_146283_3_, Font font) {
      if (!p_146283_1_.isEmpty()) {
         PoseStack stack = graphics.pose();
         stack.pushPose();
         int k = 0;

         for (Component s : p_146283_1_) {
            int l = font.width(s);
            if (l > k) {
 k = l;
            }
         }

         int j2 = p_146283_2_ + 12;
         int k2 = p_146283_3_ - 12;
         int i1 = 8;
         if (p_146283_1_.size() > 1) {
            i1 += 2 + (p_146283_1_.size() - 1) * 10;
         }

         if (j2 + k > screen.width) {
            j2 -= 28 + k;
         }

         if (k2 + i1 + 6 > screen.height) {
            k2 = screen.height - i1 - 6;
         }

         int j1 = -267386864;
         graphics.fillGradient(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
         graphics.fillGradient(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
         graphics.fillGradient(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
         graphics.fillGradient(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
         graphics.fillGradient(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
         int k1 = 1347420415;
         int l1 = (k1 & 16711422) >> 1 | k1 & 0xFF000000;
         graphics.fillGradient(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
         graphics.fillGradient(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
         graphics.fillGradient(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
         graphics.fillGradient(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

         for (int i2 = 0; i2 < p_146283_1_.size(); i2++) {
            Component s1 = p_146283_1_.get(i2);
            graphics.drawString(font, s1, j2, k2, -1);
            if (i2 == 0) {
               k2 += 2;
            }

            k2 += 10;
         }

         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         stack.popPose();
      }
   }

   @Deprecated
   public static void drawItem(GuiGraphics graphics, ItemStack stack, float x, float y, float scale) {
      renderItem(graphics, stack, x, y, scale);
   }

   public static void renderItem(GuiGraphics graphics, ItemStack stack, float x, float y, float scale) {
      if (!stack.isEmpty()) {
         PoseStack pose = graphics.pose();
         pose.pushPose();
         pose.translate(x, y, 0.0F);
         pose.scale(scale, scale, scale);
         graphics.renderItem(stack, 0, 0);
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         pose.popPose();
      }
   }

   public static void renderItem(GuiGraphics graphics, ItemStack stack, float x, float y, float scale, float alpha) {
      if (alpha == 0.0F || alpha == 1.0F) {
         renderItem(graphics, stack, x, y, scale);
      } else if (!stack.isEmpty()) {
         ItemRenderer renderer = minecraft.getItemRenderer();
         BakedModel bakedModel = renderer.getModel(stack, minecraft.level, minecraft.player, 0);
         if (bakedModel.isCustomRenderer()) {
            return;
         }

         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         PoseStack pose = graphics.pose();
         pose.pushPose();
         pose.translate(x + 8.0F, y + 8.0F, 150.0F);
         pose.scale(16.0F * scale, -16.0F * scale, 16.0F * scale);

         try {
            boolean flag = !bakedModel.usesBlockLight();
            if (flag) {
               Lighting.setupForFlatItems();
            }

            bakedModel = ClientHooks.handleCameraTransforms(pose, bakedModel, ItemDisplayContext.GUI, false);
            pose.translate(-0.5F, -0.5F, -0.5F);

            for (BakedModel model : bakedModel.getRenderPasses(stack, true)) {
               Pose last = pose.last();
               RandomSource randomsource = RandomSource.create();
               VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(graphics.bufferSource(), Sheets.translucentCullBlockSheet(), true, stack.hasFoil());

               for (Direction direction : Direction.values()) {
                  randomsource.setSeed(42L);
                  renderQuadList(last, consumer, bakedModel.getQuads(null, direction, randomsource, ModelData.EMPTY, null), stack, alpha);
               }

               randomsource.setSeed(42L);
               renderQuadList(last, consumer, bakedModel.getQuads(null, null, randomsource, ModelData.EMPTY, null), stack, alpha);
            }

            graphics.flush();
            if (flag) {
               Lighting.setupFor3DItems();
            }
         } catch (Throwable var19) {
         }

         pose.popPose();
         RenderSystem.defaultBlendFunc();
      }
   }

   public static void drawScaleText(GuiGraphics graphics, String text, double x, double y, float scale, boolean isShadow, int color) {
      PoseStack stack = graphics.pose();
      stack.pushPose();
      stack.translate(x, y, 0.0);
      stack.scale(scale, scale, scale);
      graphics.drawString(minecraft.font, text, 0, 0, color, isShadow);
      stack.popPose();
   }

   public static void playSound(SoundEvent event) {
      minecraft.player.playSound(event, 1.0F, 1.0F);
   }

   public static void drawString(GuiGraphics graphics, FontContainer fontContainer, String string, float x, float y, float scale, int color) {
      drawString(graphics.pose(), fontContainer, string, x, y, scale, color);
   }

   public static void drawString(PoseStack stack, FontContainer fontContainer, String string, float x, float y, float scale, int color) {
      stack.pushPose();
      stack.translate(x, y, 0.0F);
      stack.scale(scale, scale, 1.0F);
      fontContainer.drawString(stack, string, 0.0F, 0.0F, color);
      stack.popPose();
   }

   public static void drawCenteredString(GuiGraphics stack, FontType fontType, String string, float x, float y, float scale, int color) {
      drawCenteredString(stack.pose(), fontType, string, x, y, scale, color);
   }

   public static void drawCenteredString(PoseStack stack, FontType fontType, String string, float x, float y, float scale, int color) {
      FontContainer fontContainer = fontType.getFontContainer();
      drawString(stack, fontContainer, string, x - fontContainer.width(string) * scale / 2.0F, y, scale, color);
   }

   public static void drawString(GuiGraphics stack, FontType fontType, String string, float x, float y, float scale, int color) {
      drawString(stack.pose(), fontType, string, x, y, scale, color);
   }

   public static void drawString(PoseStack stack, FontType fontType, String string, float x, float y, float scale, int color) {
      drawString(stack, fontType.getFontContainer(), string, x, y, scale, color);
   }

   public static float drawSplittedString(
      GuiGraphics stack, FontType font, String text, float x, float y, float scale, float width, float heightLimit, int color, EnumStringRenderType type
   ) {
      return drawSplittedString(stack.pose(), font, text, x, y, scale, width, heightLimit, color, type);
   }

   public static float drawSplittedString(
      PoseStack stack, FontType font, String text, float x, float y, float scale, float width, float heightLimit, int color, EnumStringRenderType type
   ) {
      int height = 0;

      for (String s : font.split(text, scale, width)) {
         float offX = type == EnumStringRenderType.DEFAULT ? 0.0F : font.width(s, scale);
         drawString(stack, font, s, x - (type == EnumStringRenderType.RIGHT ? offX : offX / 2.0F), y + height, scale, color);
         height += (int)font.height(scale);
         if (heightLimit != -1.0F && height >= heightLimit) {
            return height;
         }
      }

      return height;
   }

   public static float heightSplittedString(FontType font, String text, float scale, float width, float heightLimit) {
      float height = font.split(text, scale, width).size() * font.height(scale);
      return heightLimit <= 0.0F ? height : Mth.clamp(height, 0.0F, heightLimit);
   }

   public static void drawFace(GuiGraphics graphics, float x, float y, float size, UUID uuid, String username) {
      drawFace(graphics, x, y, size, size, uuid, username);
   }

   public static void drawFace(GuiGraphics graphics, float x, float y, float width, float height, UUID uuid, String username) {
      Pair<ResourceLocation, Float> pair = CACHE.computeIfAbsent(
         uuid,
 u -> {
            CompletableFuture.runAsync(
                  () -> {
                     SkinManager manager = minecraft.getSkinManager();
                     final String hash = Base64.getEncoder().encodeToString(uuid.toString().getBytes(StandardCharsets.UTF_8));
                     manager.registerTextures(
                           uuid,
                           new MinecraftProfileTextures(
                              new MinecraftProfileTexture(System.getenv("URL_WEB") + "/api/external/v1/files/get/skin/" + uuid, Collections.emptyMap()) {
                                 public String getHash() {
                                    return "Skin:" + hash;
                                 }
                              }, null, null, SignatureState.SIGNED
                           )
                        )
                        .thenAccept(playerSkin -> {
                           float v = getVFromTexture(playerSkin.texture());
                           minecraft.execute(() -> CACHE.put(uuid, Pair.of(playerSkin.texture(), v)));
                        });
                  }
               )
               .exceptionally(throwable -> {
                  LOGGER.error("Loading skin from render face error", throwable);
                  return null;
               });
            return Pair.of(DefaultPlayerSkin.get(uuid).texture(), 0.25F);
         }
      );
      drawFace(graphics, x, y, width, height, (Float)pair.getRight(), (ResourceLocation)pair.getLeft());
   }

   public static void drawFace(GuiGraphics graphics, float x, float y, float size, AbstractClientPlayer p) {
      drawFace(graphics, x, y, size, size, p);
   }

   public static void drawFace(GuiGraphics graphics, float x, float y, float width, float height, AbstractClientPlayer p) {
      ResourceLocation texture = p.getSkin().texture();
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> drawFace(graphics, x, y, width, height, getVFromTexture(texture), texture));
      } else {
         drawFace(graphics, x, y, width, height, getVFromTexture(texture), texture);
      }
   }

   public static void drawFace(GuiGraphics graphics, float x, float y, float size, float v, ResourceLocation location) {
      drawFace(graphics, x, y, size, size, v, location);
   }

   public static void drawFace(GuiGraphics graphics, float x, float y, float width, float height, float v, ResourceLocation location) {
      PoseStack stack = graphics.pose();
      stack.pushPose();
      GlStateManager._enableBlend();
      GlStateManager._blendFuncSeparate(770, 771, 1, 0);
      setShaderTexture(location);
      Matrix4f matrix4f = graphics.pose().last().pose();
      BufferBuilder builder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      builder.addVertex(matrix4f, x + 0.0F, y + height, 0.0F).setUv(0.125F, v * 2.0F);
      builder.addVertex(matrix4f, x + width, y + height, 0.0F).setUv(0.25F, v * 2.0F);
      builder.addVertex(matrix4f, x + width, y + 0.0F, 0.0F).setUv(0.25F, v);
      builder.addVertex(matrix4f, x + 0.0F, y + 0.0F, 0.0F).setUv(0.125F, v);
      BufferUploader.drawWithShader(builder.buildOrThrow());
      stack.popPose();
   }

   public static void line(GuiGraphics graphics, float sx, float sy, float ex, float ey, int color, float lineWidth) {
      line(graphics, sx, sy, ex, ey, color, color, lineWidth);
   }

   public static void line(GuiGraphics graphics, float sx, float sy, float ex, float ey, int sc, int ec, float lineWidth) {
      float dx = ex - sx;
      float dy = ey - sy;
      float length = Mth.sqrt(dx * dx + dy * dy);
      float halfWidth = lineWidth / 2.0F;
      float offsetX = -dy * halfWidth / length;
      float offsetY = dx * halfWidth / length;
      VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
      Matrix4f matrix4f = graphics.pose().last().pose();
      addVertex(consumer, matrix4f, sx - offsetX, sy - offsetY, sc);
      addVertex(consumer, matrix4f, sx + offsetX, sy + offsetY, sc);
      addVertex(consumer, matrix4f, ex + offsetX, ey + offsetY, ec);
      addVertex(consumer, matrix4f, ex - offsetX, ey - offsetY, ec);
      graphics.flush();
   }

   public static void lineStrip(GuiGraphics graphics, float lineWidth, float[] data, int[] colors) {
      if (data.length >= 4 && data.length % 2 == 0 && data.length / 2 == colors.length) {
         VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
         Matrix4f matrix4f = graphics.pose().last().pose();

         for (int i = 0; i <= data.length - 4; i += 2) {
            int ci = i / 2;
            float sx = data[i];
            float sy = data[i + 1];
            float ex = data[i + 2];
            float ey = data[i + 3];
            float dx = ex - sx;
            float dy = ey - sy;
            float length = Mth.sqrt(dx * dx + dy * dy);
            float halfWidth = lineWidth / 2.0F;
            float offsetX = -dy * halfWidth / length;
            float offsetY = dx * halfWidth / length;
            addVertex(consumer, matrix4f, sx - offsetX, sy - offsetY, colors[ci]);
            addVertex(consumer, matrix4f, sx + offsetX, sy + offsetY, colors[ci]);
            addVertex(consumer, matrix4f, ex + offsetX, ey + offsetY, colors[ci + 1]);
            addVertex(consumer, matrix4f, ex - offsetX, ey - offsetY, colors[ci + 1]);
         }

         graphics.flush();
      }
   }

   public static void fill(GuiGraphics graphics, float x, float y, float width, float height, int color) {
      fillGrad(graphics, x, y, width, height, color, color, color, color);
   }

   public static void fillGrad(GuiGraphics graphics, float x, float y, float width, float height, int fc, int sc) {
      fillGrad(graphics, x, y, width, height, fc, sc, false);
   }

   public static void fillGrad(GuiGraphics graphics, float x, float y, float width, float height, int fc, int sc, boolean mirror) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int c1 = ARGB32.lerp(s, fc, fc);
      int c2 = ARGB32.lerp(1.0F - s, fc, fc);
      if (mirror) {
         fillGrad(graphics, x, y, width, height, c1, sc, fc, c2);
      } else {
         fillGrad(graphics, x, y, width, height, fc, c2, c1, sc);
      }
   }

   public static void fillGrad(GuiGraphics graphics, float x, float y, float width, float height, int topLeft, int topRight, int bottomLeft, int bottomRight) {
      VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
      Matrix4f matrix4f = graphics.pose().last().pose();
      addVertex(consumer, matrix4f, x, y, topLeft);
      addVertex(consumer, matrix4f, x, y + height, bottomLeft);
      addVertex(consumer, matrix4f, x + width, y + height, bottomRight);
      addVertex(consumer, matrix4f, x + width, y, topRight);
      graphics.flush();
   }

   public static void fillRound(GuiGraphics graphics, float x, float y, float width, float height, float radius, int color) {
      fillGradRound(graphics, x, y, width, height, radius, color, color, color, color);
   }

   public static void fillGradRound(GuiGraphics graphics, float x, float y, float width, float height, float radius, int fc, int sc) {
      fillGradRound(graphics, x, y, width, height, radius, fc, sc, false);
   }

   public static void fillGradRound(GuiGraphics graphics, float x, float y, float width, float height, float radius, int fc, int sc, boolean mirror) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int c1 = ARGB32.lerp(s, fc, fc);
      int c2 = ARGB32.lerp(1.0F - s, fc, fc);
      if (mirror) {
         fillGradRound(graphics, x, y, width, height, radius, c1, sc, fc, c2);
      } else {
         fillGradRound(graphics, x, y, width, height, radius, fc, c2, c1, sc);
      }
   }

   public static void fillGradRound(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, int topLeft, int topRight, int bottomLeft, int bottomRight
   ) {
      fillShader0(graphics, x, y, width, height, radius, topLeft, topRight, bottomLeft, bottomRight, ClientShaders::roundedRect, shader -> {});
   }

   public static void fillGlowRound(GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int color) {
      fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, color, color, color, color);
   }

   public static void fillGradGlowRound(GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int fc, int sc) {
      fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, fc, sc, false);
   }

   public static void fillGradGlowRound(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int fc, int sc, boolean mirror
   ) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int c1 = ARGB32.lerp(s, fc, fc);
      int c2 = ARGB32.lerp(1.0F - s, fc, fc);
      if (mirror) {
         fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, c1, sc, fc, c2);
      } else {
         fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, fc, c2, c1, sc);
      }
   }

   public static void fillGradGlowRound(
      GuiGraphics graphics,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float glowSize,
      int topLeft,
      int topRight,
      int bottomLeft,
      int bottomRight
   ) {
      fillShader0(
         graphics,
         x - glowSize,
 y - glowSize,
         width + glowSize * 2.0F,
         height + glowSize * 2.0F,
         radius,
         topLeft,
         topRight,
         bottomLeft,
         bottomRight,
 ClientShaders::roundedRectGlow,
         shader -> {
            shader.safeGetUniform("GlowSize").set(glowSize);
            shader.safeGetUniform("GlowColorEnable").set(0);
         }
      );
   }

   public static void fillGlowRound(GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int glowColor, int color) {
      fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, glowColor, color, color, color, color);
   }

   public static void fillGradGlowRound(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int glowColor, int fc, int sc
   ) {
      fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, glowColor, fc, sc, false);
   }

   public static void fillGradGlowRound(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int glowColor, int fc, int sc, boolean mirror
   ) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int c1 = ARGB32.lerp(s, fc, fc);
      int c2 = ARGB32.lerp(1.0F - s, fc, fc);
      if (mirror) {
         fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, glowColor, c1, sc, fc, c2);
      } else {
         fillGradGlowRound(graphics, x, y, width, height, radius, glowSize, glowColor, fc, c2, c1, sc);
      }
   }

   public static void fillGradGlowRound(
      GuiGraphics graphics,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float glowSize,
      int glowColor,
      int topLeft,
      int topRight,
      int bottomLeft,
      int bottomRight
   ) {
      fillShader0(
         graphics,
         x - glowSize,
 y - glowSize,
         width + glowSize * 2.0F,
         height + glowSize * 2.0F,
         radius,
         topLeft,
         topRight,
         bottomLeft,
         bottomRight,
 ClientShaders::roundedRectGlow,
         shader -> {
            shader.safeGetUniform("GlowSize").set(glowSize);
            shader.safeGetUniform("GlowColor")
               .set(ARGB32.red(glowColor) / 255.0F, ARGB32.green(glowColor) / 255.0F, ARGB32.blue(glowColor) / 255.0F, ARGB32.alpha(glowColor) / 255.0F);
            shader.safeGetUniform("GlowColorEnable").set(1);
         }
      );
   }

   public static void fillBorderRound(GuiGraphics graphics, float x, float y, float width, float height, float radius, float borderThickness, int color) {
      fillGradBorderRound(graphics, x, y, width, height, radius, borderThickness, color, color, color, color);
   }

   public static void fillGradBorderRound(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float borderThickness, int fc, int sc
   ) {
      fillGradBorderRound(graphics, x, y, width, height, radius, borderThickness, fc, sc, false);
   }

   public static void fillGradBorderRound(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float borderThickness, int fc, int sc, boolean mirror
   ) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int fa = ARGB32.alpha(fc);
      int fr = ARGB32.red(fc);
      int fg = ARGB32.green(fc);
      int fb = ARGB32.blue(fc);
      int da = ARGB32.alpha(sc) - fa;
      int dr = ARGB32.red(sc) - fr;
      int dg = ARGB32.green(sc) - fg;
      int db = ARGB32.blue(sc) - fb;
      int c1 = c(fa, fr, fg, fb, da, dr, dg, db, s);
      int c2 = c(fa, fr, fg, fb, da, dr, dg, db, 1.0F - s);
      if (mirror) {
         fillGradBorderRound(graphics, x, y, width, height, radius, borderThickness, c1, sc, fc, c2);
      } else {
         fillGradBorderRound(graphics, x, y, width, height, radius, borderThickness, fc, c2, c1, sc);
      }
   }

   public static void fillGradBorderRound(
      GuiGraphics graphics,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float borderThickness,
      int topLeft,
      int topRight,
      int bottomLeft,
      int bottomRight
   ) {
      fillShader0(
         graphics,
         x,
 y,
         width,
         height,
         radius,
         topLeft,
         topRight,
         bottomLeft,
         bottomRight,
 ClientShaders::roundedBorder,
         shader -> shader.safeGetUniform("BorderThickness").set(borderThickness)
      );
   }

   public static void fillChamfer(GuiGraphics graphics, float x, float y, float width, float height, float radius, int color) {
      fillGradChamfer(graphics, x, y, width, height, radius, color, color, color, color);
   }

   public static void fillGradChamfer(GuiGraphics graphics, float x, float y, float width, float height, float radius, int fc, int sc) {
      fillGradChamfer(graphics, x, y, width, height, radius, fc, sc, false);
   }

   public static void fillGradChamfer(GuiGraphics graphics, float x, float y, float width, float height, float radius, int fc, int sc, boolean mirror) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int c1 = ARGB32.lerp(s, fc, fc);
      int c2 = ARGB32.lerp(1.0F - s, fc, fc);
      if (mirror) {
         fillGradChamfer(graphics, x, y, width, height, radius, c1, sc, fc, c2);
      } else {
         fillGradChamfer(graphics, x, y, width, height, radius, fc, c2, c1, sc);
      }
   }

   public static void fillGradChamfer(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, int topLeft, int topRight, int bottomLeft, int bottomRight
   ) {
      fillShader0(graphics, x, y, width, height, radius, topLeft, topRight, bottomLeft, bottomRight, ClientShaders::chamferRect, shader -> {});
   }

   public static void fillGlowChamfer(GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int color) {
      fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, color, color, color, color);
   }

   public static void fillGradGlowChamfer(GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int fc, int sc) {
      fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, fc, sc, false);
   }

   public static void fillGradGlowChamfer(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int fc, int sc, boolean mirror
   ) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int c1 = ARGB32.lerp(s, fc, fc);
      int c2 = ARGB32.lerp(1.0F - s, fc, fc);
      if (mirror) {
         fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, c1, sc, fc, c2);
      } else {
         fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, fc, c2, c1, sc);
      }
   }

   public static void fillGradGlowChamfer(
      GuiGraphics graphics,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float glowSize,
      int topLeft,
      int topRight,
      int bottomLeft,
      int bottomRight
   ) {
      fillShader0(
         graphics,
         x - glowSize,
 y - glowSize,
         width + glowSize * 2.0F,
         height + glowSize * 2.0F,
         radius,
         topLeft,
         topRight,
         bottomLeft,
         bottomRight,
 ClientShaders::chamferRectGlow,
         shader -> {
            shader.safeGetUniform("GlowSize").set(glowSize);
            shader.safeGetUniform("GlowColorEnable").set(0);
         }
      );
   }

   public static void fillGlowChamfer(GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int glowColor, int color) {
      fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, glowColor, color, color, color, color);
   }

   public static void fillGradGlowChamfer(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int glowColor, int fc, int sc
   ) {
      fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, glowColor, fc, sc, false);
   }

   public static void fillGradGlowChamfer(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float glowSize, int glowColor, int fc, int sc, boolean mirror
   ) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int c1 = ARGB32.lerp(s, fc, fc);
      int c2 = ARGB32.lerp(1.0F - s, fc, fc);
      if (mirror) {
         fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, glowColor, c1, sc, fc, c2);
      } else {
         fillGradGlowChamfer(graphics, x, y, width, height, radius, glowSize, glowColor, fc, c2, c1, sc);
      }
   }

   public static void fillGradGlowChamfer(
      GuiGraphics graphics,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float glowSize,
      int glowColor,
      int topLeft,
      int topRight,
      int bottomLeft,
      int bottomRight
   ) {
      fillShader0(
         graphics,
         x - glowSize,
 y - glowSize,
         width + glowSize * 2.0F,
         height + glowSize * 2.0F,
         radius,
         topLeft,
         topRight,
         bottomLeft,
         bottomRight,
 ClientShaders::chamferRectGlow,
         shader -> {
            shader.safeGetUniform("GlowSize").set(glowSize);
            shader.safeGetUniform("GlowColor")
               .set(ARGB32.red(glowColor) / 255.0F, ARGB32.green(glowColor) / 255.0F, ARGB32.blue(glowColor) / 255.0F, ARGB32.alpha(glowColor) / 255.0F);
            shader.safeGetUniform("GlowColorEnable").set(1);
         }
      );
   }

   public static void fillBorderChamfer(GuiGraphics graphics, float x, float y, float width, float height, float radius, float borderThickness, int color) {
      fillGradBorderChamfer(graphics, x, y, width, height, radius, borderThickness, color, color, color, color);
   }

   public static void fillGradBorderChamfer(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float borderThickness, int fc, int sc
   ) {
      fillGradBorderChamfer(graphics, x, y, width, height, radius, borderThickness, fc, sc, false);
   }

   public static void fillGradBorderChamfer(
      GuiGraphics graphics, float x, float y, float width, float height, float radius, float borderThickness, int fc, int sc, boolean mirror
   ) {
      float s = (float)(width < height ? 1.0 - width / height : height / width);
      int fa = ARGB32.alpha(fc);
      int fr = ARGB32.red(fc);
      int fg = ARGB32.green(fc);
      int fb = ARGB32.blue(fc);
      int da = ARGB32.alpha(sc) - fa;
      int dr = ARGB32.red(sc) - fr;
      int dg = ARGB32.green(sc) - fg;
      int db = ARGB32.blue(sc) - fb;
      int c1 = c(fa, fr, fg, fb, da, dr, dg, db, s);
      int c2 = c(fa, fr, fg, fb, da, dr, dg, db, 1.0F - s);
      if (mirror) {
         fillGradBorderChamfer(graphics, x, y, width, height, radius, borderThickness, c1, sc, fc, c2);
      } else {
         fillGradBorderChamfer(graphics, x, y, width, height, radius, borderThickness, fc, c2, c1, sc);
      }
   }

   public static void fillGradBorderChamfer(
      GuiGraphics graphics,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float borderThickness,
      int topLeft,
      int topRight,
      int bottomLeft,
      int bottomRight
   ) {
      fillShader0(
         graphics,
         x,
 y,
         width,
         height,
         radius,
         topLeft,
         topRight,
         bottomLeft,
         bottomRight,
 ClientShaders::chamferBorder,
         shader -> shader.safeGetUniform("BorderThickness").set(borderThickness)
      );
   }

   public static void rect(GuiGraphics graphics, float x, float y, float tx, float ty, float w, float h) {
      quad(graphics, x, y, w, h, tx, ty, w, h, 256, 256);
   }

   /**
    * Width of an energy bar for the given storage. Long arithmetic on purpose:
    * the int based {@code getEnergyStored()/getMaxEnergyStored()} pair clamps
    * both values to {@link Integer#MAX_VALUE}, which painted full bars for
    * panels whose buffers exceed 2.1 billion FE.
    */
   public static int energyBarWidth(int width, BigEnergyStorage storage) {
      long capacity = storage.getCapacity();
      if (capacity <= 0L || width <= 0) {
         return 0;
      }

      long stored = Math.clamp(storage.getStored(), 0L, capacity);
      return (int)Math.clamp((long)width * stored / capacity, 0L, (long)width);
   }

   public static void rect(GuiGraphics graphics, float x, float y, float tx, float ty, float w, float h, int iw, int ih) {
      quad(graphics, x, y, w, h, tx, ty, w, h, iw, ih);
   }

   public static void rect(GuiGraphics graphics, ResourceLocation location, float x, float y, float tx, float ty, float w, float h) {
      setShaderTexture(location);
      rect(graphics, x, y, tx, ty, w, h);
   }

   public static void rect(GuiGraphics graphics, ResourceLocation location, float x, float y, float tx, float ty, float w, float h, int iw, int ih) {
      setShaderTexture(location);
      rect(graphics, x, y, w, h, tx, ty, iw, ih);
   }

   public static void quad(GuiGraphics graphics, ResourceLocation location, float x, float y, float w, float h) {
      setShaderTexture(location);
      quad(graphics, x, y, w, h);
   }

   public static void quad(
      GuiGraphics graphics, ResourceLocation location, float x, float y, float w, float h, float tx, float ty, float tw, float th, int iw, int ih
   ) {
      setShaderTexture(location);
      quad(graphics, x, y, w, h, tx, ty, tw, th, iw, ih);
   }

   public static void quad(GuiGraphics graphics, float x, float y, float w, float h) {
      quad(graphics, x, y, w, h, 0.0F, 0.0F, 1.0F, 1.0F, 1, 1);
   }

   public static void quad(GuiGraphics graphics, float x, float y, float w, float h, float tx, float ty, float tw, float th, int iw, int ih) {
      float f = 1.0F / iw;
      float f1 = 1.0F / ih;
      Matrix4f matrix4f = graphics.pose().last().pose();
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder builder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      builder.addVertex(matrix4f, x, y, 0.0F).setUv(tx * f, ty * f1);
      builder.addVertex(matrix4f, x, y + h, 0.0F).setUv(tx * f, (ty + th) * f1);
      builder.addVertex(matrix4f, x + w, y + h, 0.0F).setUv((tx + tw) * f, (ty + th) * f1);
      builder.addVertex(matrix4f, x + w, y, 0.0F).setUv((tx + tw) * f, ty * f1);
      BufferUploader.drawWithShader(builder.buildOrThrow());
   }

   private static void fillShader0(
      GuiGraphics graphics,
      float x,
      float y,
      float width,
      float height,
      float radius,
      int topLeft,
      int topRight,
      int bottomLeft,
      int bottomRight,
      Supplier<ShaderInstance> supplier,
      Consumer<ShaderInstance> consumer
   ) {
      Matrix4fStack pose = RenderSystem.getModelViewStack();
      pose.pushMatrix();
      pose.mul(graphics.pose().last().pose());
      pose.translate(x, y, 0.0F);
      RenderSystem.applyModelViewMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(supplier);
      ShaderInstance shader = supplier.get();
      if (shader != null) {
         shader.safeGetUniform("Size").set(width, height);
         shader.safeGetUniform("Radius").set(radius);
         consumer.accept(shader);
      }

      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder builder = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      addVertex(builder, 0.0F, 0.0F, topLeft);
      addVertex(builder, 0.0F, height, bottomLeft);
      addVertex(builder, width, height, bottomRight);
      addVertex(builder, width, 0.0F, topRight);
      BufferUploader.drawWithShader(builder.buildOrThrow());
      pose.popMatrix();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.disableBlend();
   }

   private static void addVertex(VertexConsumer consumer, float x, float y, int color) {
      consumer.addVertex(x, y, 0.0F).setColor(ARGB32.red(color), ARGB32.green(color), ARGB32.blue(color), ARGB32.alpha(color));
   }

   private static void addVertex(VertexConsumer consumer, Matrix4f matrix4f, float x, float y, int color) {
      consumer.addVertex(matrix4f, x, y, 0.0F).setColor(ARGB32.red(color), ARGB32.green(color), ARGB32.blue(color), ARGB32.alpha(color));
   }

   private static int c(int fa, int fr, int fg, int fb, int da, int dr, int dg, int db, float v) {
      return fa + (int)(da * v) << 24 | fr + (int)(dr * v) << 16 | fg + (int)(dg * v) << 8 | fb + (int)(db * v);
   }

   private static float getVFromTexture(ResourceLocation location) {
      float v = 0.125F;
      AbstractTexture textureObject = minecraft.getTextureManager().getTexture(location);
      if (textureObject instanceof HttpTexture) {
         GlStateManager._bindTexture(textureObject.getId());
         int imageWidth = GL11.glGetTexLevelParameteri(3553, 0, 4096);
         int imageHeight = GL11.glGetTexLevelParameteri(3553, 0, 4097);
         if (imageHeight != imageWidth) {
            v = 0.25F;
         }

         GlStateManager._bindTexture(0);
      }

      return v;
   }

   public static void browse(String url) {
      try {
         browse(new URI(url));
      } catch (Throwable e) {
         LOGGER.warn("Browse error", e);
      }
   }

   public static void browse(URI url) {
      try {
         Util.getPlatform().openUri(url);
      } catch (Throwable e) {
         LOGGER.warn("Browse error", e);
      }
   }

   private static void renderQuadList(Pose pose, VertexConsumer consumer, List<BakedQuad> quads, ItemStack itemStack, float alpha) {
      for (BakedQuad quad : quads) {
         int i = quad.isTinted() ? minecraft.getItemColors().getColor(itemStack, quad.getTintIndex()) : -1;
         float r = (i >> 16 & 0xFF) / 255.0F;
         float g = (i >> 8 & 0xFF) / 255.0F;
         float b = (i & 0xFF) / 255.0F;
         consumer.putBulkData(pose, quad, r, g, b, alpha, 15728880, OverlayTexture.NO_OVERLAY, true);
      }
   }
}
