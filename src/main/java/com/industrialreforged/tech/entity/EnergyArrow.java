package com.industrialreforged.tech.entity;

import com.industrialreforged.tech.core.ref.IRDamageTypes;
import com.industrialreforged.tech.core.ref.IREntities;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class EnergyArrow extends Arrow {
   public EnergyArrow(EntityType<? extends Arrow> entityType, Level level) {
      super(entityType, level);
   }

   public EnergyArrow(Level level, LivingEntity entity) {
      super(IREntities.ENERGY_ARROW.get(), level);
      this.setPos(entity.getX(), entity.getEyeY() - 0.1F, entity.getZ());
      this.setOwner(entity);
      this.pickup = Pickup.CREATIVE_ONLY;
   }

   protected void onHitEntity(@NotNull EntityHitResult result) {
      Entity entity = result.getEntity();
      float f = (float)this.getDeltaMovement().length();
      int i = Mth.ceil(Mth.clamp(f * this.getBaseDamage(), 0.0, 2.147483647E9));
      if (this.isCritArrow()) {
         long j = this.random.nextInt(i / 2 + 2);
         i = (int)Math.min(j + i, 2147483647L);
      }

      Entity entity1 = this.getOwner();
      Holder<DamageType> holder = this.level()
         .registryAccess()
         .lookup(Registries.DAMAGE_TYPE)
         .flatMap(lookup -> lookup.get(IRDamageTypes.ENERGY_ARROW))
         .orElse(null);
      DamageSource damagesource;
      if (entity1 == null) {
         damagesource = holder == null ? this.damageSources().arrow(this, this) : new DamageSource(holder, this, this);
      } else {
         damagesource = holder == null ? this.damageSources().arrow(this, entity1) : new DamageSource(holder, this, entity1);
         if (entity1 instanceof LivingEntity) {
            ((LivingEntity)entity1).setLastHurtMob(entity);
         }
      }

      boolean flag = entity.getType() == EntityType.ENDERMAN;
      int k = entity.getRemainingFireTicks();
      if (this.isOnFire() && !flag) {
         entity.setRemainingFireTicks(5);
      }

      if (entity.hurt(damagesource, i)) {
         if (flag) {
            return;
         }

         if (entity instanceof LivingEntity livingentity) {
            if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
               livingentity.setArrowCount(livingentity.getArrowCount() + 1);
            }

            this.doKnockback(livingentity, damagesource);
            if (this.level() instanceof ServerLevel serverlevel1) {
               EnchantmentHelper.doPostAttackEffectsWithItemSource(serverlevel1, livingentity, damagesource, this.getWeaponItem());
            }

            this.doPostHurtEffects(livingentity);
            if (entity1 != null && livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
               ((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }

            if (!this.level().isClientSide && entity1 instanceof ServerPlayer serverplayer && !entity.isAlive() && this.shotFromCrossbow()) {
               CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayer, List.of(entity));
            }
         }

         this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.discard();
         }
      } else {
         entity.setRemainingFireTicks(k);
         this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
         this.setYRot(this.getYRot() + 180.0F);
         this.yRotO += 180.0F;
         if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
            if (this.pickup == Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.discard();
         }
      }
   }
}
