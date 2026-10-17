package com.industrialreforged.tech.container.slot;

import com.industrialreforged.menu.slots.BaseSlot;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

public class SlotArmor extends BaseSlot {
   private final EquipmentSlot armorType;

   public SlotArmor(Inventory inventory, int index, int x, int y) {
      super(inventory, 36 + index, x, y);

      this.armorType = switch (index) {
         case 0 -> EquipmentSlot.FEET;
         case 1 -> EquipmentSlot.LEGS;
         case 2 -> EquipmentSlot.CHEST;
         case 3 -> EquipmentSlot.HEAD;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public boolean mayPlace(ItemStack itemStack) {
      EquipmentSlot slot = itemStack.getEquipmentSlot();
      if (slot != null) {
         return slot == this.armorType;
      }

      Equipable equipable = Equipable.get(itemStack);
      return equipable != null && equipable.getEquipmentSlot() == this.armorType;
   }

   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return Pair.of(InventoryMenu.BLOCK_ATLAS, switch (this.armorType) {
         case FEET -> InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS;
         case LEGS -> InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS;
         case CHEST -> InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE;
         case HEAD -> InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;
         default -> MissingTextureAtlasSprite.getLocation();
      });
   }
}
