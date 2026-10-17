package com.industrialreforged.tech.item;

import com.industrialreforged.tech.api.reactor.IBaseReactorComponent;
import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.core.RadiationMobEffect;
import com.industrialreforged.tech.core.ref.IRMobEffects;
import com.industrialreforged.tech.item.armor.ItemArmorHazmat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemNuclearResource extends Item implements IBaseReactorComponent {
   private final int radiationDuration;
   private final int radiationAmplifier;

   public ItemNuclearResource(Properties properties, int n, int n2) {
      super(properties);
      this.radiationDuration = n;
      this.radiationAmplifier = n2;
   }

   @Override
   public boolean canBePlacedIn(ItemStack itemStack, IReactor iReactor) {
      return false;
   }

   public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int n, boolean bl) {
      if (entity instanceof LivingEntity livingEntity) {
         if (!ItemArmorHazmat.hasCompleteHazmat(livingEntity)) {
            ((RadiationMobEffect)IRMobEffects.RADIATION.get()).applyTo(livingEntity, this.radiationDuration * 20, this.radiationAmplifier);
         }
      }
   }
}
