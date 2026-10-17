package com.industrialreforged.blockentity.api.capabilities;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public interface IContainerWrapper {
   IItemHandlerModifiable getContainerWrapper(@Nullable Direction var1);
}
