package com.industrialreforged.tech.crop;

import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.api.crops.ICropType;
import com.industrialreforged.tech.core.ref.IRBlocks;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public enum IRCropType implements StringRepresentable, ICropType {
   none("none", null, 0),
   weed("weed", IRBlocks.WEED_CROP, 4),
   wheat("wheat", IRBlocks.WHEAT_CROP, 7),
   carrots("carrots", IRBlocks.CARROTS_CROP, 3),
   potato("potato", IRBlocks.POTATO_CROP, 3),
   beetroots("beetroots", IRBlocks.BEETROOTS_CROP, 3),
   pumpkin("pumpkin", IRBlocks.PUMPKIN_CROP, 3),
   melon("melon", IRBlocks.MELON_CROP, 3),
   dandelion("dandelion", IRBlocks.DANDELION_CROP, 3),
   poppy("poppy", IRBlocks.POPPY_CROP, 3),
   blackthorn("blackthorn", IRBlocks.BLACKTHORN_CROP, 3),
   tulip("tulip", IRBlocks.TULIP_CROP, 3),
   cyazint("cyazint", IRBlocks.CYAZINT_CROP, 3),
   venomilia("venomilia", IRBlocks.VENOMILIA_CROP, 5),
   reed("reed", IRBlocks.REED_CROP, 2),
   stickyReed("sticky_reed", IRBlocks.STICKY_REED_CROP, 3),
   cocoa("cocoa", IRBlocks.COCOA_CROP, 3),
   flax("flax", IRBlocks.FLAX_CROP, 3),
   redMushroom("red_mushroom", IRBlocks.RED_MUSHROOM_CROP, 2),
   brownMushroom("brown_mushroom", IRBlocks.BROWN_MUSHROOM_CROP, 2),
   netherWart("nether_wart", IRBlocks.NETHER_WART_CROP, 2),
   terraWart("terra_wart", IRBlocks.TERRA_WART_CROP, 2),
   oakSapling("oak_sapling", IRBlocks.OAK_SAPLING_CROP, 4),
   spruceSapling("spruce_sapling", IRBlocks.SPRUCE_SAPLING_CROP, 4),
   birchSapling("birch_sapling", IRBlocks.BIRCH_SAPLING_CROP, 4),
   jungleSapling("jungle_sapling", IRBlocks.JUNGLE_SAPLING_CROP, 4),
   acaciaSapling("acacia_sapling", IRBlocks.ACACIA_SAPLING_CROP, 4),
   darkOakSapling("dark_oak_sapling", IRBlocks.DARK_OAK_SAPLING_CROP, 4),
   ferru("ferru", IRBlocks.FERRU_CROP, 3),
   cyprium("cyprium", IRBlocks.CYPRIUM_CROP, 3),
   stagnium("stagnium", IRBlocks.STAGNIUM_CROP, 3),
   plumbiscus("plumbiscus", IRBlocks.PLUMBISCUS_CROP, 3),
   aurelia("aurelia", IRBlocks.AURELIA_CROP, 3),
   shining("shining", IRBlocks.SHINING_CROP, 3),
   redWheat("red_wheat", IRBlocks.RED_WHEAT_CROP, 6),
   coffee("coffee", IRBlocks.COFFEE_CROP, 4),
   hops("hops", IRBlocks.HOPS_CROP, 6),
   eatingPlant("eating_plant", IRBlocks.EATING_PLANT_CROP, 5);

   private final String name;
   private final String owner;
   private final int maxAge;
   private final Supplier<? extends Block> cropBlock;
   private final IntegerProperty property;

   IRCropType(String paramString1, Supplier<IRCropBlock> paramBlock, int maxAge) {
      this.name = paramString1;
      this.owner = "industrialreforged";
      this.cropBlock = paramBlock;
      this.maxAge = maxAge;
      this.property = maxAge > 0 ? IntegerProperty.create("age", 0, this.maxAge) : null;
   }

   public String getSerializedName() {
      return this.name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getOwner() {
      return this.owner;
   }

   @Override
   public Block getCropBlock() {
      return this.cropBlock == null ? null : this.cropBlock.get();
   }

   @Override
   public int getMaxAge() {
      return this.maxAge;
   }

   @Nullable
   public IntegerProperty getProperty() {
      return this.property;
   }

   public static CropCard getCropCard(IRCropType cropType) {
      return IRCrops.INSTANCE.getCropCard(cropType.getOwner(), cropType.getName());
   }

   public static IRCropType getCropType(CropCard cropCard) {
      String[] arrayOfString = cropCard.getName().split("_");
      StringBuilder stringBuilder = new StringBuilder();

      for (String str : arrayOfString) {
         if (stringBuilder.isEmpty()) {
            stringBuilder.append(str);
         } else {
            stringBuilder.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
         }
      }

      return valueOf(stringBuilder.toString());
   }
}
