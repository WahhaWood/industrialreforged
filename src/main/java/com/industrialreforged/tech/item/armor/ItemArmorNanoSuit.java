package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.TickArmorItem;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class ItemArmorNanoSuit extends ItemArmorElectric {
   public ItemArmorNanoSuit(Holder<ArmorMaterial> armorMaterial, Type type, ElectricProperties properties) {
      super(1000000, 64000, armorMaterial, type, properties);
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      if (this.type == Type.HELMET) {
         ((TickArmorItem)IRItems.NIGHTVISION_GOOGLES.get()).onArmorTick(stack, level, player);
         if ((Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false) && player.isInWater()) {
            ItemArmorNightvisionGoggles.refreshEffect(level, player, MobEffects.WATER_BREATHING);
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (this.type == Type.HELMET) {
         tooltipComponents.add(
            Component.translatable("tooltip.industrialreforged.mode.night_vision", new Object[]{IR.keyboard.getAltKeyName(), IR.keyboard.getModeSwitchKeyName()})
         );
      }
   }

   @Override
   public float getDamageAbsorptionRatio() {
      return 1.0F;
   }

   @Override
   public int getEnergyPerDamage() {
      return 5000;
   }
}
