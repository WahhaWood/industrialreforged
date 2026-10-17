package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A machine recipe offered to the ME network as a processing pattern. There is no pattern item in
 * anyone's inventory: the definition is a hidden item carrying only the recipe id, which the decoder
 * turns back into the pattern when a crafting CPU reloads its job. Every item of a tagged input is a
 * valid substitute; the first one is what AE2 autocrafts if none is stored.
 */
public final class MEMachinePattern implements IPatternDetails {
   private final AEItemKey definition;
   private final MEMachineRecipe recipe;
   private final IInput[] inputs;
   private final List<GenericStack> outputs;

   public MEMachinePattern(MEMachineRecipe recipe) {
      this.recipe = recipe;
      ItemStack stack = new ItemStack(AE2Integration.RECIPE_PATTERN.get());
      stack.set(AE2Integration.PATTERN_RECIPE.get(), recipe.id());
      this.definition = AEItemKey.of(stack);
      this.inputs = new IInput[]{new Input(recipe)};
      Map<AEKey, Long> condensed = new LinkedHashMap<>();
      for (ItemStack output : recipe.outputs()) {
         AEItemKey key = AEItemKey.of(output);
         if (key != null) {
            condensed.merge(key, (long)output.getCount(), Long::sum);
         }
      }

      List<GenericStack> list = new ArrayList<>(condensed.size());
      condensed.forEach((key, amount) -> list.add(new GenericStack(key, amount)));
      this.outputs = List.copyOf(list);
   }

   public MEMachineRecipe recipe() {
      return this.recipe;
   }

   public ResourceLocation recipeId() {
      return this.recipe.id();
   }

   @Override
   public AEItemKey getDefinition() {
      return this.definition;
   }

   @Override
   public IInput[] getInputs() {
      return this.inputs;
   }

   @Override
   public List<GenericStack> getOutputs() {
      return this.outputs;
   }

   /** Only our own provider ever pushes these, straight into the machine. */
   @Override
   public boolean supportsPushInputsToExternalInventory() {
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof MEMachinePattern other && other.definition.equals(this.definition);
   }

   @Override
   public int hashCode() {
      return this.definition.hashCode();
   }

   private static final class Input implements IInput {
      private final MEMachineRecipe recipe;
      private final GenericStack[] possible;
      /** Items a simple (item or tag) ingredient accepts; null if it also checks components. */
      @Nullable
      private final Set<Item> items;

      private Input(MEMachineRecipe recipe) {
         this.recipe = recipe;
         Set<AEItemKey> keys = new LinkedHashSet<>();
         for (ItemStack stack : recipe.input().getItems()) {
            AEItemKey key = AEItemKey.of(stack);
            if (key != null) {
               keys.add(key);
            }
         }

         this.possible = keys.stream().map(key -> new GenericStack(key, 1)).toArray(GenericStack[]::new);
         this.items = recipe.input().isSimple() ? keys.stream().map(AEItemKey::getItem).collect(Collectors.toUnmodifiableSet()) : null;
      }

      @Override
      public GenericStack[] getPossibleInputs() {
         return this.possible;
      }

      @Override
      public long getMultiplier() {
         return this.recipe.inputCount();
      }

      @Override
      public boolean isValid(AEKey input, Level level) {
         if (!(input instanceof AEItemKey item)) {
            return false;
         }

         // Ingredient.test builds an ItemStack per call; a simple ingredient only looks at the item
         return this.items != null ? this.items.contains(item.getItem()) : item.matches(this.recipe.input());
      }

      @Nullable
      @Override
      public AEKey getRemainingKey(AEKey template) {
         return null;
      }
   }

   /** Rebuilds a pattern from its definition (crafting CPUs save jobs by definition only). */
   public static final class Decoder implements IPatternDetailsDecoder {
      @Override
      public boolean isEncodedPattern(ItemStack stack) {
         return stack.is(AE2Integration.RECIPE_PATTERN.get());
      }

      @Nullable
      @Override
      public IPatternDetails decodePattern(AEItemKey what, Level level) {
         if (what == null || level == null || !what.is(AE2Integration.RECIPE_PATTERN.get())) {
            return null;
         }

         ResourceLocation id = what.get(AE2Integration.PATTERN_RECIPE.get());
         RecipeHolder<?> holder = id == null ? null : level.getRecipeManager().byKey(id).orElse(null);
         MEMachineKind kind = holder == null ? null : MEMachineKind.byRecipeType(holder.value().getType());
         MEMachineRecipe recipe = kind == null ? null : kind.convert(holder, level.registryAccess());
         return recipe == null ? null : new MEMachinePattern(recipe);
      }
   }
}
