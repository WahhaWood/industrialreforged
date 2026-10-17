package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.datagen.provider.recipe.BlastFurnaceGen;
import com.industrialreforged.tech.datagen.provider.recipe.BlockCutterGen;
import com.industrialreforged.tech.datagen.provider.recipe.CentrifugeGen;
import com.industrialreforged.tech.datagen.provider.recipe.CompressorGen;
import com.industrialreforged.tech.datagen.provider.recipe.ExtractorGen;
import com.industrialreforged.tech.datagen.provider.recipe.IRecipeGen;
import com.industrialreforged.tech.datagen.provider.recipe.MaceratorGen;
import com.industrialreforged.tech.datagen.provider.recipe.MetalFormerGen;
import com.industrialreforged.tech.datagen.provider.recipe.MolecularTransformerGen;
import com.industrialreforged.tech.datagen.provider.recipe.OreWasherGen;
import com.industrialreforged.tech.datagen.provider.recipe.ScannerGen;
import com.industrialreforged.tech.datagen.provider.recipe.ShapelessGen;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

public class IRRecipeProvider extends RecipeProvider {
   private final List<IRecipeGen> genList = new ArrayList<>();

   public IRRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, registries);
      this.genList.add(new BlastFurnaceGen());
      this.genList.add(new BlockCutterGen());
      this.genList.add(new CentrifugeGen());
      this.genList.add(new CompressorGen());
      this.genList.add(new ExtractorGen());
      this.genList.add(new MaceratorGen());
      this.genList.add(new MetalFormerGen());
      this.genList.add(new MolecularTransformerGen());
      this.genList.add(new OreWasherGen());
      this.genList.add(new ShapelessGen());
      this.genList.add(new ScannerGen());
   }

   protected void buildRecipes(@NotNull RecipeOutput consumer) {
      this.genList.forEach(gen -> gen.build(consumer));
   }
}
