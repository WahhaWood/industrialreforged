package com.industrialreforged.tech.container;

import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public interface IProcessable<R extends Recipe<?>> {
   @Nullable
   R process(int var1);

   boolean assemble(int var1);
}
