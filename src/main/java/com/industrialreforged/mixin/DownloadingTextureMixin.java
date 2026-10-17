package com.industrialreforged.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.HttpTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HttpTexture.class)
public abstract class DownloadingTextureMixin {
   @Overwrite
   private NativeImage processLegacySkin(NativeImage p_229163_0_) {
      boolean flag = p_229163_0_.getHeight() == p_229163_0_.getWidth() / 2;
      int t = p_229163_0_.getWidth() / 64;
      if (flag) {
         NativeImage nativeimage = new NativeImage(64 * t, 64 * t, true);
         nativeimage.copyFrom(p_229163_0_);
         p_229163_0_.close();
         p_229163_0_ = nativeimage;
         nativeimage.fillRect(0, 32 * t, 64 * t, 32 * t, 0);
         nativeimage.copyRect(4 * t, 16 * t, 16 * t, 32 * t, 4 * t, 4 * t, true, false);
         nativeimage.copyRect(8 * t, 16 * t, 16 * t, 32 * t, 4 * t, 4 * t, true, false);
         nativeimage.copyRect(0, 20 * t, 24 * t, 32 * t, 4 * t, 12 * t, true, false);
         nativeimage.copyRect(4 * t, 20 * t, 16 * t, 32 * t, 4 * t, 12 * t, true, false);
         nativeimage.copyRect(8 * t, 20 * t, 8 * t, 32 * t, 4 * t, 12 * t, true, false);
         nativeimage.copyRect(12 * t, 20 * t, 16 * t, 32 * t, 4 * t, 12 * t, true, false);
         nativeimage.copyRect(44 * t, 16 * t, -8 * t, 32 * t, 4 * t, 4 * t, true, false);
         nativeimage.copyRect(48 * t, 16 * t, -8 * t, 32 * t, 4 * t, 4 * t, true, false);
         nativeimage.copyRect(40 * t, 20 * t, 0, 32 * t, 4 * t, 12 * t, true, false);
         nativeimage.copyRect(44 * t, 20 * t, -8 * t, 32 * t, 4 * t, 12 * t, true, false);
         nativeimage.copyRect(48 * t, 20 * t, -16 * t, 32 * t, 4 * t, 12 * t, true, false);
         nativeimage.copyRect(52 * t, 20 * t, -8 * t, 32 * t, 4 * t, 12 * t, true, false);
      }

      setNoAlpha(p_229163_0_, 0, 0, 32 * t, 16 * t);
      if (flag) {
         doNotchTransparencyHack(p_229163_0_, 32 * t, 0, 64 * t, 32 * t);
      }

      setNoAlpha(p_229163_0_, 0, 16 * t, 64 * t, 32 * t);
      setNoAlpha(p_229163_0_, 16 * t, 48 * t, 48 * t, 64 * t);
      return p_229163_0_;
   }

   @Shadow
   private static void doNotchTransparencyHack(NativeImage p_229158_0_, int p_229158_1_, int p_229158_2_, int p_229158_3_, int p_229158_4_) {
      for (int i = p_229158_1_; i < p_229158_3_; i++) {
         for (int j = p_229158_2_; j < p_229158_4_; j++) {
            int k = p_229158_0_.getPixelRGBA(i, j);
            if ((k >> 24 & 0xFF) < 128) {
               return;
            }
         }
      }

      for (int l = p_229158_1_; l < p_229158_3_; l++) {
         for (int i1 = p_229158_2_; i1 < p_229158_4_; i1++) {
            p_229158_0_.setPixelRGBA(l, i1, p_229158_0_.getPixelRGBA(l, i1) & 16777215);
         }
      }
   }

   @Shadow
   private static void setNoAlpha(NativeImage p_229161_0_, int p_229161_1_, int p_229161_2_, int p_229161_3_, int p_229161_4_) {
      for (int i = p_229161_1_; i < p_229161_3_; i++) {
         for (int j = p_229161_2_; j < p_229161_4_; j++) {
            p_229161_0_.setPixelRGBA(i, j, p_229161_0_.getPixelRGBA(i, j) | 0xFF000000);
         }
      }
   }
}
