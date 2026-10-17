package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.core.IR;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class IRDamageTypes {
   public static final ResourceKey<DamageType> CROP_EATING = create("crop_eating");
   public static final ResourceKey<DamageType> ENERGY_ARROW = ResourceKey.create(Registries.DAMAGE_TYPE, IR.location("energy_arrow"));
   public static final ResourceKey<DamageType> RADIATION = create("radiation");

   private static ResourceKey<DamageType> create(String name) {
      return ResourceKey.create(Registries.DAMAGE_TYPE, IR.location(name));
   }
}
