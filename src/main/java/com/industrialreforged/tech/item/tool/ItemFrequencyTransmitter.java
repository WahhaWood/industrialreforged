package com.industrialreforged.tech.item.tool;

import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.blockentity.machine.TeleporterBlockEntity;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ItemFrequencyTransmitter extends Item {
   public ItemFrequencyTransmitter(Properties properties) {
      super(properties.component(IRDataComponents.LINKED, BlockPos.ZERO));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack handStack = player.getItemInHand(hand);
      if (!level.isClientSide && player.isShiftKeyDown()) {
         handStack.set(IRDataComponents.LINKED, BlockPos.ZERO);
         handStack.remove(IRDataComponents.LINKED_DIMENSION);
         MsgUtils.send(player, "text.industrialreforged.message.frequency_transmitter.unlinked");
      }

      return InteractionResultHolder.sidedSuccess(handStack, level.isClientSide);
   }

   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
      Level level = context.getLevel();
      if (level.isClientSide) {
         return InteractionResult.PASS;
      } else {
         Player player = context.getPlayer();
         if (player == null) {
            return InteractionResult.PASS;
         } else {
            BlockPos pos = context.getClickedPos();
            if (!(level.getBlockEntity(pos) instanceof TeleporterBlockEntity tp)) {
               return InteractionResult.PASS;
            } else {
               BlockPos target = (BlockPos)stack.getOrDefault(IRDataComponents.LINKED, BlockPos.ZERO);
               if (target == BlockPos.ZERO) {
                  stack.set(IRDataComponents.LINKED, tp.getBlockPos());
                  stack.set(IRDataComponents.LINKED_DIMENSION, level.dimension().location());
                  MsgUtils.send(player, "text.industrialreforged.message.frequency_transmitter.linked");
               } else if (tp.getBlockPos().equals(target)) {
                  MsgUtils.send(player, "text.industrialreforged.message.frequency_transmitter.stuck");
               } else if (tp.hasTarget() && tp.getTarget().equals(target)) {
                  MsgUtils.send(player, "text.industrialreforged.message.frequency_transmitter.unchanged");
               } else {
                  // связь может быть межмировой: цель ищем в сохранённом измерении
                  ResourceLocation targetDim = (ResourceLocation)stack.getOrDefault(
                     IRDataComponents.LINKED_DIMENSION, level.dimension().location()
                  );
                  ServerLevel targetLevel = ((ServerLevel)level).getServer()
                     .getLevel(ResourceKey.create(Registries.DIMENSION, targetDim));
                  if (targetLevel == null || !(targetLevel.getBlockEntity(target) instanceof TeleporterBlockEntity entity)) {
                     return InteractionResult.PASS;
                  }

                  tp.setTarget(target, targetDim);
                  entity.setTarget(pos, level.dimension().location());
                  MsgUtils.send(player, "text.industrialreforged.message.frequency_transmitter.success");
               }

               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      BlockPos pos = (BlockPos)stack.getOrDefault(IRDataComponents.LINKED, BlockPos.ZERO);
      if (pos != BlockPos.ZERO) {
         tooltipComponents.add(
            Component.translatable("text.industrialreforged.tooltip.frequency_transmitter.target", new Object[]{pos.getX(), pos.getY(), pos.getZ()})
         );
         ResourceLocation dimension = (ResourceLocation)stack.get(IRDataComponents.LINKED_DIMENSION);
         if (dimension != null) {
            tooltipComponents.add(
               Component.translatable("text.industrialreforged.tooltip.frequency_transmitter.dimension", new Object[]{dimension.toString()})
            );
         }
      } else {
         tooltipComponents.add(Component.translatable("text.industrialreforged.tooltip.frequency_transmitter.blank"));
      }
   }
}
