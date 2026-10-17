package com.industrialreforged.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.container.wrapper.InputSidedInvWrapper;
import com.industrialreforged.container.wrapper.OutputSidedInvWrapper;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class BasicWorldlyContainer extends BasicSimpleContainer implements WorldlyContainer {
   private final int[] slots;

   public BasicWorldlyContainer(BaseBlockEntity tile, String invName, int size) {
      this(tile, invName, size, 64);
   }

   public BasicWorldlyContainer(BaseBlockEntity tile, String invName, int size, int stackLimit) {
      this(tile, invName, size, stackLimit, IntStream.range(0, size).toArray());
   }

   public BasicWorldlyContainer(BaseBlockEntity tile, String invName, int size, int stackLimit, int... slots) {
      super(tile, invName, size, stackLimit, null);
      this.slots = slots;
   }

   public BasicWorldlyContainer(BaseBlockEntity entity, String invName, int size, int stackLimit, @Nullable BiPredicate<Integer, ItemStack> predicate) {
      this(entity, invName, size, stackLimit, predicate, IntStream.range(0, size).toArray());
   }

   public BasicWorldlyContainer(
      BaseBlockEntity entity, String invName, int size, int stackLimit, @Nullable BiPredicate<Integer, ItemStack> predicate, int... slots
   ) {
      super(entity, invName, size, stackLimit, predicate);
      this.slots = slots;
   }

   @Nullable
   @Override
   public IItemHandlerModifiable createWrapper() {
      return this.createWrapper(null);
   }

   @Nullable
   @Override
   public IItemHandlerModifiable createWrapper(@Nullable Direction direction) {
      if (this.canInput && this.canOutput) {
         return new SidedInvWrapper(this, direction);
      } else if (this.canInput) {
         return new InputSidedInvWrapper(this, direction);
      } else {
         return this.canOutput ? new OutputSidedInvWrapper(this, direction) : com.industrialreforged.container.wrapper.EmptyInvWrapper.INSTANCE;
      }
   }

   public int[] getSlotsForFace(Direction direction) {
      return this.slots;
   }

   public boolean canPlaceItemThroughFace(int slotId, ItemStack stack, @Nullable Direction direction) {
      return this.canPlaceItem(slotId, stack);
   }

   public boolean canTakeItemThroughFace(int slotId, ItemStack stack, Direction direction) {
      return this.canOutput;
   }
}
