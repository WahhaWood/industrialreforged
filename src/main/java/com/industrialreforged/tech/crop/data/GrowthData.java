package com.industrialreforged.tech.crop.data;

import net.minecraft.nbt.CompoundTag;

public class GrowthData {
   private byte age;
   private short points;
   private CompoundTag custom = new CompoundTag();

   public void setAge(byte value) {
      this.age = value;
   }

   public byte getAge() {
      return this.age;
   }

   public void setPoints(short value) {
      this.points = value;
   }

   public short getPoints() {
      return this.points;
   }

   public CompoundTag getCustom() {
      return this.custom;
   }

   public void copy(GrowthData data) {
      this.age = data.getAge();
      this.points = data.getPoints();
      this.custom = data.getCustom();
   }

   public void reset() {
      this.age = 0;
      this.points = 0;
      this.custom = new CompoundTag();
   }

   public void save(CompoundTag compound) {
      CompoundTag growth = new CompoundTag();
      growth.putByte("age", this.age);
      growth.putShort("points", this.points);
      growth.put("custom", this.custom);
      compound.put("growth", growth);
   }

   public void load(CompoundTag compound) {
      if (compound.contains("currentAge")) {
         this.age = compound.getByte("currentAge");
         this.points = compound.getShort("growthPoints");
         this.custom = compound.getCompound("customData");
      } else {
         CompoundTag growth = compound.getCompound("growth");
         this.age = growth.getByte("age");
         this.points = growth.getShort("points");
         this.custom = growth.getCompound("custom");
      }
   }
}
