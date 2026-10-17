package com.industrialreforged.tech.block;

import com.industrialreforged.tech.api.tile.RetexturableBlock;
import com.industrialreforged.tech.api.tile.StainableBlock;
import com.industrialreforged.tech.blockentity.WallBlockEntity;
import com.industrialreforged.tech.blockentity.comp.Obscuration;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

public class WallBlock extends Block implements StainableBlock, RetexturableBlock {
   public static final DyeColor DEFAULT_COLOR = DyeColor.LIGHT_GRAY;
   private static final Map<DyeColor, WallBlock> types = new EnumMap<>(DyeColor.class);
   final DyeColor color;

   public WallBlock(Properties properties, DyeColor color) {
      super(properties);
      this.color = color;
      types.put(color, this);
   }

   @Override
   public DyeColor getColor(Level level, BlockPos blockPos, Direction direction) {
      return this.color;
   }

   @Override
   public boolean setColor(Level level, BlockPos blockPos, @Nullable Direction direction, DyeColor dyeColor) {
      WallBlock value;
      if (dyeColor != this.color && (value = get(dyeColor)) != null) {
         level.setBlockAndUpdate(blockPos, value.defaultBlockState());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean retexture(
      BlockState blockState, Level level, BlockPos blockPos, Direction direction, Player player, Obscuration.ObscurationData obscurationData
   ) {
      level.setBlockAndUpdate(blockPos, ((Block)IRBlocks.OBSCURED_WALL.get()).defaultBlockState());
      if (level.getBlockEntity(blockPos) instanceof WallBlockEntity wall) {
         wall.setColor(this.color);
         if (wall.obscuration != null && wall.obscuration.applyObscuration(direction, obscurationData)) {
            return true;
         }
      }

      level.setBlockAndUpdate(blockPos, blockState);
      return false;
   }

   public static WallBlock get(DyeColor dyeColor) {
      return types.get(dyeColor);
   }
}
