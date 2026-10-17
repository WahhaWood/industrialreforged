package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.core.ref.IRItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorMOX extends ItemReactorUranium {
   public ItemReactorMOX(Properties properties, int n) {
      super(properties, n, 10000);
   }

   @Override
   protected ItemStack getDepletedStack(ItemStack itemStack, IReactor iReactor) {
      return new ItemStack(switch (this.numberOfCells) {
         case 1 -> (Item)IRItems.DEPLETED_MOX_FUEL_ROD.get();
         case 2 -> (Item)IRItems.DEPLETED_DUAL_MOX_FUEL_ROD.get();
         default -> throw new RuntimeException("invalid cell count: " + this.numberOfCells);
         case 4 -> (Item)IRItems.DEPLETED_QUAD_MOX_FUEL_ROD.get();
      });
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (!heatRun) {
         reactor.addOutput(4.0F * ((float)reactor.getHeat() / reactor.getMaxHeat()) + 1.0F);
      }

      return true;
   }
}
