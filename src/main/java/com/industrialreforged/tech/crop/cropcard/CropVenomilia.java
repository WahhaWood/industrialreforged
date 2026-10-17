package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.IRCropCard;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropVenomilia extends IRCropCard {
   public CropVenomilia(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.VENOMILIA_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "raGan";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(3, 3, 1, 3, 3, 3);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Purple", "Flower", "Tulip", "Poison"};
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() <= 3 && paramICropTile.getLightLevel() >= 12 || paramICropTile.getCurrentAge() == 4;
   }

   @Override
   public boolean canBeHarvested(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() >= 3;
   }

   @Override
   public int getOptimalHarvestAge(ICropTile paramICropTile) {
      return 3;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return (crop == null ? !this.random.nextBoolean() : crop.getCurrentAge() != 4)
         ? (crop != null && crop.getCurrentAge() < 3 ? null : new ItemStack(Items.PURPLE_DYE))
         : new ItemStack((ItemLike)IRItems.TOXIC_DUST.get());
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return 2;
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() >= 2 ? 600 : 400;
   }

   @Override
   public boolean onRightClick(ICropTile paramICropTile, Player paramPlayer) {
      if (!paramPlayer.isShiftKeyDown()) {
         this.onEntityCollision(paramICropTile, paramPlayer);
      }

      return paramICropTile.performManualHarvest();
   }

   @Override
   public boolean onLeftClick(ICropTile paramICropTile, Player paramPlayer) {
      if (!paramPlayer.isShiftKeyDown()) {
         this.onEntityCollision(paramICropTile, paramPlayer);
      }

      return paramICropTile.pick();
   }

   @Override
   public boolean onEntityCollision(ICropTile paramICropTile, Entity paramEntity) {
      if (paramICropTile.getCurrentAge() == 4 && paramEntity instanceof LivingEntity) {
         if (paramEntity instanceof Player && paramEntity.isShiftKeyDown() && IR.random.nextInt(50) != 0) {
            return super.onEntityCollision(paramICropTile, paramEntity);
         }

         ((LivingEntity)paramEntity).addEffect(new MobEffectInstance(MobEffects.POISON, (IR.random.nextInt(10) + 5) * 20, 0));
         paramICropTile.setCurrentAge(3);
         paramICropTile.updateState();
      }

      return super.onEntityCollision(paramICropTile, paramEntity);
   }

   @Override
   public boolean isWeed(ICropTile tile) {
      return tile.getCurrentAge() == 4 && tile.getCrop().getGrowth() >= 8;
   }
}
