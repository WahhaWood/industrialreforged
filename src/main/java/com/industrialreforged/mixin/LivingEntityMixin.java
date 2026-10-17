package com.industrialreforged.mixin;

import com.industrialreforged.init.GEDataComponents;
import com.industrialreforged.item.api.IEnergyContainerItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
   /**
    * Energy items rewrite their charge almost every tick in use, and vanilla takes each write
    * for a re-equip: attributes reapplied, an equipment packet to every tracking player, an
    * equipment change event. Only the charge is ignored here, and only while it stays on the
    * same side of zero (empty <-> charged flips the energy armor gate, see onItemAttributes).
    * Everything else, including other items, goes through the vanilla comparison: the old
    * "same item and count" shortcut missed enchantment, affix and dye swaps for every item.
    */
   @Redirect(
      method = "collectEquipmentChanges",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;equipmentHasChanged(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
      )
   )
   public boolean hasChange(LivingEntity instance, ItemStack oldItem, ItemStack newItem) {
      if (oldItem.getItem() instanceof IEnergyContainerItem energyItem
         && oldItem.is(newItem.getItem())
         && oldItem.getCount() == newItem.getCount()) {
         int oldEnergy = energyItem.getEnergyStored(oldItem);
         int newEnergy = energyItem.getEnergyStored(newItem);
         if (oldEnergy != newEnergy && (oldEnergy > 0) == (newEnergy > 0)) {
            // oldItem is the entity's private snapshot (a copy made on equip): moving the charge
            // into it leaves the real differences to vanilla and keeps later ticks on the fast path.
            oldItem.set(GEDataComponents.ENERGY, newEnergy);
         }
      }

      return instance.equipmentHasChanged(oldItem, newItem);
   }
}
