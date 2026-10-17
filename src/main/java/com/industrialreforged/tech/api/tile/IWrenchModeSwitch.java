package com.industrialreforged.tech.api.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Блок, у которого ключ переключает режим работы (например, вход/выход клапана резервуара). */
public interface IWrenchModeSwitch {
   /**
    * Переключает режим. Вызывается ключом до обычного взаимодействия.
    *
    * @return true, если режим был переключён (ключ тратит прочность и проигрывает звук)
    */
   boolean switchMode(Level level, BlockPos pos, BlockState state, Player player);
}
