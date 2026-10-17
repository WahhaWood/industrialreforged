package com.industrialreforged.tech.item.upgrade;

import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ItemUpgradeMachine extends Item {
   private final Map<Class<? extends AbstractElectricMachineBlockEntity>, DeferredBlock<Block>> map;

   public ItemUpgradeMachine(Map<Class<? extends AbstractElectricMachineBlockEntity>, DeferredBlock<Block>> map, Properties properties) {
      super(properties);
      this.map = map;
   }

   public InteractionResult useOn(UseOnContext context) {
      if (context.getHand() != InteractionHand.MAIN_HAND) {
         return InteractionResult.PASS;
      }

      Player player = context.getPlayer();
      if (player == null) {
         return InteractionResult.PASS;
      }

      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();
      if (level.getBlockEntity(blockPos) instanceof AbstractStandardMachineBlockEntity<?> e) {
         DeferredBlock<?> registryObject = this.map.get(e.getClass());
         if (registryObject != null) {
            if (!level.isClientSide) {
               BlockState state = e.getBlockState();
               MetalFormerRecipe.Mode formerMode = e instanceof MetalFormer1LevelBlockEntity metalFormer ? metalFormer.getMode() : null;
               NonNullList<ItemStack> input = NonNullList.copyOf(e.inputSlot.removeAllItems());
               NonNullList<ItemStack> output = NonNullList.copyOf(e.outputSlot.removeAllItems());
               NonNullList<ItemStack> upgrade = NonNullList.copyOf(e.upgradeSlot.removeAllItems());
               Block block = (Block)registryObject.get();
               level.setBlockAndUpdate(
                  blockPos,
                  (BlockState)block.defaultBlockState()
                     .setValue(BlockStateProperties.HORIZONTAL_FACING, (Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING))
               );
               player.getMainHandItem().shrink(1);
               if (level.getBlockEntity(blockPos) instanceof AbstractStandardMachineBlockEntity<?> ee) {
                  if (formerMode != null && ee instanceof MetalFormer1LevelBlockEntity metalFormer) {
                     metalFormer.setMode(formerMode);
                  }

                  for (int i = 0; i < input.size(); i++) {
                     ee.inputSlot.setItem(i, (ItemStack)input.get(i));
                  }

                  for (int i = 0; i < output.size(); i++) {
                     ee.outputSlot.setItem(i, (ItemStack)output.get(i));
                  }

                  for (int i = 0; i < upgrade.size(); i++) {
                     ee.upgradeSlot.setItem(i, (ItemStack)upgrade.get(i));
                  }

                  ee.setChanged();
               }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }
}
