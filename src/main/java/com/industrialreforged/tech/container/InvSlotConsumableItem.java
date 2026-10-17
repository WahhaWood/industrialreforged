package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class InvSlotConsumableItem extends InvSlotConsumable {
   private final Set<Item> stacks;

   public InvSlotConsumableItem(BaseBlockEntity entity, String name, int size, ItemLike... stacks) {
      super(entity, name, size);
      this.stacks = Arrays.stream(stacks).<Item>map(ItemLike::asItem).collect(Collectors.toSet());
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return this.stacks.contains(stack.getItem());
   }
}
