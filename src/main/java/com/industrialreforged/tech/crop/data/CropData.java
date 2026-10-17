package com.industrialreforged.tech.crop.data;

import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.crop.IRCrops;
import com.industrialreforged.tech.crop.cropcard.CropEmpty;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class CropData {
   public static final CropData EMPTY = new CropData(CropEmpty.EMPTY, (byte)-1, (byte)-1, (byte)-1, (byte)4);
   private final CropCard card;
   private byte growth;
   private byte gain;
   private byte resistance;
   private byte scan;

   public CropData(Block block) {
      this.card = normalize(IRCrops.INSTANCE.getCropCard(block));
   }

   public CropData(CropCard card, byte growth, byte gain, byte resistance, byte scan) {
      this.card = normalize(card);
      this.growth = growth;
      this.gain = gain;
      this.resistance = resistance;
      this.scan = scan;
   }

   public boolean isEmpty() {
      return this.card == null || this == EMPTY;
   }

   @Nullable
   public CropCard getCard() {
      return this.card;
   }

   private static CropCard normalize(CropCard card) {
      return card != null && !card.isEmpty() ? card : null;
   }

   public void setGrowth(byte value) {
      this.growth = value;
   }

   public byte getGrowth() {
      return this.growth;
   }

   public void setGain(byte value) {
      this.gain = value;
   }

   public byte getGain() {
      return this.gain;
   }

   public void setResistance(byte value) {
      this.resistance = value;
   }

   public byte getResistance() {
      return this.resistance;
   }

   public void setScan(byte value) {
      this.scan = value;
   }

   public byte getScan() {
      return this.scan;
   }

   public void copy(CropData data) {
      this.growth = data.getGrowth();
      this.gain = data.getGain();
      this.resistance = data.getResistance();
   }

   public void reset() {
      this.growth = 0;
      this.gain = 0;
      this.resistance = 0;
      this.scan = 0;
   }

   public void save(CompoundTag compound) {
      CompoundTag crop = new CompoundTag();
      crop.putByte("growth", this.growth);
      crop.putByte("gain", this.gain);
      crop.putByte("resistance", this.resistance);
      crop.putByte("scan", this.scan);
      compound.put("crop", crop);
   }

   public void load(CompoundTag compound) {
      if (compound.contains("statGrowth")) {
         this.growth = compound.getByte("statGrowth");
         this.gain = compound.getByte("statGain");
         this.resistance = compound.getByte("statResistance");
         this.scan = compound.getByte("scanLevel");
      } else {
         CompoundTag crop = compound.getCompound("crop");
         this.growth = crop.getByte("growth");
         this.gain = crop.getByte("gain");
         this.resistance = crop.getByte("resistance");
         this.scan = crop.getByte("scan");
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CropData data = (CropData)o;
         return this.growth == data.growth
            && this.gain == data.gain
            && this.resistance == data.resistance
            && this.scan == data.scan
            && Objects.equals(this.card, data.card);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.card, this.growth, this.gain, this.resistance, this.scan);
   }
}
