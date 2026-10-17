package com.industrialreforged.tech.item.armor;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.item.armor.base.AbstractFluidArmorItem;
import com.industrialreforged.tech.item.armor.jetpack.IJetpack;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class ItemArmorJetpack extends AbstractFluidArmorItem implements IJetpack {
   private Object audioSource;
   private Object jump;

   public ItemArmorJetpack(Properties properties) {
      super(properties.component(IRDataComponents.ACTIVE, false).component(IRDataComponents.HOVER, false), IRFluids.BIOGAS.getFluidStill(), 30000);
   }

   @Override
   public void onPreArmorTick(ItemStack stack, Level level, Player player) {
      if (stack.getItem() instanceof IJetpack jetpack) {
         boolean var24 = (Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false);
         boolean hover = (Boolean)stack.getOrDefault(IRDataComponents.HOVER, false);
         boolean t = var24 && (IR.keyboard.isJumpKeyDown(player) || hover);
         if (t) {
            if (SoundUtil.isStopped(this.audioSource)) {
               this.audioSource = SoundUtil.play(IRSounds.SOUND_JETPACK_LOOP.get(), SoundSource.AMBIENT, 0.2F, 1.0F, true);
            }

            if (SoundUtil.isStopped(this.jump) && IR.keyboard.isJumpKeyDown(player)) {
               this.jump = SoundUtil.play(IRSounds.SOUND_JETPACK_FIRE.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
            }

            if (jetpack.getChargeLevel(stack) > 0.0) {
               float power = jetpack.getPower(stack);
               float dropPercentage = jetpack.getDropPercentage(stack);
               if (jetpack.getChargeLevel(stack) <= dropPercentage) {
                  power *= (float)(jetpack.getChargeLevel(stack) / dropPercentage);
               }

               if (IR.keyboard.isForwardKeyDown(player)) {
                  float retruster = hover ? 1.0F : 0.15F;
                  float boost = 0.0F;
                  float forwardpower = power * retruster * 2.0F;
                  if (forwardpower > 0.0F) {
                     player.moveRelative(0.0F, new Vec3(0.0, 0.4F * forwardpower + boost, 0.02F + boost));
                  }
               }

               int worldHeight = player.level().getHeight();
               int maxFlightHeight = (int)(worldHeight / jetpack.getWorldHeightDivisor(stack));
               double y = player.getY();
               if (y > maxFlightHeight - 25) {
                  if (y > maxFlightHeight) {
 y = maxFlightHeight;
                  }

                  power *= (float)((maxFlightHeight - y) / 25.0);
               }

               Vec3 prevDeltaMovement = player.getDeltaMovement();
               player.setDeltaMovement(prevDeltaMovement.x, Math.min(prevDeltaMovement.y + power * 0.2F, 0.6F), prevDeltaMovement.z);
               Vec3 deltaMovement = player.getDeltaMovement();
               if (hover) {
                  float maxHoverY = 0.0F;
                  if (IR.keyboard.isJumpKeyDown(player)) {
                     maxHoverY += jetpack.getHoverMultiplier(stack, true);
                  }

                  if (player.isShiftKeyDown()) {
                     maxHoverY -= jetpack.getHoverMultiplier(stack, false);
                  }

                  if (deltaMovement.y > maxHoverY) {
                     if (prevDeltaMovement.y > maxHoverY) {
                        player.setDeltaMovement(deltaMovement.x, prevDeltaMovement.y, deltaMovement.z);
                     } else {
                        player.setDeltaMovement(deltaMovement.x, maxHoverY, deltaMovement.z);
                     }
                  }
               }

               int consume = hover ? 1 : 2;
               if (!player.onGround()) {
                  jetpack.drainEnergy(stack, consume);
               }

               player.fallDistance = 0.0F;
               player.walkDist = 0.0F;
            }
         } else {
            Object __ = SoundUtil.stop(this.audioSource);
         }
      }
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      if (stack.getItem() instanceof IJetpack) {
         boolean active = (Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false);
         boolean hover = (Boolean)stack.getOrDefault(IRDataComponents.HOVER, false);
         if (!level.isClientSide) {
            if (IR.keyboard.isFlyKeyDown(player)) {
               ItemCooldowns cooldowns = player.getCooldowns();
               if (!cooldowns.isOnCooldown(stack.getItem())) {
                  cooldowns.addCooldown(stack.getItem(), 10);
                  active = !active;
                  stack.set(IRDataComponents.ACTIVE, active);
                  player.sendSystemMessage(Component.translatable("text.industrialreforged.jetpack." + (active ? "enabled" : "disabled")));
                  player.onUpdateAbilities();
               }
            } else if (IR.keyboard.isJumpKeyDown(player) && IR.keyboard.isModeSwitchKeyDown(player)) {
               ItemCooldowns cooldowns = player.getCooldowns();
               if (!cooldowns.isOnCooldown(stack.getItem())) {
                  cooldowns.addCooldown(stack.getItem(), 10);
                  hover = !hover;
                  stack.set(IRDataComponents.HOVER, hover);
                  player.sendSystemMessage(Component.translatable("text.industrialreforged.jetpack.hover." + (hover ? "enabled" : "disabled")));
               }
            }
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(
         Component.translatable(
            "info.industrialreforged.jetpack_engine",
            new Object[]{Component.translatable("tooltip.industrialreforged.mode.status." + (stack.getOrDefault(IRDataComponents.ACTIVE, false) ? "on" : "off"))}
         )
      );
      tooltipComponents.add(
         Component.translatable(
            "info.industrialreforged.jetpack_hover",
            new Object[]{Component.translatable("tooltip.industrialreforged.mode.status." + (stack.getOrDefault(IRDataComponents.HOVER, false) ? "on" : "off"))}
         )
      );
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.jetpack", new Object[]{IR.keyboard.getFlyKeyName()}));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.space", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }

   @Override
   public boolean drainEnergy(ItemStack stack, int amount) {
      if (stack.getCount() != 1) {
         return false;
      }

      IFluidHandlerItem handlerItem = (IFluidHandlerItem)FluidUtil.getFluidHandler(stack).orElse(null);
      if (handlerItem != null && !handlerItem.getFluidInTank(0).isEmpty()) {
         FluidStack fluidStack = handlerItem.drain(amount, FluidAction.SIMULATE);
         if (fluidStack.getAmount() < amount) {
            return false;
         }

         handlerItem.drain(amount, FluidAction.EXECUTE);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float getPower(ItemStack stack) {
      return 1.0F;
   }

   @Override
   public float getDropPercentage(ItemStack stack) {
      return 0.2F;
   }

   @Override
   public double getChargeLevel(ItemStack stack) {
      return this.getRatio(stack);
   }

   @Override
   public float getHoverMultiplier(ItemStack stack, boolean up) {
      return 0.2F;
   }

   @Override
   public float getWorldHeightDivisor(ItemStack stack) {
      return 1.0F;
   }
}
