package com.industrialreforged.mixin;

import com.industrialreforged.IndustrialReforged;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
   @Overwrite
   private String createTitle() {
      return IndustrialReforged.PROJECT_NAME + " - " + IndustrialReforged.TAB_SERVER_NAME;
   }
}
