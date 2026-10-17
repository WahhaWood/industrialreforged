package com.industrialreforged.core;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AsyncTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   public static int loadingTextureId;
   protected final ResourceLocation location;
   private boolean uploaded;

   public AsyncTexture(ResourceLocation location) {
      this.location = location;
   }

   public int getId() {
      return this.uploaded ? super.getId() : loadingTextureId;
   }

   public void load(ResourceManager manager) throws IOException {
      this.releaseId();
      Resource resource = manager.getResourceOrThrow(this.location);
      CompletableFuture.runAsync(() -> {
         try {
            NativeImage nativeimage;
            try (InputStream inputstream = resource.open()) {
               nativeimage = NativeImage.read(inputstream);
            }

            TextureMetadataSection texturemetadatasection = null;

            try {
               texturemetadatasection = (TextureMetadataSection)resource.metadata().getSection(TextureMetadataSection.SERIALIZER).orElse(null);
            } catch (RuntimeException runtimeexception) {
               LOGGER.warn("Failed reading metadata of: {}", this.location, runtimeexception);
            }

            boolean flag;
            boolean flag1;
            if (texturemetadatasection != null) {
               flag = texturemetadatasection.isBlur();
               flag1 = texturemetadatasection.isClamp();
            } else {
               flag = false;
               flag1 = false;
            }

            if (!RenderSystem.isOnRenderThreadOrInit()) {
               RenderSystem.recordRenderCall(() -> this.upload(nativeimage, flag, flag1));
            } else {
               this.upload(nativeimage, flag, flag1);
            }
         } catch (Exception e) {
            LOGGER.warn("Failed to load texture: " + this.location, e);
            this.id = MissingTextureAtlasSprite.getTexture().getId();
            this.uploaded = true;
         }
      });
   }

   private void upload(NativeImage p_118137_, boolean p_118138_, boolean p_118139_) {
      TextureUtil.prepareImage(this.getId(), 0, p_118137_.getWidth(), p_118137_.getHeight());
      p_118137_.upload(0, 0, 0, 0, 0, p_118137_.getWidth(), p_118137_.getHeight(), p_118138_, p_118139_, false, true);
      this.uploaded = true;
   }
}
