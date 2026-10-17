package com.industrialreforged.tech.api.reactor;

import org.jetbrains.annotations.Nullable;

public interface IReactorChamber {
   @Nullable
   IReactor getReactorInstance();

   boolean isWall();
}
