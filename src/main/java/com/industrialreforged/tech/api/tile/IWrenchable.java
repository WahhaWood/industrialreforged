package com.industrialreforged.tech.api.tile;

import com.industrialreforged.tech.api.item.IWrench;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

public interface IWrenchable {
   default float getWrenchDropRate() {
      return 1.0F;
   }

   ItemStack getNonWrenchDrop();

   /**
    * A wrench (or any other {@link IWrench}) removes the machine intact; every
    * other tool breaks it down into the casing it was built from.  The drop
    * rate is not rolled anymore: the outcome only depends on the tool.
    */
   @NotNull
   default ItemStack getWrenchDrops(@NotNull BlockState blockState, @NotNull Builder builder) {
      ItemStack tool = (ItemStack)builder.getOptionalParameter(LootContextParams.TOOL);
      boolean wrench = tool != null && tool.getItem() instanceof IWrench;
      return wrench ? blockState.getBlock().asItem().getDefaultInstance() : this.getNonWrenchDrop();
   }

   default boolean canWrench(Player player, BlockState state, BlockEntity entity) {
      return true;
   }
}
