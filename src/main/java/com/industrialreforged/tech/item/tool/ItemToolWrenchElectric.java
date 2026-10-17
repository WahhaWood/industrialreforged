package com.industrialreforged.tech.item.tool;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class ItemToolWrenchElectric extends ItemToolWrench implements IEnergyContainerItem {
   public ItemToolWrenchElectric(Properties properties) {
      super(properties.stacksTo(1).component(IRDataComponents.ACTIVE, false).component(GEDataComponents.ENERGY, 0));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (IR.keyboard.isModeSwitchKeyDown(player)) {
         if (!level.isClientSide) {
            player.displayClientMessage(
               Component.translatable(this.changeSelfMode(stack) ? "text.industrialreforged.tooltip.wrench.self.enabled" : "text.industrialreforged.tooltip.wrench.self.disabled"), false
            );
         }

         return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
      } else {
         return InteractionResultHolder.pass(stack);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable(this.isSelfMode(stack) ? "text.industrialreforged.tooltip.wrench.self.enabled" : "text.industrialreforged.tooltip.wrench.self.disabled"));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }

   @Override
   public boolean canWrenchDrop(Level level, ItemStack stack, float dropChance) {
      return this.isSelfMode(stack) || super.canWrenchDrop(level, stack, dropChance);
   }

   public boolean changeSelfMode(ItemStack stack) {
      boolean selfMode = !this.isSelfMode(stack);
      stack.set(IRDataComponents.ACTIVE, selfMode);
      return selfMode;
   }

   public boolean isSelfMode(ItemStack stack) {
      return (Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false);
   }

   @Override
   public boolean canTakeDamage(ItemStack stack, int amount) {
      return this.getEnergyStored(stack) >= (this.isSelfMode(stack) ? 20 : 1) * amount * 100;
   }

   @Override
   public void damage(ItemStack stack, int amount, Player player, InteractionHand hand) {
      this.use(stack, (this.isSelfMode(stack) ? 20 : 1) * 100 * amount, false);
   }

   public int getEnergyCapacity(ItemStack itemStack) {
      return 48000;
   }

   public int getEnergyReceiveTick(ItemStack itemStack) {
      return 128;
   }

   public int getEnergyExtractTick(ItemStack itemStack) {
      return 128;
   }

   public boolean isBarVisible(ItemStack itemStack) {
      return true;
   }

   public int getBarWidth(ItemStack itemStack) {
      return (int)Math.round(this.getRatio(itemStack) * 13.0);
   }

   public int getBarColor(ItemStack itemStack) {
      return Mth.hsvToRgb((float)(this.getRatio(itemStack) / 3.0), 1.0F, 1.0F);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged || oldStack.getItem() != newStack.getItem();
   }

   public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
      return oldStack.getItem() != newStack.getItem();
   }
}
