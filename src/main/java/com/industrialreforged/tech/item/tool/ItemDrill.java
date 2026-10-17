package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.api.item.IMiningDrill;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.Arrays;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class ItemDrill extends ItemElectricTool implements IMiningDrill {
   private final float extraSpeedMultiplier;

   public ItemDrill(int capacity, int energyTick, int operationEnergyCost, float speed, ElectricProperties properties, Tier tier) {
      super(properties, operationEnergyCost, tier, Arrays.asList(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_SHOVEL), capacity, energyTick);
      this.extraSpeedMultiplier = speed / tier.getSpeed();
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
      return itemAbility == ItemAbilities.PICKAXE_DIG || itemAbility == ItemAbilities.SHOVEL_DIG;
   }

   @Override
   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      float f = super.getDestroySpeed(itemStack, blockState);
      if (f == 1.0F) {
         return f;
      }

      Player player = getPlayerHoldingItem(itemStack);
      if (player != null) {
         if (player.isEyeInFluid(FluidTags.WATER)
            && EnchantmentHelper.getEnchantmentLevel(
                  player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.AQUA_AFFINITY), player
               )
               == 0) {
            f *= 5.0F;
         }

         if (!player.onGround()) {
            f *= 5.0F;
         }
      }

      return f * this.extraSpeedMultiplier;
   }

   @Nullable
   private static Player getPlayerHoldingItem(ItemStack itemStack) {
      if (IR.sideProxy.isRendering()) {
         Player player = IR.sideProxy.getPlayerInstance();
         if (player != null && player.getInventory().getSelected() == itemStack) {
            return player;
         }
      } else {
         for (Player player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.getInventory().getSelected() == itemStack) {
               return player;
            }
         }
      }

      return null;
   }
}
