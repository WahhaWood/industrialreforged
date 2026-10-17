package com.industrialreforged.tech.container;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.IUpgradeItem;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.item.upgrade.ItemUpgradeModule;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class InvSlotUpgrade extends BasicSimpleContainer {
   public double processTimeMultiplier;
   public double energyDemandMultiplier;
   public int augmentation;
   public int energyStorageUpgrades;
   public int extraTier;
   public int strengthening;
   public int particleAccelerator;
   public boolean soundproofing;
   public boolean invertRedstonePower;
   /**
    * Выталкиватель/затягиватель: сторона → уровень (число предметов в стаке).
    * Ключ null = режим «все стороны». Несколько улучшений на разные стороны
    * складываются, а не перезаписывают друг друга (старое «побеждает последний
    * слот»). Только чтение снаружи — пересчёт в setChanged().
    */
   private final Map<Direction, Integer> ejectLevels = new LinkedHashMap<>();
   private final Map<Direction, Integer> pullingLevels = new LinkedHashMap<>();
   private int amountFluidEject;
   private Direction fluidEject;
   private boolean hasFluidEject;
   private int amountFluidPulling;
   private Direction fluidPulling;
   private boolean hasFluidPulling;
   private final Set<UpgradableProperty> propertySet;

   public <T extends BaseBlockEntity & IUpgradableBlock> InvSlotUpgrade(T entity, int size) {
      this(entity, "upgrade", size, entity.getUpgradableProperties());
   }

   public <T extends BaseBlockEntity & IUpgradableBlock> InvSlotUpgrade(T entity, String name, int size) {
      this(entity, name, size, entity.getUpgradableProperties());
   }

   public InvSlotUpgrade(BaseBlockEntity entity, int size, Set<UpgradableProperty> propertySet) {
      this(entity, "upgrade", size, propertySet);
   }

   public InvSlotUpgrade(BaseBlockEntity entity, String name, int size, Set<UpgradableProperty> propertySet) {
      super(entity, name, size);
      this.resetRates();
      this.setCanInput(false);
      this.setCanOutput(false);
      this.propertySet = propertySet;
   }

   /** Подходит ли улучшение механизму по свойствам (без правила «одно укрепление»). */
   private boolean isSuitable(ItemStack itemStack) {
      return itemStack.getItem() instanceof IUpgradeItem upgradeItem && upgradeItem.isSuitableFor(itemStack, this.propertySet);
   }

   public boolean canPlaceItem(int index, ItemStack itemStack) {
      if (!this.isSuitable(itemStack)) {
         return false;
      }

      // «Укрепление» — только одно на механизм
      if (itemStack.getItem() instanceof ItemUpgradeModule module && module.type == ItemUpgradeModule.UpgradeType.STRENGTHENING) {
         for (int i = 0; i < this.getContainerSize(); i++) {
            if (i != index
               && this.getItem(i).getItem() instanceof ItemUpgradeModule other
               && other.type == ItemUpgradeModule.UpgradeType.STRENGTHENING) {
               return false;
            }
         }
      }

      return true;
   }

   public void setChanged() {
      this.resetRates();

      for (int i = 0; i < this.getContainerSize(); i++) {
         ItemStack stack = this.getItem(i);
         if (!stack.isEmpty() && this.isSuitable(stack) && stack.getItem() instanceof ItemUpgradeModule upgradeModule) {
            switch (upgradeModule.type) {
               case ENERGY_STORAGE:
                  this.energyStorageUpgrades = Math.min(8, this.energyStorageUpgrades + stack.getCount());
                  break;
               case OVERCLOCKER_1_LEVEL:
                  this.augmentation = this.augmentation + stack.getCount();
                  this.processTimeMultiplier = this.processTimeMultiplier * Math.pow(0.7, stack.getCount());
                  this.energyDemandMultiplier = this.energyDemandMultiplier * Math.pow(2.0, stack.getCount());
                  break;
               case OVERCLOCKER_2_LEVEL:
                  this.augmentation = this.augmentation + stack.getCount();
                  this.processTimeMultiplier = this.processTimeMultiplier * Math.pow(0.6, stack.getCount());
                  this.energyDemandMultiplier = this.energyDemandMultiplier * Math.pow(2.0, stack.getCount());
                  break;
               case OVERCLOCKER_3_LEVEL:
                  this.augmentation = this.augmentation + stack.getCount();
                  this.processTimeMultiplier = this.processTimeMultiplier * Math.pow(0.5, stack.getCount());
                  this.energyDemandMultiplier = this.energyDemandMultiplier * Math.pow(2.0, stack.getCount());
                  break;
               case TRANSFORMER:
                  this.extraTier = this.extraTier + stack.getCount();
                  break;
               case REDSTONE_INVERTER:
                  this.invertRedstonePower = true;
                  break;
               case STRENGTHENING:
                  this.strengthening = Math.min(1, this.strengthening + stack.getCount());
                  break;
               case PARTICLE_ACCELERATOR:
                  this.particleAccelerator = Math.min(64, this.particleAccelerator + stack.getCount());
                  break;
               case SOUNDPROOFING:
                  this.soundproofing = true;
                  break;
               case EJECTOR:
                  this.ejectLevels.merge(ItemUpgradeModule.getDirection(stack), stack.getCount(), Integer::sum);
                  break;
               case PULLING:
                  this.pullingLevels.merge(ItemUpgradeModule.getDirection(stack), stack.getCount(), Integer::sum);
                  break;
               case FLUID_EJECTOR:
                  this.amountFluidEject = this.amountFluidEject + stack.getCount() * 50;
                  this.fluidEject = ItemUpgradeModule.getDirection(stack);
                  this.hasFluidEject = true;
                  break;
               case FLUID_PULLING:
                  this.amountFluidPulling = this.amountFluidPulling + stack.getCount() * 50;
                  this.fluidPulling = ItemUpgradeModule.getDirection(stack);
                  this.hasFluidPulling = true;
            }
         }
      }

      if (this.entity instanceof IEnergyWrapper wrapper && IR.sideProxy.isSimulating()) {
         IEnergyStorage handler = wrapper.getEnergyWrapper(null);
         if (handler instanceof EnergyStorage storage) {
            if (storage.getDefReceive() > 0) {
               storage.setReceive(wrapper.unlimitedInput() ? Integer.MAX_VALUE : this.getReceiveTick(storage.getDefReceive()));
            }

            if (storage.getDefExtract() > 0) {
               storage.setExtract(wrapper.unlimitedInput() ? Integer.MAX_VALUE : this.getReceiveTick(storage.getDefExtract()));
            }

            storage.setCapacity(this.getEnergyCapacity(storage.getDefCapacity()));
         } else if (handler instanceof BigEnergyStorage storage) {
            if (storage.getDefReceive() > 0L) {
               storage.setReceive(wrapper.unlimitedInput() ? Long.MAX_VALUE / 4 : this.getReceiveTick(storage.getDefReceive()));
            }

            if (storage.getDefExtract() > 0L) {
               storage.setExtract(wrapper.unlimitedInput() ? Long.MAX_VALUE / 4 : this.getReceiveTick(storage.getDefExtract()));
            }

            storage.setCapacity(this.getEnergyCapacity(storage.getDefCapacity()));
         }
      }

      super.setChanged();
   }

   private void resetRates() {
      this.energyStorageUpgrades = 0;
      this.augmentation = 0;
      this.processTimeMultiplier = 1.0;
      this.energyDemandMultiplier = 1.0;
      this.extraTier = 0;
      this.invertRedstonePower = false;
      this.strengthening = 0;
      this.particleAccelerator = 0;
      this.soundproofing = false;
      this.ejectLevels.clear();
      this.pullingLevels.clear();
      this.amountFluidEject = 0;
      this.fluidEject = null;
      this.hasFluidEject = false;
      this.amountFluidPulling = 0;
      this.fluidPulling = null;
      this.hasFluidPulling = false;
   }

   /** Сторона → уровень выталкивателя (только чтение). Пустая карта = нет улучшения. */
   public Map<Direction, Integer> getEjectLevels() {
      return this.ejectLevels;
   }

   /** Сторона → уровень затягивателя (только чтение). Пустая карта = нет улучшения. */
   public Map<Direction, Integer> getPullingLevels() {
      return this.pullingLevels;
   }

   /** Есть ли выталкиватель (вне зависимости от выбранной стороны: null = все стороны). */
   public boolean hasEjectUpgrade() {
      return !this.ejectLevels.isEmpty();
   }

   /** Есть ли затягиватель (вне зависимости от выбранной стороны: null = все стороны). */
   public boolean hasPullingUpgrade() {
      return !this.pullingLevels.isEmpty();
   }

   public boolean hasFluidEjectUpgrade() {
      return this.hasFluidEject;
   }

   @org.jetbrains.annotations.Nullable
   public Direction getFluidEjectSide() {
      return this.hasFluidEject ? this.fluidEject : null;
   }

   public int getFluidEjectAmount() {
      return this.amountFluidEject;
   }

   public boolean hasFluidPullingUpgrade() {
      return this.hasFluidPulling;
   }

   @org.jetbrains.annotations.Nullable
   public Direction getFluidPullingSide() {
      return this.hasFluidPulling ? this.fluidPulling : null;
   }

   public int getFluidPullingAmount() {
      return this.amountFluidPulling;
   }

   public int getOperationsPerTick() {
      return Math.min(64, this.strengthening == 0 ? 1 : this.strengthening * 4);
   }

   /** Множитель расхода энергии за «Укрепление»: +50% за каждое улучшение. */
   public double getStrengtheningEnergyMultiplier() {
      return 1.0 + 0.5 * this.strengthening;
   }

   public int getOperationLength(int n) {
      return n == 0 ? 1 : Math.max(1, (int)Math.round(n * this.processTimeMultiplier * Math.pow(1.15, this.getOperationsPerTick())));
   }

   public int getEnergyDemand(long n) {
      return applyModifier(n, 0, this.energyDemandMultiplier);
   }

   /** База ×16 для всех механизмов + 25% за каждое улучшение хранилища, максимум 8 штук. */
   public int getEnergyCapacity(long defaultCapacity) {
      double value = defaultCapacity * 16.0 * (1.0 + 0.25 * Math.min(8, this.energyStorageUpgrades));
      return value > 2.147483647E9 ? Integer.MAX_VALUE : (int)Math.round(value);
   }

   public int getReceiveTick(long defaultReceive) {
      return (int)Math.min(2.147483647E9, defaultReceive * Math.pow(2.0, this.extraTier));
   }

   private static int applyModifier(long n, int n2, double d) {
      double d2 = Math.round(((double)n + n2) * d);
      return d2 > 2.147483647E9 ? Integer.MAX_VALUE : (int)d2;
   }
}
