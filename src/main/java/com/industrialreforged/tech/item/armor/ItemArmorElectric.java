package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.item.energy.BasicEnergyArmorItem;
import com.industrialreforged.tech.api.item.IMetalArmor;
import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import com.industrialreforged.tech.item.ElectricProperties;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.TooltipContext;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public abstract class ItemArmorElectric extends BasicEnergyArmorItem implements IMetalArmor {
   public ItemArmorElectric(int capacity, int energyTick, Holder<ArmorMaterial> armorMaterial, Type equipmentSlot, ElectricProperties properties) {
      super(armorMaterial, equipmentSlot, properties, capacity, energyTick);
   }

   @Override
   public boolean isMetalArmor(ItemStack itemStack, Player player) {
      return true;
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return IRArmorItem.getArmorTexture(this, stack, entity, slot, layer);
   }

   public abstract float getDamageAbsorptionRatio();

   public float getBaseAbsorptionRatio() {
      return switch (this.type) {
         case HELMET, BOOTS -> 0.15F;
         case CHESTPLATE -> 0.4F;
         case LEGGINGS -> 0.3F;
         case BODY -> 0.5F;
         default -> throw new MatchException(null, null);
      };
   }

   public abstract int getEnergyPerDamage();

   /** Ступень для бонусов полного комплекта: нано 1, квант 2, сингулярная 3, прочее 0. */
   public int getSetTier() {
      ArmorMaterial material = this.getMaterial().value();
      if (material == IRArmorMaterials.SINGULAR_SUIT.value()) {
         return 3;
      } else if (material == IRArmorMaterials.QUANTUM_SUIT.value()) {
         return 2;
      } else {
         return material == IRArmorMaterials.NANO_SUIT.value() ? 1 : 0;
      }
   }

   /** Ступень надетого комплекта — по худшему из четырёх кусков; 0, если комплекта нет. */
   public static int getWornSetTier(LivingEntity entity) {
      int tier = Integer.MAX_VALUE;
      for (ItemStack stack : entity.getArmorSlots()) {
         tier = Math.min(tier, stack.getItem() instanceof ItemArmorElectric armor ? armor.getSetTier() : 0);
      }

      return tier == Integer.MAX_VALUE ? 0 : tier;
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      int tier = this.getSetTier();
      if (tier > 0) {
         tooltipComponents.add(Component.translatable("tooltip.industrialreforged.armor.set_bonus", Component.translatable("enchantment.level." + tier)));
      }
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return false;
   }

   protected final void chargeHandItem(Player player, ItemStack stack) {
      if (!player.level().isClientSide && stack.getItem() instanceof IEnergyContainerItem e) {
         ItemStack handItem = player.getMainHandItem();
         if (!handItem.isEmpty()) {
            int storage = e.getEnergyStored(stack);
            if (storage > 0) {
               IEnergyStorage es = (IEnergyStorage)handItem.getCapability(EnergyStorage.ITEM);
               if (es != null) {
                  e.extractEnergy(stack, es.receiveEnergy(Math.min(storage, e.getEnergyExtractTick(stack)), false), false);
               }
            }
         }
      }
   }
}
