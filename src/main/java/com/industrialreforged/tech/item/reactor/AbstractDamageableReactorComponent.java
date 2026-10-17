package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public abstract class AbstractDamageableReactorComponent extends Item implements IReactorComponent {
   protected AbstractDamageableReactorComponent(Properties properties, int durability) {
      super(properties.durability(durability));
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      return false;
   }

   @Override
   public boolean canStoreHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return false;
   }

   @Override
   public int getMaxHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return 0;
   }

   @Override
   public int getCurrentHeat(IReactor reactor, ItemStack stack, int x, int y) {
      return 0;
   }

   @Override
   public int alterHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
      return heat;
   }

   @Override
   public float influenceExplosion(IReactor reactor, ItemStack stack) {
      return 0.0F;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(
         Component.literal(
            MsgUtils.translate("industrialreforged.tooltip.durability") + " " + (this.getMaxDamage(stack) - stack.getDamageValue()) + "/" + this.getMaxDamage(stack)
         )
      );
   }

   @Override
   public boolean canBePlacedIn(ItemStack itemStack, IReactor iReactor) {
      return true;
   }
}
