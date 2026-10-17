package com.industrialreforged.tech.blockentity;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.block.BrewBarrelBlock;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.item.ItemBooze;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class BrewBarrelBlockEntity extends AbstractBlockEntity implements IServerTick {
   public final BrewBarrelBlock.BrewBoozeData data = new BrewBarrelBlock.BrewBoozeData(0, 0, 0, false, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0);

   public BrewBarrelBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.BREW_BARREL.get(), blockPos, blockState);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.data.load(tag);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.data.save(tag);
   }

   /** Ром (тип 2) настаивается со временем. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (this.data.type == 2 && !this.data.opened && this.data.booze > 0) {
         int needed = this.timeNedForRum(this.data.booze);
         if (this.data.age < needed) {
            this.data.age++;
            this.markForSave();
         }
      }
   }

   public boolean isEmpty() {
      return this.data.type == 0 || this.data.booze <= 0;
   }

   public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction direction) {
      ItemStack stack = StackUtil.get(player, hand);
      if (stack.isEmpty()) {
         return ItemInteractionResult.FAIL;
      }

      if (direction.getAxis() != Axis.Y && !this.isActive() && StackUtil.consume(player, hand, (ItemLike)IRItems.TREETAP.get(), 1)) {
         this.setActive(true);
         RotateBlock.setHorizontalFacing(this, direction);
         return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }

      if (!this.data.opened) {
         if (this.data.type == 0 || this.data.type == 1) {
            int space = (32 - this.data.booze) * 1000;
            if (player.isShiftKeyDown()) {
               space = Math.min(space, 1000);
            }

            if (space >= 1000) {
               ItemStack stack1 = player.getItemInHand(hand);
               ItemStack container = stack1.copyWithCount(1);
               IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(container).orElse(null);
               if (handlerItem != null) {
                  FluidStack drainStack = new FluidStack(Fluids.WATER, space);
                  FluidStack fs = handlerItem.drain(drainStack, FluidAction.SIMULATE);
                  if (fs.getAmount() >= 1000) {
                     int amount = fs.getAmount() / 1000 * 1000;
                     drainStack.setAmount(amount);
                     fs = handlerItem.drain(drainStack, FluidAction.EXECUTE);
                     if (!fs.isEmpty() && fs.getAmount() == amount) {
                        stack1.shrink(1);
                        if (!player.getInventory().add(handlerItem.getContainer())) {
                           player.drop(handlerItem.getContainer(), false);
                        }

                        this.data.type = 1;
                        this.data.booze += amount / 1000;
                        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
                     }

                     return ItemInteractionResult.FAIL;
                  }
               }
            }

            if (stack.getItem() == Items.WHEAT) {
               this.data.type = 1;
               int amount = stack.getCount();
               if (player.isShiftKeyDown()) {
                  amount = 1;
               }

               if (amount > 64 - this.data.wheatCount) {
                  amount = 64 - this.data.wheatCount;
               }

               if (amount <= 0) {
                  return ItemInteractionResult.FAIL;
               }

               this.data.wheatCount += (byte)amount;
               StackUtil.consumeOrError(player, hand, amount);
               this.alterComposition();
               return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
            }

            if (StackUtil.checkItemEquality(stack, (ItemLike)IRItems.HOPS.get())) {
               this.data.type = 1;
               int amount = stack.getCount();
               if (player.isShiftKeyDown()) {
                  amount = 1;
               }

               if (amount > 64 - this.data.hopsCount) {
                  amount = 64 - this.data.hopsCount;
               }

               if (amount <= 0) {
                  return ItemInteractionResult.FAIL;
               }

               this.data.hopsCount += (byte)amount;
               StackUtil.consumeOrError(player, hand, amount);
               this.alterComposition();
               return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
            }
         }

         if ((this.data.type == 0 || this.data.type == 2) && stack.getItem() == Items.SUGAR_CANE) {
            if (this.data.age > 600) {
               return ItemInteractionResult.FAIL;
            }

            this.data.type = 2;
            int amount2 = stack.getCount();
            if (player.isShiftKeyDown()) {
               amount2 = 1;
            }

            if (this.data.booze + amount2 > 32) {
               amount2 = 32 - this.data.booze;
            }

            if (amount2 <= 0) {
               return ItemInteractionResult.FAIL;
            }

            this.data.booze += amount2;
            StackUtil.consumeOrError(player, hand, amount2);
            return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
         }
      }

      return ItemInteractionResult.FAIL;
   }

   public InteractionResult onClicked(Player player, Direction direction) {
      if (this.isActive()) {
         this.setActive(false);
         this.drainLiquid(1);
      } else {
         if (!this.level.isClientSide) {
            ItemStack stack = new ItemStack((ItemLike)IRItems.BREW_BARREL.get());
            stack.set(IRDataComponents.BOOZE, this.data);
            StackUtil.dropAsEntity(this.level, this.worldPosition, stack);
         }

         this.level.setBlockAndUpdate(this.worldPosition, ((Block)IRBlocks.WOODEN_SCAFFOLD.get()).defaultBlockState());
      }

      return InteractionResult.sidedSuccess(this.level.isClientSide);
   }

   private void alterComposition() {
      if (this.data.time <= 0) {
         this.data.age = 0;
      } else if (this.data.time == 1) {
         if (this.level.random.nextBoolean()) {
            this.data.time = 0;
         } else if (this.level.random.nextBoolean()) {
            this.data.time = 5;
         }
      } else if (this.data.time == 2) {
         if (this.level.random.nextBoolean()) {
            this.data.time = 5;
         }
      } else {
         this.data.time = 5;
      }
   }

   public boolean drainLiquid(int amount) {
      if (this.isEmpty()) {
         return false;
      }

      if (amount > this.data.booze) {
         return false;
      }

      this.open();
      if (this.data.type == 2) {
         // Зрелость считаем ДО отлива и в double. Раньше здесь стояло
         // `age * 100 / timeNedForRum(booze)`, а потом `progress / 100`: serverTick
         // держит age строго меньше нужного, поэтому целочисленное деление всегда
         // давало 0, и любой отлив обнулял выдержку, сколько её ни копи. Плюс
         // знаменатель брался уже из уменьшенного объёма.
         int neededBefore = this.timeNedForRum(this.data.booze);
         double matured = neededBefore <= 0 ? 0.0 : (double)this.data.age / neededBefore;
         this.data.booze -= amount;
         this.data.age = (int)Math.round(matured * this.timeNedForRum(this.data.booze));
      } else {
         this.data.booze -= amount;
      }

      if (this.data.booze <= 0) {
         if (this.data.type == 1) {
            this.data.hopsCount = 0;
            this.data.wheatCount = 0;
            this.data.hops = 0;
            this.data.solid = 0;
            this.data.time = 0;
         }

         this.data.type = 0;
         this.data.opened = false;
         this.data.booze = 0;
      }

      return true;
   }

   private void open() {
      if (!this.data.opened) {
         this.data.opened = true;
         if (this.data.type == 1) {
            float ratio;
            if (this.data.hopsCount <= 0) {
               ratio = 0.0F;
            } else {
               ratio = (float)this.data.hopsCount / this.data.wheatCount;
            }

            if (ratio <= 0.25F) {
               this.data.hops = 0;
            } else if (ratio <= 0.33333334F) {
               this.data.hops = 1;
            } else if (ratio <= 0.5F) {
               this.data.hops = 2;
            } else if (ratio < 2.0F) {
               this.data.hops = 3;
            } else {
               this.data.hops = (byte)Math.min(6.0, Math.floor(ratio) + 2.0);
               if (ratio >= 5.0F) {
                  this.data.time = 5;
               }
            }

            if (this.data.booze <= 0) {
               ratio = Float.POSITIVE_INFINITY;
            } else {
               ratio = (float)(this.data.hopsCount + this.data.wheatCount) / this.data.booze;
            }

            if (ratio <= 0.41666666F) {
               this.data.solid = 0;
            } else if (ratio <= 0.5F) {
               this.data.solid = 1;
            } else if (ratio < 1.0F) {
               this.data.solid = 2;
            } else if (ratio == 1.0F) {
               this.data.solid = 3;
            } else if (ratio < 2.0F) {
               this.data.solid = 4;
            } else if (ratio < 2.4F) {
               this.data.solid = 5;
            } else {
               this.data.solid = 6;
               if (ratio >= 4.0F) {
                  this.data.time = 5;
               }
            }
         }
      }
   }

   public ItemStack getBrewOutput() {
      if (this.isEmpty()) {
         return ItemStack.EMPTY;
      } else if (this.data.type == 1) {
         this.open();
         return ((ItemBooze)IRItems.BOOZE_MUG.get())
            .getItemStack((byte)this.data.type, this.data.solid, this.data.hops, this.data.time, this.data.booze - 1, (byte)0);
      } else if (this.data.type == 2) {
         this.open();
         return ((ItemBooze)IRItems.BOOZE_MUG.get())
            .getItemStack(
               (byte)this.data.type,
               (byte)0,
               (byte)0,
               (byte)0,
               this.data.booze - 1,
               (byte)Math.min(this.data.age * 100 / this.timeNedForRum(this.data.booze), 100)
            );
      } else {
         return ItemStack.EMPTY;
      }
   }

   public int timeNedForRum(int amount) {
      return (int)(1200 * amount * Math.pow(0.95, amount - 1));
   }
}
