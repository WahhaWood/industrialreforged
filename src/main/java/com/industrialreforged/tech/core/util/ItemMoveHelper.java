package com.industrialreforged.tech.core.util;

import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.IRConfig;
import com.industrialreforged.container.BasicSimpleContainer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * Работа улучшений переноса предметов:
 * «Выталкиватель» отправляет содержимое выходных слотов в соседний инвентарь,
 * «Затягиватель» тянет предметы из соседнего инвентаря во входные слоты.
 *
 * Модель уровней: число предметов в стаке улучшения = уровень стороны, бюджет
 * стороны = LIMIT × уровень (симметрия с жидкостными count*50). У каждой стороны
 * свой бюджет — веер по сторонам не голодает. Несколько явных сторон делят
 * выход по кругу (round-robin от игрового времени); режим «все стороны»
 * (null) идёт фиксированным порядком Direction.values(), как раньше.
 *
 * Выход никогда не перетекает обратно во вход (как у фабрик Mekanism): продукт,
 * который вход принимает по рецепту (пластина формовщика, пыль дробилки), обязан
 * уезжать наружу через выталкиватель, а не возвращаться в переработку. Сырьё,
 * попавшее в выход через боковую вставку, тоже вывозим как есть.
 *
 * Расписание: проход каждый тик (интервал из конфига {@code transfer.eject_interval_ticks},
 * дефолт 1). Это не грузит: ранние выходы (нет улучшения / пустой выход / полный вход)
 * стоят до сканирования соседей, так что простаивающие машины — несколько проверок
 * isEmpty, а сканирование capability идёт только когда реально есть что двигать.
 * Клиент не грузится вообще — весь перенос серверный, прилетают только синки
 * изменившихся слотов.
 */
public final class ItemMoveHelper {
   /** Сколько предметов переносим за проход на 1 уровень стороны. */
   private static final int LIMIT = 368;

   private ItemMoveHelper() {
   }

   public static void tick(Level level, BlockPos pos, InvSlotUpgrade upgrades, BasicSimpleContainer input, BasicSimpleContainer output) {
      move(level, pos, upgrades, input, output, false);
   }

   /** Немедленный проход после завершённой операции (мимо расписания конфига). */
   public static void moveNow(Level level, BlockPos pos, InvSlotUpgrade upgrades, BasicSimpleContainer input, BasicSimpleContainer output) {
      move(level, pos, upgrades, input, output, true);
   }

   /** Только выброс из выхода (без входа и затягивания): для карьера, чтобы pulling не тащил мусор в слот сканера. */
   public static void tickOutputOnly(Level level, BlockPos pos, InvSlotUpgrade upgrades, BasicSimpleContainer output) {
      if (level == null || level.isClientSide) {
         return;
      }
      if (!shouldMove(level)) {
         return;
      }
      if (!upgrades.hasEjectUpgrade() || isEmpty(output)) {
         return;
      }
      ejectIntoNeighbors(level, pos, upgrades.getEjectLevels(), output);
   }

   /** Расписание переноса из конфига: каждый тик при интервале 1, иначе раз в N тиков. */
   private static boolean shouldMove(Level level) {
      int interval = IRConfig.CONFIG.getEjectIntervalTicks();
      return interval <= 1 || (level.getGameTime() % interval) == 0;
   }

   private static void move(
      Level level, BlockPos pos, InvSlotUpgrade upgrades, BasicSimpleContainer input, BasicSimpleContainer output, boolean immediate
   ) {
      if (level == null || level.isClientSide) {
         return;
      }
      // Расписание — из конфига (дефолт: каждый тик). Примитивы
      // ejectItems/pullItems не трогаем (они корректны: insert→remove moved,
      // simulate→insert→execute).
      if (!immediate && !shouldMove(level)) {
         return;
      }

      boolean wantEject = upgrades.hasEjectUpgrade() && !isEmpty(output);
      boolean wantPull = upgrades.hasPullingUpgrade() && input != null && hasRoom(input);
      if (!wantEject && !wantPull) {
         return;
      }

      // Внутренняя фаза до внешнего выброса: выравниваем выходные слоты
      // (забитый слот 0 не стопает крафт). Обратного перетока «выход → вход» нет:
      // всё, что лежит в выходе (включая сырьё с боковой вставки), вывозим как есть.
      if (output != null) {
         compactOutput(output);
      }
      if (wantEject) {
         ejectIntoNeighbors(level, pos, upgrades.getEjectLevels(), output);
      }
      if (wantPull) {
         pullFromNeighbors(level, pos, upgrades.getPullingLevels(), input);
      }
   }

   private static boolean isEmpty(BasicSimpleContainer container) {
      if (container == null) {
         return true;
      }
      for (int i = 0; i < container.getContainerSize(); i++) {
         if (!container.getItem(i).isEmpty()) {
            return false;
         }
      }
      return true;
   }

   private static boolean hasRoom(BasicSimpleContainer container) {
      for (int i = 0; i < container.getContainerSize(); i++) {
         if (container.getItem(i).getCount() < container.getMaxStackSize()) {
            return true;
         }
      }
      return false;
   }

   /** Выравнивание выхода: сливаем одинаковые стаки к младшим слотам. */
   private static void compactOutput(BasicSimpleContainer output) {
      for (int i = 0; i < output.getContainerSize(); i++) {
         ItemStack base = output.getItem(i);
         if (base.isEmpty()) {
            continue;
         }
         for (int j = i + 1; j < output.getContainerSize(); j++) {
            ItemStack other = output.getItem(j);
            if (other.isEmpty() || !ItemStack.isSameItemSameComponents(base, other)) {
               continue;
            }
            int room = Math.min(output.getMaxStackSize(), base.getMaxStackSize()) - base.getCount();
            if (room <= 0) {
               break;
            }
            int moved = Math.min(room, other.getCount());
            output.setItem(i, base.copyWithCount(base.getCount() + moved));
            other.shrink(moved);
            base = output.getItem(i);
            if (other.isEmpty()) {
               output.setItem(j, ItemStack.EMPTY);
            }
         }
      }
   }

   private static void ejectIntoNeighbors(Level level, BlockPos pos, Map<Direction, Integer> levels, BasicSimpleContainer output) {
      for (SideBudget side : orderedSides(level, levels)) {
         if (side.budget <= 0) {
            continue;
         }
         BlockPos neighborPos = pos.relative(side.direction);
         IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, side.direction.getOpposite());
         if (target == null) {
            continue;
         }
         side.budget = ejectItems(output, target, side.budget);
      }
   }

   private static void pullFromNeighbors(Level level, BlockPos pos, Map<Direction, Integer> levels, BasicSimpleContainer input) {
      for (SideBudget side : orderedSides(level, levels)) {
         if (side.budget <= 0) {
            continue;
         }
         IItemHandler source = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(side.direction), side.direction.getOpposite());
         if (source != null) {
            side.budget = pullItems(source, input, side.budget);
         }
      }
   }

   /**
    * Порядок сторон: явные — по кругу со сдвигом от игрового времени (round-robin,
    * чтобы первая сторона не съедала весь выход), «все стороны» — фиксированный
    * Direction.values() (старые сетапы «вниз в сундук» не ломаются). Явные стороны
    * исключаются из веера «все», чтобы не получать двойной бюджет.
    */
   private static List<SideBudget> orderedSides(Level level, Map<Direction, Integer> levels) {
      List<SideBudget> result = new ArrayList<>();
      Integer allLevel = levels.get(null);
      Set<Direction> explicit = EnumSet.noneOf(Direction.class);
      List<Direction> explicitOrder = new ArrayList<>();
      for (Map.Entry<Direction, Integer> entry : levels.entrySet()) {
         if (entry.getKey() != null && entry.getValue() > 0) {
            explicit.add(entry.getKey());
            explicitOrder.add(entry.getKey());
         }
      }
      if (!explicitOrder.isEmpty()) {
         int start = explicitOrder.size() == 1 ? 0 : (int)((level.getGameTime() >> 2) % explicitOrder.size());
         for (int i = 0; i < explicitOrder.size(); i++) {
            Direction dir = explicitOrder.get((start + i) % explicitOrder.size());
            result.add(new SideBudget(dir, LIMIT * Math.max(1, levels.get(dir))));
         }
      }
      if (allLevel != null && allLevel > 0) {
         int budget = LIMIT * allLevel;
         for (Direction dir : Direction.values()) {
            if (!explicit.contains(dir)) {
               result.add(new SideBudget(dir, budget));
            }
         }
      }
      return result;
   }

   private static final class SideBudget {
      final Direction direction;
      int budget;

      SideBudget(Direction direction, int budget) {
         this.direction = direction;
         this.budget = budget;
      }
   }

   private static int ejectItems(BasicSimpleContainer output, IItemHandler target, int budget) {
      for (int i = 0; i < output.getContainerSize() && budget > 0; i++) {
         ItemStack stack = output.getItem(i);
         if (stack.isEmpty()) {
            continue;
         }

         ItemStack rest = ItemHandlerHelper.insertItemStacked(target, stack.copyWithCount(Math.min(budget, stack.getCount())), false);
         int moved = Math.min(budget, stack.getCount()) - rest.getCount();
         if (moved > 0) {
            output.removeItem(i, moved);
            budget -= moved;
         }
      }
      return budget;
   }

   private static int pullItems(IItemHandler source, BasicSimpleContainer input, int budget) {
      for (int i = 0; i < source.getSlots() && budget > 0; i++) {
         ItemStack available = source.extractItem(i, budget, true);
         if (available.isEmpty()) {
            continue;
         }

         ItemStack rest = insertIntoContainer(input, available);
         int moved = available.getCount() - rest.getCount();
         if (moved > 0) {
            source.extractItem(i, moved, false);
            budget -= moved;
         }
      }
      return budget;
   }

   /** Кладём в наши слоты: сначала в похожие стаки, потом в пустые (с учётом правил слота). */
   private static ItemStack insertIntoContainer(BasicSimpleContainer container, ItemStack stack) {
      ItemStack rest = stack.copy();

      for (int pass = 0; pass < 2 && !rest.isEmpty(); pass++) {
         for (int i = 0; i < container.getContainerSize() && !rest.isEmpty(); i++) {
            ItemStack existing = container.getItem(i);
            boolean sameStack = !existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, rest);
            if (pass == 0 != sameStack) {
               continue;
            }
            // Второй проход — только пустые слоты: иначе чужой остаток «врастёт»
            // в занятый слот с другим предметом (превращение + задвоение).
            if (pass == 1 && !existing.isEmpty()) {
               continue;
            }

            if (!container.canPlaceItem(i, rest)) {
               continue;
            }

            int max = Math.min(container.getMaxStackSize(), existing.isEmpty() ? rest.getMaxStackSize() : existing.getMaxStackSize());
            int room = max - existing.getCount();
            if (room <= 0) {
               continue;
            }

            int moved = Math.min(room, rest.getCount());
            if (existing.isEmpty()) {
               container.setItem(i, rest.copyWithCount(moved));
            } else {
               ItemStack grown = existing.copyWithCount(existing.getCount() + moved);
               container.setItem(i, grown);
            }

            rest.shrink(moved);
         }
      }

      return rest;
   }
}
