package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.TickArmorItem;
import com.industrialreforged.tech.api.item.IArmorTick;
import com.industrialreforged.tech.api.item.IHazmatLike;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ElectricProperties;
import com.industrialreforged.tech.item.armor.jetpack.ICreativeJetpack;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;

public class ItemArmorSingularSuit extends ItemArmorElectric implements ICreativeJetpack, IHazmatLike, IArmorTick {
   public ItemArmorSingularSuit(Holder<ArmorMaterial> armorMaterial, Type type, ElectricProperties properties) {
      super(1000000000, 10000000, armorMaterial, type, properties.rarity(Rarity.RARE).component(DataComponents.DYED_COLOR, new DyedItemColor(-6265536, false)));
   }

   @Override
   public void onPreArmorTick(ItemStack stack, Level level, Player player) {
      if (this.type == Type.CHESTPLATE) {
         ((IArmorTick)IRItems.GRAVI_CHESTPLATE.get()).onPreArmorTick(stack, level, player);
      } else if (this.type == Type.LEGGINGS) {
         ((IArmorTick)IRItems.QUANTUM_LEGGINGS.get()).onPreArmorTick(stack, level, player);
      }
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      if (this.type == Type.HELMET) {
         ((TickArmorItem)IRItems.QUANTUM_HELMET.get()).onArmorTick(stack, level, player);
      } else if (this.type == Type.CHESTPLATE) {
         ((TickArmorItem)IRItems.GRAVI_CHESTPLATE.get()).onArmorTick(stack, level, player);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (this.type == Type.HELMET) {
         tooltipComponents.add(
            Component.translatable("tooltip.industrialreforged.mode.night_vision", new Object[]{IR.keyboard.getAltKeyName(), IR.keyboard.getModeSwitchKeyName()})
         );
      } else if (this.type == Type.CHESTPLATE) {
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.jetpack", new Object[]{IR.keyboard.getFlyKeyName()}));
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.jetpack.boost", new Object[]{IR.keyboard.getBoostKeyName()}));
      } else if (this.type == Type.BOOTS) {
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.super_jump", new Object[]{IR.keyboard.getBoostKeyName()}));
      }
   }

   @Override
   public float getDamageAbsorptionRatio() {
      return this.type == Type.CHESTPLATE ? 1.1F : 1.0F;
   }

   @Override
   public int getEnergyPerDamage() {
      return 20000;
   }

   @Override
   public boolean addsProtection(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack itemStack) {
      return this.getEnergyStored(itemStack) > 0.0;
   }

   public int getEnchantmentValue() {
      return 0;
   }

   @Override
   public boolean isJetpackActive(ItemStack stack) {
      return this.type == Type.CHESTPLATE && ICreativeJetpack.super.isJetpackActive(stack);
   }
}
