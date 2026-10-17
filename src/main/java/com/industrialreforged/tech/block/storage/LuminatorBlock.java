package com.industrialreforged.tech.block.storage;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.block.ActiveEntityBlock;
import com.industrialreforged.tech.api.tile.IWrenchable;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.blockentity.storage.ElectricBlockEntity;
import com.industrialreforged.tech.blockentity.storage.LuminatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LuminatorBlock<T extends ElectricBlockEntity> extends ActiveEntityBlock<LuminatorBlockEntity> implements IWrenchable {
   private final VoxelShape shape = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);

   public LuminatorBlock() {
      super(
         Properties.of()
            .strength(2.0F, 10.0F)
            .requiresCorrectToolForDrops()
            .noCollission()
            .forceSolidOn()
            .sound(SoundType.METAL)
            .lightLevel(value -> ActiveBlock.isActive(value) ? 15 : 0)
      );
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return this.shape;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new LuminatorBlockEntity(pos, state);
   }

   @Override
   public boolean canWrench(Player player, BlockState state, BlockEntity entity) {
      if (entity instanceof LuminatorBlockEntity luminator) {
         luminator.lswitch = !luminator.lswitch;
      }

      return false;
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return ItemStack.EMPTY;
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      if (level.getBlockEntity(pos) instanceof AbstractBlockEntity entity) {
         entity.updateRedstoneSignal(state, neighborPos);
      }
   }
}
