package com.industrialreforged.tech.item.tool;

import com.industrialreforged.item.fluid.BasicFluidItem;
import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.block.FoamBlock;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.StackUtil;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class ItemSprayer extends BasicFluidItem {
   public ItemSprayer(Properties properties) {
      super(properties.stacksTo(1).component(IRDataComponents.MODE, (byte)0), 8000, new Fluid[]{IRFluids.CONSTRUCTION_FOAM.getFluidStill()});
   }

   public ItemStack getItemStack(Fluid fluid) {
      ItemStack ret = new ItemStack(this);
      if (fluid == null) {
         return ret;
      }

      Optional<IFluidHandlerItem> handler = FluidUtil.getFluidHandler(ret);
      if (!handler.isPresent()) {
         return ItemStack.EMPTY;
      }

      IFluidHandlerItem handlerItem = handler.orElse(null);
      return handlerItem.fill(new FluidStack(fluid, Integer.MAX_VALUE), FluidAction.EXECUTE) > 0 ? handlerItem.getContainer() : ItemStack.EMPTY;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      if (IR.sideProxy.isSimulating() && IR.keyboard.isModeSwitchKeyDown(player)) {
         ItemStack stack = StackUtil.get(player, hand);
         byte mode = (byte)(stack.getOrDefault(IRDataComponents.MODE, (byte)0) == 0 ? 1 : 0);
         stack.set(IRDataComponents.MODE, mode);
         MsgUtils.send(player, "industrialreforged.tooltip.mode", new Object[]{Component.translatable(mode == 0 ? "industrialreforged.tooltip.mode.normal" : "industrialreforged.tooltip.mode.single")});
      }

      return super.use(level, player, hand);
   }

   public InteractionResult useOn(UseOnContext context) {
      Player player = context.getPlayer();
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      InteractionHand hand = context.getHand();
      Direction direction = context.getClickedFace();
      if (IR.keyboard.isModeSwitchKeyDown(player)) {
         return InteractionResult.PASS;
      }

      if (!IR.sideProxy.isSimulating()) {
         return InteractionResult.SUCCESS;
      }

      int maxFoamBlocks = 0;
      ItemStack stack = StackUtil.get(player, hand);
      Optional<FluidStack> fluid = FluidUtil.getFluidContained(stack);
      if (fluid.isPresent() && isConstructionFoam(fluid.get())) {
         maxFoamBlocks += fluid.get().getAmount() / this.getFluidPerFoam();
      }

      ItemStack pack = (ItemStack)player.getInventory().armor.get(2);
      if (!pack.isEmpty() && pack.getItem() == IRItems.CF_PACK.get()) {
         fluid = FluidUtil.getFluidContained(pack);
         if (fluid.isPresent() && isConstructionFoam(fluid.get())) {
            maxFoamBlocks += fluid.get().getAmount() / this.getFluidPerFoam();
         } else {
            pack = null;
         }
      } else {
         pack = null;
      }

      if (maxFoamBlocks == 0) {
         return InteractionResult.FAIL;
      }

      maxFoamBlocks = Math.min(maxFoamBlocks, this.getMaxFoamBlocks(stack));
      ItemSprayer.Target target;
      if (canPlaceFoam(level, pos, ItemSprayer.Target.Scaffold)) {
         target = ItemSprayer.Target.Scaffold;
      } else {
         pos = pos.relative(direction);
         target = ItemSprayer.Target.Any;
      }

      Vec3 viewVec = player.getLookAngle();
      Direction playerViewFacing = Direction.getNearest((float)viewVec.x, (float)viewVec.y, (float)viewVec.z);
      int amount = this.sprayFoam(level, player, pos, playerViewFacing.getOpposite(), target, maxFoamBlocks);
      amount *= this.getFluidPerFoam();
      if (amount > 0) {
         if (pack != null) {
            Optional<IFluidHandlerItem> packHandler = FluidUtil.getFluidHandler(pack);
            if (packHandler.isPresent()) {
               IFluidHandlerItem handlerItem = packHandler.orElse(null);
               FluidStack fluidStack = handlerItem.drain(constructionFoam(amount), FluidAction.EXECUTE);
               amount -= fluidStack.getAmount();
               player.getInventory().armor.set(2, handlerItem.getContainer());
            }
         }

         if (amount > 0) {
            Optional<IFluidHandlerItem> handler = FluidUtil.getFluidHandler(stack);
            if (handler.isPresent()) {
               IFluidHandlerItem handlerItem = handler.orElse(null);
               handlerItem.drain(constructionFoam(amount), FluidAction.EXECUTE);
               StackUtil.set(player, hand, handlerItem.getContainer());
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public int sprayFoam(Level world, Player player, BlockPos pos, Direction excludedDir, ItemSprayer.Target target, int maxFoamBlocks) {
      if (!canPlaceFoam(world, pos, target)) {
         return 0;
      }

      Queue<BlockPos> toCheck = new ArrayDeque<>();
      Set<BlockPos> positions = new HashSet<>();
      toCheck.add(pos);

      BlockPos cPos;
      while ((cPos = toCheck.poll()) != null && positions.size() < maxFoamBlocks) {
         if (canPlaceFoam(world, cPos, target) && positions.add(cPos)) {
            for (Direction dir : Direction.values()) {
               if (dir != excludedDir) {
                  toCheck.add(cPos.relative(dir));
               }
            }
         }
      }

      toCheck.clear();
      int failedPlacements = 0;

      for (BlockPos targetPos : positions) {
         BlockState state = world.getBlockState(targetPos);
         Block targetBlock = state.getBlock();
         if (targetBlock == IRBlocks.WOODEN_SCAFFOLD.get()) {
            StackUtil.dropAsEntity(world, targetPos, new ItemStack((ItemLike)IRBlocks.WOODEN_SCAFFOLD.get()));
            world.setBlock(
               targetPos, (BlockState)((Block)IRBlocks.FOAM.get()).defaultBlockState().setValue(FoamBlock.typeProperty, FoamBlock.FoamType.normal), 3
            );
         } else if (targetBlock == IRBlocks.REINFORCED_WOODEN_SCAFFOLD.get()) {
            StackUtil.dropAsEntity(world, targetPos, new ItemStack(Items.STICK, 2));
            StackUtil.dropAsEntity(world, targetPos, new ItemStack((ItemLike)IRBlocks.WOODEN_SCAFFOLD.get()));
            world.setBlock(
               targetPos, (BlockState)((Block)IRBlocks.FOAM.get()).defaultBlockState().setValue(FoamBlock.typeProperty, FoamBlock.FoamType.normal), 3
            );
         } else if (targetBlock == IRBlocks.IRON_SCAFFOLD.get()) {
            world.setBlock(
               targetPos, (BlockState)((Block)IRBlocks.FOAM.get()).defaultBlockState().setValue(FoamBlock.typeProperty, FoamBlock.FoamType.reinforced), 3
            );
         } else if (targetBlock == IRBlocks.REINFORCED_IRON_SCAFFOLD.get()) {
            StackUtil.dropAsEntity(world, targetPos, new ItemStack((ItemLike)IRBlocks.IRON_FENCE.get()));
            world.setBlock(
               targetPos, (BlockState)((Block)IRBlocks.FOAM.get()).defaultBlockState().setValue(FoamBlock.typeProperty, FoamBlock.FoamType.reinforced), 3
            );
         } else if (!world.setBlock(
            targetPos, (BlockState)((Block)IRBlocks.FOAM.get()).defaultBlockState().setValue(FoamBlock.typeProperty, FoamBlock.FoamType.normal), 3
         )) {
            failedPlacements++;
         }
      }

      return positions.size() - failedPlacements;
   }

   protected int getMaxFoamBlocks(ItemStack stack) {
      return stack.getOrDefault(IRDataComponents.MODE, Byte.valueOf((byte)0)) == 0 ? 10 : 1;
   }

   protected int getFluidPerFoam() {
      return 100;
   }

   private static boolean isConstructionFoam(FluidStack stack) {
      return !stack.isEmpty() && stack.getFluid() == IRFluids.CONSTRUCTION_FOAM.getFluidStill();
   }

   private static FluidStack constructionFoam(int amount) {
      return new FluidStack(IRFluids.CONSTRUCTION_FOAM.getFluidStill(), amount);
   }

   private static boolean canPlaceFoam(Level world, BlockPos pos, ItemSprayer.Target target) {
      switch (target) {
         case Any:
            return world.isEmptyBlock(pos);
         case Scaffold:
            Block block = world.getBlockState(pos).getBlock();
            return block == IRBlocks.WOODEN_SCAFFOLD.get()
               || block == IRBlocks.REINFORCED_WOODEN_SCAFFOLD.get()
               || block == IRBlocks.REINFORCED_IRON_SCAFFOLD.get()
               || block == IRBlocks.IRON_SCAFFOLD.get();
         default:
            return false;
      }
   }

   private enum Target {
      Any,
      Scaffold;
   }
}
