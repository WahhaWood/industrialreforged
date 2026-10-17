package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableClass;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.menu.machine.OreBreakerMenu;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * Рудокрушитель: ломает руды так, как это сделал бы игрок киркой.
 *
 * В 9 входных слотов кладутся только руды, в слот книги — зачарованная книга:
 * Удача повышает дроп, Шёлковое касание выдаёт саму руду, Эффективность ускоряет работу.
 * Базовая скорость — 3 секунды на руду, все девять слотов работают параллельно.
 */
public class OreBreakerBlockEntity extends AbstractElectricMachineBlockEntity
   implements IUpgradableBlock,
   IClientTick,
   IContainerWrapper {
   public static final TagKey<Item> ORES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "ores"));

   private static final int CAPACITY = 64_000;
   private static final int ENERGY_PER_TICK = 128;
   private static final int BASE_TICKS = 60;
   private static final int LANES = 9;

   public final InvSlotConsumable inputSlot = new InvSlotConsumable(this, "input", LANES) {
      @Override
      public boolean canPlaceItem(int index, ItemStack stack) {
         return stack.is(ORES);
      }
   };
   public final InvSlotConsumableClass bookSlot = new InvSlotConsumableClass(this, "book", 1, EnchantedBookItem.class);
   public final InvSlotOutput outputSlot = new InvSlotOutput(this, 9);
   public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, 4);

   private final int[] progress = new int[LANES];
   /** Сдвиг старта обхода дорожек: честная делёжка энергии (см. стандартные машины). */
   private int laneCursor;
   public int opTime = BASE_TICKS;
   public int guiProgress;
   private int energyPerTick = ENERGY_PER_TICK;
   private int activeCooldown;
   private CombinedInvWrapper wrapper;

   public OreBreakerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(CAPACITY, 0, IRBlockEntities.ORE_BREAKER.get(), blockPos, blockState, false);
      this.upgradeSlot.addListener(container -> this.recomputeRates());
      this.recomputeRates();
   }

   private Object soundInstance;
   private SoundUtil.SoundState soundState;

   @Override
   public void clientTick() {
      if (this.upgradeSlot.soundproofing) {
         SoundUtil.stop(this.soundInstance);
         this.soundInstance = null;
         this.soundState = SoundUtil.SoundState.IDLE;
         return;
      }

      SoundUtil.SoundState currentState = this.isActive()
         ? SoundUtil.SoundState.RUNNING
         : (
            this.energy.getStored() > 0 && java.util.Arrays.stream(this.progress).anyMatch(value -> value > 0)
               ? SoundUtil.SoundState.INTERRUPTED
               : SoundUtil.SoundState.IDLE
         );
      if (currentState != this.soundState) {
         this.soundState = currentState;
         switch (currentState) {
            case RUNNING:
               this.soundInstance = SoundUtil.playLoop(this, this.getStartSoundFile(), this.soundInstance);
               break;
            case IDLE:
               SoundUtil.stop(this.soundInstance);
               this.soundInstance = null;
               break;
            case INTERRUPTED:
               this.soundInstance = SoundUtil.play(this, this.getInterruptSoundFile(), this.soundInstance);
         }
      }
   }

   /** Звуки рудолома: у него своя база, поэтому методы не переопределение, а свои. */
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACERATOR_START.get();
   }

   public SoundEvent getInterruptSoundFile() {
      return IRSounds.SOUND_MACHINE_INTERRUPT.get();
   }

   public void recomputeRates() {
      int efficiency = Math.min(5, this.enchantLevel(Enchantments.EFFICIENCY));
      this.opTime = Math.max(
         1,
         (int)Math.round(this.upgradeSlot.getOperationLength(BASE_TICKS) * Math.pow(0.85, efficiency))
      );
      this.energyPerTick = Math.max(1, (int)Math.round(this.upgradeSlot.getEnergyDemand(ENERGY_PER_TICK)));
      // Буфер под спрос: как в стандартных машинах — иначе с ускорениями/укреплением
      // бак осушается за пару тиков и дорожки дёргаются.
      long draw = Math.round(this.energyPerTick * Math.max(1, this.upgradeSlot.getOperationsPerTick())
         * this.upgradeSlot.getStrengtheningEnergyMultiplier());
      long demandBuffer = draw * Math.max(1, this.opTime) * LANES;
      int capacity = this.energy.getDefCapacity();
      if (demandBuffer > capacity) {
         capacity = demandBuffer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)demandBuffer;
      }
      this.energy.setCapacity(capacity);
   }

   /**
    * Ровная раскладка руд по дорожкам (группы по типу руды). Забирать можно из любой
    * дорожки: прогресс при этом не ломается — lane доедает уменьшенный остаток,
    * потерь нет. Пишем только отличия, чтобы не пачкать чанк.
    */
   private void balanceLanes() {
      boolean[] done = new boolean[LANES];
      boolean changed = false;
      for (int i = 0; i < LANES; i++) {
         if (done[i]) {
            continue;
         }
         ItemStack sample = this.inputSlot.getItem(i);
         if (sample.isEmpty()) {
            continue;
         }
         java.util.List<Integer> group = new java.util.ArrayList<>();
         int total = 0;
         for (int j = 0; j < LANES; j++) {
            if (done[j]) {
               continue;
            }
            ItemStack stack = this.inputSlot.getItem(j);
            if (stack.isEmpty() || ItemStack.isSameItemSameComponents(stack, sample)) {
               group.add(j);
               total += stack.getCount();
               done[j] = true;
            }
         }
         if (group.size() < 2 || total < 2) {
            continue;
         }
         int max = sample.getMaxStackSize();
         if (total > group.size() * max) {
            continue;
         }
         int base = total / group.size();
         int extra = total - base * group.size();
         for (int slot : group) {
            int want = base + (extra > 0 ? 1 : 0);
            if (extra > 0) {
               extra--;
            }
            ItemStack stack = this.inputSlot.getItem(slot);
            if (stack.getCount() == want
               && (want == 0 || ItemStack.isSameItemSameComponents(stack, sample))) {
               continue;
            }
            if (want == 0) {
               this.inputSlot.setItem(slot, ItemStack.EMPTY);
            } else {
               this.inputSlot.setItem(slot, sample.copyWithCount(want));
            }
            changed = true;
         }
      }
      if (changed) {
         this.markForSave();
      }
   }

   private int enchantLevel(net.minecraft.resources.ResourceKey<Enchantment> key) {
      ItemStack book = this.bookSlot.getItem(0);
      if (book.isEmpty() || this.level == null) {
         return 0;
      }

      ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(book);
      return enchantments.getLevel(this.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key));
   }

   @Override
   public void serverTick() {
      super.serverTick();
      this.recomputeRates();
      com.industrialreforged.tech.core.util.ItemMoveHelper.tick(
         this.level, this.worldPosition, this.upgradeSlot, this.inputSlot, this.outputSlot
      );
      boolean active = false;
      // «Укрепление» — пакет: за операцию ломается сразу strengthening × 4 руд,
      // расход энергии при этом растёт (+50% за каждое улучшение)
      int batch = Math.max(1, this.upgradeSlot.getOperationsPerTick());
      int draw = (int)Math.round(this.energyPerTick * batch * this.upgradeSlot.getStrengtheningEnergyMultiplier());
      int maxProgress = 0;
      // Раскладываем руды по дорожкам поровну, чтобы один стак шёл параллельно.
      // Старый trickle (1 шт/тик в первую пустую) не поспевал за пакетами «Укрепления»
      // и ускорениями: дальние дорожки вечно голодали.
      this.balanceLanes();

      for (int k = 0; k < LANES; k++) {
         int lane = (this.laneCursor + k) % LANES;
         if (this.inputSlot.getItem(lane).isEmpty()) {
            this.progress[lane] = 0;
            continue;
         }

         if (this.energy.getStored() < draw) {
            maxProgress = Math.max(maxProgress, this.progress[lane]);
            continue;
         }

         this.energy.extractForceEnergy(draw);
         this.progress[lane] += 1;
         if (this.progress[lane] >= this.opTime) {
            if (this.processLane(lane, batch)) {
               this.progress[lane] = 0;
               active = true;
            } else {
               this.progress[lane] = this.opTime - 1;
            }
         }

         maxProgress = Math.max(maxProgress, this.progress[lane]);
      }
      this.laneCursor = (this.laneCursor + 1) % LANES;

      this.guiProgress = maxProgress;
      if (this.activeCooldown-- <= 0) {
         this.activeCooldown = 8;
         com.industrialreforged.block.ActiveBlock.setActive(this, active);
      }
   }

   /** Ломает до {@code batch} руд из слота, сколько влезет в выход (пакет «Укрепления»). */
   private boolean processLane(int lane, int batch) {
      ItemStack ore = this.inputSlot.getItem(lane);
      if (ore.isEmpty()) {
         return false;
      }

      int n = Math.min(batch, ore.getCount());
      List<List<ItemStack>> perOre = new java.util.ArrayList<>(n);
      for (int i = 0; i < n; i++) {
         List<ItemStack> drops = this.breakOre(ore);
         if (drops.isEmpty()) {
            return false;
         }
         perOre.add(drops);
      }

      // если в выход влезает не всё — уменьшаем пакет, но хотя бы одну руду ломаем
      while (n > 1) {
         List<ItemStack> flat = new java.util.ArrayList<>();
         for (int i = 0; i < n; i++) {
            flat.addAll(perOre.get(i));
         }
         if (this.outputSlot.canAdd(flat)) {
            break;
         }
         n--;
      }

      List<ItemStack> flat = new java.util.ArrayList<>();
      for (int i = 0; i < n; i++) {
         flat.addAll(perOre.get(i));
      }
      if (!this.outputSlot.canAdd(flat)) {
         return false;
      }

      for (ItemStack drop : flat) {
         this.outputSlot.add(drop);
      }

      this.inputSlot.shrink(lane, n);
      this.markForSave();
      return true;
   }

   /** Дропы как от игрока с киркой: удача и шёлк берутся из книги. */
   private List<ItemStack> breakOre(ItemStack ore) {
      int fortune = this.enchantLevel(Enchantments.FORTUNE);
      boolean silk = this.enchantLevel(Enchantments.SILK_TOUCH) > 0;
      if (ore.getItem() instanceof BlockItem blockItem && this.level instanceof ServerLevel serverLevel) {
         ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
         if (fortune > 0 || silk) {
            Holder<Enchantment> fortuneHolder = serverLevel.registryAccess()
               .lookupOrThrow(Registries.ENCHANTMENT)
               .getOrThrow(Enchantments.FORTUNE);
            Holder<Enchantment> silkHolder = serverLevel.registryAccess()
               .lookupOrThrow(Registries.ENCHANTMENT)
               .getOrThrow(Enchantments.SILK_TOUCH);
            EnchantmentHelper.updateEnchantments(tool, mutable -> {
               if (fortune > 0) {
                  mutable.set(fortuneHolder, fortune);
               }

               if (silk) {
                  mutable.set(silkHolder, 1);
               }
            });
         }

         List<ItemStack> drops = Block.getDrops(
            blockItem.getBlock().defaultBlockState(), serverLevel, this.worldPosition, null, null, tool
         );
         if (!drops.isEmpty()) {
            return drops;
         }
      }

      ItemStack result = ore.copyWithCount(1);

      for (int i = 0; i < fortune; i++) {
         if (this.level.random.nextFloat() < 0.25F) {
            result.grow(1);
         }
      }

      return List.of(result);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.PROCESSING, UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.STRENGTHENING
      );
   }

   @Override
   public int getConsumption() {
      return (int)Math.round(this.energyPerTick * Math.max(1, this.upgradeSlot.getOperationsPerTick())
         * this.upgradeSlot.getStrengtheningEnergyMultiplier());
   }

   @Override
   public int getTimeCreation() {
      return this.opTime;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable net.minecraft.core.Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(
               new IItemHandlerModifiable[]{
                  this.inputSlot.createWrapper(),
                  this.bookSlot.createWrapper(),
                  this.outputSlot.createWrapper(),
                  this.upgradeSlot.createWrapper()
               }
            );
         }
         return this.wrapper;
      }

      return switch (direction) {
         case UP -> this.inputSlot.createWrapper();
         case DOWN -> this.outputSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.outputSlot.createWrapper()}
         );
      };
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new OreBreakerMenu(containerId, playerInventory, this);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputSlot.load(tag, registries);
      this.bookSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      int[] saved = tag.getIntArray("progress");

      for (int i = 0; i < LANES; i++) {
         this.progress[i] = i < saved.length ? saved[i] : 0;
      }

      this.recomputeRates();
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputSlot.save(tag, registries);
      this.bookSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putIntArray("progress", this.progress);
   }

}
