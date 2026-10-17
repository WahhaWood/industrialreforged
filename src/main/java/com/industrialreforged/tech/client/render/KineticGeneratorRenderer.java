package com.industrialreforged.tech.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.industrialreforged.tech.api.tile.IRotorProvider;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class KineticGeneratorRenderer<T extends BlockEntity & IRotorProvider> implements BlockEntityRenderer<T> {
   private static final Map<Integer, ModelPart> rotorModels = new HashMap<>();

   public KineticGeneratorRenderer(Context context) {
   }

   public void render(@NotNull T t, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
      int n3 = t.getRotorDiameter();
      if (n3 != 0) {
         float f2 = t.getAngle();
         ResourceLocation resourceLocation = t.getRotorRenderTexture();
         ModelPart modelPart = rotorModels.get(n3);
         if (modelPart == null) {
            MeshDefinition meshDefinition = new MeshDefinition();
            PartDefinition partDefinition = meshDefinition.getRoot();
            partDefinition.addOrReplaceChild(
               "1",
               CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -4.0F, 1.0F, n3 * 8, 8.0F),
               PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, -0.5F, 0.0F)
            );
            partDefinition.addOrReplaceChild(
               "2",
               CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -4.0F, 1.0F, n3 * 8, 8.0F),
               PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 3.1F, 0.5F, 0.0F)
            );
            partDefinition.addOrReplaceChild(
               "3",
               CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -4.0F, 1.0F, n3 * 8, 8.0F),
               PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 4.7F, 0.0F, 0.5F)
            );
            partDefinition.addOrReplaceChild(
               "4",
               CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, -4.0F, 1.0F, n3 * 8, 8.0F),
               PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 1.5F, 0.0F, -0.5F)
            );
            modelPart = LayerDefinition.create(meshDefinition, 32, 256).bakeRoot();
            rotorModels.put(n3, modelPart);
         }

         Direction facing = t.getFacing();
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         switch (facing) {
            case NORTH:
               poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
               break;
            case EAST:
               poseStack.mulPose(Axis.YP.rotationDegrees(-180.0F));
               break;
            case SOUTH:
               poseStack.mulPose(Axis.YP.rotationDegrees(-270.0F));
               break;
            case UP:
               poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
         }

         poseStack.mulPose(Axis.XP.rotationDegrees(f2));
         poseStack.translate(-0.2, 0.0, 0.0);
 n = LevelRenderer.getLightColor(t.getLevel(), t.getBlockPos().relative(facing));
         modelPart.render(poseStack, multiBufferSource.getBuffer(RenderType.entitySolid(resourceLocation)), n, n2);
         poseStack.popPose();
      }
   }
}
