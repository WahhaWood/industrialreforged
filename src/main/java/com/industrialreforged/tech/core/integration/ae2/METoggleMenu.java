package com.industrialreforged.tech.core.integration.ae2;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/** A menu with recipe switches driven by {@link MEMachineTogglePayload}. */
public interface METoggleMenu {
   void applyToggle(MEMachineTogglePayload.Action action, @Nullable ResourceLocation recipe);
}
