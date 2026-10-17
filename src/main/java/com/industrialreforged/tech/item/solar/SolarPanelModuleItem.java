package com.industrialreforged.tech.item.solar;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public class SolarPanelModuleItem extends Item {
   public enum Type {
      DAY,
      NIGHT,
      STORAGE,
      OUTPUT,
      SUN_LENS,
      MOON_LENS,
      RAIN_LENS
   }

   public final Type type;
   /** 0 -> +10%, 1 -> +25% */
   public final int level;

   public SolarPanelModuleItem(Type type, int level) {
      super(new Properties());
      this.type = type;
      this.level = level;
   }

   public double bonus() {
      return this.level == 0 ? 0.10 : 0.25;
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      // Для линзы дождя показываем итоговую долю от дневной выработки (базово 40% × линза)
      int pct = this.type == Type.RAIN_LENS
         ? (int) Math.round(com.industrialreforged.tech.blockentity.generator.electric.SolarPanelBlockEntity.BASE_RAIN_RATE * (1.0 + this.bonus()) * 100.0)
         : (int) Math.round(this.bonus() * 100);
      // Описание эффекта модуля
      tooltip.add(
         Component.translatable("tooltip.industrialreforged.solar_module." + this.type.name() + ".desc").withStyle(ChatFormatting.GRAY)
      );
      // Конкретный бонус в процентах
      String bonusKey;
      if (this.type == Type.SUN_LENS || this.type == Type.MOON_LENS) {
         bonusKey = "tooltip.industrialreforged.solar_module.multiplier";
      } else if (this.type == Type.RAIN_LENS) {
         bonusKey = "tooltip.industrialreforged.solar_module.rain_value";
      } else {
         bonusKey = "tooltip.industrialreforged.solar_module.additive";
      }
      tooltip.add(
         Component.translatable(bonusKey, pct).withStyle(ChatFormatting.GREEN)
      );
   }
}
