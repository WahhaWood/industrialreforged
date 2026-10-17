package com.industrialreforged.tech.item.tool;

import com.industrialreforged.item.energy.BasicEnergyDiggerItem;
import com.industrialreforged.tech.api.item.MultiTier;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class ItemElectricTool extends BasicEnergyDiggerItem {
   public int operationEnergyCost;
   private final Collection<TagKey<Block>> effectiveBlocks;
   protected Object audioSource;
   protected boolean wasEquipped;

   protected ItemElectricTool(ElectricProperties properties, int operationEnergyCost, int capacity, int transfer) {
      this(properties, operationEnergyCost, Tiers.IRON, Collections.emptyList(), capacity, transfer);
   }

   protected ItemElectricTool(
      ElectricProperties properties, int operationEnergyCost, Tier tier, Collection<TagKey<Block>> collection, int capacity, int transfer
   ) {
      this(properties, 2.0F, -3.0F, operationEnergyCost, tier, collection, capacity, transfer);
   }

   public ItemElectricTool(
      ElectricProperties properties,
      float baseAttackDamage,
      float baseAttackSpeed,
      int operationEnergyCost,
      Tier tier,
      Collection<TagKey<Block>> collection,
      int capacity,
      int transfer
   ) {
      this(properties, operationEnergyCost, tier, collection, createAttributes(tier, baseAttackDamage, baseAttackSpeed), capacity, transfer);
   }

   public ItemElectricTool(
      ElectricProperties properties,
      int operationEnergyCost,
      Tier tier,
      Collection<TagKey<Block>> collection,
      ItemAttributeModifiers modifiers,
      int capacity,
      int transfer
   ) {
      super(new MultiTier(tier, collection), null, properties.attributes(modifiers), capacity, transfer);
      this.operationEnergyCost = operationEnergyCost;
      this.effectiveBlocks = collection;
   }

   public byte getToolMode(ItemStack itemstack, int maxMode) {
      return (byte)Mth.clamp((Byte)itemstack.getOrDefault(IRDataComponents.MODE, (byte)0), 0, maxMode);
   }

   public byte nextToolMode(ItemStack stack, int maxMode, Provider provider) {
      byte mode = (byte)(this.getToolMode(stack, maxMode) + 1);
      if (mode > maxMode) {
         mode = 0;
      }

      this.setToolMode(stack, mode, provider);
      return mode;
   }

   public void setToolMode(ItemStack itemstack, int mode, Provider provider) {
      itemstack.set(IRDataComponents.MODE, (byte)mode);
   }

   public InteractionResult useOn(UseOnContext context) {
      this.use(context.getItemInHand(), 0, true);
      return super.useOn(context);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      this.use(StackUtil.get(player, hand), 0, true);
      return super.use(level, player, hand);
   }

   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      return this.isEffective(itemStack, blockState) && this.use(itemStack, this.operationEnergyCost, true)
         ? super.getDestroySpeed(itemStack, blockState)
         : 1.0F;
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return this.isEffective(stack, state) && super.isCorrectToolForDrops(stack, state);
   }

   protected boolean isEffective(ItemStack stack, BlockState blockState) {
      for (TagKey<Block> tagKey : this.effectiveBlocks) {
         if (blockState.is(tagKey)) {
            return true;
         }
      }

      return false;
   }

   public int getMiningEnergyCost(ItemStack stack) {
      return this.operationEnergyCost;
   }

   public int getEnergyLimitedBlockCount(ItemStack stack) {
      int cost = this.getMiningEnergyCost(stack);
      return cost <= 0 ? Integer.MAX_VALUE : this.getEnergyStored(stack) / cost;
   }

   public boolean useMiningEnergy(ItemStack stack, int blocks, boolean simulate) {
      if (blocks <= 0) {
         return true;
      }

      long energy = (long)this.getMiningEnergyCost(stack) * blocks;
      return energy <= 2147483647L && this.use(stack, (int)energy, simulate);
   }

   public int getEnchantmentValue() {
      return 0;
   }

   public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
         this.use(itemStack, this.operationEnergyCost, false);
      }

      return true;
   }

   public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      this.use(stack, this.operationEnergyCost * 2, false);
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return false;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      this.appendEnergy(stack, tooltipComponents);
   }

   public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int n, boolean bl) {
      boolean bl2 = bl && entity instanceof LivingEntity;
      if (IR.sideProxy.isRendering()) {
         if (bl2 && !this.wasEquipped) {
            Supplier<SoundEvent> supplier = this.getIdleSound((LivingEntity)entity, itemStack);
            if (supplier != null && SoundUtil.isStopped(this.audioSource)) {
               this.audioSource = SoundUtil.play(supplier, 1.0F, 1.0F, true);
            }

            supplier = this.getStartSound((LivingEntity)entity, itemStack);
            if (supplier != null) {
               SoundUtil.play(supplier, 1.0F, 1.0F);
            }
         } else {
            LivingEntity livingEntity;
            ItemStack itemStack2;
            if (!bl2
               && this.audioSource != null
               && entity instanceof LivingEntity
               && (
                  (itemStack2 = (livingEntity = (LivingEntity)entity).getItemBySlot(EquipmentSlot.MAINHAND)).isEmpty()
                     || itemStack2.getItem() != this
                     || itemStack2 == itemStack
               )) {
               this.removeAudioSource();
               Supplier<SoundEvent> supplier = this.getStopSound(livingEntity, itemStack);
               if (supplier != null) {
                  SoundUtil.play(supplier, 1.0F, 1.0F);
               }
            }
         }

         this.wasEquipped = bl2;
      }
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

   @Nullable
   protected Supplier<SoundEvent> getIdleSound(LivingEntity livingEntity, ItemStack itemStack) {
      return null;
   }

   @Nullable
   protected Supplier<SoundEvent> getStopSound(LivingEntity livingEntity, ItemStack itemStack) {
      return null;
   }

   @Nullable
   protected Supplier<SoundEvent> getStartSound(LivingEntity livingEntity, ItemStack itemStack) {
      return null;
   }
}
