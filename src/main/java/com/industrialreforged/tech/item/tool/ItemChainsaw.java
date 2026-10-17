package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IShearable;

public class ItemChainsaw extends ItemElectricTool {
   private final ItemAttributeModifiers charged;
   private final ItemAttributeModifiers discharged;

   public ItemChainsaw(ElectricProperties properties) {
      this(400, Tiers.IRON, properties, 120000, 800);
   }

   public ItemChainsaw(int operationEnergyCost, Tier tier, ElectricProperties properties, int capacity, int transfer) {
      super(properties, operationEnergyCost, tier, Set.of(BlockTags.MINEABLE_WITH_AXE), ItemAttributeModifiers.EMPTY, capacity, transfer);
      this.charged = createAttributes(tier, 7.0F, -3.0F);
      this.discharged = createAttributes(tier, 3.0F, -3.0F);
   }

   protected boolean isShear(ItemStack stack, Provider provider) {
      return stack.getEnchantmentLevel(provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH)) > 0;
   }

   protected boolean changeShear(ItemStack stack, Provider provider) {
      Holder<Enchantment> holder = provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
      if (stack.getEnchantmentLevel(holder) > 0) {
         EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(holder, 0));
         return false;
      } else {
         EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(holder, 1));
         return true;
      }
   }

   @Override
   public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
      return state.is(BlockTags.LEAVES)
         || state.is(Blocks.COBWEB)
         || state.is(Blocks.GRASS_BLOCK)
         || state.is(Blocks.FERN)
         || state.is(Blocks.DEAD_BUSH)
         || state.is(Blocks.HANGING_ROOTS)
         || state.is(Blocks.VINE)
         || state.is(Blocks.TRIPWIRE)
         || state.is(BlockTags.WOOL)
         || super.mineBlock(stack, level, state, pos, entity);
   }

   @Override
   public float getDestroySpeed(ItemStack stack, BlockState state) {
      if (state.is(Blocks.COBWEB) || state.is(BlockTags.LEAVES)) {
         return 15.0F;
      } else if (state.is(BlockTags.WOOL)) {
         return 5.0F;
      } else {
         return !state.is(Blocks.VINE) && !state.is(Blocks.GLOW_LICHEN) ? super.getDestroySpeed(stack, state) : 2.0F;
      }
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      if (IR.keyboard.isModeSwitchKeyDown(player)) {
         ItemStack stack = player.getItemInHand(hand);
         if (!level.isClientSide) {
            player.displayClientMessage(
               Component.translatable(
                  this.changeShear(stack, level.registryAccess()) ? "text.industrialreforged.tooltip.chainsaw.shear.enabled" : "text.industrialreforged.tooltip.chainsaw.shear.disabled"
               ),
               false
            );
         }

         return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
      } else {
         return super.use(level, player, hand);
      }
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      return this.use(stack, this.operationEnergyCost, true) ? this.charged : this.discharged;
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
      if (entity instanceof IShearable target
         && this.isShear(stack, player.registryAccess())
         && this.use(stack, this.operationEnergyCost, false)
         && target.isShearable(player, stack, entity.level(), entity.blockPosition())) {
         for (ItemStack dropStack : target.onSheared(player, stack, entity.level(), entity.blockPosition())) {
            entity.spawnAtLocation(dropStack, 1.0F);
         }
      }

      return super.interactLivingEntity(stack, player, entity, hand);
   }

   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
      this.use(stack, this.operationEnergyCost, false);
      return true;
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      Provider provider = context.registries();
      if (provider != null) {
         tooltipComponents.add(
            Component.translatable(this.isShear(stack, provider) ? "text.industrialreforged.tooltip.chainsaw.shear.enabled" : "text.industrialreforged.tooltip.chainsaw.shear.disabled")
         );
      }

      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }
}
