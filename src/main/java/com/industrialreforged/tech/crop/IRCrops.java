package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.api.crops.BaseSeed;
import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.core.ref.IRBlockTags;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.cropcard.CropBaseMetalCommon;
import com.industrialreforged.tech.crop.cropcard.CropBaseMetalUncommon;
import com.industrialreforged.tech.crop.cropcard.CropBaseMushroom;
import com.industrialreforged.tech.crop.cropcard.CropBaseSapling;
import com.industrialreforged.tech.crop.cropcard.CropBeetroots;
import com.industrialreforged.tech.crop.cropcard.CropCarrots;
import com.industrialreforged.tech.crop.cropcard.CropCocoa;
import com.industrialreforged.tech.crop.cropcard.CropCoffee;
import com.industrialreforged.tech.crop.cropcard.CropColorFlower;
import com.industrialreforged.tech.crop.cropcard.CropEating;
import com.industrialreforged.tech.crop.cropcard.CropEmpty;
import com.industrialreforged.tech.crop.cropcard.CropFlax;
import com.industrialreforged.tech.crop.cropcard.CropHops;
import com.industrialreforged.tech.crop.cropcard.CropMelon;
import com.industrialreforged.tech.crop.cropcard.CropNetherWart;
import com.industrialreforged.tech.crop.cropcard.CropPotato;
import com.industrialreforged.tech.crop.cropcard.CropPumpkin;
import com.industrialreforged.tech.crop.cropcard.CropRedWheat;
import com.industrialreforged.tech.crop.cropcard.CropReed;
import com.industrialreforged.tech.crop.cropcard.CropStickyReed;
import com.industrialreforged.tech.crop.cropcard.CropTerraWart;
import com.industrialreforged.tech.crop.cropcard.CropVenomilia;
import com.industrialreforged.tech.crop.cropcard.CropWeed;
import com.industrialreforged.tech.crop.cropcard.CropWheat;
import com.industrialreforged.tech.crop.data.CropData;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags.Biomes;

public class IRCrops {
   public static final IRCrops INSTANCE = new IRCrops();
   public static CropCard weed;
   private final Map<String, Map<String, CropCard>> cropMap = new HashMap<>();
   private final Object2IntMap<TagKey<Biome>> humidityBiomeTypeBonus = new Object2IntArrayMap();
   private final Object2IntMap<TagKey<Biome>> nutrientBiomeTypeBonus = new Object2IntArrayMap();
   private final Map<ItemStack, BaseSeed> baseSeeds = new HashMap<>();
   public static CropCard cropWheat = new CropWheat(IRCropType.wheat);
   public static CropCard cropPumpkin = new CropPumpkin(IRCropType.pumpkin);
   public static CropCard cropMelon = new CropMelon(IRCropType.melon);
   public static CropCard cropYellowFlower = new CropColorFlower(
      IRCropType.dandelion, IRBlocks.DANDELION_CROP, new String[]{"Yellow", "Flower"}, DyeColor.YELLOW
   );
   public static CropCard cropRedFlower = new CropColorFlower(IRCropType.poppy, IRBlocks.POPPY_CROP, new String[]{"Red", "Flower", "Rose"}, DyeColor.RED);
   public static CropCard cropBlackFlower = new CropColorFlower(
      IRCropType.blackthorn, IRBlocks.BLACKTHORN_CROP, new String[]{"Black", "Flower", "Rose"}, DyeColor.BLACK
   );
   public static CropCard cropPurpleFlower = new CropColorFlower(
      IRCropType.tulip, IRBlocks.TULIP_CROP, new String[]{"Purple", "Flower", "Tulip"}, DyeColor.PURPLE
   );
   public static CropCard cropBlueFlower = new CropColorFlower(IRCropType.cyazint, IRBlocks.CYAZINT_CROP, new String[]{"Blue", "Flower"}, DyeColor.CYAN);
   public static CropCard cropVenomilia = new CropVenomilia(IRCropType.venomilia);
   public static CropCard cropReed = new CropReed(IRCropType.reed);
   public static CropCard cropStickyReed = new CropStickyReed(IRCropType.stickyReed);
   public static CropCard cropCocoa = new CropCocoa(IRCropType.cocoa);
   public static CropCard cropFlax = new CropFlax(IRCropType.flax);
   public static CropCard cropRedMushroom = new CropBaseMushroom(
      IRCropType.redMushroom, IRBlocks.RED_MUSHROOM_CROP, new String[]{"Red", "Food", "Mushroom"}, Blocks.RED_MUSHROOM
   );
   public static CropCard cropBrownMushroom = new CropBaseMushroom(
      IRCropType.brownMushroom, IRBlocks.BROWN_MUSHROOM_CROP, new String[]{"Brown", "Food", "Mushroom"}, Blocks.BROWN_MUSHROOM
   );
   public static CropCard cropNetherWart = new CropNetherWart(IRCropType.netherWart);
   public static CropCard cropTerraWart = new CropTerraWart(IRCropType.terraWart);
   public static CropCard cropOakSapling = new CropBaseSapling(IRCropType.oakSapling, IRBlocks.OAK_SAPLING_CROP, "acorns", Blocks.OAK_LOG, Blocks.OAK_SAPLING);
   public static CropCard cropSpruceSapling = new CropBaseSapling(
      IRCropType.spruceSapling, IRBlocks.SPRUCE_SAPLING_CROP, "pine_cones", Blocks.SPRUCE_LOG, Blocks.SPRUCE_SAPLING
   );
   public static CropCard cropBirchSapling = new CropBaseSapling(
      IRCropType.birchSapling, IRBlocks.BIRCH_SAPLING_CROP, "catkins", Blocks.BIRCH_LOG, Blocks.BIRCH_SAPLING
   );
   public static CropCard cropJungleSapling = new CropBaseSapling(
      IRCropType.jungleSapling, IRBlocks.JUNGLE_SAPLING_CROP, "seedling", Blocks.JUNGLE_LOG, Blocks.JUNGLE_SAPLING
   );
   public static CropCard cropAcaciaSapling = new CropBaseSapling(
      IRCropType.acaciaSapling, IRBlocks.ACACIA_SAPLING_CROP, "seedling", Blocks.ACACIA_LOG, Blocks.ACACIA_SAPLING
   );
   public static CropCard cropDarkOakSapling = new CropBaseSapling(
      IRCropType.darkOakSapling, IRBlocks.DARK_OAK_SAPLING_CROP, "acorns", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_SAPLING
   );
   public static CropCard cropFerru = new CropBaseMetalCommon(
      IRCropType.ferru,
      IRBlocks.FERRU_CROP,
      new String[]{"Gray", "Leaves", "Metal"},
      Arrays.asList(BlockTags.IRON_ORES, net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS_IRON),
      IRItems.SMALL_IRON_DUST,
      500
   );
   public static CropCard cropCyprium = new CropBaseMetalCommon(
      IRCropType.cyprium,
      IRBlocks.CYPRIUM_CROP,
      new String[]{"Orange", "Leaves", "Metal"},
      Arrays.asList(BlockTags.COPPER_ORES, net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS_COPPER),
      IRItems.SMALL_COPPER_DUST,
      500
   );
   public static CropCard cropStagnium = new CropBaseMetalCommon(
      IRCropType.stagnium,
      IRBlocks.STAGNIUM_CROP,
      new String[]{"Shiny", "Leaves", "Metal"},
      Arrays.asList(IRBlockTags.ORES_TIN, IRBlockTags.STORAGE_BLOCKS_TIN),
      IRItems.SMALL_TIN_DUST,
      500
   );
   public static CropCard cropPlumbiscus = new CropBaseMetalCommon(
      IRCropType.plumbiscus,
      IRBlocks.PLUMBISCUS_CROP,
      new String[]{"Dense", "Leaves", "Metal"},
      Arrays.asList(IRBlockTags.ORES_LEAD, IRBlockTags.STORAGE_BLOCKS_LEAD),
      IRItems.SMALL_LEAD_DUST,
      2500
   );
   public static CropCard cropAurelia = new CropBaseMetalUncommon(
      IRCropType.aurelia,
      IRBlocks.AURELIA_CROP,
      new String[]{"Gold", "Leaves", "Metal"},
      Arrays.asList(BlockTags.GOLD_ORES, net.neoforged.neoforge.common.Tags.Blocks.STORAGE_BLOCKS_GOLD),
      IRItems.SMALL_GOLD_DUST,
      5000
   );
   public static CropCard cropShining = new CropBaseMetalUncommon(
      IRCropType.shining,
      IRBlocks.SHINING_CROP,
      new String[]{"Silver", "Leaves", "Metal"},
      Arrays.asList(IRBlockTags.ORES_SILVER, IRBlockTags.STORAGE_BLOCKS_SILVER),
      IRItems.SMALL_SILVER_DUST,
      50000
   );
   public static CropCard cropRedWheat = new CropRedWheat(IRCropType.redWheat);
   public static CropCard cropCoffee = new CropCoffee(IRCropType.coffee);
   public static CropCard cropHops = new CropHops(IRCropType.hops);
   public static CropCard cropCarrots = new CropCarrots(IRCropType.carrots);
   public static CropCard cropPotato = new CropPotato(IRCropType.potato);
   public static CropCard cropEatingPlant = new CropEating(IRCropType.eatingPlant);
   public static CropCard cropBeetroots = new CropBeetroots(IRCropType.beetroots);

   public static void init() {
      weed = new CropWeed(IRCropType.weed);
      INSTANCE.addBiomenutrientsBonus(BiomeTags.IS_JUNGLE, 10);
      INSTANCE.addBiomenutrientsBonus(Biomes.IS_SWAMP, 10);
      INSTANCE.addBiomenutrientsBonus(Biomes.IS_MUSHROOM, 5);
      INSTANCE.addBiomenutrientsBonus(BiomeTags.IS_FOREST, 5);
      INSTANCE.addBiomenutrientsBonus(BiomeTags.IS_RIVER, 2);
      INSTANCE.addBiomenutrientsBonus(Biomes.IS_PLAINS, 0);
      INSTANCE.addBiomenutrientsBonus(BiomeTags.IS_SAVANNA, -2);
      INSTANCE.addBiomenutrientsBonus(Biomes.IS_HILL, -5);
      INSTANCE.addBiomenutrientsBonus(BiomeTags.IS_MOUNTAIN, -5);
      INSTANCE.addBiomenutrientsBonus(Biomes.IS_WASTELAND, -8);
      INSTANCE.addBiomenutrientsBonus(BiomeTags.IS_END, -10);
      INSTANCE.addBiomenutrientsBonus(BiomeTags.IS_NETHER, -10);
      INSTANCE.addBiomenutrientsBonus(Biomes.IS_DEAD, -10);
      registerCrops();
      registerBaseSeeds();
   }

   private static void registerCrops() {
      INSTANCE.registerCrop(weed);
      INSTANCE.registerCrop(cropWheat);
      INSTANCE.registerCrop(cropPumpkin);
      INSTANCE.registerCrop(cropMelon);
      INSTANCE.registerCrop(cropYellowFlower);
      INSTANCE.registerCrop(cropRedFlower);
      INSTANCE.registerCrop(cropBlackFlower);
      INSTANCE.registerCrop(cropPurpleFlower);
      INSTANCE.registerCrop(cropBlueFlower);
      INSTANCE.registerCrop(cropVenomilia);
      INSTANCE.registerCrop(cropReed);
      INSTANCE.registerCrop(cropStickyReed);
      INSTANCE.registerCrop(cropCocoa);
      INSTANCE.registerCrop(cropFlax);
      INSTANCE.registerCrop(cropFerru);
      INSTANCE.registerCrop(cropAurelia);
      INSTANCE.registerCrop(cropRedWheat);
      INSTANCE.registerCrop(cropNetherWart);
      INSTANCE.registerCrop(cropTerraWart);
      INSTANCE.registerCrop(cropCoffee);
      INSTANCE.registerCrop(cropHops);
      INSTANCE.registerCrop(cropCarrots);
      INSTANCE.registerCrop(cropPotato);
      INSTANCE.registerCrop(cropRedMushroom);
      INSTANCE.registerCrop(cropBrownMushroom);
      INSTANCE.registerCrop(cropEatingPlant);
      INSTANCE.registerCrop(cropCyprium);
      INSTANCE.registerCrop(cropStagnium);
      INSTANCE.registerCrop(cropPlumbiscus);
      INSTANCE.registerCrop(cropShining);
      INSTANCE.registerCrop(cropBeetroots);
      INSTANCE.registerCrop(cropOakSapling);
      INSTANCE.registerCrop(cropSpruceSapling);
      INSTANCE.registerCrop(cropBirchSapling);
      INSTANCE.registerCrop(cropJungleSapling);
      INSTANCE.registerCrop(cropAcaciaSapling);
      INSTANCE.registerCrop(cropDarkOakSapling);
   }

   private static void registerBaseSeeds() {
      INSTANCE.registerBaseSeed(new ItemStack(Items.WHEAT_SEEDS), cropWheat, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.PUMPKIN_SEEDS), cropPumpkin, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.MELON_SEEDS), cropMelon, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.NETHER_WART), cropNetherWart, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack((ItemLike)IRItems.TERRA_WART.get()), cropTerraWart, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack((ItemLike)IRItems.COFFEE_BEANS.get()), cropCoffee, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.SUGAR_CANE), cropReed, 0, 3, 0, 2);
      INSTANCE.registerBaseSeed(new ItemStack(Items.COCOA_BEANS), cropCocoa, 0, 0, 0, 0);
      INSTANCE.registerBaseSeed(new ItemStack(Items.POPPY, 4), cropRedFlower, 3, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.DANDELION, 4), cropYellowFlower, 3, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.CARROT), cropCarrots, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.POTATO), cropPotato, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.BROWN_MUSHROOM, 4), cropBrownMushroom, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.RED_MUSHROOM, 4), cropRedMushroom, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.CACTUS), cropEatingPlant, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Items.BEETROOT_SEEDS), cropBeetroots, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.OAK_SAPLING), cropOakSapling, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.SPRUCE_SAPLING), cropSpruceSapling, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.BIRCH_SAPLING), cropBirchSapling, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.JUNGLE_SAPLING), cropJungleSapling, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.ACACIA_SAPLING), cropAcaciaSapling, 0, 1, 1, 1);
      INSTANCE.registerBaseSeed(new ItemStack(Blocks.DARK_OAK_SAPLING), cropDarkOakSapling, 0, 1, 1, 1);
   }

   public static void ensureInit() {
   }

   public void addBiomenutrientsBonus(TagKey<Biome> tagKey, int paramInt) {
      this.nutrientBiomeTypeBonus.put(tagKey, paramInt);
   }

   public void addBiomehumidityBonus(TagKey<Biome> tagKey, int paramInt) {
      this.humidityBiomeTypeBonus.put(tagKey, paramInt);
   }

   public int getHumidityBiomeBonus(Holder<Biome> holder) {
      int result = 0;
      ObjectIterator var3 = this.humidityBiomeTypeBonus.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<TagKey<Biome>> entry = (Entry<TagKey<Biome>>)var3.next();
         if (holder.is((TagKey)entry.getKey())) {
            result = Math.max(result, entry.getIntValue());
         }
      }

      return result;
   }

   public int getNutrientBiomeBonus(Holder<Biome> holder) {
      int result = 0;
      ObjectIterator var3 = this.nutrientBiomeTypeBonus.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry<TagKey<Biome>> entry = (Entry<TagKey<Biome>>)var3.next();
         if (holder.is((TagKey)entry.getKey())) {
            result = Math.max(result, entry.getIntValue());
         }
      }

      return result;
   }

   public CropCard getCropCard(String id) {
      if (id != null && !id.isBlank() && !id.equals("industrialreforged:empty")) {
         String[] split = id.split(":");
         return split.length == 1 ? this.getCropCard("industrialreforged", split[0]) : this.getCropCard(split[0], split[1]);
      } else {
         return CropEmpty.EMPTY;
      }
   }

   public CropCard getCropCard(String owner, String name) {
      Map<String, CropCard> map = this.cropMap.get(owner);
      return map == null ? CropEmpty.EMPTY : map.getOrDefault(name, CropEmpty.EMPTY);
   }

   public CropCard getCropCard(ItemStack itemStack) {
      ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
      if (itemStack.is(ItemTags.SAPLINGS) && resourceLocation.getNamespace().equals("minecraft")) {
         return this.getCropCard("industrialreforged", resourceLocation.getPath());
      }

      CropData cropData = (CropData)itemStack.get(IRDataComponents.CROP);
      return cropData != null ? cropData.getCard() : CropEmpty.EMPTY;
   }

   public CropCard getCropCard(Block block) {
      ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);
      String str1 = resourceLocation.getNamespace();
      String str2 = resourceLocation.getPath().replace("_crop", "");
      return this.getCropCard(str1, str2);
   }

   public Stream<CropCard> getCrops() {
      return this.cropMap.values().stream().flatMap(map -> map.values().stream());
   }

   public void registerCrop(CropCard paramCropCard) {
      String str1 = paramCropCard.getOwner();
      String str2 = paramCropCard.getName();
      if (!str1.equals(str1.toLowerCase(Locale.ENGLISH))) {
         throw new IllegalArgumentException("The crop owner=" + str1 + " id=" + str2 + " uses a non-lower case owner");
      }

      Map<String, CropCard> map = this.cropMap.computeIfAbsent(str1, k -> new HashMap<>());
      CropCard cropCard = map.put(str2, paramCropCard);
      if (cropCard != null) {
         throw new IllegalArgumentException("The crop owner=" + str1 + " id=" + str2 + " uses a non-unique owner+id pair");
      }
   }

   public boolean registerBaseSeed(ItemStack paramItemStack, CropCard paramCropCard, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
      for (ItemStack itemStack : this.baseSeeds.keySet()) {
         if (itemStack.getItem() == paramItemStack.getItem() && itemStack.getDamageValue() == paramItemStack.getDamageValue()) {
            return false;
         }
      }

      this.baseSeeds.put(paramItemStack, new BaseSeed(paramCropCard, paramInt1, paramInt2, paramInt3, paramInt4));
      return true;
   }

   public BaseSeed getBaseSeed(ItemStack paramItemStack) {
      if (paramItemStack == null) {
         return null;
      }

      for (java.util.Map.Entry<ItemStack, BaseSeed> entry : this.baseSeeds.entrySet()) {
         if (entry.getKey().getItem() == paramItemStack.getItem()) {
            return entry.getValue();
         }
      }

      return null;
   }
}
