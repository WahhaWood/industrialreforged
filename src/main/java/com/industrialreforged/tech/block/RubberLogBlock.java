package com.industrialreforged.tech.block;

import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RubberLogBlock extends RotatedPillarBlock {
   public static final EnumProperty<RubberLogBlock.RubberWoodState> STATE = EnumProperty.create("state", RubberLogBlock.RubberWoodState.class);

   public RubberLogBlock(Properties properties) {
      super(properties.randomTicks());
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(STATE, RubberLogBlock.RubberWoodState.PLAIN));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{STATE});
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      // Отработанное бревно (после сбора латекса) ставится «сухим» и латекс больше не даёт
      RubberLogBlock.RubberWoodState woodState = blockPlaceContext.getItemInHand().is(IRItems.SPENT_RUBBER_LOG.get())
         ? RubberLogBlock.RubberWoodState.SPENT
         : RubberLogBlock.RubberWoodState.PLAIN;
      return (BlockState)((BlockState)this.defaultBlockState().setValue(AXIS, blockPlaceContext.getClickedFace().getAxis()))
         .setValue(STATE, woodState);
   }

   /** Игрок всегда получает «отработанное» бревно: латекс нельзя добыть перестановкой брёвен. */
   public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
      List<ItemStack> drops = super.getDrops(state, builder);
      RubberLogBlock.RubberWoodState woodState = (RubberLogBlock.RubberWoodState)state.getValue(STATE);
      boolean byPlayer = builder.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof Player;
      if (!woodState.isSpent() && !byPlayer) {
         return drops;
      }
      List<ItemStack> replaced = new ArrayList<>(drops.size());
      for (ItemStack drop : drops) {
         if (drop.is(IRItems.RUBBER_LOG.get())) {
            replaced.add(new ItemStack((ItemLike)IRItems.SPENT_RUBBER_LOG.get(), drop.getCount()));
         } else {
            replaced.add(drop);
         }
      }
      return replaced;
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
      if (randomSource.nextInt(7) == 0) {
         RubberLogBlock.RubberWoodState rubberWoodState = (RubberLogBlock.RubberWoodState)state.getValue(STATE);
         if (rubberWoodState.canRegenerate()) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(STATE, rubberWoodState.getWet()));
         }
      }
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
      super.playerDestroy(level, player, blockPos, state, blockEntity, stack);
      RubberLogBlock.RubberWoodState rubberWoodState = (RubberLogBlock.RubberWoodState)state.getValue(STATE);
      if (rubberWoodState.isSpent()) {
         return;
      }
      if (rubberWoodState.isPlain()) {
         // Untouched wood of a freshly grown hevea tree always gives one latex
         // when it is chopped for the first time.
         this.dropResin(level, blockPos, 1);
      } else if (rubberWoodState.wet) {
         this.dropResin(level, blockPos, level.random.nextInt(1) + 1);
      } else {
         Holder<Enchantment> holder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);

         float chance = switch (stack.getEnchantmentLevel(holder)) {
            case 1 -> 0.18F;
            case 2 -> 0.21F;
            case 3 -> 0.25F;
            default -> 0.166667F;
         };
         if (level.random.nextFloat() < chance) {
            this.dropResin(level, blockPos, level.random.nextInt(Math.max(1, (int)(chance * 15.0F))) + 1);
         }
      }
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      Axis axis = (Axis)blockState.getValue(AXIS);
      return axis != Axis.X && axis != Axis.Y && axis != Axis.Z ? PushReaction.BLOCK : PushReaction.NORMAL;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      ItemStack itemStack = player.getMainHandItem();
      if (!(itemStack.getItem() instanceof AxeItem)) {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }

      strip(
         state,
         level,
         pos,
         player,
         itemStack,
         (BlockState)((RotatedPillarBlock)IRBlocks.STRIPPED_RUBBER_LOG.get())
            .defaultBlockState()
            .setValue(RotatedPillarBlock.AXIS, (Axis)state.getValue(RotatedPillarBlock.AXIS))
      );
      RubberLogBlock.RubberWoodState rubberWoodState = (RubberLogBlock.RubberWoodState)state.getValue(STATE);
      if (rubberWoodState == RubberLogBlock.RubberWoodState.WET_NORTH
         || rubberWoodState == RubberLogBlock.RubberWoodState.WET_SOUTH
         || rubberWoodState == RubberLogBlock.RubberWoodState.WET_WEST
         || rubberWoodState == RubberLogBlock.RubberWoodState.WET_EAST) {
         this.dropResin(level, pos, level.random.nextInt(2) + 1);
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   private void dropResin(Level level, BlockPos blockPos, int n) {
      for (int i = 0; i < n; i++) {
         ItemEntity itemEntity = new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack((ItemLike)IRItems.RESIN.get()));
         itemEntity.setDefaultPickUpDelay();
         level.addFreshEntity(itemEntity);
      }
   }

   public static void strip(BlockState blockState, Level level, BlockPos blockPos, Player player, ItemStack itemStack, BlockState blockState2) {
      level.playSound(player, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
      if (player instanceof ServerPlayer) {
         CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockPos, itemStack);
      }

      level.setBlock(blockPos, blockState2, 11);
      itemStack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
   }

   public enum RubberWoodState implements StringRepresentable {
      PLAIN(null, false),
      DRY_NORTH(Direction.NORTH, false),
      DRY_SOUTH(Direction.SOUTH, false),
      DRY_WEST(Direction.WEST, false),
      DRY_EAST(Direction.EAST, false),
      WET_NORTH(Direction.NORTH, true),
      WET_SOUTH(Direction.SOUTH, true),
      WET_WEST(Direction.WEST, true),
      WET_EAST(Direction.EAST, true),
      /** Отработанная древесина: латекс уже собран, смола больше не даётся и не восстанавливается. */
      SPENT(null, false);

      @Nullable
      public final Direction facing;
      public final boolean wet;
      private static final RubberLogBlock.RubberWoodState[] values = values();

      RubberWoodState(@Nullable Direction direction, boolean bl) {
         this.facing = direction;
         this.wet = bl;
      }

      public String getSerializedName() {
         return this.name().toLowerCase();
      }

      public boolean isPlain() {
         return this == PLAIN;
      }

      public boolean isSpent() {
         return this == SPENT;
      }

      public boolean canRegenerate() {
         return this != PLAIN && this != SPENT && !this.wet;
      }

      @Nullable
      public RubberLogBlock.RubberWoodState getWet() {
         if (this.isPlain() || this.isSpent()) {
            return null;
         } else {
            return this.wet ? this : values[this.ordinal() + 4];
         }
      }

      public RubberLogBlock.RubberWoodState getDry() {
         return !this.isPlain() && this.wet ? values[this.ordinal() - 4] : this;
      }

      public static RubberLogBlock.RubberWoodState getWet(Direction direction) {
         switch (direction) {
            case NORTH:
               return WET_NORTH;
            case SOUTH:
               return WET_SOUTH;
            case WEST:
               return WET_WEST;
            case EAST:
               return WET_EAST;
            default:
               throw new IllegalArgumentException("incompatible facing: " + direction);
         }
      }
   }
}
