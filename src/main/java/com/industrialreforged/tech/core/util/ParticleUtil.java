package com.industrialreforged.tech.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

public class ParticleUtil {
   public static void showFurnaceFlames(Level level, BlockPos blockPos, Direction direction) {
      if (level.random.nextInt(8) == 0) {
         double d = blockPos.getX() + (direction.getStepX() * 1.04 + 1.0) / 2.0;
         double d2 = blockPos.getY() + level.random.nextFloat() * 0.375;
         double d3 = blockPos.getZ() + (direction.getStepZ() * 1.04 + 1.0) / 2.0;
         if (direction.getAxis() == Axis.X) {
            d3 += level.random.nextFloat() * 0.625 - 0.3125;
         } else {
 d += level.random.nextFloat() * 0.625 - 0.3125;
         }

         level.addParticle(ParticleTypes.SMOKE, d, d2, d3, 0.0, 0.0, 0.0);
         level.addParticle(ParticleTypes.FLAME, d, d2, d3, 0.0, 0.0, 0.0);
      }
   }
}
