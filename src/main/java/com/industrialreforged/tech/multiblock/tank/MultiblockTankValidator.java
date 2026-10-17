package com.industrialreforged.tech.multiblock.tank;

import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.multiblock.MultiblockValidationResult;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class MultiblockTankValidator {
   public static final int MIN_SIZE = 3;
   public static final int MAX_SIZE = 63;
   public static final int MB_PER_BLOCK = 16000;
   /**
    * Предельный объём полости, который ещё влезает в int-ёмкость {@code FluidTank}.
    * 2 147 483 647 / 16 000 = 134 217 блоков, то есть куб примерно 51×51×51.
    */
   public static final int MAX_INNER_VOLUME = Integer.MAX_VALUE / MB_PER_BLOCK;

   public static MultiblockValidationResult validate(Level level, BlockPos valvePos) {
      if (level == null || valvePos == null) {
         return MultiblockValidationResult.error(Component.translatable("text.industrialreforged.multiblock.tank.error.invalid_world"));
      }

      // 1. Ищем смежную полость (воздух или разрешенная жидкость)
      List<Direction> candidateDirs = new ArrayList<>();
      for (Direction dir : Direction.values()) {
         BlockPos adjacent = valvePos.relative(dir);
         if (isInteriorCandidate(level.getBlockState(adjacent))) {
            candidateDirs.add(dir);
         }
      }

      if (candidateDirs.isEmpty()) {
         return MultiblockValidationResult.error(
            valvePos,
            Component.translatable("text.industrialreforged.multiblock.tank.error.no_cavity", valvePos.getX(), valvePos.getY(), valvePos.getZ())
         );
      }

      // Пробуем построить структуру по каждому найденному направлению полости
      MultiblockValidationResult lastError = null;
      for (Direction interiorDir : candidateDirs) {
         MultiblockValidationResult res = tryValidateFromDirection(level, valvePos, interiorDir);
         if (res.isSuccess()) {
            return res;
         }
         lastError = res;
      }

      return lastError != null ? lastError : MultiblockValidationResult.error(valvePos, Component.translatable("text.industrialreforged.multiblock.tank.error.generic"));
   }

   /**
    * Валидация с запасными якорями: клапан/контроллер может стоять на углу или ребре оболочки,
    * где рядом нет полости. Тогда якорем становится соседний блок, а найденная структура обязана
    * содержать исходный блок.
    */
   public static MultiblockValidationResult validateWithFallback(Level level, BlockPos pos) {
      MultiblockValidationResult result = validate(level, pos);
      if (result.isSuccess()) {
         return result;
      }

      for (Direction dir : Direction.values()) {
         MultiblockValidationResult candidate = validate(level, pos.relative(dir));
         if (candidate.isSuccess() && candidate.getAllBlocks().contains(pos)) {
            return candidate;
         }
      }

      return result;
   }

   private static MultiblockValidationResult tryValidateFromDirection(Level level, BlockPos valvePos, Direction interiorDir) {
      BlockPos startPos = valvePos.relative(interiorDir);

      // Определяем границы полости расширением по осям X, Y, Z (до 5 блоков полости)
      int minInnerX = startPos.getX();
      int maxInnerX = startPos.getX();
      while (maxInnerX - startPos.getX() < MAX_SIZE - 2 && isInteriorCandidate(level.getBlockState(new BlockPos(maxInnerX + 1, startPos.getY(), startPos.getZ())))) {
         maxInnerX++;
      }
      while (startPos.getX() - minInnerX < MAX_SIZE - 2 && isInteriorCandidate(level.getBlockState(new BlockPos(minInnerX - 1, startPos.getY(), startPos.getZ())))) {
         minInnerX--;
      }

      int minInnerY = startPos.getY();
      int maxInnerY = startPos.getY();
      while (maxInnerY - startPos.getY() < MAX_SIZE - 2 && isInteriorCandidate(level.getBlockState(new BlockPos(startPos.getX(), maxInnerY + 1, startPos.getZ())))) {
         maxInnerY++;
      }
      while (startPos.getY() - minInnerY < MAX_SIZE - 2 && isInteriorCandidate(level.getBlockState(new BlockPos(startPos.getX(), minInnerY - 1, startPos.getZ())))) {
         minInnerY--;
      }

      int minInnerZ = startPos.getZ();
      int maxInnerZ = startPos.getZ();
      while (maxInnerZ - startPos.getZ() < MAX_SIZE - 2 && isInteriorCandidate(level.getBlockState(new BlockPos(startPos.getX(), startPos.getY(), maxInnerZ + 1)))) {
         maxInnerZ++;
      }
      while (startPos.getZ() - minInnerZ < MAX_SIZE - 2 && isInteriorCandidate(level.getBlockState(new BlockPos(startPos.getX(), startPos.getY(), minInnerZ - 1)))) {
         minInnerZ--;
      }

      int innerDx = maxInnerX - minInnerX + 1;
      int innerDy = maxInnerY - minInnerY + 1;
      int innerDz = maxInnerZ - minInnerZ + 1;

      int outerDx = innerDx + 2;
      int outerDy = innerDy + 2;
      int outerDz = innerDz + 2;

      if (outerDx < MIN_SIZE || outerDx > MAX_SIZE || outerDy < MIN_SIZE || outerDy > MAX_SIZE || outerDz < MIN_SIZE || outerDz > MAX_SIZE) {
         return MultiblockValidationResult.error(
            valvePos,
            Component.translatable("text.industrialreforged.multiblock.tank.error.invalid_size", outerDx, outerDy, outerDz)
         );
      }

      BlockPos minBox = new BlockPos(minInnerX - 1, minInnerY - 1, minInnerZ - 1);
      BlockPos maxBox = new BlockPos(maxInnerX + 1, maxInnerY + 1, maxInnerZ + 1);

      // valvePos должен лежать на внешней оболочке
      boolean onShell = (valvePos.getX() == minBox.getX() || valvePos.getX() == maxBox.getX()
         || valvePos.getY() == minBox.getY() || valvePos.getY() == maxBox.getY()
         || valvePos.getZ() == minBox.getZ() || valvePos.getZ() == maxBox.getZ())
         && valvePos.getX() >= minBox.getX() && valvePos.getX() <= maxBox.getX()
         && valvePos.getY() >= minBox.getY() && valvePos.getY() <= maxBox.getY()
         && valvePos.getZ() >= minBox.getZ() && valvePos.getZ() <= maxBox.getZ();

      if (!onShell) {
         return MultiblockValidationResult.error(
            valvePos,
            Component.translatable("text.industrialreforged.multiblock.tank.error.valve_not_on_shell")
         );
      }

      List<BlockPos> valves = new ArrayList<>();
      List<BlockPos> allBlocks = new ArrayList<>();

      for (int x = minBox.getX(); x <= maxBox.getX(); x++) {
         for (int y = minBox.getY(); y <= maxBox.getY(); y++) {
            for (int z = minBox.getZ(); z <= maxBox.getZ(); z++) {
               BlockPos pos = new BlockPos(x, y, z);
               BlockState state = level.getBlockState(pos);

               boolean isInterior = x > minBox.getX() && x < maxBox.getX()
                  && y > minBox.getY() && y < maxBox.getY()
                  && z > minBox.getZ() && z < maxBox.getZ();

               if (isInterior) {
                  if (!isInteriorCandidate(state)) {
                     return MultiblockValidationResult.error(
                        pos,
                        Component.translatable("text.industrialreforged.multiblock.tank.error.interior_block", x, y, z, state.getBlock().getName())
                     );
                  }
               } else {
                  // Оболочка
                  if (!isCasingBlock(state)) {
                     return MultiblockValidationResult.error(
                        pos,
                        Component.translatable("text.industrialreforged.multiblock.tank.error.missing_casing", x, y, z, state.getBlock().getName())
                     );
                  }

                  if (state.is(IRBlocks.TANK_VALVE.get())) {
                     valves.add(pos);
                  }
                  allBlocks.add(pos);
               }
            }
         }
      }

      if (valves.isEmpty()) {
         return MultiblockValidationResult.error(valvePos, Component.translatable("text.industrialreforged.multiblock.tank.error.no_valve"));
      }

      int innerVolume = innerDx * innerDy * innerDz;
      // Ёмкость хранится в int внутри FluidTank, и больше Integer.MAX_VALUE бак вместить не может.
      // Молча резать значение нельзя: игрок увидит «полный» бак, в который налито втрое меньше,
      // и ни за что не поймёт почему. Поэтому структура недопустима целиком, и мы говорим об этом.
      if (innerVolume > MAX_INNER_VOLUME) {
         return MultiblockValidationResult.error(
            valvePos,
            Component.translatable("text.industrialreforged.multiblock.tank.error.too_large", innerVolume, MAX_INNER_VOLUME)
         );
      }

      return MultiblockValidationResult.success(minBox, maxBox, innerVolume, innerVolume * MB_PER_BLOCK, valves, allBlocks);
   }

   public static boolean isInteriorCandidate(BlockState state) {
      if (state.isAir()) {
         return true;
      }
      if (state.is(Blocks.WATER) || state.getFluidState().is(Fluids.WATER)) {
         return true;
      }
      if (state.getFluidState().is(IRFluids.DISTILLED_WATER.getFluidStill())) {
         return true;
      }
      if (state.getFluidState().is(IRFluids.STEAM.getFluidStill())) {
         return true;
      }
      return false;
   }

   /** Оболочка: стенки резервуара, укреплённое стекло (окна), клапан и контроллер. */
   public static boolean isCasingBlock(BlockState state) {
      return state.is(IRBlocks.TANK_WALL.get())
         || state.is(IRBlocks.REINFORCED_GLASS.get())
         || state.is(IRBlocks.TANK_VALVE.get())
         || state.is(IRBlocks.TANK_CONTROLLER.get());
   }
}
