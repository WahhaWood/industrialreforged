package com.industrialreforged.tech.item.tool;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.entity.EnergyArrow;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.ArrowNockEvent;

public class ItemBow extends BowItem implements IEnergyContainerItem {
   private final int[][] matrix = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
   private final int extractTick;
   private final int capacity;
   private final double damage;

   public ItemBow(int extractTick, int maxEnergy, double damage, Properties properties) {
      super(properties.stacksTo(1).component(IRDataComponents.MODE, (byte)0).component(GEDataComponents.ENERGY, 0));
      this.extractTick = extractTick;
      this.capacity = maxEnergy;
      this.damage = damage;
   }

   public ItemBow.BowMode nextMode(ItemStack stack) {
      byte mode = (byte)((Byte)stack.getOrDefault(IRDataComponents.MODE, (byte)0) + 1);
      if (mode < 0 || mode >= ItemBow.BowMode.values().length) {
         mode = 0;
      }

      stack.set(IRDataComponents.MODE, mode);
      return ItemBow.BowMode.values()[mode];
   }

   public ItemBow.BowMode getMode(ItemStack stack) {
      return ItemBow.BowMode.values()[stack.getOrDefault(IRDataComponents.MODE, (byte)0)];
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      this.appendEnergy(stack, tooltipComponents);
      tooltipComponents.add(
         Component.translatable("industrialreforged.tooltip.mode", new Object[]{Component.translatable("industrialreforged.tooltip.bow.mode." + this.getMode(stack).name().toLowerCase())})
      );
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }

   public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
      if (entity instanceof Player player) {
         ItemBow.BowMode mode = this.getMode(stack);
         int charge = this.getUseDuration(stack, entity) - timeLeft;
         ArrowLooseEvent event = new ArrowLooseEvent(player, stack, level, charge, false);
         NeoForge.EVENT_BUS.post(event);
         if (!event.isCanceled()) {
            float f = (event.getCharge() + (mode == ItemBow.BowMode.RAPID_FIRE ? 10 : 0)) / 20.0F;
            f = Math.min((f * f + f * 2.0F) / 3.0F, 1.0F);
            if (!(f < 0.1)) {
               if (!level.isClientSide) {
                  EnergyArrow arrow = new EnergyArrow(level, player);
                  arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                  arrow.setBaseDamage(1.0);
                  if (f == 1.0F) {
                     arrow.setCritArrow(true);
                     arrow.setBaseDamage(this.damage + mode.criticalDamage * 0.5);
                  }

                  arrow.pickup = Pickup.CREATIVE_ONLY;
                  if (mode == ItemBow.BowMode.SPREAD) {
                     level.addFreshEntity(arrow);
                     if (arrow.isCritArrow()) {
                        for (int[] par : this.matrix) {
                           EnergyArrow arrow2 = new EnergyArrow(level, player);
                           arrow2.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                           arrow2.setBaseDamage(this.damage + mode.criticalDamage * 0.5);
                           arrow2.setPos(arrow2.position().x + 0.25 * par[0], arrow2.position().y + 0.25 * par[1], arrow2.position().z);
                           arrow2.setCritArrow(true);
                           arrow2.pickup = Pickup.CREATIVE_ONLY;
                           level.addFreshEntity(arrow2);
                        }
                     }
                  } else {
                     level.addFreshEntity(arrow);
                  }

                  this.use(stack, mode.energyPerUse, false);
               }

               level.playSound(player, player, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            }
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      ArrowNockEvent event = new ArrowNockEvent(player, stack, hand, level, false);
      NeoForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         return event.getAction();
      }

      if (IR.keyboard.isModeSwitchKeyDown(player)) {
         if (!level.isClientSide) {
            player.displayClientMessage(
               Component.translatable(
                  "industrialreforged.tooltip.mode", new Object[]{Component.translatable("industrialreforged.tooltip.bow.mode." + this.nextMode(stack).name().toLowerCase())}
               ),
               false
            );
         }
      } else if (player.isCreative() || this.use(stack, this.getMode(stack).energyPerUse, true)) {
         player.startUsingItem(hand);
      }

      return InteractionResultHolder.consume(stack);
   }

   public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
      ItemBow.BowMode mode = this.getMode(stack);
      if (mode == ItemBow.BowMode.RAPID_FIRE) {
         int j = this.getUseDuration(stack, entity) - count;
         if (j >= 10 && this.use(stack, mode.energyPerUse, true)) {
            entity.releaseUsingItem();
         }
      }
   }

   public boolean isBarVisible(ItemStack itemStack) {
      return true;
   }

   public int getBarWidth(ItemStack stack) {
      return Math.round(13.0F * this.getRatio(stack));
   }

   public int getBarColor(ItemStack stack) {
      return Mth.hsvToRgb(this.getRatio(stack) / 3.0F, 1.0F, 1.0F);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged || oldStack.getItem() != newStack.getItem();
   }

   public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
      return oldStack.getItem() != newStack.getItem();
   }

   public int getEnergyCapacity(ItemStack itemStack) {
      return this.capacity;
   }

   public int getEnergyReceiveTick(ItemStack itemStack) {
      return this.extractTick;
   }

   public int getEnergyExtractTick(ItemStack itemStack) {
      return this.extractTick;
   }

   public enum BowMode {
      NORMAL(300, 1, 1),
      RAPID_FIRE(150, 1, 0),
      SPREAD(400, 0, 0),
      SNIPER(10000, 8, 5),
      FLAME(200, 0, 0);

      public final int energyPerUse;
      public final int criticalDamage;
      public final int knockbackStrength;

      BowMode(int energyPerUse, int criticalDamage, int knockbackStrength) {
         this.energyPerUse = energyPerUse;
         this.criticalDamage = criticalDamage;
         this.knockbackStrength = knockbackStrength;
      }
   }
}
