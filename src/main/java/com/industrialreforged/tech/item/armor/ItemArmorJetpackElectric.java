package com.industrialreforged.tech.item.armor;

import com.industrialreforged.item.TickArmorItem;
import com.industrialreforged.item.energy.BasicEnergyArmorItem;
import com.industrialreforged.tech.api.item.IArmorTick;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRArmorMaterials;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.armor.jetpack.IJetpack;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemArmorJetpackElectric extends BasicEnergyArmorItem implements IJetpack {
   public ItemArmorJetpackElectric(int capacity, Properties properties) {
      super(
         IRArmorMaterials.UTILITY,
         Type.CHESTPLATE,
         properties.component(IRDataComponents.ACTIVE, false).component(IRDataComponents.HOVER, false),
         capacity,
         512
      );
   }

   @Override
   public void onPreArmorTick(ItemStack stack, Level level, Player player) {
      ((IArmorTick)IRItems.JETPACK.get()).onPreArmorTick(stack, level, player);
   }

   public void onArmorTick(ItemStack stack, Level level, Player player) {
      ((TickArmorItem)IRItems.JETPACK.get()).onArmorTick(stack, level, player);
   }

   @Nullable
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return IRArmorItem.getArmorTexture(this, stack, entity, slot, layer);
   }

   @Override
   public boolean drainEnergy(ItemStack stack, int amount) {
      return this.use(stack, amount, false);
   }

   @Override
   public float getPower(ItemStack stack) {
      return 1.0F;
   }

   @Override
   public float getDropPercentage(ItemStack stack) {
      return 0.2F;
   }

   @Override
   public double getChargeLevel(ItemStack stack) {
      return this.getRatio(stack);
   }

   @Override
   public float getHoverMultiplier(ItemStack stack, boolean up) {
      return 0.2F;
   }

   @Override
   public float getWorldHeightDivisor(ItemStack stack) {
      return 1.0F;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(
         Component.translatable(
            "info.industrialreforged.jetpack_engine",
            new Object[]{Component.translatable("tooltip.industrialreforged.mode.status." + (stack.getOrDefault(IRDataComponents.ACTIVE, false) ? "on" : "off"))}
         )
      );
      tooltipComponents.add(
         Component.translatable(
            "info.industrialreforged.jetpack_hover",
            new Object[]{Component.translatable("tooltip.industrialreforged.mode.status." + (stack.getOrDefault(IRDataComponents.HOVER, false) ? "on" : "off"))}
         )
      );
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.jetpack", new Object[]{IR.keyboard.getFlyKeyName()}));
      tooltipComponents.add(Component.translatable("tooltip.industrialreforged.mode.space", new Object[]{IR.keyboard.getModeSwitchKeyName()}));
   }
}
