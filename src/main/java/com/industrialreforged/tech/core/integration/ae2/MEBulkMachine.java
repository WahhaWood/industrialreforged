package com.industrialreforged.tech.core.integration.ae2;

/**
 * A crafting provider that takes a whole CPU task in one push ({@link MECraftingCpuHooks#pushMore}).
 * IR's ME machines and the ME machines for Botania recipes implement it.
 */
public interface MEBulkMachine {
   /** Crafts the machine's queue still takes. */
   long freeCapacity();

   /** More copies of the push just accepted; their inputs are already taken from the CPU. */
   void acceptMore(long crafts);
}
