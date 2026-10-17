package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.tech.core.util.ItemInfo;
import net.minecraft.world.item.ItemStack;

public class SimpleFuelContainer extends BasicSimpleContainer {
   public final boolean allowLava;

   public SimpleFuelContainer(BaseBlockEntity entity, boolean allowLava) {
      super(entity, "fuel", 1);
      this.allowLava = allowLava;
   }

   public boolean canPlaceItem(int index, ItemStack itemStack) {
      return this.getFuelValue(itemStack) > 0;
   }

   public int consumeFuel() {
      ItemStack itemStack = this.removeItem(0, 1);
      return itemStack.isEmpty() ? 0 : this.getFuelValue(itemStack);
   }

   /**
    * Fuel value in generator ticks.  Regular furnace fuels use their vanilla
    * burn time; anything that can be smelted in a furnace is also accepted,
    * using its smelting time as the fuel value.
    */
   public int getFuelValue(ItemStack itemStack) {
      int value = ItemInfo.INSTANCE.getFuelValue(itemStack, this.allowLava);
      if (value > 0) {
         return value;
      }

      net.minecraft.world.level.Level level = this.entity.getLevel();
      if (level == null || itemStack.isEmpty()) {
         return 0;
      }

      var recipe = level.getRecipeManager()
         .getRecipeFor(net.minecraft.world.item.crafting.RecipeType.SMELTING,
            new net.minecraft.world.item.crafting.SingleRecipeInput(itemStack), level);
      return recipe.map(holder -> holder.value().getCookingTime()).orElse(0);
   }
}
