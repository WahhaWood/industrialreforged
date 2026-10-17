package com.industrialreforged.tech.core;

import com.industrialreforged.tech.core.ref.IRDamageSource;
import com.industrialreforged.tech.core.ref.IRMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class RadiationMobEffect extends MobEffect {
   public RadiationMobEffect(MobEffectCategory mobEffectCategory, int n) {
      super(mobEffectCategory, n);
   }

   public boolean applyEffectTick(LivingEntity entity, int n) {
      return entity.hurt(IRDamageSource.radiation(entity.level()), n / 100 + 0.5F);
   }

   public boolean isDurationEffectTick(int n, int n2) {
      int n3 = 25 >> n2;
      return n3 == 0 || n % n3 == 0;
   }

   public void applyTo(LivingEntity entity, int n, int n2) {
      entity.addEffect(new MobEffectInstance(IRMobEffects.RADIATION, n, n2));
   }
}
