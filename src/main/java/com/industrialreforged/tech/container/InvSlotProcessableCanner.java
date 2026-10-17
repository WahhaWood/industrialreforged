package com.industrialreforged.tech.container;

import com.industrialreforged.tech.blockentity.machine.CannerBlockEntity;
import com.industrialreforged.tech.core.recipe.CannerRecipe;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.LiquidUtil;
import com.industrialreforged.tech.item.ItemCell;
import java.util.function.Predicate;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.Nullable;

public class InvSlotProcessableCanner extends InvSlotProcessable<CannerBlockEntity, CannerRecipe> {
   private static final CannerRecipe DEFAULT = new CannerRecipe();

   public InvSlotProcessableCanner(CannerBlockEntity entity, String name, int size) {
      super(entity, name, size, null);
   }

   @Override
   public boolean canPlaceItem(int index, ItemStack stack) {
      return switch (this.entity.getMode()) {
         case BottleSolid -> {
            ItemStack input = this.entity.canInputSlot.getItem();
            Predicate<SolidCannerRecipe> predicate = input.isEmpty() ? r -> r.center.test(stack) : r -> r.input.test(input) && r.center.test(stack);
            yield SolidCannerRecipe.gets().stream().anyMatch(predicate);
         }
         case EmptyLiquid, BottleLiquid -> false;
         case EnrichLiquid -> {
            Item item = stack.getItem();
            yield item == IRItems.CF_POWDER.get() || stack.is(IRItemTags.DUSTS_LAPIS) || item == IRItems.BIO_CHAFF.get() || item == Items.STICK;
         }
      };
   }

   @Nullable
   public CannerRecipe process(int slot) {
      CannerBlockEntity canner = this.entity;
      CannerBlockEntity.Mode mode = canner.getMode();
      if (mode != CannerBlockEntity.Mode.EmptyLiquid && mode != CannerBlockEntity.Mode.BottleLiquid
         ? !canner.inputSlot.isEmpty()
         : !canner.canInputSlot.isEmpty()) {
         return switch (mode) {
            case BottleSolid -> {
               ItemStack input = canner.canInputSlot.getItem();
               ItemStack center = canner.inputSlot.getItem();
               SolidCannerRecipe recipe = SolidCannerRecipe.gets().stream().filter(r -> r.matches(input, center)).findFirst().orElse(null);
               yield recipe != null && canner.outputSlot.canAdd(recipe.result) ? DEFAULT : null;
            }
            case EmptyLiquid -> {
               ItemStack container = canner.canInputSlot.getItem();
               if (container.getCount() == 1) {
                  IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(container).orElse(null);
                  yield handlerItem == null ? null : (LiquidUtil.tryFluidTransferForce(canner.outputTank, handlerItem, 8000, false).isEmpty() ? null : DEFAULT);
               } else {
                  ItemStack copy = container.copy();
                  copy.setCount(1);
                  IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(copy).orElse(null);
                  if (handlerItem == null) {
                     yield null;
                  } else {
                     FluidStack containerStack = handlerItem.getFluidInTank(0);
                     if (containerStack.isEmpty()) {
                        yield null;
                     } else {
                        FluidStack fluidStack = LiquidUtil.tryFluidTransferForce(canner.outputTank, handlerItem, 8000, false);
                        if (!fluidStack.isEmpty() && fluidStack.getAmount() == containerStack.getAmount()) {
                           handlerItem.drain(containerStack, FluidAction.EXECUTE);
                           yield canner.outputSlot.canAdd(copy) ? DEFAULT : null;
                        } else {
                           yield null;
                        }
                     }
                  }
               }
            }
            case BottleLiquid -> {
               ItemStack container = canner.canInputSlot.getItem();
               if (container.getCount() == 1) {
                  IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(container).orElse(null);
                  if (handlerItem == null) {
                     yield null;
                  } else {
                     FluidStack inputFluid = canner.inputTank.getFluid();
                     yield !inputFluid.isEmpty() && handlerItem.isFluidValid(0, inputFluid)
                        ? (LiquidUtil.tryFluidTransferForce(handlerItem, canner.inputTank, handlerItem.getTankCapacity(0), false).isEmpty() ? null : DEFAULT)
                        : null;
                  }
               } else {
                  ItemStack copy = container.copy();
                  copy.setCount(1);
                  IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(copy).orElse(null);
                  if (handlerItem == null) {
                     yield null;
                  } else {
                     FluidStack inputFluid = canner.inputTank.getFluid();
                     if (!inputFluid.isEmpty() && handlerItem.isFluidValid(0, inputFluid)) {
                        FluidStack containerStack = handlerItem.getFluidInTank(0);
                        if (!containerStack.isEmpty()) {
                           yield null;
                        } else {
                           FluidStack fluidStack = LiquidUtil.tryFluidTransferForce(handlerItem, canner.inputTank, handlerItem.getTankCapacity(0), false);
                           if (!fluidStack.isEmpty() && fluidStack.getAmount() == handlerItem.getTankCapacity(0)) {
                              handlerItem.fill(fluidStack, FluidAction.EXECUTE);
                              yield canner.outputSlot.canAdd(copy) ? DEFAULT : null;
                           } else {
                              yield null;
                           }
                        }
                     } else {
                        yield null;
                     }
                  }
               }
            }
            case EnrichLiquid -> {
               FluidStack fluidStack = canner.inputTank.getFluid();
               if (!fluidStack.isEmpty()) {
                  ItemStack centerStack = this.getItem();
                  if (centerStack.isEmpty()) {
                     yield null;
                  }

                  Item item = centerStack.getItem();
                  if (fluidStack.getAmount() >= 1000) {
                     if (fluidStack.getFluid() == Fluids.WATER) {
                        if (item == IRItems.CF_POWDER.get()) {
                           yield this.hasEnrichLiquid(IRFluids.CONSTRUCTION_FOAM.getFluidStill()) ? DEFAULT : null;
                        }

                        if (centerStack.getCount() >= 8 && centerStack.is(IRItemTags.DUSTS_LAPIS)) {
                           yield this.hasEnrichLiquid(IRFluids.COOLANT.getFluidStill()) ? DEFAULT : null;
                        }

                        if (item == IRItems.BIO_CHAFF.get()) {
                           yield this.hasEnrichLiquid(IRFluids.BIOMASS.getFluidStill()) ? DEFAULT : null;
                        }

                        if (fluidStack.getAmount() >= 6000 && item == Items.STICK) {
                           yield this.hasEnrichLiquid(IRFluids.HOT_WATER.getFluidStill()) ? DEFAULT : null;
                        }
                     } else if (fluidStack.getFluid() == IRFluids.DISTILLED_WATER.getFluidStill() && centerStack.is(IRItemTags.DUSTS_LAPIS)) {
                        yield this.hasEnrichLiquid(IRFluids.COOLANT.getFluidStill()) ? DEFAULT : null;
                     }
                  }
               }

               yield null;
            }
         };
      } else {
         return null;
      }
   }

   @Override
   public boolean assemble(int slot) {
      CannerBlockEntity canner = this.entity;

      return switch (canner.getMode()) {
         case BottleSolid -> {
            ItemStack input = canner.canInputSlot.getItem();
            ItemStack center = canner.inputSlot.getItem();
            SolidCannerRecipe recipe = SolidCannerRecipe.gets().stream().filter(r -> r.matches(input, center)).findFirst().orElse(null);
            if (recipe == null) {
               yield false;
            } else {
               recipe.assemble(canner.canInputSlot, canner.inputSlot, canner.outputSlot);
               yield true;
            }
         }
         case EmptyLiquid -> {
            ItemStack container = canner.canInputSlot.getItem();
            if (container.getCount() == 1) {
               IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(container).orElse(null);
               if (handlerItem == null) {
                  yield false;
               }

               LiquidUtil.tryFluidTransferForce(canner.outputTank, handlerItem, 8000, true);
               if (handlerItem instanceof FluidBucketWrapper) {
                  canner.canInputSlot.setItem(0, new ItemStack(Items.BUCKET));
               }

               FluidStack fluidStack = handlerItem.getFluidInTank(0);
               if (fluidStack.isEmpty()) {
                  container = handlerItem.getContainer();
                  if (canner.outputSlot.canAdd(container)) {
                     canner.outputSlot.add(container.copy());
                     canner.canInputSlot.shrink();
                  }
               }
            } else {
               ItemStack copy = container.copy();
               copy.setCount(1);
               IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(copy).orElse(null);
               if (handlerItem == null) {
                  yield false;
               }

               LiquidUtil.tryFluidTransferForce(canner.outputTank, handlerItem, 8000, true);
               canner.canInputSlot.shrink();
               canner.outputSlot.add(copy);
            }

            yield true;
         }
         case BottleLiquid -> {
            ItemStack container = canner.canInputSlot.getItem();
            if (container.getCount() == 1) {
               IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(container).orElse(null);
               if (handlerItem == null) {
                  yield false;
               }

               LiquidUtil.tryFluidTransferForce(handlerItem, canner.inputTank, handlerItem.getTankCapacity(0), true);
               FluidStack fluidStack = handlerItem.getFluidInTank(0);
               if (fluidStack.getAmount() == handlerItem.getTankCapacity(0)) {
                  container = handlerItem.getContainer();
                  if (canner.outputSlot.canAdd(container)) {
                     canner.outputSlot.add(container.copy());
                     canner.canInputSlot.shrink();
                  }
               }
            } else {
               ItemStack copy = container.copy();
               copy.setCount(1);
               IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(copy).orElse(null);
               if (handlerItem == null) {
                  yield false;
               }

               LiquidUtil.tryFluidTransferForce(handlerItem, canner.inputTank, handlerItem.getTankCapacity(0), true);
               canner.canInputSlot.shrink();
               canner.outputSlot.add(copy);
            }

            yield true;
         }
         case EnrichLiquid -> {
            FluidStack fluidStack = canner.inputTank.getFluid();
            if (!fluidStack.isEmpty() && fluidStack.getAmount() >= 1000) {
               ItemStack centerStack = this.getItem();
               if (fluidStack.getFluid() == Fluids.WATER) {
                  Item item = centerStack.getItem();
                  if (item == IRItems.CF_POWDER.get()) {
                     this.enrichLiquid(1, 1000, IRFluids.CONSTRUCTION_FOAM.getFluidStill());
                     yield true;
                  }

                  if (centerStack.getCount() >= 8 && centerStack.is(IRItemTags.DUSTS_LAPIS)) {
                     this.enrichLiquid(8, 1000, IRFluids.COOLANT.getFluidStill());
                     yield true;
                  }

                  if (item == IRItems.BIO_CHAFF.get()) {
                     this.enrichLiquid(1, 1000, IRFluids.BIOMASS.getFluidStill());
                     yield true;
                  }

                  if (fluidStack.getAmount() >= 6000 && item == Items.STICK) {
                     this.enrichLiquid(1, 6000, IRFluids.HOT_WATER.getFluidStill());
                     yield true;
                  }
               } else if (fluidStack.getFluid() == IRFluids.DISTILLED_WATER.getFluidStill() && centerStack.is(IRItemTags.DUSTS_LAPIS)) {
                  this.enrichLiquid(1, 1000, IRFluids.COOLANT.getFluidStill());
                  yield true;
               }
            }

            yield false;
         }
      };
   }

   private boolean hasEnrichLiquid(Fluid output) {
      CannerBlockEntity canner = this.entity;
      ItemStack input = canner.canInputSlot.getItem();
      if (input.getItem() instanceof ItemCell && FluidUtil.getFluidContained(input).orElse(FluidStack.EMPTY).isEmpty()) {
         ItemStack cell = ItemCell.getCell(output);
         return canner.outputSlot.canAdd(cell);
      } else {
         return canner.outputTank.fillForce(new FluidStack(output, 1000), FluidAction.SIMULATE) == 1000;
      }
   }

   private void enrichLiquid(int count, int maxDrain, Fluid output) {
      CannerBlockEntity canner = this.entity;
      ItemStack input = canner.canInputSlot.getItem();
      if (input.getItem() instanceof ItemCell && FluidUtil.getFluidContained(input).orElse(FluidStack.EMPTY).isEmpty()) {
         ItemStack cell = ItemCell.getCell(output);
         if (canner.outputSlot.canAdd(cell)) {
            this.shrink(0, count);
            canner.canInputSlot.shrink();
            canner.inputTank.drainForce(maxDrain, FluidAction.EXECUTE);
            canner.outputSlot.add(cell);
         }
      } else {
         this.shrink(0, count);
         canner.inputTank.drainForce(maxDrain, FluidAction.EXECUTE);
         canner.outputTank.fillForce(new FluidStack(output, 1000), FluidAction.EXECUTE);
      }
   }
}
