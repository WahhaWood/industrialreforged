package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.api.item.IEnhancedOverlayProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemToolCutter extends ItemToolCrafting implements IEnhancedOverlayProvider {
   public ItemToolCutter(Properties properties) {
      super(properties);
   }

   @Override
   public boolean providesEnhancedOverlay(Level level, BlockPos blockPos, Direction direction, Player player, ItemStack itemStack) {
      return false;
   }
}
