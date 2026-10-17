package com.industrialreforged.tech.core.ref;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.item.api.IFluidContainerItem;
import com.industrialreforged.item.energy.BasicEnergyItem;
import com.industrialreforged.item.fluid.BasicFluidItem;
import com.industrialreforged.tech.core.integration.botania.BotaniaIntegration;
import com.industrialreforged.tech.crop.IRCrops;
import com.industrialreforged.tech.item.ItemCell;
import com.industrialreforged.tech.item.ItemCropSeed;
import com.industrialreforged.tech.item.armor.base.AbstractFluidArmorItem;
import com.industrialreforged.tech.item.tool.ItemDrillIridium;
import com.industrialreforged.tech.item.tool.ItemElectricTools;
import com.industrialreforged.tech.item.tool.ItemSprayer;
import com.industrialreforged.tech.item.tool.ItemToolPainter;
import com.industrialreforged.tech.item.tool.ItemVajra;
import java.lang.reflect.Field;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IRCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "industrialreforged");
   public static final Supplier<CreativeModeTab> TAB = REGISTRY.register(
      "machines",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("item_group.industrialreforged.name"))
         .icon(() -> new ItemStack((ItemLike)IRItems.ADVANCED_VAJRA.get()))
         .displayItems((params, output) -> {
            output.accept((ItemLike)com.industrialreforged.init.GEItems.SUPER_ALLOY.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_1_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_2_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_3_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_4_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_5_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_6_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_7_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_8_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_9_LEVEL.get());
            output.accept((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_10_LEVEL.get());
            try {
               for (DeferredHolder<Item, ? extends Item> object : IRItems.REGISTRY.getEntries()) {
                  if (object != null) {
                     Item item = (Item)object.get();
                     if (item instanceof ItemToolPainter itemToolPainter) {
                        for (DyeColor color : DyeColor.values()) {
                           ItemStack newPainter = new ItemStack(itemToolPainter);
                           ItemToolPainter.setColor(newPainter, color);
                           output.accept(newPainter);
                        }
                     }

                     if (item instanceof ItemVajra vajra && vajra.fortuneLevel() > 0) {
                        output.accept(energyContainer(vajra, 0, params, vajra.fortuneLevel()));
                        output.accept(energyContainer(vajra, vajra.getEnergyCapacity(ItemStack.EMPTY), params, vajra.fortuneLevel()));
                     } else if (item instanceof ItemElectricTools tool) {
                        output.accept(energyContainer(tool, 0, params, tool.getLevel()));
                        output.accept(energyContainer(tool, tool.getEnergyCapacity(ItemStack.EMPTY), params, tool.getLevel()));
                     } else if (item instanceof ItemDrillIridium tool) {
                        output.accept(energyContainer(tool, 0, params, 3));
                        output.accept(energyContainer(tool, tool.getEnergyCapacity(ItemStack.EMPTY), params, 3));
                     } else if (item instanceof IEnergyContainerItem energyItem) {
                        output.accept(energyContainer(energyItem, 0, params, 0));
                        output.accept(energyContainer(energyItem, energyItem.getEnergyCapacity(ItemStack.EMPTY), params, 0));
                     } else if (item instanceof ItemCell) {
                        output.accept(item);
                        output.accept(ItemCell.getCell(Fluids.WATER));
                        output.accept(ItemCell.getCell(Fluids.LAVA));

                        for (Field f : IRFluids.class.getDeclaredFields()) {
                           if (f.get(null) instanceof IRFluids.FluidRefs refs) {
                              output.accept(refs.getCell());
                           }
                        }
                     } else if (item instanceof ItemCropSeed) {
                        IRCrops.INSTANCE.getCrops().forEach(card -> output.accept(ItemCropSeed.generateItemStackFromValues(card, 1, 1, 1, 4)));
                     } else if (item instanceof AbstractFluidArmorItem armor) {
                        output.accept(armor);
                        output.accept(fillContainer(armor, armor.getBaseFluid()));
                     } else if (item instanceof ItemSprayer sprayer) {
                        output.accept(sprayer);
                        output.accept(fillContainer(sprayer, IRFluids.CONSTRUCTION_FOAM.getFluidStill()));
                     } else {
                        output.accept(item);
                     }
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }

            if (ModList.get().isLoaded("botania")) {
               BotaniaIntegration.tab(output);
            }

            if (com.industrialreforged.tech.core.IR.ae2Integration) {
               com.industrialreforged.tech.core.integration.ae2.AE2Integration.tab(output);
            }
         })
         .build()
   );

   private static ItemStack fillContainer(IFluidContainerItem item, Fluid fluid) {
      return BasicFluidItem.getItemStack(item, new FluidStack(fluid, item.getFluidCapacity(ItemStack.EMPTY)));
   }

   private static ItemStack energyContainer(IEnergyContainerItem item, int energy, ItemDisplayParameters parameters, int fortune) {
      ItemStack stack = BasicEnergyItem.getItemStack(item, energy);
      if (fortune > 0) {
         RegistryLookup<Enchantment> lookup = parameters.holders().lookupOrThrow(Enchantments.FORTUNE.registryKey());
         stack.enchant(lookup.getOrThrow(Enchantments.FORTUNE), fortune);
      }

      return stack;
   }
}
