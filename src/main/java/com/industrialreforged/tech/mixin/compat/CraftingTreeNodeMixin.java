package com.industrialreforged.tech.mixin.compat;

import appeng.api.stacks.AEKey;
import appeng.crafting.CraftBranchFailure;
import appeng.crafting.CraftingTreeProcess;
import appeng.crafting.inv.ChildCraftingSimulationState;
import appeng.crafting.inv.CraftingSimulationState;
import com.industrialreforged.tech.core.integration.ae2.MEMachinePattern;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * AE2 crafting calculation, one item of the tree. Checked against AE2 19.2.17 (javap: {@code request} has one
 * {@code addStackBytes} call and two {@code CraftingTreeProcess.request} calls, the second one in the loop over
 * several patterns; {@code totalRequestedItems} is in the LVT). Without a target it does nothing.
 *
 * <ul>
 *   <li>Bytes: AE2 bills an item entering a pattern at 8 times the storage price. For an ME machine pattern
 *       the plain price is billed, the machine holds nothing for long.</li>
 *   <li>Time: when an item has several patterns (dust from an ingot, from crushed ore, from a plate...) AE2
 *       plans them one craft at a time, each with its own copy of the inventory: hundreds of thousands of
 *       dust are hundreds of thousands of passes. For an ME machine pattern the whole amount is tried at once;
 *       if the inputs fall short, half of it, and so on, so a lack of inputs ends up with the same plan.</li>
 * </ul>
 */
@Pseudo
@Mixin(targets = "appeng.crafting.CraftingTreeNode", remap = false)
public abstract class CraftingTreeNodeMixin {
   @Shadow(remap = false)
   @Final
   private CraftingTreeProcess parent;
   @Shadow(remap = false)
   @Final
   private AEKey what;

   @WrapOperation(
      method = "request",
      at = @At(value = "INVOKE", target = "Lappeng/crafting/inv/CraftingSimulationState;addStackBytes(Lappeng/api/stacks/AEKey;JJ)V"),
      require = 0
   )
   private void ir$meInputsAtStoragePrice(CraftingSimulationState inv, AEKey key, long amount, long multiplier, Operation<Void> original) {
      if (this.parent != null && ((CraftingTreeProcessAccessor)this.parent).ir$details() instanceof MEMachinePattern) {
         inv.addBytes((double)amount * multiplier / key.getType().getAmountPerByte());
      } else {
         original.call(inv, key, amount, multiplier);
      }
   }

   @WrapOperation(
      method = "request",
      at = @At(value = "INVOKE", target = "Lappeng/crafting/CraftingTreeProcess;request(Lappeng/crafting/inv/CraftingSimulationState;J)V", ordinal = 1),
      require = 0
   )
   private void ir$planMEPatternInBulk(
      CraftingTreeProcess process,
      CraftingSimulationState child,
      long times,
      Operation<Void> original,
      @Local(argsOnly = true) CraftingSimulationState inv,
      @Local(name = "totalRequestedItems") long totalRequestedItems
   ) {
      CraftingTreeProcessAccessor pattern = (CraftingTreeProcessAccessor)process;
      long perCraft = pattern.ir$details() instanceof MEMachinePattern && !pattern.ir$limitsQuantity() ? pattern.ir$outputCount(this.what) : 0L;
      if (perCraft > 0) {
         for (long crafts = (totalRequestedItems + perCraft - 1) / perCraft; crafts > 1; crafts /= 2) {
            // a failed attempt leaves its inventory half used, so every attempt gets a fresh one
            ChildCraftingSimulationState attempt = new ChildCraftingSimulationState(inv);
            try {
               original.call(process, attempt, crafts);
            } catch (Exception e) {
               if (e instanceof CraftBranchFailure) {
                  continue;
               }

               throw e;
            }

            attempt.applyDiff(child);
            return;
         }
      }

      original.call(process, child, times);
   }
}
