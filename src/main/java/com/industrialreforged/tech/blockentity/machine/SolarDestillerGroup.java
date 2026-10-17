package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.fluid.FluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluids;

/**
 * One shared buffer for a wall of touching solar distillers.  Every block of the
 * group reads and writes the same tanks, and the capacities scale with the
 * number of joined distillers (10000 mB each).
 */
public final class SolarDestillerGroup {
   public static final int CAPACITY_PER_BLOCK = 10000;

   public final FluidTank input;
   public final FluidTank output;
   public final int size;
   public final BlockPos leader;

   public SolarDestillerGroup(int size, BlockPos leader) {
      this.size = Math.max(1, size);
      this.leader = leader;
      this.input = new FluidTank(CAPACITY_PER_BLOCK * this.size, true, true, stack -> stack.is(Fluids.WATER));
      this.output = new FluidTank(CAPACITY_PER_BLOCK * this.size, true, true);
   }

   /** True for the one block of the group that does the actual distilling. */
   public boolean isLeader(BlockPos pos) {
      return this.leader.equals(pos);
   }
}
