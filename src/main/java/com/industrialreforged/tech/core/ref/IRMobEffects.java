package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.core.RadiationMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IRMobEffects {
   public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, "industrialreforged");
   public static final DeferredHolder<MobEffect, RadiationMobEffect> RADIATION = REGISTRY.register(
      "radiation", () -> new RadiationMobEffect(MobEffectCategory.HARMFUL, 5149489)
   );
}
