package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.TickArmorItem;
import com.industrialreforged.tech.core.IR;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;

public class IRArmorItem extends TickArmorItem {
   public IRArmorItem(Holder<ArmorMaterial> armorMaterial, Type equipmentSlot, Properties properties) {
      super(armorMaterial, equipmentSlot, properties);
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return getArmorTexture(this, stack, entity, slot, layer);
   }

   public static ResourceLocation getArmorTexture(Item item, ItemStack stack, Entity entity, EquipmentSlot slot, Layer type) {
      String s = BuiltInRegistries.ITEM.getKey(item).getPath().replace("_helmet", "").replace("_chestplate", "").replace("_leggings", "").replace("_boots", "");
      return IR.texture("armor/" + s + "_" + (slot == EquipmentSlot.LEGS ? 2 : 1));
   }
}
