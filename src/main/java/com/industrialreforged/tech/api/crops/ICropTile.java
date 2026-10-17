package com.industrialreforged.tech.api.crops;

import com.industrialreforged.tech.api.info.ILocatable;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.crop.data.GrowthData;
import com.industrialreforged.tech.crop.data.StorageData;
import com.industrialreforged.tech.crop.data.TerrainData;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public interface ICropTile extends ILocatable {
   CropData getCrop();

   StorageData getStorage();

   TerrainData getTerrain();

   GrowthData getGrowth();

   void setCrop(CropCard var1);

   int getCurrentAge();

   void setCurrentAge(int var1);

   boolean isCrossingBase();

   void setCrossingBase(boolean var1);

   int getLightLevel();

   boolean pick();

   boolean performManualHarvest();

   List<ItemStack> performHarvest();

   void reset();

   void updateState();

   boolean isBlockBelow(Block var1);

   boolean isBlockBelow(TagKey<Block> var1);

   ItemStack generateSeeds(CropCard var1, int var2, int var3, int var4, int var5);
}
