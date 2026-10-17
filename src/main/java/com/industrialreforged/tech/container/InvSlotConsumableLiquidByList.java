package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.level.material.Fluid;

public class InvSlotConsumableLiquidByList extends InvSlotConsumableLiquid {
   private final Set<Fluid> acceptedFluids = new HashSet<>();

   public InvSlotConsumableLiquidByList(BaseBlockEntity entity, String name, int size, Fluid... fluids) {
      super(entity, name, size);
      this.acceptedFluids.addAll(Arrays.asList(fluids));
   }

   public InvSlotConsumableLiquidByList(BaseBlockEntity entity, String name, int size, InvSlotConsumableLiquid.OpType opType, Fluid... fluids) {
      super(entity, name, size, opType);
      this.acceptedFluids.addAll(Arrays.asList(fluids));
   }

   @Override
   protected boolean acceptsLiquid(Fluid fluid) {
      return this.acceptedFluids.contains(fluid);
   }

   @Override
   protected Iterable<Fluid> getPossibleFluids() {
      return this.acceptedFluids;
   }
}
