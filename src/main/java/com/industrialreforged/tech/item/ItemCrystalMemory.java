package com.industrialreforged.tech.item;

import com.industrialreforged.tech.core.recipe.manager.UuRecipeManager;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.util.Util;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class ItemCrystalMemory extends Item {
   public ItemCrystalMemory() {
      super(new Properties().stacksTo(1).component(IRDataComponents.PATTERN, IRDataComponents.ItemStackRecord.EMPTY));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      ItemStack pattern = this.getPattern(stack);
      if (!pattern.isEmpty() && context.level() != null) {
         tooltipComponents.add(Component.translatable("item.industrialreforged.crystal_memory.tooltip.item", new Object[]{pattern.getHoverName()}));
         tooltipComponents.add(
            Component.translatable(
               "item.industrialreforged.crystal_memory.tooltip.uu_matter", new Object[]{Util.toSiStringUU(UuRecipeManager.instance.getCost(context.level(), pattern))}
            )
         );
      } else {
         tooltipComponents.add(Component.translatable("item.industrialreforged.crystal_memory.tooltip.empty"));
      }
   }

   public ItemStack getPattern(ItemStack container) {
      IRDataComponents.ItemStackRecord pattern = (IRDataComponents.ItemStackRecord)container.get(IRDataComponents.PATTERN);
      return pattern != null ? pattern.stack() : ItemStack.EMPTY;
   }

   public void setPattern(ItemStack container, ItemStack pattern) {
      if (container.has(IRDataComponents.PATTERN)) {
         container.set(IRDataComponents.PATTERN, new IRDataComponents.ItemStackRecord(pattern));
      }
   }
}
