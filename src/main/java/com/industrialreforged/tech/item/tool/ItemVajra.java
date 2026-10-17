package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ItemVajra extends ItemElectricTool {
   private final float efficiencyOnProperMaterial = 20000.0F;
   private final boolean hasFortune;

   public ItemVajra(int capacity, int energyTick, int operationEnergyCost, boolean hasFortune, ElectricProperties properties) {
      super(
         properties.component(IRDataComponents.MODE, (byte)0),
         operationEnergyCost,
         Tiers.NETHERITE,
         Set.of(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_PICKAXE),
         capacity,
         energyTick
      );
      this.hasFortune = hasFortune;
   }

   @Override
   public void setToolMode(ItemStack itemstack, int mode, Provider provider) {
      super.setToolMode(itemstack, mode, provider);
      Holder<Enchantment> holder = provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
      EnchantmentHelper.updateEnchantments(itemstack, mutable -> mutable.set(holder, mode == 1 ? 1 : 0));
   }

   public int fortuneLevel() {
      return 0;
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (IR.keyboard.isModeSwitchKeyDown(player)) {
         if (!level.isClientSide) {
            player.displayClientMessage(
               Component.translatable(
                  this.nextToolMode(stack, 1, level.registryAccess()) == 0
                     ? "text.industrialreforged.tooltip.vajra.silk_touch.disabled"
                     : "text.industrialreforged.tooltip.vajra.silk_touch.enabled"
               ),
               false
            );
         }

         return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
      } else {
         return InteractionResultHolder.pass(stack);
      }
   }

   @Override
   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();
      if (context.getHand() == InteractionHand.MAIN_HAND) {
         ItemStack stack = context.getItemInHand();
         if (this.getToolMode(stack, 1) == 1 && this.use(stack, this.operationEnergyCost, true)) {
            if (!level.isClientSide) {
               Block bucketWheelBlock = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("immersiveengineering", "bucket_wheel"));
               BlockState blockState = level.getBlockState(blockPos);
               if (blockState.getBlock().equals(bucketWheelBlock)) {
                  return InteractionResult.FAIL;
               }

               if (blockState.getDestroySpeed(level, blockPos) > 0.0F && blockState.canHarvestBlock(level, blockPos, context.getPlayer())) {
                  ((ServerPlayer)context.getPlayer()).gameMode.destroyBlock(blockPos);
               }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   @Override
   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      return this.use(itemStack, this.operationEnergyCost, true) && blockState.getBlock() != Blocks.BEDROCK ? this.efficiencyOnProperMaterial : 1.0F;
   }

   @Override
   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return state.getBlock() != Blocks.BEDROCK;
   }

   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
      if (this.use(stack, this.operationEnergyCost * 2, false)) {
         entity.hurt(player.damageSources().playerAttack(player), 25.0F);
      } else {
         entity.hurt(player.damageSources().playerAttack(player), 1.0F);
      }

      return false;
   }

   @Override
   public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
      if (state.getDestroySpeed(level, pos) != 0.0F) {
         this.use(stack, this.operationEnergyCost, false);
      }

      return true;
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("text.industrialreforged.tooltip.vajra.silk_touch." + (this.getToolMode(stack, 1) == 0 ? "disabled" : "enabled")));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }
}
