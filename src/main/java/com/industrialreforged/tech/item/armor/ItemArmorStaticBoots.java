package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.tech.core.IR;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemArmorStaticBoots extends ItemArmorUtility {
   public ItemArmorStaticBoots(Properties properties) {
      super(properties, Type.BOOTS);
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      ItemStack chestplate = (ItemStack)player.getInventory().armor.get(2);
      if (!chestplate.isEmpty()) {
         boolean ret = false;
         if ((player.getVehicle() != null || player.isInWater())
            && player.fallDistance >= 5.0
            && chestplate.getItem() instanceof IEnergyContainerItem energyItem) {
            ret = energyItem.receiveEnergy(chestplate, (int)Math.min(3.0, player.fallDistance / 5.0), false) > 0.0;
         }

         if (ret) {
            player.getInventory().setChanged();
         }
      }
   }

   @Nullable
   @Override
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return IR.texture("armor/rubber_1");
   }
}
