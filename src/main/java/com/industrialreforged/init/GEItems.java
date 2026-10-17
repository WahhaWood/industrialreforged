package com.industrialreforged.init;

import com.industrialreforged.item.HiddenBlockItem;
import com.industrialreforged.item.HiddenItem;
import java.util.function.Consumer;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class GEItems {
   public static final Items REGISTRY = DeferredRegister.createItems("industrialreforged");
   public static final DeferredItem<Item> SUPER_ALLOY = REGISTRY.register("super_alloy", GEItems::defaultItem);
   public static Item defaultItem() {
      return new Item(new Properties());
   }

   public static Item item(Consumer<Properties> consumer) {
      Properties properties = new Properties();
      consumer.accept(properties);
      return new Item(properties);
   }

   public static HiddenItem defaultHiddenItem() {
      return new HiddenItem(new Properties());
   }

   public static BlockItem defaultBlock(DeferredBlock<? extends Block> object) {
      return defaultBlock((Block)object.get());
   }

   public static BlockItem defaultBlock(Block object) {
      return new BlockItem(object, new Properties());
   }

   public static BlockItem defaultBlock(DeferredBlock<? extends Block> object, Consumer<Properties> consumer) {
      return defaultBlock((Block)object.get(), consumer);
   }

   public static BlockItem defaultBlock(Block object, Consumer<Properties> consumer) {
      Properties properties = new Properties();
      consumer.accept(properties);
      return new BlockItem(object, properties);
   }

   public static HiddenBlockItem defaultHiddenBlock(DeferredBlock<? extends Block> object) {
      return defaultHiddenBlock((Block)object.get());
   }

   public static HiddenBlockItem defaultHiddenBlock(Block object) {
      return new HiddenBlockItem(object, new Properties());
   }
}
