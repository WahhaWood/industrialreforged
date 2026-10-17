package com.industrialreforged.tech.item.tool;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.item.ElectricProperties;
import com.industrialreforged.tech.item.armor.ItemArmorNanoSuit;
import com.industrialreforged.tech.item.armor.ItemArmorQuantumSuit;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ItemSaber extends SwordItem implements IEnergyContainerItem {
   /** Доля максимального здоровья цели, добавляемая к удару включённой сабли. */
   private static final float PERCENT_DAMAGE = 0.05F;
   private final ItemAttributeModifiers enabled;
   private final ItemAttributeModifiers disabled;
   public int operationEnergyCost;
   public int capacity;
   public int energyTick;
   protected Object audioSource;
   protected boolean wasEquipped;
   private int soundTicker = 0;
   private final int energyPerOperation;

   public ItemSaber(int energyPerOperation, int damage, Tiers tiers, ElectricProperties properties) {
      super(tiers, properties.component(IRDataComponents.ACTIVE, false));
      this.energyPerOperation = energyPerOperation;
      this.capacity = 640000;
      this.energyTick = 2048;
      this.enabled = createAttributes(tiers, damage, -1.8F);
      this.disabled = createAttributes(tiers, 0, -3.0F);
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      return isActive(stack) && this.use(stack, this.energyPerOperation, true) ? this.enabled : this.disabled;
   }

   public boolean useOnRelease(ItemStack itemStack) {
      return super.useOnRelease(itemStack);
   }

   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      if (isActive(itemStack)) {
         this.soundTicker++;
         if (IR.sideProxy.isRendering() && this.soundTicker % 4 == 0) {
            SoundUtil.play(IRSounds.SOUND_SABER_SWING, SoundSource.AMBIENT, 1.0F, 1.0F);
         }

         if (blockState.is(Blocks.COBWEB)) {
            return 50.0F;
         } else {
            return blockState.is(BlockTags.SWORD_EFFICIENT) ? 4.0F : 1.0F;
         }
      } else {
         return 1.0F;
      }
   }

   public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2) {
      if (!isActive(itemStack)) {
         return true;
      }

      if (IR.sideProxy.isSimulating()) {
         drainSaber(itemStack, this.energyPerOperation, livingEntity2);
         if (!(livingEntity2 instanceof ServerPlayer) || !(livingEntity instanceof Player) || ((ServerPlayer)livingEntity2).canHarmPlayer((Player)livingEntity)
            )
          {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
               if (equipmentSlot.isArmor()) {
                  if (!this.use(itemStack, this.energyPerOperation * 5, true)) {
                     break;
                  }

                  ItemStack itemStack2 = livingEntity.getItemBySlot(equipmentSlot);
                  if (!itemStack2.isEmpty()) {
                     int d = 0;
                     if (itemStack2.getItem() instanceof ItemArmorNanoSuit) {
 d = 48000;
                     } else if (itemStack2.getItem() instanceof ItemArmorQuantumSuit) {
 d = 300000;
                     }

                     if (d != 0) {
                        this.use(itemStack2, d, false);
                        if (!this.use(itemStack2, 1, true)) {
                           livingEntity.setItemSlot(equipmentSlot, ItemStack.EMPTY);
                        }

                        drainSaber(itemStack, this.energyPerOperation * 5, livingEntity2);
                     }
                  }
               }
            }
         }
      }

      if (IR.sideProxy.isRendering()) {
         SoundUtil.play(IRSounds.SOUND_SABER_SWING, SoundSource.AMBIENT, 1.0F, 1.0F);
      }

      return true;
   }

   public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
   }

   /**
    * Включённая сабля добавляет 5 % от максимального здоровья цели. Недозаряженный
    * удар режет бонус так же, как основной урон (иначе спам-клик бил бы на полные 5 %).
    */
   @Override
   public float getAttackDamageBonus(Entity target, float damage, DamageSource source) {
      ItemStack weapon = source.getWeaponItem();
      if (weapon == null
         || weapon.getItem() != this
         || !isActive(weapon)
         || !this.use(weapon, this.energyPerOperation, true)
         || !(target instanceof LivingEntity living)) {
         return 0.0F;
      }

      float bonus = living.getMaxHealth() * PERCENT_DAMAGE;
      if (source.getEntity() instanceof Player player) {
         float charge = player.getAttackStrengthScale(0.5F);
         bonus *= 0.2F + charge * charge * 0.8F;
      }

      return bonus;
   }

   public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      return player.isCreative() ? false : super.canAttackBlock(blockState, level, blockPos, player);
   }

   public InteractionResult useOn(UseOnContext context) {
      return super.useOn(context);
   }

   public static void drainSaber(ItemStack itemStack, int d, LivingEntity livingEntity) {
      if (itemStack.getItem() instanceof IEnergyContainerItem energyItem && !energyItem.use(itemStack, d, false)) {
         setActive(livingEntity, itemStack, false);
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (level.isClientSide || itemStack.getItem() != this) {
         return new InteractionResultHolder(InteractionResult.PASS, itemStack);
      } else if (hand.equals(InteractionHand.OFF_HAND)) {
         return new InteractionResultHolder(InteractionResult.PASS, itemStack);
      } else if (isActive(itemStack)) {
         setActive(player, itemStack, false);
         return new InteractionResultHolder(InteractionResult.SUCCESS, itemStack);
      } else if (this.use(itemStack, 16, true)) {
         setActive(player, itemStack, true);
         return new InteractionResultHolder(InteractionResult.SUCCESS, itemStack);
      } else {
         return super.use(level, player, hand);
      }
   }

   public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int n, boolean equipment) {
      boolean active = isActive(itemStack);
      if (level.isClientSide) {
         if (entity instanceof LivingEntity living) {
            ItemStack mainHand = living.getMainHandItem();
            if (mainHand == itemStack) {
               if (active && !this.wasEquipped) {
                  if (SoundUtil.isStopped(this.audioSource)) {
                     this.audioSource = SoundUtil.play(IRSounds.SOUND_SABER_IDLE, 1.0F, 1.0F, true);
                  }

                  SoundUtil.play(IRSounds.SOUND_SABER_POWER, 1.0F, 1.0F);
               } else if (!active && this.wasEquipped) {
                  this.removeAudioSource();
               }

               this.wasEquipped = active;
            } else if (this.wasEquipped && mainHand.getItem() != this) {
               this.removeAudioSource();
               this.wasEquipped = false;
            }
         }
      } else if (active && entity instanceof ServerPlayer player && player.tickCount % 16 == 0) {
         if (n < 9) {
            drainSaber(itemStack, 64, (LivingEntity)entity);
         } else if (player.tickCount % 64 == 0) {
            drainSaber(itemStack, 16, (LivingEntity)entity);
         }
      }
   }

   public static boolean isActive(ItemStack itemStack) {
      return (Boolean)itemStack.getOrDefault(IRDataComponents.ACTIVE, false);
   }

   private static void setActive(LivingEntity entity, ItemStack stack, boolean value) {
      stack.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
         AttributeInstance instance = entity.getAttributes().getInstance(attribute);
         if (instance != null) {
            instance.removeModifier(modifier);
         }
      });
      stack.set(IRDataComponents.ACTIVE, value);
      if (entity.getMainHandItem() == stack) {
         stack.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            AttributeInstance instance = entity.getAttributes().getInstance(attribute);
            if (instance != null) {
               instance.addOrUpdateTransientModifier(modifier);
            }
         });
      }
   }

   public int getEnchantmentValue() {
      return 0;
   }

   public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
         this.use(itemStack, this.operationEnergyCost * 2, false);
      }

      return true;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      this.appendEnergy(stack, tooltipComponents);
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.saber.percent_damage", Math.round(PERCENT_DAMAGE * 100.0F)));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.active.right"));
   }

   protected void removeAudioSource() {
      if (this.audioSource != null) {
         SoundUtil.stop(this.audioSource);
         this.audioSource = null;
      }
   }

   public boolean onDroppedByPlayer(ItemStack itemStack, Player player) {
      this.removeAudioSource();
      return true;
   }

   public boolean isBarVisible(ItemStack itemStack) {
      return true;
   }

   public int getBarWidth(ItemStack itemStack) {
      return (int)Math.round(this.getRatio(itemStack) * 13.0);
   }

   public int getBarColor(ItemStack itemStack) {
      return Mth.hsvToRgb((float)(this.getRatio(itemStack) / 3.0), 1.0F, 1.0F);
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
      return this.energyTick;
   }

   public int getEnergyExtractTick(ItemStack itemStack) {
      return this.energyTick;
   }
}
