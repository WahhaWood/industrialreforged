package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.util.WindUtil;
import com.industrialreforged.tech.item.ElectricProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemWindMeter extends ItemElectricTool {
   public ItemWindMeter(ElectricProperties properties) {
      super(properties, 100, 10000, 100);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!level.isClientSide && this.use(stack, this.operationEnergyCost, false)) {
         double windStrength = WindUtil.getStrength(level, player.position().y);
         player.displayClientMessage(
            Component.translatable("text.industrialreforged.message.wind_meter.power", new Object[]{(float)Math.round(windStrength * 100.0) / 100.0F}), false
         );
      }

      return InteractionResultHolder.consume(stack);
   }
}
