package com.industrialreforged.core;

import com.industrialreforged.IndustrialReforged;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber(modid = "industrialreforged", value = Dist.CLIENT)
public class ClientShaders {
   private static ShaderInstance roundedRect;
   private static ShaderInstance roundedRectGlow;
   private static ShaderInstance roundedBorder;
   private static ShaderInstance chamferRect;
   private static ShaderInstance chamferRectGlow;
   private static ShaderInstance chamferBorder;

   @SubscribeEvent
   public static void onPlayerTick(Post e) {
      Player player = e.getEntity();
      if (player == Minecraft.getInstance().player) {
         BlockPos pos = player.getOnPos();
         Level level = player.level();
         if (!level.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
            player.setDeltaMovement(0.0, 0.0, 0.0);
         }
      }
   }

   @SubscribeEvent
   public static void onRegisterShaders(RegisterShadersEvent event) {
      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), IndustrialReforged.location("rounded_rect"), DefaultVertexFormat.POSITION_COLOR),
            shaderInstance -> roundedRect = shaderInstance
         );
      } catch (IOException e) {
         throw new RuntimeException("Failed to register rounded gradient shader", e);
      }

      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), IndustrialReforged.location("rounded_rect_glow"), DefaultVertexFormat.POSITION_COLOR),
            shaderInstance -> roundedRectGlow = shaderInstance
         );
      } catch (IOException e) {
         throw new RuntimeException("Failed to register rounded gradient shader", e);
      }

      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), IndustrialReforged.location("rounded_border"), DefaultVertexFormat.POSITION_COLOR),
            shaderInstance -> roundedBorder = shaderInstance
         );
      } catch (IOException e) {
         throw new RuntimeException("Failed to register rounded border shader", e);
      }

      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), IndustrialReforged.location("chamfer_rect"), DefaultVertexFormat.POSITION_COLOR),
            shaderInstance -> chamferRect = shaderInstance
         );
      } catch (IOException e) {
         throw new RuntimeException("Failed to register chamfer gradient shader", e);
      }

      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), IndustrialReforged.location("chamfer_rect_glow"), DefaultVertexFormat.POSITION_COLOR),
            shaderInstance -> chamferRectGlow = shaderInstance
         );
      } catch (IOException e) {
         throw new RuntimeException("Failed to register chamfer gradient shader", e);
      }

      try {
         event.registerShader(
            new ShaderInstance(event.getResourceProvider(), IndustrialReforged.location("chamfer_border"), DefaultVertexFormat.POSITION_COLOR),
            shaderInstance -> chamferBorder = shaderInstance
         );
      } catch (IOException e) {
         throw new RuntimeException("Failed to register chamfer border shader", e);
      }
   }

   public static ShaderInstance roundedRect() {
      return roundedRect;
   }

   public static ShaderInstance roundedRectGlow() {
      return roundedRectGlow;
   }

   public static ShaderInstance roundedBorder() {
      return roundedBorder;
   }

   public static ShaderInstance chamferRect() {
      return chamferRect;
   }

   public static ShaderInstance chamferRectGlow() {
      return chamferRectGlow;
   }

   public static ShaderInstance chamferBorder() {
      return chamferBorder;
   }
}
