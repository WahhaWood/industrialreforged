package com.industrialreforged.transfer.render;

import com.industrialreforged.transfer.util.ConnectionType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public final class WireBakedModel implements IDynamicBakedModel {
   private static final Map<String, BlockElement> ELEMENTS = new HashMap<>();
   private static final int CACHE_QUAD_COUNT = 5000;
   private final IGeometryBakingContext context;
   private final LoadingCache<WireRenderState, List<BakedQuad>> cableModelCache;
   private final TextureAtlasSprite particleTexture;
   private final TextureAtlasSprite resistorTexture;
   private final TextureAtlasSprite transformerTexture;
   private final ChunkRenderTypeSet renderTypes;

   WireBakedModel(IGeometryBakingContext context, TextureAtlasSprite particleTexture, TextureAtlasSprite resistorTexture, TextureAtlasSprite transformerTexture) {
      this.context = context;
      this.particleTexture = particleTexture;
      this.resistorTexture = resistorTexture;
      this.transformerTexture = transformerTexture;
      this.renderTypes = ChunkRenderTypeSet.of(new RenderType[]{RenderType.cutout()});
      this.cableModelCache = CacheBuilder.<WireRenderState, List<BakedQuad>>newBuilder()
         .maximumWeight(5000L)
         .weigher((WireRenderState key, List<BakedQuad> value) -> value.size())
         .build(new CacheLoader<WireRenderState, List<BakedQuad>>() {
            public List<BakedQuad> load(WireRenderState renderState) {
               List<BakedQuad> model = new ArrayList<>();
               WireBakedModel.this.addCableQuads(renderState, model);
               return model;
            }
         });
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
      WireRenderState renderState = (WireRenderState)data.get(WireRenderState.PROPERTY);
      if (renderState == null || side != null) {
         return Collections.emptyList();
      } else {
         return renderType != null && renderType != RenderType.cutout() ? Collections.emptyList() : (List)this.cableModelCache.getUnchecked(renderState);
      }
   }

   public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
      return this.renderTypes;
   }

   public boolean useAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean usesBlockLight() {
      return false;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particleTexture;
   }

   public ItemOverrides getOverrides() {
      return ItemOverrides.EMPTY;
   }

   private void addCableQuads(WireRenderState renderState, List<BakedQuad> quadsOut) {
      EnumMap<Direction, ConnectionType> connectionTypes = renderState.getConnectionTypes();
      this.addQuads(quadsOut, ELEMENTS.get("center"), this.particleTexture);

      for (Entry<Direction, ConnectionType> entry : connectionTypes.entrySet()) {
         ConnectionType connectionType = entry.getValue();
         if (connectionType != ConnectionType.NONE) {
            Direction direction = entry.getKey();
            BlockElement element = ELEMENTS.get(direction.getName());
            switch (connectionType) {
               case DEFAULT:
               case BLOCK:
                  this.addQuads(quadsOut, element, this.particleTexture);
                  break;
               case RESISTOR:
                  this.addQuads(quadsOut, element, this.resistorTexture);
                  break;
               case TRANSFORMER:
                  this.addQuads(quadsOut, element, this.transformerTexture);
            }
         }
      }
   }

   private void addQuads(List<BakedQuad> quads, BlockElement element, TextureAtlasSprite sprite) {
      BlockModelRotation modelState = BlockModelRotation.X0_Y0;
      IQuadTransformer postTransform = QuadTransformers.empty();
      Transformation rootTransform = this.context.getRootTransform();
      if (!rootTransform.isIdentity()) {
         postTransform = UnbakedGeometryHelper.applyRootTransform(modelState, rootTransform);
      }

      for (Direction direction : element.faces.keySet()) {
         BlockElementFace face = (BlockElementFace)element.faces.get(direction);
         BakedQuad quad = BlockModel.bakeFace(element, face, sprite, direction, modelState);
         postTransform.processInPlace(quad);
         quads.add(quad);
      }
   }

   private static BlockElementFace create(Direction direction, float[] uvs) {
      return new BlockElementFace(direction, 0, "#0", new BlockFaceUV(uvs, 0));
   }

   static {
      ELEMENTS.put(
         "center",
         new BlockElement(
            new Vector3f(6.0F, 6.0F, 6.0F),
            new Vector3f(10.0F, 10.0F, 10.0F),
            Map.of(
               Direction.NORTH,
               create(Direction.NORTH, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.EAST,
               create(Direction.EAST, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.SOUTH,
               create(Direction.SOUTH, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.WEST,
               create(Direction.WEST, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.UP,
               create(Direction.UP, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.DOWN,
               create(Direction.DOWN, new float[]{6.0F, 6.0F, 10.0F, 10.0F})
            ),
            null,
            false
         )
      );
      ELEMENTS.put(
         "down",
         new BlockElement(
            new Vector3f(6.0F, 0.0F, 6.0F),
            new Vector3f(10.0F, 6.0F, 10.0F),
            Map.of(
               Direction.NORTH,
               create(Direction.NORTH, new float[]{6.0F, 10.0F, 10.0F, 16.0F}),
               Direction.EAST,
               create(Direction.EAST, new float[]{6.0F, 10.0F, 10.0F, 16.0F}),
               Direction.SOUTH,
               create(Direction.SOUTH, new float[]{6.0F, 10.0F, 10.0F, 16.0F}),
               Direction.WEST,
               create(Direction.WEST, new float[]{6.0F, 10.0F, 10.0F, 16.0F}),
               Direction.UP,
               create(Direction.UP, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.DOWN,
               create(Direction.DOWN, new float[]{6.0F, 6.0F, 10.0F, 10.0F})
            ),
            null,
            false
         )
      );
      ELEMENTS.put(
         "up",
         new BlockElement(
            new Vector3f(6.0F, 10.0F, 6.0F),
            new Vector3f(10.0F, 16.0F, 10.0F),
            Map.of(
               Direction.NORTH,
               create(Direction.NORTH, new float[]{6.0F, 0.0F, 10.0F, 6.0F}),
               Direction.EAST,
               create(Direction.EAST, new float[]{6.0F, 0.0F, 10.0F, 6.0F}),
               Direction.SOUTH,
               create(Direction.SOUTH, new float[]{6.0F, 0.0F, 10.0F, 6.0F}),
               Direction.WEST,
               create(Direction.WEST, new float[]{6.0F, 0.0F, 10.0F, 6.0F}),
               Direction.UP,
               create(Direction.UP, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.DOWN,
               create(Direction.DOWN, new float[]{6.0F, 6.0F, 10.0F, 10.0F})
            ),
            null,
            false
         )
      );
      ELEMENTS.put(
         "north",
         new BlockElement(
            new Vector3f(6.0F, 6.0F, 0.0F),
            new Vector3f(10.0F, 10.0F, 6.0F),
            Map.of(
               Direction.NORTH,
               create(Direction.NORTH, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.EAST,
               create(Direction.EAST, new float[]{10.0F, 6.0F, 16.0F, 10.0F}),
               Direction.SOUTH,
               create(Direction.SOUTH, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.WEST,
               create(Direction.WEST, new float[]{0.0F, 6.0F, 6.0F, 10.0F}),
               Direction.UP,
               create(Direction.UP, new float[]{6.0F, 0.0F, 10.0F, 6.0F}),
               Direction.DOWN,
               create(Direction.DOWN, new float[]{6.0F, 10.0F, 10.0F, 16.0F})
            ),
            null,
            false
         )
      );
      ELEMENTS.put(
         "south",
         new BlockElement(
            new Vector3f(6.0F, 6.0F, 10.0F),
            new Vector3f(10.0F, 10.0F, 16.0F),
            Map.of(
               Direction.NORTH,
               create(Direction.NORTH, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.EAST,
               create(Direction.EAST, new float[]{0.0F, 6.0F, 6.0F, 10.0F}),
               Direction.SOUTH,
               create(Direction.SOUTH, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.WEST,
               create(Direction.WEST, new float[]{10.0F, 6.0F, 16.0F, 10.0F}),
               Direction.UP,
               create(Direction.UP, new float[]{6.0F, 10.0F, 10.0F, 16.0F}),
               Direction.DOWN,
               create(Direction.DOWN, new float[]{6.0F, 0.0F, 10.0F, 6.0F})
            ),
            null,
            false
         )
      );
      ELEMENTS.put(
         "west",
         new BlockElement(
            new Vector3f(0.0F, 6.0F, 6.0F),
            new Vector3f(6.0F, 10.0F, 10.0F),
            Map.of(
               Direction.NORTH,
               create(Direction.NORTH, new float[]{10.0F, 6.0F, 16.0F, 10.0F}),
               Direction.EAST,
               create(Direction.EAST, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.SOUTH,
               create(Direction.SOUTH, new float[]{0.0F, 6.0F, 6.0F, 10.0F}),
               Direction.WEST,
               create(Direction.WEST, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.UP,
               create(Direction.UP, new float[]{0.0F, 6.0F, 6.0F, 10.0F}),
               Direction.DOWN,
               create(Direction.DOWN, new float[]{0.0F, 6.0F, 6.0F, 10.0F})
            ),
            null,
            false
         )
      );
      ELEMENTS.put(
         "east",
         new BlockElement(
            new Vector3f(10.0F, 6.0F, 6.0F),
            new Vector3f(16.0F, 10.0F, 10.0F),
            Map.of(
               Direction.NORTH,
               create(Direction.NORTH, new float[]{0.0F, 6.0F, 6.0F, 10.0F}),
               Direction.EAST,
               create(Direction.EAST, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.SOUTH,
               create(Direction.SOUTH, new float[]{10.0F, 6.0F, 16.0F, 10.0F}),
               Direction.WEST,
               create(Direction.WEST, new float[]{6.0F, 6.0F, 10.0F, 10.0F}),
               Direction.UP,
               create(Direction.UP, new float[]{10.0F, 6.0F, 16.0F, 10.0F}),
               Direction.DOWN,
               create(Direction.DOWN, new float[]{10.0F, 6.0F, 16.0F, 10.0F})
            ),
            null,
            false
         )
      );
   }
}
