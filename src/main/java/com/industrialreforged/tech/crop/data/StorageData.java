package com.industrialreforged.tech.crop.data;

import net.minecraft.nbt.CompoundTag;

public class StorageData {
   private short nutrients;
   private short water;
   private short weedEx;

   public void setNutrients(short value) {
      this.nutrients = value;
   }

   public short getNutrients() {
      return this.nutrients;
   }

   public void setWater(short value) {
      this.water = value;
   }

   public short getWater() {
      return this.water;
   }

   public void setWeedEX(short value) {
      this.weedEx = value;
   }

   public short getWeedEx() {
      return this.weedEx;
   }

   public void copy(StorageData data) {
      this.nutrients = data.getNutrients();
      this.water = data.getWater();
      this.weedEx = data.getWeedEx();
   }

   public void reset() {
   }

   public void save(CompoundTag compound) {
      CompoundTag storage = new CompoundTag();
      storage.putShort("nutrients", this.nutrients);
      storage.putShort("water", this.water);
      storage.putShort("weedEx", this.weedEx);
      compound.put("storage", storage);
   }

   public void load(CompoundTag compound) {
      if (compound.contains("storageNutrients")) {
         this.nutrients = compound.getShort("storageNutrients");
         this.water = compound.getShort("storageWater");
         this.weedEx = compound.getShort("storageWeedEX");
      } else {
         CompoundTag storage = compound.getCompound("storage");
         this.nutrients = storage.getShort("nutrients");
         this.water = storage.getShort("water");
         this.weedEx = storage.getShort("weedEx");
      }
   }
}
