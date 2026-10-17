package com.industrialreforged.blockentity.api.capabilities;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public interface IFluidWrapper {
   IFluidHandler getFluidWrapper(@Nullable Direction var1);
}
