package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.crop.CropBase;
import com.industrialreforged.tech.crop.IRCropBlock;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropBaseSapling extends CropBase {
   protected final Supplier<IRCropBlock> cropBlock;
   protected final String saplingName;
   protected final ItemLike cropSapling;

   public CropBaseSapling(ICropType cropType, Supplier<IRCropBlock> blockSupplier, String paramString, ItemLike paramItemStack1, ItemLike paramItemStack2) {
      super(cropType, paramItemStack1);
      this.cropBlock = blockSupplier;
      this.saplingName = "crop.industrialreforged." + paramString;
      this.cropSapling = paramItemStack2;
   }

   @Override
   public Block getCropBlock() {
      return (Block)this.cropBlock.get();
   }

   @Override
   public String getSeedType() {
      return this.saplingName;
   }

   @Override
   public String getDiscoveredBy() {
      return "Speiger";
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(3, 1, 0, 4, 4, 0);
   }

   @Override
   public String[] getAttributes() {
      return new String[]{"Leaves", "Sapling", "Green"};
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() < this.getMaxAge() && paramICropTile.getLightLevel() >= 9;
   }

   @NotNull
   @Override
   public ItemStack[] getGains(@Nullable ICropTile crop) {
      ArrayList<ItemStack> arrayList = new ArrayList<>();
      arrayList.add(new ItemStack(this.cropDrop));
      if (IR.random.nextInt(100) >= 75) {
         arrayList.add(new ItemStack(this.cropSapling));
      }

      if (this.getName().equalsIgnoreCase("oak_sapling") && IR.random.nextInt(100) >= 75) {
         arrayList.add(new ItemStack(Items.APPLE));
      }

      return arrayList.toArray(new ItemStack[0]);
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() >= this.getMaxAge() - 1 ? 150 : 600;
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return this.getMaxAge() - 1;
   }
}
