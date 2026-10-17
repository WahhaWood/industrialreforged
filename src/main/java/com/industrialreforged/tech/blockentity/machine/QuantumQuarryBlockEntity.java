package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableClass;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.recipe.MaceratorRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.item.tool.FilterData;
import com.industrialreforged.tech.menu.machine.QuantumQuarryMenu;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * Квантовый карьер: добывает руды «из ниоткуда», симулируя генерацию.
 *
 * Цена фиксированная — 4 000 000 RF за руду. Лимита операций в тик нет: сколько энергии
 * хватает по цене блока — столько руд за тик и добудется. Естественные ограничители —
 * запас в буфере (200 млн = 50 руд залпом) и место в выходном буфере.
 * Слоты: сканер КР/ЦР (обязателен), книга зачарований, два улучшения, два фильтра.
 */
public class QuantumQuarryBlockEntity extends AbstractElectricMachineBlockEntity
   implements IUpgradableBlock,
   IContainerWrapper {
   public static final TagKey<Item> ORES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores"));

   private static final int CAPACITY = 200_000_000;
   private static final int ENERGY_PER_OP = 4_000_000;

   public final InvSlotConsumable scannerSlot = new InvSlotConsumable(this, "scanner", 1) {
      @Override
      public boolean canPlaceItem(int index, ItemStack stack) {
         return stack.is(IRItems.SCANNER.get()) || stack.is(IRItems.ADVANCED_SCANNER.get());
      }
   };
   public final InvSlotConsumableClass bookSlot = new InvSlotConsumableClass(this, "book", 1, EnchantedBookItem.class);
   public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, 2);
   public final InvSlotConsumable filterWhiteSlot = new InvSlotConsumable(this, "filterWhite", 1) {
      @Override
      public boolean canPlaceItem(int index, ItemStack stack) {
         return QuantumQuarryBlockEntity.isFilter(stack, false);
      }
   };
   public final InvSlotConsumable filterBlackSlot = new InvSlotConsumable(this, "filterBlack", 1) {
      @Override
      public boolean canPlaceItem(int index, ItemStack stack) {
         return QuantumQuarryBlockEntity.isFilter(stack, true);
      }
   };
   public final InvSlotOutput outputSlot = new InvSlotOutput(this, 18);

   public int blockMined;
   public boolean silkMode;
   /** Сколько руд добыто в прошлом тике (замер, для панели). */
   public int opsLastTick;
   private int activeCooldown;
   private CombinedInvWrapper wrapper;

   public QuantumQuarryBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(CAPACITY, 0, IRBlockEntities.QUANTUM_QUARRY.get(), blockPos, blockState, false);
      this.upgradeSlot.addListener(container -> this.recomputeRates());
      this.recomputeRates();
   }

   /** Можно ли положить этот предмет в слот фильтра с указанным режимом. */
   public static boolean isFilter(ItemStack stack, boolean blacklist) {
      if (stack.getItem() instanceof BlockItem) {
         return !blacklist;
      }

      if (!stack.is(IRItems.ITEM_FILTER.get())) {
         return false;
      }

      FilterData data = stack.get(IRDataComponents.ITEM_FILTER.get());
      boolean isBlacklist = data != null && data.blacklist();
      return isBlacklist == blacklist;
   }

   private static List<ItemStack> filterItems(InvSlotConsumable slot) {
      ItemStack stack = slot.getItem(0);
      if (stack.isEmpty()) {
         return List.of();
      }

      if (stack.is(IRItems.ITEM_FILTER.get())) {
         FilterData data = stack.get(IRDataComponents.ITEM_FILTER.get());
         return data == null ? List.of() : data.items();
      }

      return stack.getItem() instanceof BlockItem blockItem ? List.of(new ItemStack(blockItem)) : List.of();
   }

   public void recomputeRates() {
      this.energy.setCapacity(this.upgradeSlot.getEnergyCapacity(this.energy.getDefCapacity()));
   }

   private int enchantLevel(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key) {
      ItemStack book = this.bookSlot.getItem(0);
      if (book.isEmpty()) {
         return 0;
      }

      ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(book);
      return enchantments.getLevel(this.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key));
   }

   private int fortuneLevel() {
      int fortune = this.enchantLevel(Enchantments.FORTUNE);
      if (this.scannerSlot.getItem(0).is(IRItems.ADVANCED_SCANNER.get())) {
         fortune++;
      }

      return fortune;
   }

   @Override
   public void serverTick() {
      super.serverTick();
      this.recomputeRates();
      // Только выброс из выхода: затягивание в слот сканера запрещено —
      // pulling тащил бы мусор в слот сканера вместо выработки.
      com.industrialreforged.tech.core.util.ItemMoveHelper.tickOutputOnly(
         this.level, this.worldPosition, this.upgradeSlot, this.outputSlot
      );
      boolean active = false;
      this.opsLastTick = 0;
      if (!this.scannerSlot.isEmpty()) {
         long affordable = this.energy.getStored() / ENERGY_PER_OP;
         if (affordable > 0) {
            // Пул руд строим один раз на тик: при десятках операций за тик
            // перестройка списка из тега на каждую руду давала бы лаги.
            List<ItemStack> pool = this.buildPool();
            // Кэш дропа дробилки на тик: поиск рецепта на каждую руду тоже дорог.
            Map<Item, ItemStack> dropCache = new HashMap<>();
            // Лимита операций в тик нет: сколько энергии хватает по 4M за руду —
            // столько и добудем. Цикл рвётся сам: кончилась энергия, пустой пул
            // или забит выход (проверки внутри mineOne, списания при этом нет).
            for (long i = 0; i < affordable; i++) {
               if (!this.mineOne(pool, dropCache)) {
                  break;
               }

               this.opsLastTick++;
               active = true;
            }
         }
      }

      if (this.activeCooldown-- <= 0) {
         this.activeCooldown = 8;
         com.industrialreforged.block.ActiveBlock.setActive(this, active);
      }
   }

   /** Одна операция: списать энергию и положить руду в буфер. */
   private boolean mineOne(List<ItemStack> pool, Map<Item, ItemStack> dropCache) {
      if (this.energy.getStored() < ENERGY_PER_OP) {
         return false;
      }

      if (pool.isEmpty()) {
         return false;
      }

      ItemStack ore = pool.get(this.level.random.nextInt(pool.size()));
      if (ore.isEmpty()) {
         return false;
      }

      ItemStack drop = this.silkMode ? ore.copy() : this.maceratedDrop(ore, dropCache);
      int fortune = this.silkMode ? 0 : this.fortuneLevel();

      for (int i = 0; i < fortune; i++) {
         if (this.level.random.nextFloat() < 0.25F) {
            drop.grow(1);
         }
      }

      if (drop.isEmpty() || !this.outputSlot.canAdd(drop)) {
         return false;
      }

      this.energy.extractForceEnergy(ENERGY_PER_OP);
      this.outputSlot.add(drop);
      this.blockMined++;
      this.markForSave();
      return true;
   }

   /** Пул руд на тик: белый список пропускает, чёрный — исключает. */
   private List<ItemStack> buildPool() {
      Optional<net.minecraft.core.HolderSet.Named<Item>> holders = this.level.registryAccess().registryOrThrow(Registries.ITEM).getTag(ORES);
      if (holders.isEmpty()) {
         return List.of();
      }

      List<ItemStack> all = new ArrayList<>();
      for (Holder<Item> holder : holders.get()) {
         all.add(new ItemStack(holder.value()));
      }

      List<ItemStack> white = filterItems(this.filterWhiteSlot);
      List<ItemStack> pool = new ArrayList<>();
      if (white.isEmpty()) {
         pool.addAll(all);
      } else {
         // Белый список пропускает только руды из c:ores. Без проверки сюда можно
         // было положить любой предмет (например, иридиевую руду) и гнать его
         // по цене обычной операции — мимо всей экономики мода.
         net.minecraft.core.HolderSet.Named<Item> ores = holders.get();
         for (ItemStack allowed : white) {
            if (!allowed.isEmpty() && ores.contains(allowed.getItemHolder())) {
               pool.add(allowed.copyWithCount(1));
            }
         }
      }

      List<ItemStack> black = filterItems(this.filterBlackSlot);
      if (!black.isEmpty()) {
         pool.removeIf(stack -> {
            for (ItemStack blocked : black) {
               if (!blocked.isEmpty() && ItemStack.isSameItem(blocked, stack)) {
                  return true;
               }
            }

            return false;
         });
      }

      return pool;
   }

   /** Обычный режим: то, что дала бы дробилка (иначе сама руда). Прототип из кэша копируем: дроп мутирует (удача). */
   private ItemStack maceratedDrop(ItemStack ore, Map<Item, ItemStack> cache) {
      ItemStack proto = cache.get(ore.getItem());
      if (proto == null) {
         proto = this.computeMaceratedDrop(ore);
         cache.put(ore.getItem(), proto);
      }

      return proto.copy();
   }

   private ItemStack computeMaceratedDrop(ItemStack ore) {
      RecipeInput input = new RecipeInput() {
         @Override
         public ItemStack getItem(int index) {
            return ore;
         }

         @Override
         public int size() {
            return 1;
         }
      };
      return this.level.getRecipeManager()
         .getRecipeFor(IRRecipeTypes.MACERATOR.get(), input, this.level)
         .map(holder -> holder.value().getResultItems().get(0).copy())
         .orElseGet(ore::copy);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(UpgradableProperty.ENERGY_STORAGE, UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.ITEM_CONSUMING);
   }

   /**
    * Generic-панель рисует это значение с подписью «FE/t», а у карьера цена —
    * за операцию (4M за руду), а не за тик. Возвращаем -1 («неприменимо»),
    * чтобы ложная строка не рисовалась; настоящая цена показана в своей панели.
    */
   @Override
   public int getConsumption() {
      return -1;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable net.minecraft.core.Direction direction) {
      if (direction != null) {
         return this.outputSlot.createWrapper();
      }

      if (this.wrapper == null) {
         this.wrapper = new CombinedInvWrapper(
            new IItemHandlerModifiable[]{
               this.scannerSlot.createWrapper(),
               this.bookSlot.createWrapper(),
               this.upgradeSlot.createWrapper(),
               this.filterWhiteSlot.createWrapper(),
               this.filterBlackSlot.createWrapper(),
               this.outputSlot.createWrapper()
            }
         );
      }

      return this.wrapper;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new QuantumQuarryMenu(containerId, playerInventory, this);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACERATOR_START.get();
   }

   @Override
   protected boolean isSoundproofed() {
      return this.upgradeSlot.soundproofing;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.scannerSlot.load(tag, registries);
      this.bookSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.filterWhiteSlot.load(tag, registries);
      this.filterBlackSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.blockMined = tag.getInt("blockMined");
      this.silkMode = tag.getBoolean("silkMode");
      this.recomputeRates();
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.scannerSlot.save(tag, registries);
      this.bookSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      this.filterWhiteSlot.save(tag, registries);
      this.filterBlackSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      tag.putInt("blockMined", this.blockMined);
      tag.putBoolean("silkMode", this.silkMode);
   }
}
