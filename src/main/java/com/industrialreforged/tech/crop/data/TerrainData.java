package com.industrialreforged.tech.crop.data;

import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.crop.IRCrops;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainData {
   private final CropBlockEntity entity;
   private byte humidity = -1;
   private byte nutrients = -1;
   private byte airQuality = -1;

   public TerrainData(CropBlockEntity entity) {
      this.entity = entity;
   }

   public void setHumidity(byte value) {
      this.humidity = value;
   }

   public byte getHumidity() {
      if (this.humidity == -1) {
         this.updateHumidity();
      }

      return this.humidity;
   }

   public void setNutrients(byte value) {
      this.nutrients = value;
   }

   public byte getNutrients() {
      if (this.nutrients == -1) {
         this.updateNutrients();
      }

      return this.nutrients;
   }

   public void setAirQuality(byte value) {
      this.airQuality = value;
   }

   public byte getAirQuality() {
      if (this.airQuality == -1) {
         this.updateAirQuality();
      }

      return this.airQuality;
   }

   public void copy(TerrainData data) {
      this.humidity = data.getHumidity();
      this.nutrients = data.getNutrients();
      this.airQuality = data.getAirQuality();
   }

   public void reset() {
      this.humidity = -1;
      this.nutrients = -1;
      this.airQuality = -1;
   }

   public void save(CompoundTag compound) {
      CompoundTag terrain = new CompoundTag();
      terrain.putByte("humidity", this.humidity);
      terrain.putByte("nutrients", this.nutrients);
      terrain.putByte("airQuality", this.airQuality);
      compound.put("terrain", terrain);
   }

   public void load(CompoundTag compound) {
      if (compound.contains("terrainHumidity")) {
         this.humidity = compound.getByte("terrainHumidity");
         this.nutrients = compound.getByte("terrainNutrients");
         this.airQuality = compound.getByte("terrainAirQuality");
      } else {
         CompoundTag terrain = compound.getCompound("terrain");
         this.humidity = terrain.getByte("humidity");
         this.nutrients = terrain.getByte("nutrients");
         this.airQuality = terrain.getByte("airQuality");
      }
   }

   public void updateAirQuality() {
      Level level = this.entity.getLevel();
      BlockPos pos = this.entity.getBlockPos();
      int i = 0;
      int j = (int)Math.floor((pos.getY() - 40) / 15.0);
      if (j > 2) {
         j = 2;
      }

      if (j < 0) {
         j = 0;
      }

      i += j;
      byte b = 9;

      for (int k = pos.getX() - 1; k < pos.getX() + 1 && b > 0; k++) {
         for (int m = pos.getZ() - 1; m < pos.getZ() + 1 && b > 0; m++) {
            BlockPos blockPos = new BlockPos(k, pos.getY(), m);
            if (level.getBlockState(blockPos).isCollisionShapeFullBlock(level, blockPos)
               || level.getBlockEntity(new BlockPos(k, pos.getY(), m)) instanceof CropBlockEntity) {
               b--;
            }
         }
      }

      i += b / 2;
      if (level.canSeeSky(pos.above())) {
         i += 4;
      }

      this.setAirQuality((byte)i);
   }

   public void updateHumidity() {
      Level level = this.entity.getLevel();
      BlockPos pos = this.entity.getBlockPos();
      int i = IRCrops.INSTANCE.getHumidityBiomeBonus(level.getBiome(pos));
      BlockState state = level.getBlockState(pos.below());
      if (state.getBlock() == Blocks.FARMLAND && (Integer)state.getValue(FarmBlock.MOISTURE) >= 7) {
         i += 2;
      }

      int storage = this.entity.getStorage().getWater();
      if (storage >= 5) {
         i += 2;
      }

      i += (storage + 24) / 25;
      this.setHumidity((byte)i);
   }

   public void updateNutrients() {
      Level level = this.entity.getLevel();
      BlockPos pos = this.entity.getBlockPos();
      int i = IRCrops.INSTANCE.getNutrientBiomeBonus(level.getBiome(pos));

      for (byte b = 1; b < 5 && level.getBlockState(pos.below(b)).getBlock() == Blocks.DIRT; b++) {
         i++;
      }

      i += (this.entity.getStorage().getNutrients() + 19) / 20;
      this.setNutrients((byte)i);
   }
}
