package com.industrialreforged.tech.core;

import com.industrialreforged.tech.block.generator.electric.SolarPanelBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class IRConfig {
   public static final IRConfig CONFIG;
   public static final ModConfigSpec CONFIG_SPEC;
   public final ConfigValue<Long>[] productions = new ConfigValue[30];
   public final ConfigValue<Integer> ejectIntervalTicks;

   private IRConfig(Builder builder) {
      builder.push("panel");
      builder.push("basic");
      this.productions[0] = this.get(builder, 1, 2L, 0L);
      this.productions[1] = this.get(builder, 2, 10L, 0L);
      this.productions[2] = this.get(builder, 3, 50L, 0L);
      this.productions[3] = this.get(builder, 4, 250L, 0L);
      this.productions[4] = this.get(builder, 5, 1250L, 0L);
      this.productions[5] = this.get(builder, 6, 6250L, 0L);
      this.productions[6] = this.get(builder, 7, 31250L, 0L);
      this.productions[7] = this.get(builder, 8, 156250L, 0L);
      this.productions[8] = this.get(builder, 9, 781250L, 0L);
      this.productions[9] = this.get(builder, 10, 3500000L, 0L);
      this.productions[10] = this.get(builder, 11, 15000000L, 0L);
      this.productions[11] = this.get(builder, 12, 65000000L, 0L);
      this.productions[12] = this.get(builder, 13, 275000000L, 0L);
      this.productions[13] = this.get(builder, 14, 1175000000L, 0L);
      this.productions[14] = this.get(builder, 15, 5000000000L, 0L);
      builder.pop();
      builder.pop();
      builder.push("transfer");
      this.ejectIntervalTicks = builder.defineInRange("eject_interval_ticks", 1, 1, 100);
      builder.pop();
   }

   private ConfigValue<Long> get(Builder builder, int level, long defaultValue, long min) {
      return builder.define(String.format("%d_level", level), defaultValue, obj -> obj instanceof Number number && number.longValue() >= min);
   }

   public long getProduction(int level) {
      return this.get(level - 1);
   }

   /** Раз в сколько тиков выталкиватель/затягиватель сканируют соседей. 1 = каждый тик. */
   public int getEjectIntervalTicks() {
      return Math.max(1, this.ejectIntervalTicks.get());
   }

   private long get(int index) {
      Number number = (Number)this.productions[index].get();
      return number.longValue();
   }

   static {
      Pair<IRConfig, ModConfigSpec> pair = new Builder().configure(IRConfig::new);
      CONFIG = (IRConfig)pair.getLeft();
      CONFIG_SPEC = (ModConfigSpec)pair.getRight();
   }
}
