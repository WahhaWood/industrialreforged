package com.industrialreforged.tech.block.storage;

import com.industrialreforged.tech.block.base.AbstractElectricMachineBlock;
import com.industrialreforged.tech.blockentity.storage.ChargepadBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChargepadBlock<T extends ChargepadBlockEntity> extends AbstractElectricMachineBlock<T> {
   private static final VoxelShape SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);

   public ChargepadBlock(Supplier<BlockEntityType<T>> supplier) {
      super(Properties.of().strength(2.0F, 10.0F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion(), supplier);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (level.getBlockEntity(pos) instanceof ChargepadBlockEntity e) {
         e.entityInside(entity);
      }
   }

   @Override
   public float getWrenchDropRate() {
      return 1.0F;
   }
}
