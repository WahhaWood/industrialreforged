package com.industrialreforged.tech.api.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Tool.Rule;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiTier implements Tier {
   private final Tier parent;
   private final Collection<TagKey<Block>> blocks;

   public MultiTier(Tier parent, Collection<TagKey<Block>> blocks) {
      this.parent = parent;
      this.blocks = blocks;
   }

   public int getUses() {
      return this.parent.getUses();
   }

   public float getSpeed() {
      return this.parent.getSpeed();
   }

   public float getAttackDamageBonus() {
      return this.parent.getAttackDamageBonus();
   }

   @NotNull
   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.parent.getIncorrectBlocksForDrops();
   }

   public int getEnchantmentValue() {
      return this.parent.getUses();
   }

   @NotNull
   public Ingredient getRepairIngredient() {
      return this.parent.getRepairIngredient();
   }

   @NotNull
   public Tool createToolProperties(@Nullable TagKey<Block> ignored) {
      List<Rule> rules = new ArrayList<>();
      rules.add(Rule.deniesDrops(this.getIncorrectBlocksForDrops()));
      this.blocks.stream().map(key -> Rule.minesAndDrops(key, this.getSpeed())).forEach(rules::add);
      return new Tool(List.copyOf(rules), 1.0F, 1);
   }
}
