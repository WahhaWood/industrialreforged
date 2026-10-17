package com.industrialreforged.tech.core.ref;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class IRDamageSource extends DamageSource {
   private IRDamageSource(Holder<DamageType> damageType) {
      super(damageType);
   }

   public static IRDamageSource cropEating(Level level) {
      return source(level, IRDamageTypes.CROP_EATING);
   }

   public static IRDamageSource radiation(Level level) {
      return source(level, IRDamageTypes.RADIATION);
   }

   private static IRDamageSource source(Level level, ResourceKey<DamageType> damageType) {
      return new IRDamageSource(level.registryAccess().holderOrThrow(damageType));
   }
}
