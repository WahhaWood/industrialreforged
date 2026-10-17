package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.crop.IRCrops;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ItemWeedingTrowel extends Item {
   public ItemWeedingTrowel(Properties properties) {
      super(properties);
   }

   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
      Level level = context.getLevel();
      if (level.isClientSide) {
         return InteractionResult.PASS;
      } else {
         BlockPos pos = context.getClickedPos();
         if (level.getBlockEntity(pos) instanceof CropBlockEntity entityCrop && entityCrop.getCrop().getCard() == IRCrops.weed) {
            StackUtil.dropAsEntity(level, pos, new ItemStack((ItemLike)IRItems.WEED.get(), entityCrop.getCurrentAge()));
            entityCrop.reset();
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }
}
