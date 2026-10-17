package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.tech.blockentity.machine.CentrifugeBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Compressor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.recipe.CountIngredient;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.Nullable;

/**
 * The recipe-backed ME machines: which recipe type each one exposes to the ME network, the price of
 * one operation (FE of the level-1 machine, paid as AE at 2 FE = 1 AE) and whether its recipes start
 * enabled. No AE2 types here: the client screen uses it too.
 */
public enum MEMachineKind {
   FURNACE(
      "me_furnace", () -> RecipeType.SMELTING, ElectricFurnace1LevelBlockEntity.ENERGY_CONSUME * ElectricFurnace1LevelBlockEntity.OPERATION_LENGTH, false
   ),
   MACERATOR("me_macerator", IRRecipeTypes.MACERATOR, Macerator1LevelBlockEntity.ENERGY_CONSUME * Macerator1LevelBlockEntity.OPERATION_LENGTH, true),
   COMPRESSOR("me_compressor", IRRecipeTypes.COMPRESSOR, Compressor1LevelBlockEntity.ENERGY_CONSUME * Compressor1LevelBlockEntity.OPERATION_LENGTH, true),
   EXTRACTOR("me_extractor", IRRecipeTypes.EXTRACTOR, Extractor1LevelBlockEntity.ENERGY_CONSUME * Extractor1LevelBlockEntity.OPERATION_LENGTH, true),
   /** All three modes at once: rolling, cutting and extruding recipes share one recipe type. */
   METAL_FORMER(
      "me_metal_former", IRRecipeTypes.METAL_FORMER, MetalFormer1LevelBlockEntity.ENERGY_CONSUME * MetalFormer1LevelBlockEntity.OPERATION_LENGTH, true
   ),
   /** No heating: the ME version runs every recipe regardless of its minimum heat. */
   CENTRIFUGE("me_thermal_centrifuge", IRRecipeTypes.CENTRIFUGE, CentrifugeBlockEntity.ENERGY_CONSUME * CentrifugeBlockEntity.OPERATION_LENGTH, true),
   /** The price is the recipe's own energy, not a per-kind constant (see {@link #convert}). */
   MOLECULAR_TRANSFORMER("me_molecular_transformer", IRRecipeTypes.MOLECULAR_TRANSFORMER, 0, true);

   /** Exchange rate of AE2's own energy acceptor: 2 FE = 1 AE. */
   public static final double FE_PER_AE = 2.0;

   public final String id;
   private final Supplier<? extends RecipeType<?>> recipeType;
   /** FE one operation costs in the regular level-1 machine. */
   public final int energyPerOperation;
   public final boolean enabledByDefault;

   MEMachineKind(String id, Supplier<? extends RecipeType<?>> recipeType, int energyPerOperation, boolean enabledByDefault) {
      this.id = id;
      this.recipeType = recipeType;
      this.energyPerOperation = energyPerOperation;
      this.enabledByDefault = enabledByDefault;
   }

   public RecipeType<?> recipeType() {
      return this.recipeType.get();
   }

   /** AE per operation, the "honest" price: the machine's FE at AE2's exchange rate. */
   public double aePerOperation() {
      return this.energyPerOperation / FE_PER_AE;
   }

   @Nullable
   public static MEMachineKind byRecipeType(RecipeType<?> type) {
      for (MEMachineKind kind : values()) {
         if (kind.recipeType() == type) {
            return kind;
         }
      }

      return null;
   }

   /** Single-input form of a recipe of this kind, or null if the recipe is unusable (empty tag, no output). */
   @Nullable
   public MEMachineRecipe convert(RecipeHolder<?> holder, HolderLookup.Provider registries) {
      if (holder.value() instanceof SimpleInputRecipe simple) {
         CountIngredient input = simple.getIngredient();
         // the transformer prices every recipe on its own, the other machines have one price per operation
         double cost = holder.value() instanceof MolecularTransformerRecipe molecular ? molecular.energy / FE_PER_AE : this.aePerOperation();
         return MEMachineRecipe.of(holder.id(), input.ingredient(), input.count(), simple.getResultItems(), cost);
      }

      if (holder.value() instanceof SmeltingRecipe smelting && !smelting.getIngredients().isEmpty()) {
         return MEMachineRecipe.of(holder.id(), smelting.getIngredients().get(0), 1, List.of(smelting.getResultItem(registries)), this.aePerOperation());
      }

      return null;
   }
}
