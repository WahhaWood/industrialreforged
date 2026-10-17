package com.industrialreforged.tech.item.armor.jetpack;

import com.industrialreforged.tech.core.ref.IRDataComponents;
import net.minecraft.world.item.ItemStack;

public interface ICreativeJetpack {
   default boolean isJetpackActive(ItemStack stack) {
      return (Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false);
   }
}
