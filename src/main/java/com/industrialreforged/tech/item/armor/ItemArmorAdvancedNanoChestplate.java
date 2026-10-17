package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.TickArmorItem;
import com.industrialreforged.tech.api.item.IArmorTick;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ElectricProperties;
import com.industrialreforged.tech.item.armor.jetpack.IJetpack;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class ItemArmorAdvancedNanoChestplate extends ItemArmorElectric implements IJetpack {
   public ItemArmorAdvancedNanoChestplate(ElectricProperties properties) {
      super(
         30000000,
         256000,
         IRArmorMaterials.NANO_SUIT,
         Type.CHESTPLATE,
         properties.component(IRDataComponents.ACTIVE, false).component(IRDataComponents.HOVER, false)
      );
   }

   @Override
   public void onPreArmorTick(ItemStack stack, Level level, Player player) {
      ((IArmorTick)IRItems.JETPACK.get()).onPreArmorTick(stack, level, player);
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      this.chargeHandItem(player, stack);
      ((TickArmorItem)IRItems.JETPACK.get()).onArmorTick(stack, level, player);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.jetpack", new Object[]{IR.keyboard.getFlyKeyName()}));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.space", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }

   @Override
   public float getDamageAbsorptionRatio() {
      return 1.0F;
   }

   @Override
   public int getEnergyPerDamage() {
      return 5000;
   }

   @Override
   public boolean drainEnergy(ItemStack stack, int amount) {
      return this.use(stack, amount + 6, false);
   }

   @Override
   public float getPower(ItemStack stack) {
      return 1.0F;
   }

   @Override
   public float getDropPercentage(ItemStack stack) {
      return 0.05F;
   }

   @Override
   public double getChargeLevel(ItemStack stack) {
      return this.getRatio(stack);
   }

   @Override
   public float getHoverMultiplier(ItemStack stack, boolean up) {
      return 0.1F;
   }

   @Override
   public float getWorldHeightDivisor(ItemStack stack) {
      return 0.9F;
   }
}
