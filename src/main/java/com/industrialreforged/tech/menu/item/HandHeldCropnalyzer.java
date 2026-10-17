package com.industrialreforged.tech.menu.item;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.api.crops.ICropSeed;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.IWorldTickCallback;
import com.industrialreforged.tech.core.TickHandler;
import com.industrialreforged.tech.core.util.ItemInfo;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.item.ItemCropSeed;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HandHeldCropnalyzer extends AbstractHandHeldContainer implements IWorldTickCallback {
   public HandHeldCropnalyzer(Inventory inventory) {
      super(inventory, 3);
      if (IR.sideProxy.isSimulating()) {
         TickHandler.requestContinuousWorldTick(inventory.player.level(), this);
      }
   }

   public void stopOpen(@NotNull Player player) {
      super.stopOpen(player);
      if (IR.sideProxy.isSimulating()) {
         TickHandler.removeContinuousWorldTick(player.level(), this);
      }
   }

   @Override
   public void onTick(Level var1) {
      if (this.container.getItem() instanceof IEnergyContainerItem containerEnergy) {
         ItemStack var9 = (ItemStack)this.inv.get(2);
         ItemStack input = (ItemStack)this.inv.get(0);
         ItemStack output = (ItemStack)this.inv.get(1);
         if (!var9.isEmpty() && var9.getItem() instanceof IEnergyContainerItem energyItem) {
            int need = containerEnergy.receiveEnergy(this.container, Integer.MAX_VALUE, true);
            if (need > 0) {
               int get = ItemInfo.INSTANCE.getEnergyValue(var9);
               if (get > 0.0) {
                  var9.shrink(1);
                  this.inv.set(2, var9);
               } else {
                  get = energyItem.extractEnergy(var9, need, false);
               }

               if (get > 0.0) {
                  containerEnergy.receiveEnergy(this.container, get, false);
               }
            }
         }

         if (output.isEmpty() && !input.isEmpty() && input.getItem() instanceof ICropSeed cropSeed) {
            int level = ItemCropSeed.getCropData(input).getScan();
            if (level < 4) {
               int ned = energyForLevel(level);
               if (containerEnergy.use(this.container, ned, false)) {
                  cropSeed.incrementScannedFromStack(input);
               }
            }

            this.inv.set(1, input);
            this.inv.set(0, ItemStack.EMPTY);
         }
      }
   }

   public static int energyForLevel(int i) {
      return switch (i) {
         case 1 -> 90;
         case 2 -> 900;
         case 3 -> 9000;
         default -> 10;
      };
   }

   public CropData crop() {
      return ItemCropSeed.getCropData((ItemStack)this.inv.get(1));
   }

   public int getScannedLevel() {
      CropData data = this.crop();
      return data.isEmpty() ? -1 : data.getScan();
   }

   public String getSeedName() {
      return MsgUtils.translate(((ItemStack)this.inv.get(1)).getDescriptionId());
   }

   public String getSeedTier() {
      return switch (this.crop().getCard().getProperties().tier()) {
         case 1 -> "I";
         case 2 -> "II";
         case 3 -> "III";
         case 4 -> "IV";
         case 5 -> "V";
         case 6 -> "VI";
         case 7 -> "VII";
         case 8 -> "VIII";
         case 9 -> "IX";
         case 10 -> "X";
         case 11 -> "XI";
         case 12 -> "XII";
         case 13 -> "XIII";
         case 14 -> "XIV";
         case 15 -> "XV";
         case 16 -> "XVI";
         default -> "0";
      };
   }

   public String getSeedDiscovered() {
      return this.crop().getCard().getDiscoveredBy();
   }

   public String getSeedDesc(int i) {
      return this.crop().getCard().desc(i);
   }

   public int getSeedGrowth() {
      return this.crop().getGrowth();
   }

   public int getSeedGain() {
      return this.crop().getGain();
   }

   public int getSeedResistence() {
      return this.crop().getResistance();
   }
}
