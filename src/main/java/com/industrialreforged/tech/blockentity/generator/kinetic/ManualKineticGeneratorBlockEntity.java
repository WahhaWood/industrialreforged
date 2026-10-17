package com.industrialreforged.tech.blockentity.generator.kinetic;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.api.tile.IKineticSource;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class ManualKineticGeneratorBlockEntity extends BaseBlockEntity implements IKineticSource, IServerTick {
   public int clicks;
   public int power;

   public ManualKineticGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.MANUAL_KINETIC_GENERATOR.get(), blockPos, blockState);
   }

   @Override
   public int getKineticEnergy(Direction direction) {
      return this.power;
   }

   @Override
   public int extractKineticEnergy(Direction direction, int amount, boolean simulation) {
      int extract = Math.min(this.power, amount);
      if (!simulation) {
         this.power -= extract;
      }

      return extract;
   }

   public InteractionResult playerClicked(Player player) {
      if (player.getFoodData().getFoodLevel() > 6 && this.clicks < 10) {
         if (player instanceof ServerPlayer) {
            int n = !Util.isFakePlayer(player, false) ? 400 : 20;
 n = (int)n;
            this.power = Math.min(this.power + n, 1000);
            player.causeFoodExhaustion(0.25F);
            this.clicks++;
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }
}
