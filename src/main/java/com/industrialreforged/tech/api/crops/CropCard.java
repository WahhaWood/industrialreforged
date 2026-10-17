package com.industrialreforged.tech.api.crops;

import com.industrialreforged.tech.crop.IRCrops;
import com.industrialreforged.tech.crop.data.CropData;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CropCard {
   protected final ICropType cropType;

   public CropCard(ICropType cropType) {
      this.cropType = cropType;
   }

   public boolean isEmpty() {
      return false;
   }

   public String getId() {
      return String.format("%s:%s", this.getOwner(), this.getName());
   }

   public String getName() {
      return this.cropType.getName();
   }

   public String getOwner() {
      return this.cropType.getOwner();
   }

   public abstract Block getCropBlock();

   public Component getDisplayName() {
      return Component.translatable(this.getUnlocalizedName());
   }

   public String getUnlocalizedName() {
      return "crop." + this.getOwner() + "." + this.getName();
   }

   public String getDiscoveredBy() {
      return "unknown";
   }

   public String desc(int paramInt) {
      String[] arrayOfString = this.getAttributes();
      if (arrayOfString == null || arrayOfString.length == 0) {
         return "";
      }

      if (paramInt == 0) {
         String str1 = arrayOfString[0];
         if (arrayOfString.length >= 2) {
            str1 = str1 + ", " + str1;
            if (arrayOfString.length >= 3) {
               str1 = str1 + ",";
            }
         }

         return str1;
      } else {
         if (arrayOfString.length < 3) {
            return "";
         }

         String str = arrayOfString[2];
         if (arrayOfString.length >= 4) {
            str = str + ", " + str;
         }

         return str;
      }
   }

   public int getRootsLength(ICropTile paramICropTile) {
      return 1;
   }

   public abstract CropProperties getProperties();

   public String[] getAttributes() {
      return new String[0];
   }

   public String getSeedType() {
      return "crop.industrialreforged.seeds";
   }

   public int getMaxAge() {
      return this.cropType.getMaxAge();
   }

   public int getGrowthDuration(ICropTile paramICropTile) {
      return this.getProperties().tier() * 200;
   }

   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge();
   }

   public int getWeightInfluences(ICropTile paramICropTile, int paramInt1, int paramInt2, int paramInt3) {
      return paramInt1 + paramInt2 + paramInt3;
   }

   public boolean canCross(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() >= 2;
   }

   public boolean onRightClick(ICropTile paramICropTile, Player paramPlayer) {
      return paramICropTile.performManualHarvest();
   }

   public int getOptimalHarvestAge(ICropTile paramICropTile) {
      return this.getMaxAge();
   }

   public boolean canBeHarvested(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge();
   }

   public double dropGainChance() {
      return Math.pow(0.95, this.getProperties().tier());
   }

   @Deprecated
   @NotNull
   public ItemStack getGain(@Nullable ICropTile crop) {
      return ItemStack.EMPTY;
   }

   @NotNull
   public ItemStack[] getGains(@Nullable ICropTile crop) {
      return new ItemStack[]{this.getGain(crop)};
   }

   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return 0;
   }

   public boolean onLeftClick(ICropTile paramICropTile, Player paramPlayer) {
      return paramICropTile.pick();
   }

   public float dropSeedChance(ICropTile paramICropTile) {
      if (paramICropTile.getCurrentAge() == 0) {
         return 0.0F;
      }

      float f = 0.5F;
      if (paramICropTile.getCurrentAge() == 1) {
         f /= 2.0F;
      }

      for (byte b = 0; b < this.getProperties().tier(); b++) {
         f = (float)(f * 0.8);
      }

      return f;
   }

   public ItemStack getSeeds(ICropTile tile) {
      CropData data = tile.getCrop();
      return tile.generateSeeds(data.getCard(), data.getGrowth(), data.getGain(), data.getResistance(), data.getScan());
   }

   public void onNeighbourChange(ICropTile paramICropTile) {
   }

   public boolean isRedstoneSignalEmitter(ICropTile paramICropTile) {
      return false;
   }

   public int getEmittedRedstoneSignal(ICropTile paramICropTile) {
      return 0;
   }

   public void onBlockDestroyed(ICropTile paramICropTile) {
   }

   public int getEmittedLight(ICropTile paramICropTile) {
      return 0;
   }

   public boolean onEntityCollision(ICropTile paramICropTile, Entity paramEntity) {
      return paramEntity instanceof LivingEntity && paramEntity.isSprinting();
   }

   public void tick(ICropTile paramICropTile) {
   }

   public boolean isWeed(ICropTile tile) {
      return tile.getCurrentAge() >= 1 && (tile.getCrop().getCard() == IRCrops.weed || tile.getCrop().getGrowth() >= 24);
   }

   public Level getWorld(ICropTile paramICropTile) {
      return paramICropTile.getWorldObj();
   }

   public abstract List<ResourceLocation> getTexturesLocation();
}
