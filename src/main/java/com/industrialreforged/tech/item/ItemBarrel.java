package com.industrialreforged.tech.item;

import com.industrialreforged.tech.block.BrewBarrelBlock;
import com.industrialreforged.tech.blockentity.BrewBarrelBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.util.StackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ItemBarrel extends BlockItem {
   public ItemBarrel() {
      super(
         (Block)IRBlocks.BREW_BARREL.get(),
         new Properties().component(IRDataComponents.BOOZE, new BrewBarrelBlock.BrewBoozeData(0, 0, 0, false, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0))
      );
   }

   public Component getName(ItemStack stack) {
      BrewBarrelBlock.BrewBoozeData data = (BrewBarrelBlock.BrewBoozeData)stack.get(IRDataComponents.BOOZE);
      return Component.translatable(data != null && data.type > 0 ? "item.industrialreforged.brew_barrel.booze" : "item.industrialreforged.brew_barrel.empty");
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      Player player = context.getPlayer();
      BlockPos pos = context.getClickedPos();
      ItemStack stack = context.getItemInHand();
      if (level.getBlockState(pos).is((Block)IRBlocks.WOODEN_SCAFFOLD.get())) {
         StackUtil.consumeOrError(player, context.getHand(), 1);
         level.setBlockAndUpdate(pos, this.getBlock().defaultBlockState());
         BrewBarrelBlock.BrewBoozeData data = (BrewBarrelBlock.BrewBoozeData)stack.get(IRDataComponents.BOOZE);
         if (data != null && level.getBlockEntity(pos) instanceof BrewBarrelBlockEntity barrel) {
            barrel.data.copy(data);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }
}
