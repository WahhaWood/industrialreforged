package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.item.ElectricProperties;
import java.util.Collection;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;

public class ItemElectricTools extends ItemElectricTool {
   private final int level;

   public ItemElectricTools(int level, int capacity, int energyTick, int energyCost, ElectricProperties properties, Tier tier, Collection<TagKey<Block>> keys) {
      super(properties, energyCost, tier, keys, capacity, energyTick);
      this.level = level;
   }

   public int getLevel() {
      return this.level;
   }
}
