package com.industrialreforged.tech.item;

import com.industrialreforged.tech.core.RadiationMobEffect;
import com.industrialreforged.tech.core.ref.IRMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemTerraWart extends Item {
   public ItemTerraWart(Properties properties) {
      super(properties);
   }

   public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
      livingEntity.removeEffect(MobEffects.CONFUSION);
      livingEntity.removeEffect(MobEffects.DIG_SLOWDOWN);
      livingEntity.removeEffect(MobEffects.HUNGER);
      livingEntity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
      livingEntity.removeEffect(MobEffects.WEAKNESS);
      livingEntity.removeEffect(MobEffects.BLINDNESS);
      livingEntity.removeEffect(MobEffects.POISON);
      livingEntity.removeEffect(MobEffects.WITHER);
      MobEffectInstance mobEffectInstance = livingEntity.getEffect(IRMobEffects.RADIATION);
      if (mobEffectInstance != null) {
         if (mobEffectInstance.getDuration() <= 600) {
            livingEntity.removeEffect(IRMobEffects.RADIATION);
         } else {
            livingEntity.removeEffect(IRMobEffects.RADIATION);
            ((RadiationMobEffect)IRMobEffects.RADIATION.get()).applyTo(livingEntity, mobEffectInstance.getDuration() - 600, mobEffectInstance.getAmplifier());
         }
      }

      return super.finishUsingItem(itemStack, level, livingEntity);
   }
}
