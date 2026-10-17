package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.block.WallBlock;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ItemToolPainter extends Item {
   public ItemToolPainter() {
      super(new Properties().durability(32).component(DataComponents.BASE_COLOR, DyeColor.WHITE));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("industrialreforged.tooltip.painter", new Object[]{getColor(stack).getName()}));
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public InteractionResult useOn(UseOnContext context) {
      Level world = context.getLevel();
      BlockPos pos = context.getClickedPos();
      if (world.isClientSide) {
         return InteractionResult.FAIL;
      }

      Player player = context.getPlayer();
      if (player == null) {
         return InteractionResult.FAIL;
      }

      ItemStack stack = player.getItemInHand(context.getHand());
      if (stack.getItem().equals(this) && world.getBlockState(pos).getBlock() instanceof WallBlock wall) {
         DyeColor dyeColor = getColor(stack);
         if (wall.setColor(world, pos, null, dyeColor)) {
            stack.hurtAndBreak(1, player, context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            if (stack.isEmpty()) {
               ItemStack newStack = new ItemStack((ItemLike)IRItems.PAINTER.get());
               player.setItemInHand(context.getHand(), newStack);
            }
         }
      }

      return super.useOn(context);
   }

   public static DyeColor getColor(ItemStack stack) {
      return (DyeColor)stack.getOrDefault(DataComponents.BASE_COLOR, DyeColor.WHITE);
   }

   public static void setColor(ItemStack stack, DyeColor color) {
      stack.set(DataComponents.BASE_COLOR, color);
   }
}
