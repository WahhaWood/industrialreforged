package com.industrialreforged.tech.mixin.compat;

import appeng.api.crafting.IPatternDetails;
import appeng.crafting.inv.CraftingSimulationState;
import com.industrialreforged.tech.core.integration.ae2.MEMachinePattern;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * AE2 crafting calculation, one pattern: AE2 bills a byte per craft. An ME machine takes a whole task in one
 * push, so its crafts are free (the items still cost bytes, see {@link CraftingTreeNodeMixin}).
 * Checked against AE2 19.2.17 ({@code CraftingTreeProcess.request} has one {@code addBytes} call).
 */
@Pseudo
@Mixin(targets = "appeng.crafting.CraftingTreeProcess", remap = false)
public abstract class CraftingTreeProcessMixin {
   @Shadow(remap = false)
   @Final
   IPatternDetails details;

   @WrapOperation(
      method = "request",
      at = @At(value = "INVOKE", target = "Lappeng/crafting/inv/CraftingSimulationState;addBytes(D)V"),
      require = 0
   )
   private void ir$meCraftsAreFree(CraftingSimulationState inv, double bytes, Operation<Void> original) {
      if (!(this.details instanceof MEMachinePattern)) {
         original.call(inv, bytes);
      }
   }
}
