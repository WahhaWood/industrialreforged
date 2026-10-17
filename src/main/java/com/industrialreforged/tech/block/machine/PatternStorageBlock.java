package com.industrialreforged.tech.block.machine;

import com.industrialreforged.tech.api.recipe.IPatternStorage;
import com.industrialreforged.tech.block.base.AbstractMachineBlock;
import com.industrialreforged.tech.blockentity.machine.PatternStorageBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

public class PatternStorageBlock extends AbstractMachineBlock<PatternStorageBlockEntity> {
   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new PatternStorageBlockEntity(pos, state);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      NonNullList<IRDataComponents.ItemStackRecord> patterns = (NonNullList<IRDataComponents.ItemStackRecord>)stack.get(IRDataComponents.PATTERNS);
      if (patterns != null && level.getBlockEntity(pos) instanceof IPatternStorage storage) {
         patterns.stream().map(IRDataComponents.ItemStackRecord::stack).forEach(storage::addPattern);
      }
   }

   @Override
   public ItemStack getNonWrenchDrop() {
      return IRBlocks.ADVANCED_MACHINE.toStack();
   }

   @Override
   public ItemStack getWrenchDrops(BlockState blockState, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      ItemStack stack = super.getWrenchDrops(blockState, builder);
      if (stack.has(IRDataComponents.PATTERNS) && builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IPatternStorage storage) {
         stack.set(IRDataComponents.PATTERNS, NonNullList.copyOf(storage.getPatterns().stream().map(IRDataComponents.ItemStackRecord::new).toList()));
      }

      return stack;
   }
}
