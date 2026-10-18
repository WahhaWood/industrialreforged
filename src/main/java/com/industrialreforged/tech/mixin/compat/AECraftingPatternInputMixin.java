package com.industrialreforged.tech.mixin.compat;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Input slot of an AE2 crafting-table pattern. For every craft a CPU pushes, AE2 works out the container item
 * left in that slot by running the recipe's {@code getRemainingItems} on a freshly built grid (its source notes
 * this could be cached). Almost every input leaves nothing, so that answer is remembered per slot and item; an
 * input that leaves something (a bucket, a damaged tool) still goes through the recipe on every craft.
 * The pattern holds its recipe for life, so the answer cannot go stale. Checked against AE2 19.2.17.
 */
@Pseudo
@Mixin(targets = "appeng.crafting.pattern.AECraftingPattern$Input", remap = false)
public abstract class AECraftingPatternInputMixin {
   /** Items known to leave no container item in this slot; planning threads read it too. */
   @Unique
   private final Set<AEKey> ir$noRemainder = ConcurrentHashMap.newKeySet();

   @Inject(method = "getRemainingKey", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
   private void ir$knownEmpty(AEKey template, CallbackInfoReturnable<AEKey> cir) {
      if (this.ir$noRemainder.contains(template)) {
         cir.setReturnValue(null);
      }
   }

   @Inject(method = "getRemainingKey", at = @At("RETURN"), require = 0, remap = false)
   private void ir$rememberEmpty(AEKey template, CallbackInfoReturnable<AEKey> cir) {
      if (cir.getReturnValue() == null && template instanceof AEItemKey) {
         this.ir$noRemainder.add(template);
      }
   }
}
