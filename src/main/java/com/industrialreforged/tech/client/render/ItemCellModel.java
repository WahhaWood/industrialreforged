package com.industrialreforged.tech.client.render;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ItemCellModel extends SimpleBakedModel {
   public ItemCellModel(
      List<BakedQuad> unculledFaces,
      Map<Direction, List<BakedQuad>> culledFaces,
      boolean hasAmbientOcclusion,
      boolean isGui3d,
      boolean usesBlockLight,
      TextureAtlasSprite particleIcon,
      ItemTransforms transforms,
      ItemOverrides overrides,
      RenderTypeGroup renderTypes
   ) {
      super(unculledFaces, culledFaces, hasAmbientOcclusion, isGui3d, usesBlockLight, particleIcon, transforms, overrides, renderTypes);
   }

   @NotNull
   public List<BakedQuad> getQuads(@Nullable BlockState p_235054_, @Nullable Direction p_235055_, @NotNull RandomSource p_235056_) {
      return p_235055_ == null ? this.unculledFaces : (List)this.culledFaces.get(p_235055_);
   }
}
