package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.entity.EnergyArrow;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IREntities {
   public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, "industrialreforged");
   public static final Supplier<EntityType<EnergyArrow>> ENERGY_ARROW = REGISTRY.register(
      "energy_arrow", () -> Builder.<EnergyArrow>of(EnergyArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("energy_arrow")
   );
}
