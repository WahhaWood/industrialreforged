package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.TickArmorItem;
import com.industrialreforged.tech.api.item.IArmorTick;
import com.industrialreforged.tech.api.item.IHazmatLike;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.ElectricProperties;
import com.industrialreforged.tech.item.armor.jetpack.ICreativeJetpack;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ItemArmorGraviChestaplate extends ItemArmorElectric implements ICreativeJetpack, IHazmatLike, IArmorTick {
   public ItemArmorGraviChestaplate(ElectricProperties properties) {
      super(
         300000000,
         4096000,
         IRArmorMaterials.QUANTUM_SUIT,
         Type.CHESTPLATE,
         properties.component(IRDataComponents.ACTIVE, false).component(IRDataComponents.HOVER, false)
      );
   }

   @Override
   public void onPreArmorTick(ItemStack stack, Level level, Player player) {
      ((IArmorTick)IRItems.ADVANCED_NANO_CHESTPLATE.get()).onPreArmorTick(stack, level, player);
      if ((Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false)) {
         Abilities abilities = player.getAbilities();
         if (!player.onGround() && abilities.flying && IR.keyboard.isBoostKeyDown(player)) {
            if (player.isCreative() || this.use(stack, 834, false)) {
               boolean sneaking = player.isCrouching();
               float speed = 0.11000001F * (player.isInWater() ? 1.2F : 1.0F) * (sneaking ? 0.1F : 1.0F);
               if (player.zza > 0.0F) {
                  player.moveRelative(speed, new Vec3(0.0, 0.0, 1.0));
               } else if (player.zza < 0.0F) {
                  player.moveRelative(-speed * 0.3F, new Vec3(0.0, 0.0, 1.0));
               }

               if (player.xxa != 0.0F) {
                  player.moveRelative(speed * 0.5F * Math.signum(player.xxa), new Vec3(1.0, 0.0, 0.0));
               }

               if (IR.keyboard.isJumpKeyDown(player)) {
                  player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.3, 0.0));
               }

               if (player.isShiftKeyDown()) {
                  player.setDeltaMovement(player.getDeltaMovement().subtract(0.0, 0.3, 0.0));
               }
            } else if (!player.level().isClientSide) {
               player.sendSystemMessage(Component.translatable("text.industrialreforged.gravity_engine.no_energy_to_boost"));
            }
         }
      }
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      super.onArmorTick(stack, level, player);
      ((TickArmorItem)IRItems.ADVANCED_NANO_CHESTPLATE.get()).onArmorTick(stack, level, player);
      boolean active = (Boolean)stack.getOrDefault(IRDataComponents.ACTIVE, false);
      if (!level.isClientSide && IR.keyboard.isFlyKeyDown(player)) {
         ItemCooldowns cooldowns = player.getCooldowns();
         if (!cooldowns.isOnCooldown(stack.getItem())) {
            cooldowns.addCooldown(stack.getItem(), 10);
            active = !active;
            stack.set(IRDataComponents.ACTIVE, active);
            player.sendSystemMessage(Component.translatable("text.industrialreforged.gravity_engine." + (active ? "enabled" : "disabled")));
            Abilities abilities = player.getAbilities();
            if (!abilities.mayfly || !player.isCreative()) {
               abilities.mayfly = active;
               abilities.flying = active && !player.onGround();
               player.onUpdateAbilities();
            }
         }
      }

      if (active && !player.level().isClientSide) {
         Abilities abilities = player.getAbilities();
         if (!player.onGround() && abilities.flying && !player.isCreative()) {
            if (!this.use(stack, 278, true)) {
               player.sendSystemMessage(Component.translatable("text.industrialreforged.gravity_engine.shutdown"));
               stack.set(IRDataComponents.ACTIVE, false);
               if (abilities.mayfly && !player.isCreative()) {
                  abilities.mayfly = false;
                  abilities.flying = false;
                  player.onUpdateAbilities();
               }
            } else if (!player.onGround()) {
               this.use(stack, 278, false);
            } else {
               this.use(stack, 1, false);
            }
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(
         Component.translatable(
            "info.industrialreforged.gravity_engine", new Object[]{Component.translatable("tooltip.industrialreforged.mode.status." + (this.isJetpackActive(stack) ? "on" : "off"))}
         )
      );
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.jetpack", new Object[]{IR.keyboard.getFlyKeyName()}));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.jetpack.boost", new Object[]{IR.keyboard.getBoostKeyName()}));
   }

   @Override
   public float getDamageAbsorptionRatio() {
      return 1.1F;
   }

   @Override
   public int getEnergyPerDamage() {
      return 20000;
   }

   @Override
   public boolean addsProtection(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack itemStack) {
      return this.getEnergyStored(itemStack) > 0.0;
   }

   @NotNull
   public Rarity getRarity(@NotNull ItemStack itemStack) {
      return Rarity.RARE;
   }

   public int getEnchantmentValue() {
      return 0;
   }
}
