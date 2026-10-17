package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.api.crops.CropSoilType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ItemCrop extends BlockItem {
   public ItemCrop(Properties paramProperties) {
      super((Block)IRBlocks.CROP_STICK.get(), paramProperties);
   }

   protected boolean canPlace(BlockPlaceContext paramBlockPlaceContext, @NotNull BlockState paramBlockState) {
      return CropSoilType.contains(paramBlockPlaceContext.getLevel().getBlockState(paramBlockPlaceContext.getClickedPos().below()).getBlock())
         && super.canPlace(paramBlockPlaceContext, paramBlockState);
   }
}
