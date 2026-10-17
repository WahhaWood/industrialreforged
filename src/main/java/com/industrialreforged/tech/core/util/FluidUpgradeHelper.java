package com.industrialreforged.tech.core.util;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

/**
 * Работа жидкостных улучшений: «Жидкостный выброс» (FLUID_EJECTOR) выталкивает жидкость
 * из танка машины в соседние обработчики, «Жидкостный забор» (FLUID_PULLING) — тянет
 * жидкость из соседей в танк. Вызывается каждый серверный тик из BaseBlockEntity.Ticker.
 */
public final class FluidUpgradeHelper {
   /** Кэш поля upgradeSlot по классу блочной сущности (одно поле на класс). */
   private static final Map<Class<?>, Optional<Field>> SLOT_FIELDS = new ConcurrentHashMap<>();

   private FluidUpgradeHelper() {
   }

   public static void tick(BlockEntity blockEntity) {
      Level level = blockEntity.getLevel();
      if (level == null || level.isClientSide) {
         return;
      }
      if (!(blockEntity instanceof IFluidWrapper wrapper)) {
         return;
      }

      // Троттлинг: соседи сканируются раз в 4 тика, а не каждый. Примитивы
      // simulate→execute не трогаем (потерь/дублирований там нет).
      // Редстоун-гейта нет by design: перенос — логистика, а не работа; готовая
      // жидкость должна уходить даже когда работа встала (единого «гейта работы»
      // в коде нет — каждая машина использует сигнал по-своему).
      if ((level.getGameTime() & 3) != 0) {
         return;
      }

      InvSlotUpgrade slot = findUpgradeSlot(blockEntity);
      if (slot == null) {
         return;
      }
      boolean eject = slot.hasFluidEjectUpgrade();
      boolean pull = slot.hasFluidPullingUpgrade();
      if (!eject && !pull) {
         return;
      }

      IFluidHandler handler = wrapper.getFluidWrapper(null);
      if (handler == null) {
         return;
      }

      BlockPos pos = blockEntity.getBlockPos();
      if (eject) {
         eject(level, pos, handler, slot.getFluidEjectSide(), Math.max(1, slot.getFluidEjectAmount()));
      }
      if (pull) {
         pull(level, pos, handler, slot.getFluidPullingSide(), Math.max(1, slot.getFluidPullingAmount()));
      }
   }

   private static void eject(Level level, BlockPos pos, IFluidHandler handler, @Nullable Direction side, int budget) {
      for (Direction dir : directions(side)) {
         if (budget <= 0) {
            return;
         }
         IFluidHandler target = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(dir), dir.getOpposite());
         if (target == null) {
            continue;
         }
         FluidStack drainable = handler.drain(budget, FluidAction.SIMULATE);
         if (drainable.isEmpty()) {
            return;
         }
         int accepted = target.fill(drainable, FluidAction.SIMULATE);
         if (accepted <= 0) {
            continue;
         }
         FluidStack drained = handler.drain(Math.min(accepted, drainable.getAmount()), FluidAction.EXECUTE);
         if (!drained.isEmpty()) {
            budget -= target.fill(drained, FluidAction.EXECUTE);
         }
      }
   }

   private static void pull(Level level, BlockPos pos, IFluidHandler handler, @Nullable Direction side, int budget) {
      for (Direction dir : directions(side)) {
         if (budget <= 0) {
            return;
         }
         IFluidHandler source = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(dir), dir.getOpposite());
         if (source == null) {
            continue;
         }
         FluidStack available = source.drain(budget, FluidAction.SIMULATE);
         if (available.isEmpty()) {
            continue;
         }
         int accepted = handler.fill(available, FluidAction.SIMULATE);
         if (accepted <= 0) {
            continue;
         }
         FluidStack drained = source.drain(Math.min(accepted, available.getAmount()), FluidAction.EXECUTE);
         if (!drained.isEmpty()) {
            budget -= handler.fill(drained, FluidAction.EXECUTE);
         }
      }
   }

   private static Direction[] directions(@Nullable Direction side) {
      return side == null ? Direction.values() : new Direction[]{side};
   }

   @Nullable
   private static InvSlotUpgrade findUpgradeSlot(BlockEntity blockEntity) {
      Field field = SLOT_FIELDS.computeIfAbsent(blockEntity.getClass(), cls -> {
         for (Class<?> type = cls; type != null && type != Object.class; type = type.getSuperclass()) {
            try {
               Field candidate = type.getDeclaredField("upgradeSlot");
               if (InvSlotUpgrade.class.isAssignableFrom(candidate.getType())) {
                  candidate.setAccessible(true);
                  return Optional.of(candidate);
               }
            } catch (NoSuchFieldException ignored) {
            }
         }
         return Optional.empty();
      }).orElse(null);
      if (field == null) {
         return null;
      }
      try {
         return (InvSlotUpgrade)field.get(blockEntity);
      } catch (IllegalAccessException exception) {
         return null;
      }
   }
}
