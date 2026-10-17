package com.industrialreforged.tech.item;

import com.industrialreforged.tech.blockentity.BrewBarrelBlockEntity;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.StackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ItemMug extends Item implements ItemLike {
   public ItemMug.MugType mugType;

   public ItemMug(Properties properties, ItemMug.MugType mugType) {
      super(properties);
      this.mugType = mugType;
   }

   public InteractionResult useOn(UseOnContext context) {
      ItemStack stack = context.getItemInHand();
      ItemMug.MugType type = this.getType(stack);
      if (type == ItemMug.MugType.empty) {
         Level level = context.getLevel();
         if (level.isClientSide) {
            return InteractionResult.FAIL;
         }

         Player player = context.getPlayer();
         BlockPos pos = context.getClickedPos();
         if (level.getBlockEntity(pos) instanceof BrewBarrelBlockEntity barrel) {
            Direction direction = context.getClickedFace();
            if (!barrel.isActive() || barrel.getHorizontalFacing() != direction) {
               return InteractionResult.PASS;
            }

            ItemStack output = barrel.getBrewOutput();
            if (!output.isEmpty() && barrel.drainLiquid(1)) {
               stack.shrink(1);
               if (!stack.isEmpty()) {
                  if (!player.getInventory().add(output)) {
                     player.drop(output, false);
                  }
               } else {
                  player.setItemInHand(context.getHand(), output);
               }

               return InteractionResult.SUCCESS;
            }
         }
      }

      return InteractionResult.PASS;
   }

   public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
      if (livingEntity instanceof Player player) {
         ItemMug.MugType mugType = this.getType(itemStack);
         if (mugType == ItemMug.MugType.empty) {
            return itemStack;
         }

         int n;

         int n2 = switch (mugType) {
            case cold_coffee -> {
 n = 1;
               yield 600;
            }
            case dark_coffee -> {
 n = 5;
               yield 1200;
            }
            case coffee -> {
 n = 6;
               yield 1200;
            }
            default -> throw new IllegalStateException("unexpected type: " + mugType);
         };
         int n3 = 0;
         int n4 = this.amplifyEffect(player, MobEffects.MOVEMENT_SPEED, n, n2);
         if (n4 > n3) {
            n3 = n4;
         }

         if ((n4 = this.amplifyEffect(player, MobEffects.DIG_SPEED, n, n2)) > n3) {
            n3 = n4;
         }

         if (mugType == ItemMug.MugType.coffee) {
            n3 -= 2;
         }

         if (n3 >= 3) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, (n3 - 2) * 200, 0));
            if (n3 >= 4) {
               player.addEffect(new MobEffectInstance(MobEffects.HARM, 1, n3 - 3));
            }
         }

         return new ItemStack((ItemLike)IRItems.EMPTY_MUG.get(), 1);
      } else {
         return itemStack;
      }
   }

   private int amplifyEffect(Player player, Holder<MobEffect> mobEffect, int n, int n2) {
      MobEffectInstance mobEffectInstance = player.getEffect(mobEffect);
      if (mobEffectInstance != null) {
         int n3 = mobEffectInstance.getAmplifier();
         int n4 = mobEffectInstance.getDuration();
         if (n3 < n) {
            n3++;
         }

         n4 += n2;
         assert mobEffect == mobEffectInstance.getEffect();
         player.addEffect(new MobEffectInstance(mobEffect, n4, n3));
         return n3;
      } else {
         player.addEffect(new MobEffectInstance(mobEffect, 300, 0));
         return 1;
      }
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      ItemMug.MugType mugType = this.getType(stack);
      return mugType == ItemMug.MugType.empty ? 0 : 32;
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      ItemMug.MugType mugType = this.getType(itemStack);
      return mugType == ItemMug.MugType.empty ? UseAnim.NONE : UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemMug.MugType mugType = this.getType(StackUtil.get(player, interactionHand));
      if (mugType != ItemMug.MugType.empty) {
         player.startUsingItem(interactionHand);
      }

      return super.use(level, player, interactionHand);
   }

   private ItemMug.MugType getType(ItemStack itemStack) {
      return ((ItemMug)itemStack.getItem()).mugType;
   }

   public enum MugType {
      empty,
      cold_coffee,
      dark_coffee,
      coffee;
   }
}
