package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.tech.block.generator.electric.SolarPanelBlock;
import com.industrialreforged.tech.blockentity.base.AbstractElectricGeneratorBlockEntity;
import com.industrialreforged.tech.container.InvSlotPanelModules;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.IRConfig;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.item.solar.SolarPanelModuleItem;
import com.industrialreforged.tech.menu.generator.electric.SolarPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends AbstractElectricGeneratorBlockEntity {
   protected boolean wetBiome;
   protected boolean noSunWorld;
   public boolean sunIsVisible = false;
   public boolean sunIsUp;
   /** Rain in a wet biome: the panel then makes {@link #rainRate} of its day output instead of day or night output. */
   protected boolean rainWeather;
   public int ticker = IR.random.nextInt(128);
   public long defaultProduction;
   /** Слоты модулей панели (день/ночь/хранение/выход/линзы). */
   public final InvSlotPanelModules moduleSlot;
   // множители от модулей, пересчитываются в recalculateModules()
   /** Базовая выработка в дождь — минимум 40% дневной даже без линз. */
   public static final double BASE_RAIN_RATE = 0.40;
   public double dayRate = 1.0;
   public double nightRate = 1.0;
   /** Выработка в дождь как доля от дневной (базово 40%, линза дождя умножает). */
   public double rainRate = BASE_RAIN_RATE;
   public double capacityMult = 1.0;
   public double outputMult = 1.0;

   public SolarPanelBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState, 2147483647L, 0L, 0);
      this.moduleSlot = new InvSlotPanelModules(this, "solar_modules", 4);
   }

   public SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState) {
      this(blockPos, blockState, IRConfig.CONFIG.getProduction(((SolarPanelBlock)blockState.getBlock()).getLevel()));
   }

   private SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState, long production) {
      super(IRBlockEntities.SOLAR_GENERATOR.get(), blockPos, blockState, Math.max(production * 6400L, 64000L), production, 4);
      this.defaultProduction = this.production;
      this.moduleSlot = new InvSlotPanelModules(this, "solar_modules", 4);
   }

   public void updateVisibility() {
      if (this.level != null) {
         boolean raining = this.level.isRaining() || this.level.isThundering();
         boolean rainWeather = this.wetBiome && raining;
         this.rainWeather = rainWeather;
         this.sunIsUp = this.level.isDay() && !rainWeather;
         this.sunIsVisible = this.level.canSeeSky(this.getBlockPos().above()) && !this.noSunWorld;
         if (rainWeather) {
            // дождь глушит панель; линза дождя даёт часть выработки
            this.production = Math.round((double)this.defaultProduction * this.rainRate);
         } else if (this.sunIsUp) {
            this.production = Math.round((double)this.defaultProduction * this.dayRate);
         } else {
            // ночью панель даёт 60%
            this.production = Math.round((double)this.defaultProduction * 0.6 * this.nightRate);
         }

         this.energy.setExtract(Math.max(1L, Math.round((double)this.defaultProduction * 4L * this.outputMult)));
      }
   }

   /** Пересчёт множителей из слотов модулей; вызывается при изменении слотов и при загрузке. */
   public void recalculateModules() {
      double day = 1.0;
      double night = 1.0;
      double cap = 1.0;
      double out = 1.0;
      double rain = BASE_RAIN_RATE;

      for (int i = 0; i < this.moduleSlot.getContainerSize(); i++) {
         if (this.moduleSlot.getItem(i).getItem() instanceof SolarPanelModuleItem module) {
            double bonus = module.bonus();
            switch (module.type) {
               case DAY -> day += bonus;
               case NIGHT -> night += bonus;
               case STORAGE -> cap += bonus;
               case OUTPUT -> out += bonus;
               case SUN_LENS -> day *= 1.0 + bonus;
               case MOON_LENS -> night *= 1.0 + bonus;
               case RAIN_LENS -> rain *= 1.0 + bonus;
            }
         }
      }

      this.dayRate = day;
      this.nightRate = night;
      this.rainRate = rain;
      this.capacityMult = cap;
      this.outputMult = out;
      this.applyCapacity();
      this.updateVisibility();
      this.markForSave();
   }

   private void applyCapacity() {
      long base = Math.max(this.defaultProduction * 6400L, 64000L);
      long target = Math.round((double)base * this.capacityMult);
      if (target != this.energy.getCapacity()) {
         long stored = this.energy.getStored();
         this.energy.setCapacity(target);
         if (stored > target) {
            this.energy.setStored(target);
         }
      }
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.moduleSlot.load(tag, registries);
      this.recalculateModules();
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.moduleSlot.save(tag, registries);
   }

   public void onLoad() {
      super.onLoad();
      this.wetBiome = ((Biome)this.level.getBiome(this.getBlockPos()).value()).getModifiedClimateSettings().downfall() > 0.0F;
      this.noSunWorld = !this.level.dimensionType().hasSkyLight();
      this.updateVisibility();
   }

   @Override
   public boolean needsFuel() {
      return true;
   }

   @Override
   public boolean isConverting() {
      return this.sunIsVisible && this.production > 0L && this.energy.hasSpace();
   }

   @Override
   protected void onServerTick() {
      if (--this.ticker <= 0) {
         this.ticker = 20;
         boolean upBefore = this.sunIsUp;
         boolean visibleBefore = this.sunIsVisible;
         long productionBefore = this.production;
         this.updateVisibility();
         // День/ночь/дождь стоят — выработка та же, чанк не пачкаем.
         if (this.sunIsUp != upBefore || this.sunIsVisible != visibleBefore || this.production != productionBefore) {
            this.markForSave();
         }
      }
   }

   public boolean delayActiveUpdate() {
      return true;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SolarPanelMenu(containerId, playerInventory, this);
   }
}
