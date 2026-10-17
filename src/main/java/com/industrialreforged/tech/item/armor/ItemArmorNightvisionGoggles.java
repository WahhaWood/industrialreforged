package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.item.energy.BasicEnergyArmorItem;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemArmorNightvisionGoggles extends BasicEnergyArmorItem {
   public ItemArmorNightvisionGoggles(Properties properties) {
      super(IRArmorMaterials.UTILITY, Type.HELMET, properties.component(IRDataComponents.ACTIVE, false), 200000, 1024);
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      boolean active = (Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false);
      if (!level.isClientSide && IR.keyboard.isAltKeyDown(player) && IR.keyboard.isModeSwitchKeyDown(player)) {
         ItemCooldowns cooldowns = player.getCooldowns();
         if (!cooldowns.isOnCooldown(stack.getItem())) {
            cooldowns.addCooldown(stack.getItem(), 10);
            active = !active;
            stack.set(IRDataComponents.ACTIVE, active);
            player.sendSystemMessage(Component.translatable("text.industrialreforged.nightvision.goggles." + (active ? "enabled" : "disabled")));
         }
      }

      if (active) {
         // ПНВ жрёт заряд: на пустом шлеме гаснет само. Списываем 200 FE раз в секунду, а не
         // 10 каждый тик: расход тот же, а слот со сменившимся зарядом уходит клиенту в 20 раз реже.
         if (!level.isClientSide
            && player.tickCount % 20 == 0
            && stack.getItem() instanceof IEnergyContainerItem energy
            && !energy.use(stack, 200, false)) {
            stack.set(IRDataComponents.ACTIVE, false);
            player.sendSystemMessage(Component.translatable("text.industrialreforged.nightvision.goggles.discharged"));
         } else {
            refreshEffect(level, player, MobEffects.NIGHT_VISION);
         }
      }
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return IRArmorItem.getArmorTexture(this, stack, entity, slot, layer);
   }

   public static void refreshEffect(Level level, Player player, Holder<MobEffect> effect) {
      if (!level.isClientSide) {
         MobEffectInstance current = player.getEffect(effect);
         if (current == null || current.getDuration() <= 220) {
            player.addEffect(new MobEffectInstance(effect, 300, 0, false, false));
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(
         Component.translatable("tooltip.industrialreforged.mode.night_vision", new Object[]{IR.keyboard.getAltKeyName(), IR.keyboard.getModeSwitchKeyName()})
      );
   }
}
