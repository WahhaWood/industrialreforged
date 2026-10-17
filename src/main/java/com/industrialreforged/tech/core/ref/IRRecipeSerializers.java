package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.core.recipe.BlastFurnaceRecipe;
import com.industrialreforged.tech.core.recipe.BlockCutterRecipe;
import com.industrialreforged.tech.core.recipe.CentrifugeRecipe;
import com.industrialreforged.tech.core.recipe.CompressorRecipe;
import com.industrialreforged.tech.core.recipe.ExtractorRecipe;
import com.industrialreforged.tech.core.recipe.MaceratorRecipe;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import com.industrialreforged.tech.core.recipe.OreWashingRecipe;
import com.industrialreforged.tech.core.recipe.PainterRecipe;
import com.industrialreforged.tech.core.recipe.ScannerRecipe;
import com.industrialreforged.tech.core.recipe.ToolUseRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IRRecipeSerializers {
   public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "industrialreforged");
   public static final Supplier<RecipeSerializer<ToolUseRecipe>> TOOL_USE = REGISTRY.register("tool_use", ToolUseRecipe.Serializer::new);
   public static final Supplier<MaceratorRecipe.Serializer> MACERATOR = REGISTRY.register("macerator", MaceratorRecipe.Serializer::new);
   public static final Supplier<ExtractorRecipe.Serializer> EXTRACTOR = REGISTRY.register("extractor", ExtractorRecipe.Serializer::new);
   public static final Supplier<CompressorRecipe.Serializer> COMPRESSOR = REGISTRY.register("compressor", CompressorRecipe.Serializer::new);
   public static final Supplier<CentrifugeRecipe.Serializer> CENTRIFUGE = REGISTRY.register("centrifuge", CentrifugeRecipe.Serializer::new);
   public static final Supplier<BlockCutterRecipe.Serializer> BLOCK_CUTTER = REGISTRY.register("block_cutter", BlockCutterRecipe.Serializer::new);
   public static final Supplier<BlastFurnaceRecipe.Serializer> BLAST_FURNACE = REGISTRY.register("blast_furnace", BlastFurnaceRecipe.Serializer::new);
   public static final Supplier<MetalFormerRecipe.Serializer> METAL_FORMER = REGISTRY.register("metal_former", MetalFormerRecipe.Serializer::new);
   public static final Supplier<OreWashingRecipe.Serializer> ORE_WASHER = REGISTRY.register("ore_washer", OreWashingRecipe.Serializer::new);
   public static final Supplier<MolecularTransformerRecipe.Serializer> MOLECULAR_TRANSFORMER = REGISTRY.register(
      "molecular_transformer", MolecularTransformerRecipe.Serializer::new
   );
   public static final Supplier<RecipeSerializer<PainterRecipe>> PAINTER_RECIPE = REGISTRY.register("painter_recipe", PainterRecipe.Serializer::new);
   public static final Supplier<RecipeSerializer<ScannerRecipe>> SCANNER = REGISTRY.register("scanner", ScannerRecipe.Serializer::new);
}
