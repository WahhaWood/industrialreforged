package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.crop.CropBase;
import com.industrialreforged.tech.crop.IRCropBlock;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropBaseMushroom extends CropBase {
   protected final String[] attributes;
   protected final Supplier<IRCropBlock> cropBlock;

   public CropBaseMushroom(ICropType cropType, Supplier<IRCropBlock> blockSupplier, String[] attributes, ItemLike dropCrop) {
      super(cropType, dropCrop);
      this.attributes = attributes;
      this.cropBlock = blockSupplier;
   }

   @Override
   public Block getCropBlock() {
      return (Block)this.cropBlock.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(2, 0, 4, 0, 0, 4);
   }

   @Override
   public String[] getAttributes() {
      return this.attributes;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      return new ItemStack(this.cropDrop);
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return 200;
   }
}
