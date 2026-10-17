package com.industrialreforged.menu.sync;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RecipeDataSlot<I extends RecipeInput, T extends Recipe<I>> extends AbstractCustomDataSlot {
   private final RecipeManager manager;
   private final RecipeType<T> type;
   private final Supplier<RecipeHolder<T>> getter;
   private final Consumer<RecipeHolder<T>> setter;
   private RecipeHolder<T> recipe;

   public RecipeDataSlot(BlockEntity entity, RecipeType<T> type, Supplier<RecipeHolder<T>> getter, Consumer<RecipeHolder<T>> setter) {
      this(entity.getLevel(), type, getter, setter);
   }

   public RecipeDataSlot(Level level, RecipeType<T> type, Supplier<RecipeHolder<T>> getter, Consumer<RecipeHolder<T>> setter) {
      this(level.getRecipeManager(), type, getter, setter);
   }

   public RecipeDataSlot(RecipeManager manager, RecipeType<T> type, Supplier<RecipeHolder<T>> getter, Consumer<RecipeHolder<T>> setter) {
      this.manager = manager;
      this.type = type;
      this.getter = getter;
      this.setter = setter;
   }

   @Override
   public boolean check() {
      RecipeHolder<T> recipe = this.getter.get();
      return !Objects.equals(recipe, this.recipe) || super.check();
   }

   @Override
   public void update(ServerPlayer player) {
      this.recipe = this.getter.get();
      super.update(player);
   }

   @Override
   public void write(RegistryFriendlyByteBuf buf) {
      buf.writeNullable(this.recipe, (buffer, value) -> buffer.writeResourceLocation(value.id()));
   }

   @Override
   public void apply(RegistryFriendlyByteBuf buf) {
      this.recipe = (RecipeHolder<T>)buf.readNullable(buffer -> {
         ResourceLocation location = buffer.readResourceLocation();
         return this.manager.getAllRecipesFor(this.type).stream().filter(o -> o.id().equals(location)).findFirst().orElse(null);
      });
      this.setter.accept(this.recipe);
   }
}
