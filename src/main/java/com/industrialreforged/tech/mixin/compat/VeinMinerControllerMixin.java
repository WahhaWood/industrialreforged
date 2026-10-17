package com.industrialreforged.tech.mixin.compat;

import com.industrialreforged.tech.item.tool.ItemElectricTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.sheath.veinminer.logic.VeinMinerController", remap = false)
public abstract class VeinMinerControllerMixin {
   @Redirect(
      method = {"handleBlockBreak", "computeBlockCap", "applyPlan"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z"),
      require = 0
   )
   private boolean ir$useEnergyLimit(ItemStack stack) {
      return stack.getItem() instanceof ItemElectricTool || stack.isDamageableItem();
   }

   @Redirect(
      method = {"handleBlockBreak", "applyPlan"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxDamage()I"),
      require = 0
   )
   private int ir$energyAsMaxDamage(ItemStack stack) {
      return stack.getItem() instanceof ItemElectricTool tool ? saturatedIncrement(tool.getEnergyLimitedBlockCount(stack)) : stack.getMaxDamage();
   }

   @Redirect(
      method = {"handleBlockBreak", "applyPlan"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getDamageValue()I"),
      require = 0
   )
   private int ir$energyHasNoDamageValue(ItemStack stack) {
      return stack.getItem() instanceof ItemElectricTool ? 0 : stack.getDamageValue();
   }

   @Redirect(
      method = "applyPlan",
      at = @At(
         value = "INVOKE",
         target = "Lcom/sheath/veinminer/logic/VeinMinerController;damageTool(Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/server/level/ServerPlayer;)V"
      ),
      require = 0
   )
   private void ir$consumeEnergyInsteadOfDurability(VeinMinerControllerMixin controller, ItemStack stack, int blocks, ServerPlayer player) {
      if (stack.getItem() instanceof ItemElectricTool tool) {
         tool.useMiningEnergy(stack, blocks, false);
      } else {
         stack.hurtAndBreak(blocks, player, EquipmentSlot.MAINHAND);
      }
   }

   private static int saturatedIncrement(int value) {
      return value == Integer.MAX_VALUE ? value : value + 1;
   }
}
