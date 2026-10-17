package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemReactorHeatSwitch extends ItemReactorHeatStorage {
   public final int switchSide;
   public final int switchReactor;

   public ItemReactorHeatSwitch(Properties properties, int n, int n2, int n3) {
      super(properties, n);
      this.switchSide = n2;
      this.switchReactor = n3;
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
      if (heatRun) {
         int n3 = 0;
         ArrayList<ItemReactorHeatSwitch.ItemStackCoord> arrayList = new ArrayList<>();
         if (this.switchSide > 0) {
            this.checkHeatAcceptor(reactor, x - 1, y, arrayList);
            this.checkHeatAcceptor(reactor, x + 1, y, arrayList);
            this.checkHeatAcceptor(reactor, x, y - 1, arrayList);
            this.checkHeatAcceptor(reactor, x, y + 1, arrayList);
         }

         if (this.switchSide > 0) {
            for (ItemReactorHeatSwitch.ItemStackCoord itemStackCoord : arrayList) {
               IReactorComponent iReactorComponent = (IReactorComponent)itemStackCoord.stack.getItem();
               double d = this.getCurrentHeat(reactor, stack, x, y) * 100.0 / this.getMaxHeat(reactor, stack, x, y);
               double d2 = iReactorComponent.getCurrentHeat(reactor, itemStackCoord.stack, itemStackCoord.x, itemStackCoord.y)
                  * 100.0
                  / iReactorComponent.getMaxHeat(reactor, itemStackCoord.stack, itemStackCoord.x, itemStackCoord.y);
               int n4 = (int)(iReactorComponent.getMaxHeat(reactor, itemStackCoord.stack, itemStackCoord.x, itemStackCoord.y) / 100.0 * (d2 + d / 2.0));
               if (n4 > this.switchSide) {
                  n4 = this.switchSide;
               }

               if (d2 + d / 2.0 < 1.0) {
                  n4 = this.switchSide / 2;
               }

               if (d2 + d / 2.0 < 0.75) {
                  n4 = this.switchSide / 4;
               }

               if (d2 + d / 2.0 < 0.5) {
                  n4 = this.switchSide / 8;
               }

               if (d2 + d / 2.0 < 0.25) {
                  n4 = 1;
               }

               if (Math.round(d2 * 10.0) / 10.0 > Math.round(d * 10.0) / 10.0) {
                  n4 -= 2 * n4;
               } else if (Math.round(d2 * 10.0) / 10.0 == Math.round(d * 10.0) / 10.0) {
                  n4 = 0;
               }

               n3 -= n4;
               n4 = iReactorComponent.alterHeat(reactor, itemStackCoord.stack, itemStackCoord.x, itemStackCoord.y, n4);
               n3 += n4;
            }
         }

         if (this.switchReactor > 0) {
            double d = this.getCurrentHeat(reactor, stack, x, y) * 100.0 / this.getMaxHeat(reactor, stack, x, y);
            double d3 = reactor.getHeat() * 100.0 / reactor.getMaxHeat();
            int n5 = (int)Math.round(reactor.getMaxHeat() / 100.0 * (d3 + d / 2.0));
            if (n5 > this.switchReactor) {
               n5 = this.switchReactor;
            }

            if (d3 + d / 2.0 < 1.0) {
               n5 = this.switchSide / 2;
            }

            if (d3 + d / 2.0 < 0.75) {
               n5 = this.switchSide / 4;
            }

            if (d3 + d / 2.0 < 0.5) {
               n5 = this.switchSide / 8;
            }

            if (d3 + d / 2.0 < 0.25) {
               n5 = 1;
            }

            if (Math.round(d3 * 10.0) / 10.0 > Math.round(d * 10.0) / 10.0) {
               n5 -= 2 * n5;
            } else if (Math.round(d3 * 10.0) / 10.0 == Math.round(d * 10.0) / 10.0) {
               n5 = 0;
            }

            n3 -= n5;
            reactor.setHeat(reactor.getHeat() + n5);
         }

         this.alterHeat(reactor, stack, x, y, n3);
      }
   }

   private void checkHeatAcceptor(IReactor iReactor, int n, int n2, ArrayList<ItemReactorHeatSwitch.ItemStackCoord> arrayList) {
      ItemStack itemStack = iReactor.getItemAt(n, n2);
      if (itemStack != null
         && itemStack.getItem() instanceof IReactorComponent
         && ((IReactorComponent)itemStack.getItem()).canStoreHeat(iReactor, itemStack, n, n2)) {
         arrayList.add(new ItemReactorHeatSwitch.ItemStackCoord(itemStack, n, n2));
      }
   }

   private class ItemStackCoord {
      public ItemStack stack;
      public int x;
      public int y;

      public ItemStackCoord(ItemStack itemStack, int n, int n2) {
         this.stack = itemStack;
         this.x = n;
         this.y = n2;
      }
   }
}
