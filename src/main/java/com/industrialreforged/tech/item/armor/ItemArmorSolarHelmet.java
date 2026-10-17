package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.tech.blockentity.machine.SolarDestillerBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemArmorSolarHelmet extends ItemArmorUtility {
   public ItemArmorSolarHelmet(Properties properties) {
      super(properties, Type.HELMET);
   }

   /**
    * Заряжает нагрудник до 40 FE/т, но порцией раз в секунду и только на сервере: каждый
    * тик менял заряд и слал слот клиенту 20 раз в секунду, а клиентская копия всё равно
    * перезаписывается сервером. Цикл — потому что приём за вызов ограничен (электроджетпак 512).
    */
   public void onArmorTick(ItemStack stack, Level level, Player player) {
      if (level.isClientSide || player.tickCount % 20 != 0) {
         return;
      }

      boolean ret = false;
      ItemStack stack1 = (ItemStack)player.getInventory().armor.get(2);
      if (stack1.getItem() instanceof IEnergyContainerItem energyItem) {
         int chargeAmount = (int)(40.0F * 20 * SolarDestillerBlockEntity.getSkyLight(level, player.blockPosition()));
         for (int got; chargeAmount > 0 && (got = energyItem.receiveEnergy(stack1, chargeAmount, false)) > 0; chargeAmount -= got) {
            ret = true;
         }
      }

      if (ret) {
         player.getInventory().setChanged();
      }
   }
}
