package com.industrialreforged.tech.mixin;

import com.industrialreforged.tech.item.armor.ItemArmorElectric;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {
   @Redirect(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getArmorValue()I"))
   private static int getArmorValue(Player player) {
      float absorb = 0.0F;

      for (ItemStack stack : player.getInventory().armor) {
         // Бонус показываем только для заряженных кусков — как и саму защиту (см. гейт в EventHandlerForge).
         if (!stack.isEmpty()
            && stack.getItem() instanceof ItemArmorElectric electric
            && electric.getEnergyStored(stack) > 0) {
            absorb += electric.getBaseAbsorptionRatio() * electric.getDamageAbsorptionRatio();
         }
      }

      return Mth.floor(absorb * 20.0F) + player.getArmorValue();
   }
}
