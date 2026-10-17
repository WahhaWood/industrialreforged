package com.industrialreforged.tech.item;

import com.industrialreforged.tech.crop.CropBlockEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ItemWeedExCell extends Item {
   public ItemWeedExCell() {
      super(new Properties().durability(1000));
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      if (level.getBlockEntity(context.getClickedPos()) instanceof CropBlockEntity crop) {
         Player player = context.getPlayer();
         if (player != null) {
            return this.useCrop(crop, player, player.getItemInHand(context.getHand()));
         }
      }

      return super.useOn(context);
   }

   public <T extends LivingEntity> InteractionResult useCrop(CropBlockEntity crop, T entity, ItemStack stack) {
      Level level = crop.getLevel();
      if (level != null && !stack.isEmpty()) {
         boolean isClientSide = level.isClientSide;
         int damage = this.getDamage(stack) + 1;
         int maxDamage = this.getMaxDamage(stack);
         int apply = crop.applyWeedEx(Math.min(50, maxDamage - damage), true, false);
         if (apply <= 0) {
            return InteractionResult.FAIL;
         }

         if (!isClientSide && stack.isDamageableItem() && !(entity instanceof Player player && player.isCreative())) {
            stack.hurtAndBreak(apply, (ServerLevel)level, entity instanceof ServerPlayer player ? player : null, item -> {
               stack.shrink(1);
               if (entity instanceof Player) {
                  ((Player)entity).awardStat(Stats.ITEM_BROKEN.get(item));
               }

               stack.setDamageValue(0);
            });
         }

         return InteractionResult.sidedSuccess(isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }
}
