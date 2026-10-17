package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.inv.ListCraftingInventory;

/**
 * Called from {@code CraftingCpuLogicMixin}: a crafting CPU hands an ME machine a whole task in one push.
 *
 * <p>AE2 pushes a pattern one craft at a time, and every craft is a full pass: inputs picked from the CPU
 * (tag substitutes searched one by one), network power checked and drawn, the expected outputs noted.
 * At {@value AbstractMENodeBlockEntity#OPERATIONS_PER_TICK} crafts per tick for each step of a chain that
 * cost the server tens of milliseconds a tick. Here AE2 makes its usual first push, then the machine gets
 * the rest of the task (as far as its queue, the CPU's stock and the network's power go) in one block, and
 * AE2 books it with its own code for that push, scaled. A tick costs the same for 1 or 10 000 crafts.
 * Pushes into an ME machine do not use up the CPU's co-processor operations either.
 */
public final class MECraftingCpuHooks {
   private MECraftingCpuHooks() {
   }

   public static boolean isMEMachine(ICraftingProvider provider) {
      return provider instanceof MEMachineBlockEntity;
   }

   /**
    * After AE2 pushed one craft of {@code details} into the ME machine {@code provider}: moves up to
    * {@code limit} more copies of the same craft (same input items) from the CPU inventory into the machine,
    * and adds their outputs to {@code expectedOutputs}, which AE2 then books as awaited. The caller scales
    * the pattern power AE2 is about to draw and takes the returned count off the task.
    *
    * @return how many more crafts the machine took, 0..limit
    */
   public static long pushMore(
      ICraftingProvider provider, IPatternDetails details, KeyCounter[] inputs, long limit, ListCraftingInventory cpu,
      IEnergyService energy, double patternPower, KeyCounter expectedOutputs
   ) {
      if (!(provider instanceof MEMachineBlockEntity machine) || limit <= 0) {
         return 0;
      }

      long extra = Math.min(limit, machine.freeCapacity());
      KeyCounter perCraft = new KeyCounter();
      for (KeyCounter slot : inputs) {
         perCraft.addAll(slot);
      }

      for (var input : perCraft) {
         if (input.getLongValue() > 0) {
            extra = Math.min(extra, cpu.extract(input.getKey(), Long.MAX_VALUE, Actionable.SIMULATE) / input.getLongValue());
         }
      }

      if (patternPower > 0 && extra > 0) {
         // AE2 checked the power of one craft and draws it after the push: this one and the extra ones must fit
         double available = energy.extractAEPower(patternPower * (extra + 1), Actionable.SIMULATE, PowerMultiplier.CONFIG);
         extra = Math.min(extra, (long)Math.floor(available / patternPower + 1.0E-9) - 1);
      }

      if (extra <= 0) {
         return 0;
      }

      for (var input : perCraft) {
         cpu.extract(input.getKey(), input.getLongValue() * extra, Actionable.MODULATE);
      }

      for (GenericStack output : details.getOutputs()) {
         expectedOutputs.add(output.what(), output.amount() * extra);
      }

      machine.acceptMore(extra);
      return extra;
   }
}
