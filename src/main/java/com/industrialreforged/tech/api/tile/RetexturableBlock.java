package com.industrialreforged.tech.api.tile;

import com.industrialreforged.tech.blockentity.comp.Obscuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface RetexturableBlock {
   boolean retexture(BlockState var1, Level var2, BlockPos var3, Direction var4, Player var5, Obscuration.ObscurationData var6);
}
