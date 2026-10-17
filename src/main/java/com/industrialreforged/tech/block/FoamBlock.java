package com.industrialreforged.tech.block;

import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.core.util.Util;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FoamBlock extends Block {
   public static final EnumProperty<FoamBlock.FoamType> typeProperty = EnumProperty.create("type", FoamBlock.FoamType.class);

   public FoamBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(typeProperty, FoamBlock.FoamType.normal));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{typeProperty});
   }

   @NotNull
   public VoxelShape getCollisionShape(
      @NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext
   ) {
      return Shapes.empty();
   }

   protected void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
      int n = serverLevel.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
      if (n <= 0) {
         throw new IllegalStateException("Foam was randomly ticked when world " + serverLevel + " is not ticking?");
      }

      FoamBlock.FoamType foamType = (FoamBlock.FoamType)blockState.getValue(typeProperty);
      float f = getHardenChance(serverLevel, blockPos, blockState, foamType) * 4096.0F / n;
      if (random.nextFloat() < f) {
         serverLevel.setBlockAndUpdate(blockPos, ((FoamBlock.FoamType)blockState.getValue(typeProperty)).getResult());
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (StackUtil.consume(player, interactionHand, Blocks.SAND, 1)) {
         if (!level.isClientSide) {
            level.setBlockAndUpdate(blockPos, ((FoamBlock.FoamType)blockState.getValue(typeProperty)).getResult());
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public static float getHardenChance(Level level, BlockPos blockPos, BlockState blockState, FoamBlock.FoamType foamType) {
      int n = level.getMaxLocalRawBrightness(blockPos);
      if (blockState.getLightBlock(level, blockPos) == 0) {
         for (Direction direction : Util.ALL_DIRS) {
 n = Math.max(n, level.getMaxLocalRawBrightness(blockPos.relative(direction)));
         }
      }

      int n2 = foamType.hardenTime * (16 - n);
      // IR used * 20 here, which made foam take five to ten minutes to dry.
      // The divisor is ten times smaller so foam visibly hardens in ~30-60s.
      return 1.0F / (n2 * 2);
   }

   public enum FoamType implements StringRepresentable {
      normal(300),
      reinforced(600);

      public final int hardenTime;

      FoamType(int n2) {
         this.hardenTime = n2;
      }

      @NotNull
      public String getSerializedName() {
         return this.name();
      }

      public List<ItemStack> getDrops() {
         switch (this) {
            case normal:
            case reinforced:
               return new ArrayList<>();
            default:
               throw new UnsupportedOperationException();
         }
      }

      public BlockState getResult() {
         switch (this) {
            case normal:
               return WallBlock.get(WallBlock.DEFAULT_COLOR).defaultBlockState();
            case reinforced:
               return ((Block)IRBlocks.REINFORCED_STONE.get()).defaultBlockState();
            default:
               throw new UnsupportedOperationException();
         }
      }
   }
}
