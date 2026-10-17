package com.industrialreforged.tech.core.ref;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public final class IRFluidTags {
   public static final TagKey<Fluid> STEAM = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", "steam"));

   public static void init() {
   }
}
