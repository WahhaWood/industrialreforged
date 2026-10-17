package com.industrialreforged.tech.api.item;

import com.industrialreforged.tech.core.ref.IRItems;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public enum ItemToolMaterials implements Tier {
   BRONZE(
      BlockTags.INCORRECT_FOR_IRON_TOOL,
      2,
      350,
      6.0F,
      2.0F,
      14,
      () -> Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/bronze")))
   ),
   NANO(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 3, 1000, 10.4F, 2.0F, 0, () -> Ingredient.of(new ItemLike[]{(ItemLike)IRItems.CARBON_PLATE.get()})),
   QUANTUM(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 4, 2500, 14.8F, 3.0F, 0, () -> Ingredient.of(new ItemLike[]{(ItemLike)IRItems.IRIDIUM_PLATE.get()})),
   SINGULAR(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 5, 5000, 16.0F, 4.0F, 0, () -> Ingredient.of(new ItemLike[]{(ItemLike)IRItems.IRIDIUM_PLATE.get()}));

   private final TagKey<Block> incorrectBlocksForDrops;
   private final int miningLevel;
   private final int itemDurability;
   private final float miningSpeed;
   private final float attackDamage;
   private final int enchantability;
   private final LazyLoadedValue<Ingredient> repairIngredient;

   ItemToolMaterials(
      TagKey<Block> incorrectBlocksForDrops,
      int miningLevel,
      int itemDurability,
      float miningSpeed,
      float attackDamage,
      int enchantability,
      Supplier<Ingredient> supplier
   ) {
      this.incorrectBlocksForDrops = incorrectBlocksForDrops;
      this.miningLevel = miningLevel;
      this.itemDurability = itemDurability;
      this.miningSpeed = miningSpeed;
      this.attackDamage = attackDamage;
      this.enchantability = enchantability;
      this.repairIngredient = new LazyLoadedValue(supplier);
   }

   public int getUses() {
      return this.itemDurability;
   }

   public float getSpeed() {
      return this.miningSpeed;
   }

   public float getAttackDamageBonus() {
      return this.attackDamage;
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   public int getLevel() {
      return this.miningLevel;
   }

   public int getEnchantmentValue() {
      return this.enchantability;
   }

   @NotNull
   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }
}
