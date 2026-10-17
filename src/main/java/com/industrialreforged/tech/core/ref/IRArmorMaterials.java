package com.industrialreforged.tech.core.ref;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IRArmorMaterials {
   public static final DeferredRegister<ArmorMaterial> REGISTRY = DeferredRegister.create(Registries.ARMOR_MATERIAL, "industrialreforged");
   public static final Holder<ArmorMaterial> BRONZE = REGISTRY.register("bronze", id -> {
      EnumMap<Type, Integer> map = createProtection(2, 6, 5, 2, 5);
      Supplier<Ingredient> repairIngredient = () -> Ingredient.of(IRItemTags.INGOTS_BRONZE);
      List<Layer> layers = List.of(new Layer(id));
      return new ArmorMaterial(map, 9, SoundEvents.ARMOR_EQUIP_IRON, repairIngredient, layers, 0.0F, 0.0F);
   });
   public static final Holder<ArmorMaterial> ALLOY = REGISTRY.register("alloy", id -> {
      EnumMap<Type, Integer> map = createProtection(4, 9, 7, 4, 13);
      Supplier<Ingredient> repairIngredient = () -> Ingredient.of(new ItemLike[]{IRItems.ALLOY});
      List<Layer> layers = List.of(new Layer(id));
      return new ArmorMaterial(map, 12, SoundEvents.ARMOR_EQUIP_IRON, repairIngredient, layers, 2.0F, 0.0F);
   });
   public static final Holder<ArmorMaterial> UTILITY = REGISTRY.register(
      "utility", id -> createEnergyBase(id, 2, 5, 4, 1, 4, 0.0F, 0.0F)
   );
   public static final Holder<ArmorMaterial> NANO_SUIT = REGISTRY.register(
      "nano", id -> createEnergyBase(id, 3, 8, 6, 3, 8, 2.0F, 0.0F)
   );
   public static final Holder<ArmorMaterial> QUANTUM_SUIT = REGISTRY.register(
      "quantum", id -> createEnergyBase(id, 4, 10, 7, 4, 10, 3.0F, 0.1F)
   );
   public static final Holder<ArmorMaterial> SINGULAR_SUIT = REGISTRY.register(
      "singular", id -> createEnergyBase(id, 5, 12, 9, 5, 12, 4.0F, 0.1F)
   );

   /**
    * База энергоброни: ванильная защита ненулевая, но действует только при заряде —
    * разряженные куски гасятся в {@code EventHandlerForge#onItemAttributes} (см. гейт по энергии).
    */
   private static ArmorMaterial createEnergyBase(
      ResourceLocation id, int helmet, int chestplate, int leggings, int boots, int body, float toughness, float knockbackResistance
   ) {
      EnumMap<Type, Integer> map = createProtection(helmet, chestplate, leggings, boots, body);
      Supplier<Ingredient> repairIngredient = () -> Ingredient.EMPTY;
      List<Layer> layers = List.of(new Layer(id));
      return new ArmorMaterial(map, 0, SoundEvents.ARMOR_EQUIP_IRON, repairIngredient, layers, toughness, knockbackResistance);
   }

   private static EnumMap<Type, Integer> createProtection(int helmet, int chestplate, int leggings, int boots, int body) {
      EnumMap<Type, Integer> map = new EnumMap<>(Type.class);
      map.put(Type.HELMET, helmet);
      map.put(Type.CHESTPLATE, chestplate);
      map.put(Type.LEGGINGS, leggings);
      map.put(Type.BOOTS, boots);
      map.put(Type.BODY, body);
      return map;
   }
}
