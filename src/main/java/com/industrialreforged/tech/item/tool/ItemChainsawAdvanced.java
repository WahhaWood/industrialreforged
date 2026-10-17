package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.item.ElectricProperties;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;

public class ItemChainsawAdvanced extends ItemChainsaw {
   public ItemChainsawAdvanced(ElectricProperties properties) {
      super(600, Tiers.DIAMOND, properties, 180000, 2000);
   }

   @Override
   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
      if (this.use(stack, this.operationEnergyCost * 2, false)) {
         entity.hurt(player.damageSources().playerAttack(player), 13.0F);
      } else {
         entity.hurt(player.damageSources().playerAttack(player), 1.0F);
      }

      return false;
   }
}
