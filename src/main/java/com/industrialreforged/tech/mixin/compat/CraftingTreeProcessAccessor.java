package com.industrialreforged.tech.mixin.compat;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/** The package-private parts of AE2's crafting calculation node for one pattern, which {@link CraftingTreeNodeMixin} needs. */
@Pseudo
@Mixin(targets = "appeng.crafting.CraftingTreeProcess", remap = false)
public interface CraftingTreeProcessAccessor {
   @Accessor(value = "details", remap = false)
   IPatternDetails ir$details();

   @Invoker(value = "limitsQuantity", remap = false)
   boolean ir$limitsQuantity();

   @Invoker(value = "getOutputCount", remap = false)
   long ir$outputCount(AEKey what);
}
