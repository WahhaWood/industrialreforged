package com.industrialreforged.tech.block;

import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.core.util.Util;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ScaffoldBlock extends Block {
   private static final Ingredient STICK = Ingredient.of(new ItemLike[]{Items.STICK});
   private static final Direction[] supportedFacings = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private static final double border = 0.03125;
   private static final VoxelShape aabb = Shapes.box(0.03125, 0.0, 0.03125, 0.96875, 1.0, 0.96875);
   private final int maxDistance;

   public ScaffoldBlock(Properties properties, int n) {
      super(properties);
      this.maxDistance = n;
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         livingEntity.fallDistance = 0.0F;
         double d = 0.15;
         Vec3 vec3 = livingEntity.getDeltaMovement();
         double d2 = Util.limit(vec3.x(), -d, d);
         double d3 = Util.limit(vec3.z(), -d, d);
         livingEntity.setDeltaMovement(d2, vec3.y(), d3);
         if (livingEntity.isShiftKeyDown() && livingEntity instanceof Player) {
            if (livingEntity.isInWater()) {
               livingEntity.setDeltaMovement(d2, 0.02, d3);
            } else {
               livingEntity.setDeltaMovement(d2, 0.08, d3);
            }
         } else if (livingEntity.horizontalCollision) {
            livingEntity.setDeltaMovement(d2, 0.2, d3);
         } else {
            livingEntity.setDeltaMovement(d2, -0.07, d3);
         }
      }
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return aabb;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.block();
   }

   public VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return Shapes.block();
   }

   public InteractionResult use(
      BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (player.isShiftKeyDown()) {
         return InteractionResult.PASS;
      }

      ItemStack itemStack = player.getItemInHand(interactionHand);
      if (itemStack.isEmpty()) {
         return InteractionResult.PASS;
      }

      Block block2 = blockState.getBlock();
      Block block;
      if (block2 == IRBlocks.WOODEN_SCAFFOLD.get()) {
         if (!itemStack.is(Items.STICK) || itemStack.getCount() < 2) {
            return InteractionResult.PASS;
         }

         block = (Block)IRBlocks.REINFORCED_WOODEN_SCAFFOLD.get();
      } else {
         if (block2 != IRBlocks.IRON_SCAFFOLD.get()) {
            return InteractionResult.PASS;
         }

         if (!StackUtil.checkItemEquality(itemStack, (ItemLike)IRBlocks.IRON_FENCE.get()) || itemStack.getCount() < 1) {
            return InteractionResult.PASS;
         }

         block = (Block)IRBlocks.REINFORCED_IRON_SCAFFOLD.get();
      }

      if (!this.isPillar(level, blockPos)) {
         return InteractionResult.PASS;
      }

      if (!level.isClientSide) {
         if (block2 == IRBlocks.WOODEN_SCAFFOLD.get()) {
            StackUtil.consumeOrError(player, interactionHand, Items.STICK, 2);
         } else {
            StackUtil.consumeOrError(player, interactionHand, (ItemLike)IRBlocks.IRON_FENCE.get(), 1);
         }

         level.setBlockAndUpdate(blockPos, block.defaultBlockState());
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      InteractionHand interactionHand = InteractionHand.MAIN_HAND;
      ItemStack itemStack = player.getItemInHand(interactionHand);
      if (!itemStack.isEmpty()) {
         if (StackUtil.checkItemEquality(itemStack, Item.byBlock(this))) {
            while (level.getBlockState(blockPos).getBlock() == this) {
               blockPos = blockPos.above();
            }

            if (this.canSurvive(this.defaultBlockState(), level, blockPos) && blockPos.getY() < level.getHeight()) {
               boolean bl = player.getAbilities().instabuild;
               ItemStack itemStack2 = bl ? itemStack.copy() : null;
               itemStack.useOn(
                  new BlockPlaceContext(
                     player,
                     interactionHand,
                     itemStack,
                     new BlockHitResult(
                        new Vec3(0.5, 1.0, 0.5).add(blockPos.below().getX(), blockPos.below().getY(), blockPos.below().getZ()),
                        Direction.UP,
                        blockPos.below(),
                        true
                     )
                  )
               );
               if (!bl) {
                  if (player.getItemInHand(interactionHand).isEmpty()) {
                     StackUtil.clear(player, interactionHand);
                  }
               } else {
                  StackUtil.set(player, interactionHand, itemStack2);
               }
            }
         }
      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return super.canSurvive(blockState, levelReader, blockPos) && this.hasSupport(levelReader, blockPos, this);
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
      this.checkSupport(level, blockPos);
   }

   public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
      if (random.nextInt(8) == 0) {
         this.checkSupport(serverLevel, blockPos);
      }
   }

   private boolean isPillar(Level level, BlockPos blockPos) {
      while (level.getBlockState(blockPos).getBlock() == this) {
         blockPos = blockPos.below();
      }

      return level.getBlockState(blockPos).isFaceSturdy(level, blockPos, Direction.UP, SupportType.FULL);
   }

   private boolean hasSupport(BlockGetter blockGetter, BlockPos blockPos, ScaffoldBlock blockScaffold) {
      return this.calculateSupport(blockGetter, blockPos, blockScaffold).get(blockPos).strength >= 0;
   }

   private void checkSupport(Level level, BlockPos blockPos) {
      Block block = level.getBlockState(blockPos).getBlock();
      if (block instanceof ScaffoldBlock) {
         Map<BlockPos, ScaffoldBlock.Support> map = this.calculateSupport(level, blockPos, (ScaffoldBlock)block);
         boolean bl = false;

         for (ScaffoldBlock.Support support : map.values()) {
            if (support.strength < 0) {
               level.setBlock(support.pos, Blocks.AIR.defaultBlockState(), 2);
               Block.dropResources(support.block.defaultBlockState(), level, support.pos);
               bl = true;
            }
         }

         if (bl) {
            for (ScaffoldBlock.Support support : map.values()) {
               if (support.strength < 0) {
                  level.blockUpdated(support.pos, this);
               }
            }
         }
      }
   }

   private Map<BlockPos, ScaffoldBlock.Support> calculateSupport(BlockGetter blockGetter, BlockPos blockPos, ScaffoldBlock blockScaffold) {
      HashMap<BlockPos, ScaffoldBlock.Support> hashMap = new HashMap<>();
      ArrayDeque<ScaffoldBlock.Support> arrayDeque = new ArrayDeque<>();
      HashSet<BlockPos> hashSet = new HashSet<>();
      ScaffoldBlock.Support support = new ScaffoldBlock.Support(blockPos, blockScaffold, -1);
      hashMap.put(blockPos, support);
      arrayDeque.add(support);

      while ((support = arrayDeque.poll()) != null) {
         for (Direction direction : Util.ALL_DIRS) {
            BlockPos blockPos2 = support.pos.relative(direction);
            if (!hashMap.containsKey(blockPos2)) {
               BlockState object = blockGetter.getBlockState(blockPos2);
               Block block = object.getBlock();
               if (block instanceof ScaffoldBlock) {
                  ScaffoldBlock.Support support2 = new ScaffoldBlock.Support(blockPos2, (ScaffoldBlock)block, -1);
                  hashMap.put(blockPos2, support2);
                  arrayDeque.add(support2);
               } else if (object.isCollisionShapeFullBlock(blockGetter, blockPos2)) {
                  hashSet.add(blockPos2);
               }
            }
         }
      }

      for (BlockPos blockPos3 : hashSet) {
         BlockPos blockPos4 = blockPos3.above();
         int n = 0;

         while ((support = hashMap.get(blockPos4)) != null) {
            int n2;
            if (support.block.maxDistance >= n) {
               n2 = support.block.maxDistance;
 n = n2 - 1;
            } else {
               n2 = n--;
            }

            if (support.strength < n2) {
               support.strength = n2;

               for (Direction direction : Util.HORIZONTAL_DIRS) {
                  BlockPos blockPos2 = blockPos4.relative(direction);
                  ScaffoldBlock.Support support3 = hashMap.get(blockPos2);
                  if (support3 != null && support3.strength < n2) {
                     support3.strength = n2 - 1;
                     arrayDeque.add(support3);
                  }
               }
            }

            blockPos4 = blockPos4.above();
         }
      }

      while ((support = arrayDeque.poll()) != null) {
         for (Direction direction : supportedFacings) {
            BlockPos blockPos6 = support.pos.relative(direction);
            ScaffoldBlock.Support support4 = hashMap.get(blockPos6);
            if (support4 != null && support4.strength < support.strength) {
               support4.strength = support.strength - 1;
               if (support4.strength > 0) {
                  arrayDeque.add(support4);
               }
            }
         }
      }

      return hashMap;
   }

   private static class Support {
      final BlockPos pos;
      final ScaffoldBlock block;
      int strength;

      Support(BlockPos blockPos, ScaffoldBlock blockScaffold, int n) {
         this.pos = blockPos;
         this.block = blockScaffold;
         this.strength = n;
      }
   }
}
