package com.industrialreforged.tech.block.heat;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.blockentity.heat.HeatPipeBlockEntity;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Труба охлаждения: тонкий «провод» с отводами к машинам, охладителям и другим трубам. */
public class HeatPipeBlock extends Block implements EntityBlock {
   private static final Map<Direction, BooleanProperty> CONNECTIONS = PipeBlock.PROPERTY_BY_DIRECTION;
   private static final VoxelShape CORE = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
   private static final VoxelShape[] SHAPES = new VoxelShape[64];

   static {
      for (int mask = 0; mask < SHAPES.length; mask++) {
         VoxelShape shape = CORE;

         for (Direction dir : Direction.values()) {
            if ((mask & 1 << dir.ordinal()) != 0) {
               shape = Shapes.or(shape, arm(dir));
            }
         }

         SHAPES[mask] = shape;
      }
   }

   private static VoxelShape arm(Direction dir) {
      return switch (dir) {
         case DOWN -> Block.box(5.0, 0.0, 5.0, 11.0, 5.0, 11.0);
         case UP -> Block.box(5.0, 11.0, 5.0, 11.0, 16.0, 11.0);
         case NORTH -> Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 5.0);
         case SOUTH -> Block.box(5.0, 5.0, 11.0, 11.0, 11.0, 16.0);
         case WEST -> Block.box(0.0, 5.0, 5.0, 5.0, 11.0, 11.0);
         case EAST -> Block.box(11.0, 5.0, 5.0, 16.0, 11.0, 11.0);
      };
   }

   /** Сколько тепла труба пропускает через себя за тик (усилено x20 под большие системы). */
   public static int rate(int tier) {
      return switch (tier) {
         case 2 -> 2000;
         case 1 -> 800;
         default -> 320;
      };
   }

   /** Буфер тепла в трубе. */
   public static int capacity(int tier) {
      return switch (tier) {
         case 2 -> 320000;
         case 1 -> 160000;
         default -> 80000;
      };
   }

   /** Пассивное рассеивание тепла в воздух за тик (x4, чтобы длинные трассы не съедали всё). */
   public static int ambientLoss(int tier) {
      return switch (tier) {
         case 2 -> 32;
         case 1 -> 16;
         default -> 8;
      };
   }

   public final int tier;

   public HeatPipeBlock(int tier) {
      super(Properties.of()
         .strength(1.0F, 5.0F)
         .requiresCorrectToolForDrops()
         .sound(SoundType.METAL)
         .noOcclusion()
         .dynamicShape());
      this.tier = tier;
      BlockState state = this.defaultBlockState();

      for (BooleanProperty property : CONNECTIONS.values()) {
         state = state.setValue(property, false);
      }

      this.registerDefaultState(state);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(PipeBlock.NORTH, PipeBlock.SOUTH, PipeBlock.WEST, PipeBlock.EAST, PipeBlock.UP, PipeBlock.DOWN);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return this.connectionState(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
   }

   @Override
   protected BlockState updateShape(
      BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos
   ) {
      return state.setValue(CONNECTIONS.get(direction), this.canConnectTo(level.getBlockEntity(neighborPos)));
   }

   /** Пересчитывает отводы по всем сторонам; используется при загрузке чанка. */
   public BlockState connectionState(BlockState state, BlockGetter level, BlockPos pos) {
      for (Direction dir : Direction.values()) {
         state = state.setValue(CONNECTIONS.get(dir), this.canConnectTo(level.getBlockEntity(pos.relative(dir))));
      }

      return state;
   }

   private boolean canConnectTo(@Nullable BlockEntity neighbor) {
      // камера подключается только когда она часть структуры реактора
      if (neighbor instanceof com.industrialreforged.tech.blockentity.reactor.ReactorChamberElectricBlockEntity chamber) {
         return chamber.getReactorInstance() != null;
      }

      return neighbor instanceof HeatPipeBlockEntity
         || neighbor instanceof AbstractStandardMachineBlockEntity
         || neighbor instanceof com.industrialreforged.tech.multiblock.tank.TankValveBlockEntity
         || neighbor instanceof com.industrialreforged.tech.api.tile.IHeatSource;
   }

   private static int mask(BlockState state) {
      int mask = 0;

      for (Direction dir : Direction.values()) {
         if (state.getValue(CONNECTIONS.get(dir))) {
            mask |= 1 << dir.ordinal();
         }
      }

      return mask;
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES[mask(state)];
   }

   @Override
   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES[mask(state)];
   }

   @Override
   protected boolean isPathfindable(BlockState state, PathComputationType type) {
      return false;
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && !level.isClientSide) {
         // соседние трубы перестроят сеть на следующем тике
         for (Direction dir : Direction.values()) {
            if (level.getBlockEntity(pos.relative(dir)) instanceof HeatPipeBlockEntity pipe) {
               pipe.invalidateNetwork();
            }
         }
      }
      super.onRemove(state, level, pos, newState, isMoving);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new HeatPipeBlockEntity(pos, state);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return BaseBlockEntity.serverTick(level);
   }
}
