package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.api.item.IBlockCuttingBlade;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class ItemCuttingBlade extends Item implements IBlockCuttingBlade {
   private final int hardness;

   public ItemCuttingBlade(int hardness) {
      super(new Properties());
      this.hardness = hardness;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("industrialreforged.tooltip.blade.hardness", new Object[]{this.hardness}));
   }

   @Override
   public int getHardness(ItemStack stack) {
      return this.hardness;
   }
}
