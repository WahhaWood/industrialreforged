package com.industrialreforged.tech.blockentity.base;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.container.InsertOnlyHandler;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotProcessable;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.util.ItemMoveHelper;
import com.industrialreforged.tech.core.recipe.basic.BasicRecipe;
import com.industrialreforged.tech.core.recipe.basic.SimpleInputRecipe;
import com.industrialreforged.tech.core.util.SoundUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStandardMachineBlockEntity<R extends Recipe<?>>
   extends AbstractElectricMachineBlockEntity
   implements IUpgradableBlock,
   MenuProvider,
   RecipeInput,
   ContainerData,
   IClientTick,
   IContainerWrapper {
   public final InvSlotProcessable<?, ?> inputSlot;
   public final InvSlotOutput outputSlot;
   public final InvSlotUpgrade upgradeSlot;
   public final int defaultEnergyConsume;
   public final int defaultOperationLength;
   public int energyConsume;
   public int operationLength;
   public int operationsPerTick;
   public int guiProgress;
   protected int[] progress = new int[0];
   protected Object[] pendingRecipes = new Object[0];
   protected Object soundInstance;
   private SoundUtil.SoundState soundState;
   private int ticksSinceActiveUpdate = 8;
   /** До следующей балансировки входов (раз в секунду, не каждый тик). */
   private int balanceCooldown = 20;
   /** Сдвиг старта обхода слотов: каждый тик начинаем со следующего (честная делёжка энергии). */
   private int processCursor;
   private CombinedInvWrapper wrapper;

   // ------------------------------------------------------------------
   // Тепло машины: растёт при работе, замедляет обработку, выше лимита —
   // машина останавливается и остывает (пассивно или через тепловые трубы).
   // ------------------------------------------------------------------
   public static final int HEAT_MAX = 10000;
   public static final int HEAT_RESUME = 4000;
   /** Нагрев за один завершённый рецепт (вместо прибавки по тикам). */
   public static final int HEAT_PER_RECIPE = 500;
   /**
    * ТЕСТ-БИЛД: нагрев стандартных машин полностью выключен. Машины не копят тепло,
    * не замедляются и не уходят в перегрев; трубы забирать нечего. Реактор, доменная
    * печь и теплогенераторы живут своей системой и не тронуты. Вернуть: false.
    */
   public static boolean HEAT_DISABLED = true;
   private static final int HEAT_SYNC_OVERHEATED = 0x10000;
   protected int machineHeat;
   protected boolean overheated;
   /** Упаковано для GUI: младшие 16 бит — тепло, бит 0x10000 — флаг перегрева. */
   public int guiMachineHeat;
   private double speedCarry;
   /** Честный расход для панели (синк с сервера); на сервере считается вживую. */
   private int displayConsumption;
   /** Голод: начатая работа стоит без энергии (считает сервер, см. serverTick). */
   private boolean starved;

   public AbstractStandardMachineBlockEntity(
      int energyConsume, int length, int outputs, BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      this(128, energyConsume, length, outputs, blockEntityType, blockPos, blockState);
   }

   public AbstractStandardMachineBlockEntity(
      int receiveTick, int energyConsume, int length, int outputs, BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      this(receiveTick, energyConsume, length, outputs, 4, blockEntityType, blockPos, blockState);
   }

   public AbstractStandardMachineBlockEntity(
      int receiveTick, int energyConsume, int length, int outputs, int upgrades, BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(Math.max(Math.max(receiveTick * 2, energyConsume * length), 8000) * outputs, receiveTick * outputs, blockEntityType, blockPos, blockState);
      this.defaultEnergyConsume = this.energyConsume = energyConsume;
      this.defaultOperationLength = this.operationLength = length;
      this.inputSlot = this.createInputSlot(outputs);
      this.outputSlot = new InvSlotOutput(this, outputs);
      this.upgradeSlot = new InvSlotUpgrade(this, upgrades);
      this.upgradeSlot.addListener(container -> this.setOverclockRates());
      this.progress = new int[this.inputSlot.getContainerSize()];
      this.pendingRecipes = new Object[this.inputSlot.getContainerSize()];
   }

   protected abstract InvSlotProcessable<?, ?> createInputSlot(int var1);

   public int size() {
      return this.inputSlot.getContainerSize();
   }

   public ItemStack getItem(int index) {
      return this.inputSlot.getItem(index);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.machineHeat = tag.getInt("machine_heat");
      this.overheated = tag.getBoolean("overheated");
      this.progress = tag.getIntArray("progress");
      if (this.progress.length != this.inputSlot.getContainerSize()) {
         this.progress = new int[this.inputSlot.getContainerSize()];
         this.pendingRecipes = new Object[this.inputSlot.getContainerSize()];
      } else {
         Arrays.fill(this.pendingRecipes, null);
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putInt("machine_heat", this.machineHeat);
      tag.putBoolean("overheated", this.overheated);
      tag.putIntArray("progress", this.progress);
   }

   /** Machines with fluid container slots move fluid here every tick. */
   protected void tickFluidSlots() {
   }

   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      com.industrialreforged.tech.core.util.ItemMoveHelper.tick(
         this.level, this.worldPosition, this.upgradeSlot, this.inputSlot, this.outputSlot
      );
      if (--this.balanceCooldown <= 0) {
         this.balanceCooldown = 20;
         this.balanceInputSlots();
      }
      boolean active = false;

      // Честный порядок: старт обхода сдвигаем каждый тик. При нехватке энергии
      // (куча ускорений) фиксированный порядок 0..n отдавал всё первым слотам,
      // а последние вечно стояли — отсюда «в дробителе-4 работают 2 ячейки из 4».
      // С ротацией все слоты продвигаются равномерно, пусть и медленнее каждый.
      int slots = this.inputSlot.getContainerSize();
      for (int k = 0; k < slots; k++) {
         if (this.processSlot((this.processCursor + k) % slots)) {
            active = true;
         }
      }
      if (slots > 0) {
         this.processCursor = (this.processCursor + 1) % slots;
      }

      // Голод: есть начатая работа, но на полный тик не хватает. Считаем после
      // трат тика, по остатку — иначе панель врёт при куче ускорений.
      this.starved = this.hasProgress() && this.energy.getStored() < this.fullTickCost();

      this.tickMachineHeat();

      int gui = 0;

      for (int i = 0; i < this.progress.length; i++) {
         int value = this.operationLength <= 0 ? 0 : (int)((long)this.progress[i] * 255L / this.operationLength);
         gui |= (Mth.clamp(value, 0, 255) & 0xFF) << (i * 8);
      }

      if (this.guiProgress != gui) {
         this.guiProgress = gui;
      }

      if (--this.ticksSinceActiveUpdate <= 0) {
         this.ticksSinceActiveUpdate = 8;
         ActiveBlock.setActive(this, active);
      }
   }

   /** Есть ли начатая работа хоть в одном слоте. Цикл, а не стрим: клиентский тик зовёт его каждой машине 20 раз в секунду. */
   protected boolean hasProgress() {
      for (int p : this.progress) {
         if (p > 0) {
            return true;
         }
      }

      return false;
   }

   /** Runs one tick of the machine's work loop for the given input slot.
    *
    * @return true while the machine is actually consuming energy / working
    */
   protected boolean processSlot(int index) {
      if (!HEAT_DISABLED && this.overheated) {
         return false;
      }

      Object pending = this.pendingRecipes[index];

      if (pending == null) {
         R recipe = this.getRecipe(index);
         // Стартовый гейт — по полному первому тику (с пакетом и наценкой),
         // а не по голому energyConsume: иначе слот стартует и тут же встаёт
         // с замороженным прогрессом (старт-глох при ускорениях + укреплении).
         if (recipe == null || !this.canProcess(recipe) || this.energy.getStored() < this.fullTickCost()) {
            this.progress[index] = 0;
            return false;
         }

         this.energy.extractForceEnergy(this.energyConsume);
         this.pendingRecipes[index] = recipe;
         this.progress[index] = 1;
         this.markForSave();
         return true;
      }

      // «Укрепление» работает пакетом: операция длится как одна, но по завершении
      // обрабатывает сразу operationsPerTick предметов (4 за каждое улучшение).
      // Пакет и наценка сидят в fullTickCost(), здесь остаётся только шаг тепла.
      float heatFactor = HEAT_DISABLED ? 1.0F : this.getHeatSpeedFactor();
      int step = 1;

      if (heatFactor < 1.0F) {
         // тепло режет скорость: копим дробный остаток, чтобы средний темп был точным
         double exact = heatFactor + this.speedCarry;
         step = (int)exact;
         this.speedCarry = exact - step;
         if (step <= 0) {
            return true;
         }
      } else {
         this.speedCarry = 0.0;
      }

      int cost = (int)Math.min(Integer.MAX_VALUE, (long)this.fullTickCost() * step);
      if (this.energy.getStored() < cost) {
         return false;
      }

      this.energy.extractForceEnergy(cost);
      this.progress[index] += step;

      if (this.progress[index] >= this.operationLength) {
         R recipe = (R)pending;
         if (!this.outputSlot.canAdd(this.getRecipeResults(recipe))) {
            // Выход забит: ждём место. Прогресс держим ровно на operationLength, а не
            // копим дальше — иначе он растёт без потолка, переполняет short в
            // setOverclockRates() и переезжает туда уже отрицательным.
            this.progress[index] = this.operationLength;
            return false;
         }

         this.progress[index] = 0;
         this.pendingRecipes[index] = null;
         int actual = this.inputSlot.assemble(index, this.getBatchSize(index, recipe));
         if (actual <= 0) {
            return false;
         }

         // Готовое — сразу к соседям, мимо троттлинга переноса: выталкиватель
         // не ждёт следующего прохода раз в 4 тика.
         ItemMoveHelper.moveNow(
            this.level, this.worldPosition, this.upgradeSlot, this.inputSlot, this.outputSlot
         );

         this.addHeat(HEAT_PER_RECIPE * actual);
      }

      this.markForSave();
      return true;
   }

   /**
    * Автобаланс входов: одинаковые предметы делятся поровну между свободными слотами.
    * Игрок кидает всё в один слот — дальше слоты разбирают сами и работают параллельно.
    * Правила — как auto-sort фабрик Mekanism (sortInventory/distributeItems):
    * только входы группами одинаковых предметов (выходы не трогаем вообще);
    * минимум на слот — порция рецепта (не капаем крохи, с которыми рецепт не стартует);
    * пустые слоты подключаем, только если хватает минимум на две порции;
    * группу без рецепта или с забитым выходом не раздаём (не кормим дорожку,
    * которая не сможет выдать результат); слоты с pending-рецептом — только источники
    * излишка (резерв под завершение операции оставляем). Потерь нет: делим целое
    * (база + сдача кусками с клампом под максимум стака), занятым оставляем их вклад.
    * Разные предметы не смешиваем. Уже ровно — ничего не пишем, чтобы не пачкать
    * чанк через listener.
    */
   protected void balanceInputSlots() {
      int n = this.inputSlot.getContainerSize();
      if (n < 2) {
         return;
      }
      int reserve = Math.max(1, this.operationsPerTick);
      boolean[] busy = new boolean[n];
      for (int i = 0; i < n && i < this.pendingRecipes.length; i++) {
         busy[i] = this.pendingRecipes[i] != null;
      }
      boolean[] done = new boolean[n];
      for (int i = 0; i < n; i++) {
         if (busy[i] || done[i]) {
            continue;
         }
         // якорь группы: первый непустой свободный слот
         ItemStack sample = this.inputSlot.getItem(i);
         if (sample.isEmpty()) {
            continue;
         }
         // Порция рецепта группы (как minPerSlot у Mekanism): меньше порции в слот
         // не кладём. Без рецепта или с забитым выходом не раздаём вовсе —
         // не кормим дорожку, которая не сможет выдать результат.
         R recipe = this.getRecipe(i);
         if (recipe == null || !this.canProcess(recipe)) {
            continue;
         }
         int minPerSlot = 1;
         if (recipe instanceof SimpleInputRecipe simple) {
            minPerSlot = Math.max(1, simple.getIngredient().count());
         }
         // группа: свободные (пустые или с тем же предметом) — получатели;
         // занятые с тем же предметом — источники излишка сверх резерва.
         java.util.List<Integer> receivers = new java.util.ArrayList<>();
         boolean[] inGroup = new boolean[n];
         int total = 0;
         int locked = 0;
         for (int j = 0; j < n; j++) {
            if (done[j]) {
               continue;
            }
            ItemStack stack = this.inputSlot.getItem(j);
            if (stack.isEmpty()) {
               if (!busy[j]) {
                  receivers.add(j);
                  inGroup[j] = true;
                  done[j] = true;
               }
               continue;
            }
            if (!ItemStack.isSameItemSameComponents(stack, sample)) {
               continue;
            }
            done[j] = true;
            inGroup[j] = true;
            total += stack.getCount();
            if (busy[j]) {
               locked += Math.min(stack.getCount(), reserve);
            } else {
               receivers.add(j);
            }
         }
         if (receivers.isEmpty() || total / minPerSlot <= 1) {
            continue;
         }
         int max = Math.min(sample.getMaxStackSize(), this.inputSlot.getMaxStackSize());
         int pool = total - locked;
         if (pool > receivers.size() * max) {
            continue; // переполнено вне правил контейнера — не наше дело, главное не удалить
         }
         // Базу округляем вниз до целых порций, недобор уходит в сдачу, а сдачу
         // раздаём кусками не больше порции (с клампом под максимум стака, чтобы
         // не переполнить почти полный слот). Сохранение целое: база × слоты + сдача.
         int perSlot = pool / receivers.size();
         int remainder = pool % receivers.size();
         if (minPerSlot > 1) {
            int drip = perSlot % minPerSlot;
            perSlot -= drip;
            remainder += drip * receivers.size();
         }
         for (int slot : receivers) {
            int want = perSlot;
            if (remainder > 0) {
               int chunk = Math.min(remainder, Math.min(minPerSlot, max - perSlot));
               want += chunk;
               remainder -= chunk;
            }
            ItemStack stack = this.inputSlot.getItem(slot);
            if (stack.getCount() == want
               && (want == 0 || ItemStack.isSameItemSameComponents(stack, sample))) {
               continue;
            }
            // Пишем только через setItem: прямая мутация стака мимо listener'а
            // не позовёт markForSave и не уедет клиентам в меню.
            if (want == 0) {
               this.inputSlot.setItem(slot, ItemStack.EMPTY);
            } else {
               ItemStack moved = sample.copy();
               moved.setCount(want);
               this.inputSlot.setItem(slot, moved);
            }
         }
         // Занятые ужимаем до резерва, но никогда не докладываем и не забираем последнее.
         for (int j = 0; j < n; j++) {
            if (!busy[j] || !inGroup[j]) {
               continue;
            }
            ItemStack stack = this.inputSlot.getItem(j);
            int keep = Math.min(stack.getCount(), reserve);
            if (!stack.isEmpty() && stack.getCount() != keep) {
               ItemStack left = stack.copy();
               left.setCount(keep);
               this.inputSlot.setItem(j, left);
            }
         }
      }
   }

   /**
    * Сколько предметов обработать за одну операцию (пакет укрепления) с учётом
    * остатка во входном слоте и места в выходных. Без улучшения — 1.
    */
   protected int getBatchSize(int index, R recipe) {
      int wanted = Math.max(1, this.operationsPerTick);
      if (wanted <= 1 || recipe == null) {
         return 1;
      }

      int perCraft = 1;
      if (recipe instanceof SimpleInputRecipe simple) {
         perCraft = Math.max(1, simple.getIngredient().count());
      }

      ItemStack input = this.inputSlot.getItem(index);
      int byInput = input.getCount() / perCraft;
      int byOutput = this.maxOutputCrafts(this.getRecipeResults(recipe), wanted);
      return Math.max(1, Math.min(wanted, Math.min(byInput, byOutput)));
   }

   /** Сколько наборов результатов влезет в выходные слоты (от wanted вниз, минимум 1). */
   protected int maxOutputCrafts(List<ItemStack> results, int wanted) {
      if (results.isEmpty()) {
         return wanted;
      }

      for (int n = wanted; n >= 1; n--) {
         List<ItemStack> stacks = new ArrayList<>(results.size() * n);
         for (int i = 0; i < n; i++) {
            for (ItemStack result : results) {
               stacks.add(result.copy());
            }
         }

         if (this.outputSlot.simulationAdd(stacks)) {
            return n;
         }
      }

      return 1;
   }

   /** Результаты рецепта в виде списка (для проверки места в выходе). */
   protected List<ItemStack> getRecipeResults(R recipe) {
      if (recipe instanceof BasicRecipe basic) {
         return basic.getResultItems();
      }

      ItemStack result = recipe.getResultItem(RegistryAccess.EMPTY);
      return result.isEmpty() ? List.of() : List.of(result);
   }

   /** Добавляет тепло (за завершённый рецепт или извне). */
   public void addHeat(int amount) {
      if (!HEAT_DISABLED && amount > 0) {
         this.machineHeat = Math.min(this.getMaxMachineHeat(), this.machineHeat + amount);
      }
   }

   private void tickMachineHeat() {
      if (HEAT_DISABLED) {
         // Старые сейвы могли сохраниться горячими — остужаем раз и навсегда.
         if (this.machineHeat != 0 || this.overheated) {
            this.machineHeat = 0;
            this.overheated = false;
            this.guiMachineHeat = 0;
            this.markForSave();
         }
         return;
      }
      if (this.overheated) {
         if (this.machineHeat <= HEAT_RESUME) {
            this.overheated = false;
         }
      }

      if (this.machineHeat >= this.getMaxMachineHeat() && !this.overheated) {
         this.overheated = true;
      }

      if (this.machineHeat > 0) {
         this.machineHeat--;
      }

      int sync = this.machineHeat | (this.overheated ? HEAT_SYNC_OVERHEATED : 0);
      if (this.guiMachineHeat != sync) {
         this.guiMachineHeat = sync;
         this.markForSave();
      }
   }

   /** Множитель скорости обработки: 1.0 до HEAT_RESUME, линейно до 0.5 на пике. */
   public float getHeatSpeedFactor() {
      if (this.machineHeat <= HEAT_RESUME) {
         return 1.0F;
      }

      return 1.0F - 0.5F * (this.machineHeat - HEAT_RESUME) / (float)(this.getMaxMachineHeat() - HEAT_RESUME);
   }

   protected int getMaxMachineHeat() {
      return HEAT_MAX;
   }

   /** Забор тепла внешним охлаждением (тепловые трубы). Возвращает фактически взятое. */
   public int pullHeat(int amount) {
      if (HEAT_DISABLED) {
         return 0;
      }
      int take = Math.min(amount, this.machineHeat);
      if (take > 0) {
         this.machineHeat -= take;
         if (this.machineHeat < HEAT_RESUME) {
            this.overheated = false;
         }

         this.markForSave();
      }

      return take;
   }

   public float getHeatRatio() {
      return (this.guiMachineHeat & 0xFFFF) / (float)HEAT_MAX;
   }

   public int getMachineHeat() {
      return this.guiMachineHeat & 0xFFFF;
   }

   public boolean isOverheated() {
      return (this.guiMachineHeat & HEAT_SYNC_OVERHEATED) != 0;
   }

   public net.minecraft.network.chat.MutableComponent getHeatTooltip() {
      net.minecraft.network.chat.MutableComponent base = net.minecraft.network.chat.Component.translatable("text.industrialreforged.heat_title")
         .append(": " + this.getMachineHeat() + " / " + this.getMaxMachineHeat());
      if (this.isOverheated()) {
         base = base.append(net.minecraft.network.chat.CommonComponents.NEW_LINE)
            .append(net.minecraft.network.chat.Component.translatable("text.industrialreforged.overheated"));
      }

      return base;
   }

   public float getProgress() {
      return this.getProgress(0);
   }

   public float getProgress(int index) {
      return (this.guiProgress >> index * 8 & 0xFF) / 255.0F;
   }

   public void onLoad() {
      super.onLoad();
      this.upgradeSlot.setChanged();
   }

   public void onChunkUnloaded() {
      if (this.level != null && this.level.isClientSide) {
         this.stopSound();
      }

      super.onChunkUnloaded();
   }

   public void setRemoved() {
      if (this.level != null && this.level.isClientSide) {
         this.stopSound();
      }

      super.setRemoved();
   }

   public void clientTick() {
      if (this.upgradeSlot.soundproofing) {
         this.stopSound();
      } else {
         SoundUtil.SoundState currentState = this.isActive()
            ? SoundUtil.SoundState.RUNNING
            : (
               this.hasProgress() && this.energy.getStored() > 0
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
   }

   private void stopSound() {
      SoundUtil.stop(this.soundInstance);
      this.soundInstance = null;
      this.soundState = SoundUtil.SoundState.IDLE;
   }

   public boolean canProcess(R recipe) {
      return recipe instanceof BasicRecipe r ? this.outputSlot.canAdd(r.getResultItems()) : this.outputSlot.canAdd(recipe.getResultItem(RegistryAccess.EMPTY));
   }

   @Nullable
   public R getRecipe(int index) {
      return (R)this.inputSlot.process(index);
   }

   public void setOverclockRates() {
      double[] d = new double[this.progress.length];

      for (int i = 0; i < d.length; i++) {
 d[i] = (double)this.progress[i] / this.operationLength;
      }

      this.operationsPerTick = this.upgradeSlot.getOperationsPerTick();
      this.operationLength = this.upgradeSlot.getOperationLength(this.defaultOperationLength);
      this.energyConsume = this.upgradeSlot.getEnergyDemand(this.defaultEnergyConsume);
      // Буфер под выросший спрос: каждое ускорение удваивает расход (×2^n), а ёмкость
      // раньше росла только от хранилищ. Без этого буфера хватало на пару тиков, и машина
      // дёргалась: слоты стартовали и тут же вставали по голоду. Полка = один полный
      // прогон всех слотов, дальше ограничивает уже сеть, а не внутренний бак.
      long demandBuffer = (long)this.energyConsume * Math.max(1, this.operationLength) * Math.max(1, this.inputSlot.getContainerSize());
      int capacity = this.upgradeSlot.getEnergyCapacity(this.energy.getDefCapacity());
      if (demandBuffer > capacity) {
         capacity = demandBuffer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)demandBuffer;
      }
      this.energy.setCapacity(capacity);

      for (int i = 0; i < d.length; i++) {
         // Каст именно int: короткий здесь ломал машину. progress — это int[], и если
         // прогресс успел перерасти 32767 (см. ветку «выход забит» ниже), то (short)
         // заворачивал его в минус. Дальше `progress >= operationLength` не выполнялось
         // никогда: машина переставала завершать рецепт навсегда, но оставалась активной
         // и продолжала списывать энергию. Это была ошибка, а не замысел.
         this.progress[i] = (int)Math.floor(d[i] * this.operationLength + 0.1);
      }
   }

   @Nullable
   public SoundEvent getStartSoundFile() {
      return null;
   }

   @Nullable
   public SoundEvent getInterruptSoundFile() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setConsumption(int value) {
      this.energyConsume = value;
   }

   @Override
   public int getConsumption() {
      return this.energyConsume;
   }

   /**
    * Полный тиковый расход: база × пакет укрепления × 1.5. Именно столько тик
    * реально списывает (стартовый гейт и рабочий расход выше), а панель раньше
    * показывала голую базу — отсюда молчаливый голод «×6».
    */
   public int fullTickCost() {
      return (int)Math.min(
         Integer.MAX_VALUE,
         Math.round((double)this.energyConsume * Math.max(1, this.operationsPerTick)
            * this.upgradeSlot.getStrengtheningEnergyMultiplier())
      );
   }

   @Override
   public int getDisplayConsumption() {
      // Клиент рисует синкнутое значение; сервер считает вживую. Раздельный
      // канал нужен, потому что базовый energyConsume уже занят своим синком.
      if (this.level != null && this.level.isClientSide) {
         return this.displayConsumption;
      }
      return this.fullTickCost();
   }

   @Override
   public void setDisplayConsumption(int value) {
      this.displayConsumption = value;
   }

   @Override
   public boolean isStarved() {
      return this.starved;
   }

   @Override
   public void setStarved(boolean value) {
      this.starved = value;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setTimeCreation(int value) {
      this.operationLength = value;
   }

   @Override
   public int getTimeCreation() {
      return this.operationLength;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.outputSlot.createWrapper()});
         }
         return this.wrapper;
      }

      return switch (direction) {
         // Верх — только вставка во вход: сосед сверху не выгребает сырьё.
         case UP -> new InsertOnlyHandler(this.inputSlot.createWrapper());
         case DOWN -> this.outputSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> {
            if (this.wrapper == null) {
               this.wrapper = new CombinedInvWrapper(new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.outputSlot.createWrapper()});
            }
            yield this.wrapper;
         }
      };
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> this.energyConsume;
         case 1 -> this.operationLength;
         case 2 -> this.guiProgress;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.energyConsume = value;
            break;
         case 1:
            this.operationLength = value;
            break;
         case 2:
            this.guiProgress = value;
      }
   }

   public int getCount() {
      return 3;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putBoolean("soundproofing", this.upgradeSlot.soundproofing);
      return tag;
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.upgradeSlot.soundproofing = tag.getBoolean("soundproofing");
   }
}
