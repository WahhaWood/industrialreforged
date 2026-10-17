package com.industrialreforged.transfer.render;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

@OnlyIn(Dist.CLIENT)
public final class WireGeometry implements IUnbakedGeometry<WireGeometry> {
   private WireGeometry() {
   }

   public BakedModel bake(
      IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides
   ) {
      return new WireBakedModel(
         context,
         spriteGetter.apply(context.getMaterial("particle")),
         spriteGetter.apply(context.getMaterial("resistor")),
         spriteGetter.apply(context.getMaterial("transformer"))
      );
   }

   public static final class Loader implements IGeometryLoader<WireGeometry> {
      public WireGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
         return new WireGeometry();
      }
   }
}
