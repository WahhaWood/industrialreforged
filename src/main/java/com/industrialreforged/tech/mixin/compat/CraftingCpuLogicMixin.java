package com.industrialreforged.tech.mixin.compat;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.inv.ListCraftingInventory;
import com.industrialreforged.tech.core.integration.ae2.MECraftingCpuHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Crafting CPU: an ME machine (IR's, or one for Botania recipes) takes a whole task in one push (see {@link MECraftingCpuHooks}).
 * AdvancedAE's quantum computer runs a copy of AE2's CPU logic with the same method and local names,
 * so one wrapper serves both. Checked against AE2 19.2.17 and AdvancedAE 1.6.12 (javap: one
 * {@code pushPattern} call, locals {@code task}, {@code expectedOutputs}, {@code expectedContainerItems}, {@code patternPower},
 * {@code pushedPatterns} in the LVT; both have {@code finishJob(boolean)}). Without a target it does nothing.
 */
@Pseudo
@Mixin(targets = {"appeng.crafting.execution.CraftingCpuLogic", "net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic"}, remap = false)
public abstract class CraftingCpuLogicMixin {
   @Shadow(remap = false)
   @Final
   private ListCraftingInventory inventory;

   @WrapOperation(
      method = "executeCrafting",
      at = @At(
         value = "INVOKE",
         target = "Lappeng/api/networking/crafting/ICraftingProvider;pushPattern(Lappeng/api/crafting/IPatternDetails;[Lappeng/api/stacks/KeyCounter;)Z"
      ),
      require = 0
   )
   private boolean ir$pushToMEMachine(
      ICraftingProvider provider,
      IPatternDetails details,
      KeyCounter[] inputs,
      Operation<Boolean> original,
      @Local(argsOnly = true) IEnergyService energy,
      @Local(name = "task") Map.Entry<IPatternDetails, ?> task,
      @Local(name = "expectedOutputs") KeyCounter expectedOutputs,
      @Local(name = "expectedContainerItems") KeyCounter expectedContainerItems,
      @Local(name = "patternPower") LocalDoubleRef patternPower,
      @Local(name = "pushedPatterns") LocalIntRef pushedPatterns
   ) {
      if (!MECraftingCpuHooks.isMEMachine(provider)) {
         return original.call(provider, details, inputs);
      }

      boolean pushed;
      MECraftingCpuHooks.beginPush(this);
      try {
         pushed = original.call(provider, details, inputs);
      } finally {
         MECraftingCpuHooks.endPush();
      }

      if (!pushed) {
         return false;
      }

      // the CPU counts the push right after this call: cancel it out, ME machines cost no co-processor time
      pushedPatterns.set(pushedPatterns.get() - 1);
      TaskProgressAccessor progress = (TaskProgressAccessor)task.getValue();
      // AE2 takes this push off the task itself, so at most value - 1 more
      long extra = MECraftingCpuHooks.pushMore(
         provider, inputs, progress.ir$getValue() - 1, this.inventory, energy, patternPower.get(), expectedOutputs, expectedContainerItems
      );
      if (extra > 0) {
         progress.ir$setValue(progress.ir$getValue() - extra);
         // drawn by AE2 after this call, together with the outputs and container items pushMore scaled
         patternPower.set(patternPower.get() * (extra + 1));
      }

      return pushed;
   }

   /** Job over without success (cancel, stop): the ME machines give back what this CPU pushed and they have not run. */
   @Inject(method = "finishJob", at = @At("HEAD"), require = 0, remap = false)
   private void ir$returnMEMachineWork(boolean success, CallbackInfo ci) {
      if (!success) {
         MECraftingCpuHooks.jobAborted(this);
      }
   }
}
