package com.industrialreforged.tech.crop.cropcard;

import com.industrialreforged.tech.api.crops.CropProperties;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.crop.CropBase;
import com.industrialreforged.tech.crop.IRCropBlock;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropBaseMetalCommon extends CropBase {
   protected final String[] cropAttributes;
   protected final Supplier<IRCropBlock> cropBlock;
   protected final Collection<TagKey<Block>> cropRootsRequirement;
   protected final int farmingChange;

   public CropBaseMetalCommon(
      ICropType cropType,
      Supplier<IRCropBlock> blockSupplier,
      String[] cropAttributes,
      Collection<TagKey<Block>> cropRootsRequirement,
      ItemLike cropDrop,
      int farmingChange
   ) {
      super(cropType, cropDrop);
      this.cropBlock = blockSupplier;
      this.cropAttributes = cropAttributes;
      this.cropRootsRequirement = cropRootsRequirement;
      this.farmingChange = farmingChange;
   }

   @Override
   public Block getCropBlock() {
      return (Block)this.cropBlock.get();
   }

   @Override
   public CropProperties getProperties() {
      return new CropProperties(6, 2, 0, 0, 1, 0);
   }

   @Override
   public String[] getAttributes() {
      return this.cropAttributes;
   }

   @Override
   public boolean canGrow(ICropTile paramICropTile) {
      int i = this.getMaxAge() - 1;
      if (paramICropTile.getCurrentAge() < i) {
         return true;
      }

      if (paramICropTile.getCurrentAge() == i) {
         if (this.cropRootsRequirement == null || this.cropRootsRequirement.isEmpty()) {
            return true;
         }

         for (TagKey<Block> tagKey : this.cropRootsRequirement) {
            if (paramICropTile.isBlockBelow(tagKey)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public int getRootsLength(ICropTile paramICropTile) {
      return 5;
   }

   @NotNull
   @Override
   public ItemStack getGain(@Nullable ICropTile crop) {
      if (crop == null) {
         return this.random.nextInt(this.farmingChange) < 5 ? new ItemStack(this.cropDrop) : ItemStack.EMPTY;
      } else {
         return new ItemStack(this.cropDrop);
      }
   }

   @Override
   public double dropGainChance() {
      return super.dropGainChance() / 2.0;
   }

   @Override
   public int getGrowthDuration(ICropTile paramICropTile) {
      return paramICropTile.getCurrentAge() == this.getMaxAge() - 1 ? 2000 : 800;
   }

   @Override
   public int getAgeAfterHarvest(ICropTile paramICropTile) {
      return 1;
   }
}
