package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.machine.Recycler1LevelBlockEntity;
import com.industrialreforged.tech.core.recipe.RecyclerRecipe;
import com.industrialreforged.tech.core.ref.IRItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** Recycler input: takes anything outside the blacklist, one item per operation, scrap on a 1/8 roll. */
public class InvSlotProcessableRecycler extends InvSlotProcessable<Recycler1LevelBlockEntity, RecyclerRecipe> {
   public InvSlotProcessableRecycler(Recycler1LevelBlockEntity entity, String name, int size) {
      super(entity, name, size, null);
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack itemStack) {
      return RecyclerRecipe.accepts(itemStack);
   }

   @Nullable
   @Override
   public RecyclerRecipe process(int slot) {
      return RecyclerRecipe.accepts(this.getItem(slot)) ? RecyclerRecipe.INSTANCE : null;
   }

   @Override
   public boolean assemble(int slot) {
      if (!RecyclerRecipe.accepts(this.getItem(slot))) {
         return false;
      }

      this.shrink(slot);
      Level level = this.entity.getLevel();
      if (level != null && level.random.nextInt(RecyclerRecipe.SCRAP_CHANCE) == 0) {
         this.entity.outputSlot.add(new ItemStack(IRItems.SCRAP.get()));
      }

      return true;
   }
}
