package com.industrialreforged.tech.api.tile;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public interface IRotorProvider {
   int getRotorDiameter();

   Direction getFacing();

   float getAngle();

   ResourceLocation getRotorRenderTexture();
}
