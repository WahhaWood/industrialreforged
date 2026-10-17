package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ItemDrillIridium extends ItemDrill {
   private static final Tier IRIDIUM_TOOL_MATERIAL = new Tier() {
      public int getUses() {
         return 3000;
      }

      public float getSpeed() {
         return 15.0F;
      }

      public float getAttackDamageBonus() {
         return 5.0F;
      }

      public int getLevel() {
         return 100;
      }

      public int getEnchantmentValue() {
         return 20;
      }

      public Ingredient getRepairIngredient() {
         return Ingredient.of(new ItemLike[]{(ItemLike)IRItems.IRIDIUM_PLATE.get()});
      }

      public TagKey<Block> getIncorrectBlocksForDrops() {
         return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
      }
   };

   public ItemDrillIridium(ElectricProperties properties) {
      super(1200000, 4000, 800, 24.0F, properties, IRIDIUM_TOOL_MATERIAL);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      if (!level.isClientSide && IR.keyboard.isModeSwitchKeyDown(player)) {
         ItemStack itemStack = StackUtil.get(player, hand);
         ItemEnchantments enchantments = itemStack.getTagEnchantments();
         Holder<Enchantment> holder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
         if (enchantments.getLevel(holder) == 0) {
            EnchantmentHelper.updateEnchantments(itemStack, mutable -> mutable.set(holder, 1));
            player.displayClientMessage(Component.translatable("text.industrialreforged.tooltip.drill.silk_touch"), false);
         } else {
            EnchantmentHelper.updateEnchantments(itemStack, mutable -> mutable.set(holder, 0));
            player.displayClientMessage(Component.translatable("text.industrialreforged.tooltip.drill.normal"), false);
         }
      }

      return super.use(level, player, hand);
   }

   @Override
   public InteractionResult useOn(UseOnContext context) {
      return IR.keyboard.isModeSwitchKeyDown(context.getPlayer()) ? InteractionResult.PASS : super.useOn(context);
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.right", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }
}
