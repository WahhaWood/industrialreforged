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
import com.industrialreforged.tech.core.recipe.ScannerRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IRRecipeTypes {
   public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, "industrialreforged");
   public static final Supplier<RecipeType<MaceratorRecipe>> MACERATOR = REGISTRY.register("macerator", RecipeType::simple);
   public static final Supplier<RecipeType<ExtractorRecipe>> EXTRACTOR = REGISTRY.register("extractor", RecipeType::simple);
   public static final Supplier<RecipeType<CompressorRecipe>> COMPRESSOR = REGISTRY.register("compressor", RecipeType::simple);
   public static final Supplier<RecipeType<CentrifugeRecipe>> CENTRIFUGE = REGISTRY.register("centrifuge", RecipeType::simple);
   public static final Supplier<RecipeType<BlockCutterRecipe>> BLOCK_CUTTER = REGISTRY.register("block_cutter", RecipeType::simple);
   public static final Supplier<RecipeType<BlastFurnaceRecipe>> BLAST_FURNACE = REGISTRY.register("blast_furnace", RecipeType::simple);
   public static final Supplier<RecipeType<MetalFormerRecipe>> METAL_FORMER = REGISTRY.register("metal_former", RecipeType::simple);
   public static final Supplier<RecipeType<OreWashingRecipe>> ORE_WASHER = REGISTRY.register("ore_washing", RecipeType::simple);
   public static final Supplier<RecipeType<MolecularTransformerRecipe>> MOLECULAR_TRANSFORMER = REGISTRY.register("molecular_transformer", RecipeType::simple);
   public static final Supplier<RecipeType<ScannerRecipe>> SCANNER = REGISTRY.register("scanner", RecipeType::simple);
}
