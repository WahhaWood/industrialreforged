package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.api.item.IMetalArmor;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDamageSource;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.crop.IRCropCard;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags.Biomes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropEating extends IRCropCard {
   @Override
   public Block getCropBlock() {
      return (Block)IRBlocks.EATING_PLANT_CROP.get();
   }

   @Override
   public String getDiscoveredBy() {
      return "Hasudako";
   }

   public CropEating(ICropType paramICropType) {
      super(paramICropType);
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(6, 1, 1, 3, 1, 4);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Bad", "Food"};
   }

   @Override
   public void tick(ICropTile tile) {
      if (tile.getCurrentAge() != 0) {
         BlockPos blockPos = tile.getPosition();
         double d1 = blockPos.getX() + 0.5;
         double d2 = blockPos.getY() + 0.5;
         double d3 = blockPos.getZ() + 0.5;
         if (tile.getGrowth().getCustom().getBoolean("eaten")) {
            StackUtil.dropAsEntity(tile.getWorldObj(), blockPos, new ItemStack(Items.ROTTEN_FLESH));
            tile.getGrowth().getCustom().putBoolean("eaten", false);
         }

         List<LivingEntity> list = tile.getWorldObj()
            .getEntitiesOfClass(LivingEntity.class, new AABB(d1 - 1.0, blockPos.getY(), d3 - 1.0, d1 + 1.0, blockPos.getY() + 1.0 + 1.0, d3 + 1.0), null);
         if (!list.isEmpty()) {
            Collections.shuffle(list);

            for (LivingEntity livingEntity : list) {
               if (!(livingEntity instanceof Player) || !((Player)livingEntity).getAbilities().instabuild) {
                  livingEntity.setDeltaMovement(
                     (d1 - livingEntity.getX()) * 0.5, Math.min(livingEntity.getDeltaMovement().y(), -0.05), (d3 - livingEntity.getZ()) * 0.5
                  );
                  livingEntity.hurt(IRDamageSource.cropEating(livingEntity.level()), (tile.getCurrentAge() + 1) * 2.0F);
                  if (!hasMetalAromor(livingEntity)) {
                     livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 64, 50));
                     livingEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 64, 0));
                     livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 64, 0));
                  }

                  if (this.canGrow(tile)) {
                     tile.getGrowth().setPoints((short)(tile.getGrowth().getPoints() + 100));
                  }

                  tile.getWorldObj().playSound(null, d1, d2, d3, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, IR.random.nextFloat() * 0.1F + 0.9F);
                  tile.getGrowth().getCustom().putBoolean("eaten", true);
               }
            }
         }
      }
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < 2
         ? paramICropTile.getLightLevel() > 10
         : paramICropTile.isBlockBelow(Blocks.LAVA) && paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() > 10;
   }

   @Override
   public int getRootsLength(ICropTile paramICropTile) {
      return 5;
   }

   @Override
   public int getOptimalHarvestAge(ICropTile paramICropTile) {
      return this.getMaxAge() - 2;
   }

   @Override
   public boolean canBeHarvested(ICropTile crop) {
      return crop.getCurrentAge() >= this.getOptimalHarvestAge(crop) && crop.getCurrentAge() < this.getMaxAge();
   }

   @Override
   public int getGrowthDuration(ICropTile tile) {
      float f = 1.0F;
      BlockPos blockPos = tile.getPosition();
      Level level = tile.getWorldObj();
      Holder<Biome> holder = level.getBiome(blockPos);
      if (holder.is(Biomes.IS_SWAMP) || holder.is(BiomeTags.IS_MOUNTAIN)) {
         f /= 1.5F;
      }

      f /= 1.0F + tile.getTerrain().getAirQuality() / 10.0F;
      return (int)(super.getGrowthDuration(tile) * f);
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return crop != null && !this.canBeHarvested(crop) ? null : new ItemStack(Blocks.CACTUS);
   }

   private static boolean hasMetalAromor(LivingEntity paramLivingEntity) {
      if (paramLivingEntity instanceof Player player) {
         for (ItemStack itemStack : player.getInventory().armor) {
            if (itemStack != null && itemStack.getItem() instanceof IMetalArmor armor && armor.isMetalArmor(itemStack, player)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }
}
