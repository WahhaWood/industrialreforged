package com.industrialreforged.tech.api.item;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.tech.container.InventoryDynamic;
import com.industrialreforged.tech.core.forge.EventHandlerForge;
import com.industrialreforged.tech.core.util.StackUtil;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.EventHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public interface IMiningDrill {
   default boolean breakAOEBlocks(
      ItemStack stack, BlockPos pos, int energyUse, int breakRadius, int breakDepth, Player player, boolean aoeSafe, @Nullable Consumer<ItemStack> onMine
   ) {
      if (!(stack.getItem() instanceof IEnergyContainerItem containerItem)) {
         return false;
      } else {
         if (containerItem.getEnergyStored(stack) < energyUse && !player.getAbilities().instabuild) {
            return false;
         }

         BlockState blockState = player.level().getBlockState(pos);
         if (!stack.getItem().isCorrectToolForDrops(stack, blockState)) {
            return false;
         }

         InventoryDynamic inventoryDynamic = new InventoryDynamic();
         float refStrength = blockStrength(blockState, player, player.level(), pos);
         Pair<BlockPos, BlockPos> aoe = this.getMiningArea(pos, player, breakRadius, breakDepth);
         List<BlockPos> aoeBlocks = BlockPos.betweenClosedStream((BlockPos)aoe.getKey(), (BlockPos)aoe.getValue()).<BlockPos>map(BlockPos::new).toList();
         if (aoeSafe) {
            for (BlockPos block : aoeBlocks) {
               if (!player.level().isEmptyBlock(block) && player.level().getBlockEntity(block) != null) {
                  if (player.level().isClientSide) {
                     player.displayClientMessage(Component.translatable("text.industrialreforged.aoe_blocked"), false);
                  } else {
                     ((ServerPlayer)player).connection.send(new ClientboundBlockUpdatePacket(player.level(), block));
                  }

                  return true;
               }
            }
         }

         BlockPos pos1 = (BlockPos)aoe.getKey();
         BlockPos pos2 = (BlockPos)aoe.getValue();
         aoeBlocks.forEach(
            block -> this.breakAOEBlock(
               stack,
               player.level(),
               block,
               player,
               energyUse,
               refStrength,
               inventoryDynamic,
               player.level().random.nextInt(Math.max(5, breakRadius * breakDepth / 5)) == 0,
               onMine
            )
         );

         for (ItemEntity item : player.level()
            .getEntitiesOfClass(ItemEntity.class, new AABB(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX() + 1, pos2.getY() + 1, pos2.getZ() + 1))) {
            if (!player.level().isClientSide && item.isAlive()) {
               inventoryDynamic.addItem(item.getItem());
               item.discard();
            }
         }

         StackUtil.handleItemCollection(player, inventoryDynamic);
         return true;
      }
   }

   static float blockStrength(BlockState state, Player player, Level world, BlockPos pos) {
      float hardness = state.getDestroySpeed(world, pos);
      if (hardness < 0.0F) {
         return 0.0F;
      } else {
         return !EventHooks.doPlayerHarvestCheck(player, state, world, pos)
            ? player.getDigSpeed(state, pos) / hardness / 100.0F
            : player.getDigSpeed(state, pos) / hardness / 30.0F;
      }
   }

   default Pair<BlockPos, BlockPos> getMiningArea(BlockPos pos, Player player, int breakRadius, int breakDepth) {
      Vec3 startVec = player.position().add(0.0, player.getEyeHeight(), 0.0);
      Vec3 lookVec = player.getViewVector(2.0F);
      Vec3 endVec = startVec.add(lookVec.x * 5.0, lookVec.y * 5.0, lookVec.z * 5.0);
      ClipContext rayTraceContext = new ClipContext(startVec, endVec, Block.OUTLINE, Fluid.NONE, player);
      BlockHitResult traceResult = player.level().clip(rayTraceContext);
      if (traceResult.getType() == Type.MISS) {
         return Pair.of(pos, pos);
      }

      int sideHit = traceResult.getDirection().get3DDataValue();
      int xMax = breakRadius;
      int xMin = breakRadius;
      int yMax = breakRadius;
      int yMin = breakRadius;
      int zMax = breakRadius;
      int zMin = breakRadius;
      int yOffset = 0;
      switch (sideHit) {
         case 0:
            yMax = breakDepth;
            yMin = 0;
            zMax = breakRadius;
            break;
         case 1:
            yMin = breakDepth;
            yMax = 0;
            zMax = breakRadius;
            break;
         case 2:
            xMax = breakRadius;
            zMin = 0;
            zMax = breakDepth;
            yOffset = breakRadius - 1;
            break;
         case 3:
            xMax = breakRadius;
            zMax = 0;
            zMin = breakDepth;
            yOffset = breakRadius - 1;
            break;
         case 4:
            xMax = breakDepth;
            xMin = 0;
            zMax = breakRadius;
            yOffset = breakRadius - 1;
            break;
         case 5:
            xMin = breakDepth;
            xMax = 0;
            zMax = breakRadius;
            yOffset = breakRadius - 1;
      }

      if (breakRadius == 0) {
         yOffset = 0;
      }

      return Pair.of(pos.offset(-xMin, yOffset - yMin, -zMin), pos.offset(xMax, yOffset + yMax, zMax));
   }

   default void breakAOEBlock(
      ItemStack stack,
      Level world,
      BlockPos pos,
      Player player,
      int energyUse,
      float refStrength,
      InventoryDynamic inventory,
      boolean breakFX,
      @Nullable Consumer<ItemStack> onMine
   ) {
      if (stack.getItem() instanceof IEnergyContainerItem containerItem) {
         if (containerItem.getEnergyStored(stack) >= energyUse && !world.isEmptyBlock(pos)) {
            BlockState state = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            net.minecraft.world.level.block.Block block = state.getBlock();
            if (stack.getItem().isCorrectToolForDrops(stack, state)) {
               float strength = blockStrength(state, player, world, pos);
               if (EventHooks.doPlayerHarvestCheck(player, state, world, pos) && !(refStrength / strength > 10.0F)) {
                  if (player instanceof ServerPlayer serverPlayer) {
                     int xp = state.getExpDrop(world, pos, null, serverPlayer, stack);
                     if (xp == -1) {
                        serverPlayer.connection.send(new ClientboundBlockUpdatePacket(world, pos));
                        return;
                     }

                     if (player.getAbilities().instabuild) {
                        if (block.onDestroyedByPlayer(state, world, pos, player, false, fluidState)) {
                           block.destroy(world, pos, state);
                        }
                     } else {
                        if (onMine != null) {
                           onMine.accept(stack);
                        }

                        EventHandlerForge.breakAndCollectWithPlayer(world, pos, inventory, player, xp);
                        containerItem.extractEnergy(stack, energyUse, false);
                     }
                  } else {
                     if (block.onDestroyedByPlayer(state, world, pos, player, true, fluidState)) {
                        block.destroy(world, pos, state);
                     }

                     if (Minecraft.getInstance().hitResult instanceof BlockHitResult) {
                        Minecraft.getInstance()
                           .getConnection()
                           .send(
                              new ServerboundPlayerActionPacket(
                                 Action.STOP_DESTROY_BLOCK, pos, ((BlockHitResult)Minecraft.getInstance().hitResult).getDirection()
                              )
                           );
                     }
                  }
               }
            }
         }
      }
   }
}
