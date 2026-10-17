package com.industrialreforged.tech.item.armor.base;

import com.industrialreforged.item.fluid.BasicFluidArmorItem;
import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import com.industrialreforged.tech.item.armor.IRArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFluidArmorItem extends BasicFluidArmorItem {
   private final Fluid baseFluid;

   public AbstractFluidArmorItem(Properties properties, Fluid fluid, int capacity) {
      super(IRArmorMaterials.UTILITY, Type.CHESTPLATE, properties, capacity, new Fluid[]{fluid});
      this.baseFluid = fluid;
   }

   public Fluid getBaseFluid() {
      return this.baseFluid;
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return IRArmorItem.getArmorTexture(this, stack, entity, slot, layer);
   }
}
