package com.industrialreforged.tech.item;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.Unbreakable;
import org.jetbrains.annotations.NotNull;

public class ElectricProperties extends Properties {
   public ElectricProperties() {
      this.setNoRepair();
      this.stacksTo(1);
      this.component(DataComponents.UNBREAKABLE, new Unbreakable(false));
   }

   public ElectricProperties food(FoodProperties p_41490_) {
      return (ElectricProperties)super.food(p_41490_);
   }

   public ElectricProperties stacksTo(int p_41488_) {
      return (ElectricProperties)super.stacksTo(p_41488_);
   }

   public ElectricProperties durability(int p_41504_) {
      return (ElectricProperties)super.durability(0);
   }

   public ElectricProperties craftRemainder(Item p_41496_) {
      return (ElectricProperties)super.craftRemainder(p_41496_);
   }

   public ElectricProperties rarity(Rarity p_41498_) {
      return (ElectricProperties)super.rarity(p_41498_);
   }

   public ElectricProperties fireResistant() {
      return (ElectricProperties)super.fireResistant();
   }

   public ElectricProperties setNoRepair() {
      return (ElectricProperties)super.setNoRepair();
   }

   public ElectricProperties requiredFeatures(FeatureFlag @NotNull ... p_250948_) {
      return (ElectricProperties)super.requiredFeatures(p_250948_);
   }

   public <T> ElectricProperties component(DataComponentType<T> component, T value) {
      return (ElectricProperties)super.component(component, value);
   }

   public <T> ElectricProperties component(Supplier<? extends DataComponentType<T>> componentType, T value) {
      return (ElectricProperties)super.component(componentType, value);
   }

   public static ElectricProperties of(Rarity rarity) {
      return new ElectricProperties().rarity(rarity);
   }
}
