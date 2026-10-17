package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.crop.IRCropBlock;
import com.industrialreforged.tech.crop.IRCropCard;
import java.util.function.Supplier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropColorFlower extends IRCropCard {
   public String name;
   public Supplier<IRCropBlock> cropBlock;
   public String[] attributes;
   public DyeColor color;

   public CropColorFlower(ICropType cropType, Supplier<IRCropBlock> blockSupplier, String[] attributes, DyeColor color) {
      super(cropType);
      this.name = cropType.getName();
      this.cropBlock = blockSupplier;
      this.attributes = attributes;
      this.color = color;
   }

   @Override
   public Block getCropBlock() {
      return (Block)this.cropBlock.get();
   }

   @Override
   public String getDiscoveredBy() {
      return !this.name.equals("dandelion") && !this.name.equals("rose") ? "Alblaka" : "Notch";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(2, 1, 1, 0, 5, 1);
   }

   @Override
   public String[] getAttributes() {
      return this.attributes;
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() <= this.getMaxAge() - 1 && paramICropTile.getLightLevel() >= 12;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack(DyeItem.byColor(this.color));
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return this.getMaxAge() - 1;
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() - 1 ? 600 : 400;
   }
}
