package com.industrialreforged.tech.item;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.item.api.IFluidContainerItem;
import com.industrialreforged.item.fluid.BasicFluidItem;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.crop.data.StorageData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class ItemCell extends BasicFluidItem {
   private static final int WATER_DRAIN = 10;

   public ItemCell(Properties properties) {
      super(properties, 1000);
   }

   public static ItemStack getCell(Fluid fluid) {
      return getCell(fluid, 1000);
   }

   public static ItemStack getCell(Fluid fluid, int amount) {
      return getItemStack((IFluidContainerItem)IRItems.CELL.get(), new FluidStack(fluid, amount));
   }

   @VisibleForDebug
   public static Ingredient getCellIngredient(Fluid fluid) {
      return DataComponentIngredient.of(false, GEDataComponents.FLUID, SimpleFluidContent.copyOf(new FluidStack(fluid, 1000)), new ItemLike[]{IRItems.CELL});
   }

   @VisibleForDebug
   public static Ingredient getEmptyCellIngredient() {
      return DataComponentIngredient.of(false, GEDataComponents.FLUID, SimpleFluidContent.EMPTY, new ItemLike[]{IRItems.CELL});
   }

   public InteractionResult useOn(UseOnContext context) {
      Player player = context.getPlayer();
      Level level = context.getLevel();
      InteractionHand hand = context.getHand();
      BlockPos pos = context.getClickedPos();
      Direction direction = context.getClickedFace();
      Inventory inventory = player.getInventory();
      ItemStack container = inventory.getSelected();
      FluidStack fluidStack = FluidUtil.getFluidContained(container).orElse(FluidStack.EMPTY);
      if (!fluidStack.isEmpty()) {
         FluidActionResult actionResult = FluidUtil.tryPlaceFluid(player, level, hand, pos.relative(direction), container, fluidStack);
         if (actionResult.success) {
            if (!level.isClientSide) {
               if (container.getCount() == 1) {
                  player.setItemInHand(hand, actionResult.result);
               } else {
                  container.shrink(1);
                  player.setItemInHand(hand, container.copy());
                  ItemHandlerHelper.giveItemToPlayer(player, actionResult.result);
               }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         }

         if (fluidStack.getFluid() == Fluids.WATER && fluidStack.getAmount() >= 10 && level.getBlockEntity(pos) instanceof CropBlockEntity crop) {
            StorageData data = crop.getStorage();
            if (data.getWater() < 10) {
               FluidHandlerItemStack handler = (FluidHandlerItemStack)FluidUtil.getFluidHandler(container).orElse(null);
               if (handler != null && handler.drain(10, FluidAction.SIMULATE).getAmount() == 10) {
                  if (!level.isClientSide) {
                     handler.drain(10, FluidAction.EXECUTE);
                     data.setWater((short)10);
                  }

                  return InteractionResult.sidedSuccess(level.isClientSide);
               }
            }
         }
      } else {
         FluidActionResult actionResult = FluidUtil.tryPickUpFluid(container, player, level, pos.relative(direction), direction);
         if (actionResult.success) {
            if (!level.isClientSide) {
               if (container.getCount() == 1) {
                  player.setItemInHand(hand, actionResult.result);
               } else {
                  container.shrink(1);
                  player.setItemInHand(hand, container.copy());
                  ItemHandlerHelper.giveItemToPlayer(player, actionResult.result);
               }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   public Component getName(ItemStack stack) {
      FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
      return fluidStack.isEmpty()
         ? Component.translatable("item.industrialreforged.empty_cell")
         : Component.translatable("item.industrialreforged.fluid_cell", new Object[]{fluidStack.getHoverName()});
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
      FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
      if (!fluidStack.isEmpty()) {
         components.add(Component.translatable("text.industrialreforged.fluid.storage", new Object[]{fluidStack.getAmount(), 1000}));
      }
   }

   public boolean isBarVisible(ItemStack stack) {
      FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
      return !fluidStack.isEmpty() && fluidStack.getAmount() < 1000;
   }

   public int getBarWidth(ItemStack stack) {
      FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
      return Math.round(13.0F * fluidStack.getAmount() / 1000.0F);
   }

   public int getBarColor(ItemStack stack) {
      FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
      float f = Math.max(0.0F, (1000.0F - fluidStack.getAmount()) / 1000.0F);
      return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
   }
}
