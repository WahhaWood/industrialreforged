package com.industrialreforged.item;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class TickArmorItem extends ArmorItem {
   public TickArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
      super(material, type, properties);
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      // Было: slotId == 36 + type.getSlot().getIndex() — но EquipmentSlot.getIndex() для
      // брони это 2..5 (FEET..HEAD), а слоты брони в инвентаре — 36..39. Условие не выполнялось
      // никогда, и onArmorTick (ПНВ, солнечный шлем, статичные ботинки) молча не работал.
      // Проверяем напрямую: кусок реально надет.
      if (entity instanceof ServerPlayer player && player.getItemBySlot(this.type.getSlot()) == stack) {
         this.onArmorTick(stack, level, player);
      }
   }
}
