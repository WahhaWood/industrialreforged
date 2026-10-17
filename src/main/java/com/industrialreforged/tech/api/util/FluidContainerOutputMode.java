package com.industrialreforged.tech.api.util;

public enum FluidContainerOutputMode {
   EmptyFullToOutput(true),
   AnyToOutput(true),
   InPlacePreferred(false),
   InPlace(false);

   private final boolean outputEmptyFull;

   FluidContainerOutputMode(boolean bl) {
      this.outputEmptyFull = bl;
   }

   public boolean isOutputEmptyFull() {
      return this.outputEmptyFull;
   }
}
