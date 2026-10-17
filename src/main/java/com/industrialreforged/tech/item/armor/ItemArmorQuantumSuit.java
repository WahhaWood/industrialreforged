package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.TickArmorItem;
import com.industrialreforged.tech.api.item.IArmorTick;
import com.industrialreforged.tech.api.item.IHazmatLike;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ElectricProperties;
import com.industrialreforged.tech.item.ItemTinCan;
import com.industrialreforged.tech.item.armor.jetpack.IJetpack;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ItemArmorQuantumSuit extends ItemArmorElectric implements IJetpack, IHazmatLike, IArmorTick {
   public ItemArmorQuantumSuit(Holder<ArmorMaterial> armorMaterial, Type type, ElectricProperties properties) {
      super(100000000, 512000, armorMaterial, type, properties.rarity(Rarity.RARE).component(DataComponents.DYED_COLOR, new DyedItemColor(-6265536, false)));
   }

   @Override
   public void onPreArmorTick(ItemStack stack, Level level, Player player) {
      if (this.type == Type.CHESTPLATE) {
         ((IArmorTick)IRItems.ADVANCED_NANO_CHESTPLATE.get()).onPreArmorTick(stack, level, player);
      } else if (this.type == Type.LEGGINGS && player.onGround() && IR.keyboard.isBoostKeyDown(player) && this.use(stack, 1000, true)) {
         if (player.tickCount % 10 == 0) {
            this.use(stack, 1000, false);
         }

         if (player.isInWater() && IR.keyboard.isJumpKeyDown(player)) {
            player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.1, 0.0));
         }

         boolean sneaking = player.isCrouching();
         float speed = 0.3F * (player.isInWater() ? 1.2F : 1.0F) * (sneaking ? 0.3F : 1.0F);
         if (player.zza > 0.0F) {
            player.moveRelative(speed, new Vec3(0.0, 0.0, 1.0));
         } else if (player.zza < 0.0F) {
            player.moveRelative(-speed * 0.3F, new Vec3(0.0, 0.0, 1.0));
         }

         if (player.xxa != 0.0F) {
            player.moveRelative(speed * 0.5F * Math.signum(player.xxa), new Vec3(1.0, 0.0, 0.0));
         }
      }
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      if (this.type == Type.HELMET) {
         ((TickArmorItem)IRItems.NIGHTVISION_GOOGLES.get()).onArmorTick(stack, level, player);
         if ((Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false) && player.isInWater()) {
            ItemArmorNightvisionGoggles.refreshEffect(level, player, MobEffects.WATER_BREATHING);
         }

         if (player.getFoodData().needsFood() && this.use(stack, 1000, true)) {
            int slot = -1;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
               ItemStack ifThere = player.getInventory().getItem(i);
               if (!ifThere.isEmpty() && ifThere.getItem() == IRItems.FILLED_TIN_CAN.get()) {
                  slot = i;
                  break;
               }
            }

            if (slot > -1) {
               ItemStack can = player.getInventory().getItem(slot);
               if (can.getItem() == IRItems.FILLED_TIN_CAN.get()) {
                  InteractionResultHolder<ItemStack> itemStack = ((ItemTinCan)can.getItem()).onEaten(player, can);
                  can = (ItemStack)itemStack.getObject();
                  if (can.isEmpty()) {
                     player.getInventory().setItem(slot, ItemStack.EMPTY);
                  } else {
                     player.getInventory().setItem(slot, can);
                  }

                  this.use(stack, 1000, false);
               }
            }
         }
      } else if (this.type == Type.CHESTPLATE) {
         ((TickArmorItem)IRItems.ADVANCED_NANO_CHESTPLATE.get()).onArmorTick(stack, level, player);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (this.type == Type.HELMET) {
         tooltipComponents.add(
            Component.translatable("tooltip.industrialreforged.mode.night_vision", new Object[]{IR.keyboard.getAltKeyName(), IR.keyboard.getModeSwitchKeyName()})
         );
      } else if (this.type == Type.CHESTPLATE) {
         tooltipComponents.add(
            Component.translatable("tooltip.industrialreforged.mode.jetpack", new Object[]{IR.keyboard.getFlyKeyName()})
               .append(" + ")
               .append(IR.keyboard.getModeSwitchKeyName())
         );
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.space", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
      } else if (this.type == Type.BOOTS) {
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.super_jump", new Object[]{IR.keyboard.getBoostKeyName()}));
      }
   }

   @Override
   public float getDamageAbsorptionRatio() {
      return this.type == Type.CHESTPLATE ? 1.1F : 1.0F;
   }

   @Override
   public int getEnergyPerDamage() {
      return 20000;
   }

   @Override
   public boolean addsProtection(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack itemStack) {
      return this.getEnergyStored(itemStack) > 0.0;
   }

   public int getEnchantmentValue() {
      return 0;
   }

   @Override
   public boolean drainEnergy(ItemStack stack, int amount) {
      return this.use(stack, amount + 6, false);
   }

   @Override
   public float getPower(ItemStack stack) {
      return 1.0F;
   }

   @Override
   public float getDropPercentage(ItemStack stack) {
      return 0.05F;
   }

   @Override
   public double getChargeLevel(ItemStack stack) {
      return this.getRatio(stack);
   }

   @Override
   public float getHoverMultiplier(ItemStack stack, boolean up) {
      return 0.1F;
   }

   @Override
   public float getWorldHeightDivisor(ItemStack stack) {
      return 0.9F;
   }
}
