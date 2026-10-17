package com.industrialreforged.tech.item.resources;

import com.industrialreforged.tech.api.item.IKineticRotor;
import com.industrialreforged.tech.client.screen.generator.kinetic.WaterKineticGeneratorScreen;
import com.industrialreforged.tech.client.screen.generator.kinetic.WindKineticGeneratorScreen;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ItemLike;

public class ItemWindRotor extends Item implements IKineticRotor {
   private final int maxWindStrength;
   private final int minWindStrength;
   private final int radius;
   private final float efficiency;
   private final ResourceLocation renderTexture;
   private final boolean water;
   private final Supplier<? extends ItemLike> repairItem;

   public ItemWindRotor(
      Properties properties,
      int radius,
      boolean water,
      float efficiency,
      int minWindStrength,
      int maxWindStrength,
      ResourceLocation renderTexture,
      Supplier<? extends ItemLike> repairItem
   ) {
      super(properties);
      this.radius = radius;
      this.efficiency = efficiency;
      this.minWindStrength = minWindStrength;
      this.maxWindStrength = maxWindStrength;
      this.renderTexture = renderTexture;
      this.water = water;
      this.repairItem = repairItem;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("text.industrialreforged.tooltip.kinetic.rotor.info", new Object[]{this.minWindStrength, this.maxWindStrength}));
      if (stack.getMaxDamage() > 0) {
         tooltipComponents.add(
            Component.translatable("text.industrialreforged.tooltip.rotor.durability")
               .append(": " + (stack.getMaxDamage() - stack.getDamageValue()) + " / " + stack.getMaxDamage())
         );
      }

      IKineticRotor.GearboxType gearboxType = null;
      if (Minecraft.getInstance().screen instanceof WaterKineticGeneratorScreen) {
         gearboxType = IKineticRotor.GearboxType.WATER;
      } else if (Minecraft.getInstance().screen instanceof WindKineticGeneratorScreen) {
         gearboxType = IKineticRotor.GearboxType.WIND;
      }

      if (gearboxType != null) {
         tooltipComponents.add(Component.translatable("industrialreforged.itemrotor.fitsin." + this.isAcceptedType(stack, gearboxType)));
      }
   }

   @Override
   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return !repair.isEmpty() && repair.is(this.repairItem.get().asItem());
   }

   @Override
   public int getDiameter(ItemStack itemStack) {
      return this.radius;
   }

   @Override
   public ResourceLocation getRotorRenderTexture(ItemStack itemStack) {
      return this.renderTexture;
   }

   @Override
   public float getEfficiency(ItemStack itemStack) {
      return this.efficiency;
   }

   @Override
   public int getMinWindStrength(ItemStack itemStack) {
      return this.minWindStrength;
   }

   @Override
   public int getMaxWindStrength(ItemStack itemStack) {
      return this.maxWindStrength;
   }

   @Override
   public boolean isAcceptedType(ItemStack itemStack, IKineticRotor.GearboxType gearboxType) {
      return gearboxType == IKineticRotor.GearboxType.WIND || this.water;
   }
}
