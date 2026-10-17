package com.industrialreforged.tech.item;

import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.api.crops.ICropSeed;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.crop.IRCrops;
import com.industrialreforged.tech.crop.data.CropData;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;

public class ItemCropSeed extends Item implements ICropSeed {
   public ItemCropSeed() {
      super(new Properties().stacksTo(1).component(IRDataComponents.CROP, CropData.EMPTY));
   }

   public String getDescriptionId(ItemStack itemStack) {
      CropData data = this.getCropFromStack(itemStack);
      int n = data.getScan();
      if (n == 0) {
         return "crop.industrialreforged.unknown";
      } else {
         return n >= 0 && data.getCard() != null ? data.getCard().getUnlocalizedName() : "crop.industrialreforged.invalid";
      }
   }

   public Component getName(ItemStack itemStack) {
      CropCard cropCard = this.getCropFromStack(itemStack).getCard();
      return Component.translatable(cropCard == null ? "crop.industrialreforged.seeds" : cropCard.getSeedType(), new Object[]{super.getName(itemStack)});
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      CropData data = this.getCropFromStack(stack);
      if (data.getScan() >= 4) {
         tooltipComponents.add(Component.literal("§2Gr§7 " + data.getGrowth()));
         tooltipComponents.add(Component.literal("§6Ga§7 " + data.getGain()));
         tooltipComponents.add(Component.literal("§3Re§7 " + data.getResistance()));
      }
   }

   public InteractionResult useOn(UseOnContext context) {
      if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CropBlockEntity entity) {
         ItemStack itemHand = context.getItemInHand();
         CropData data = this.getCropFromStack(itemHand);
         if (data.getCard() != null && data.getCard() != IRCrops.weed && !entity.isCrossingBase() && data.getCard().canGrow(entity)) {
            CropBlockEntity entityCrop = entity.transformCropBlock(data.getCard(), 0);
            entityCrop.getCrop().copy(data);
            entityCrop.getStorage().copy(entity.getStorage());
            Player player = context.getPlayer();
            if (!player.getAbilities().instabuild) {
               player.getInventory().items.set(player.getInventory().selected, ItemStack.EMPTY);
            }

            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }

   public static ItemStack generateItemStackFromValues(CropCard cropCard, int growth, int gain, int resistance, int scan) {
      ItemStack itemStack = new ItemStack((ItemLike)IRItems.CROP_SEED_BAG.get());
      itemStack.set(IRDataComponents.CROP, new CropData(cropCard, (byte)growth, (byte)gain, (byte)resistance, (byte)scan));
      return itemStack;
   }

   @Override
   public CropData getCropFromStack(ItemStack itemStack) {
      return (CropData)itemStack.getOrDefault(IRDataComponents.CROP, CropData.EMPTY);
   }

   @Override
   public void incrementScannedFromStack(ItemStack itemStack) {
      CropData data = this.getCropFromStack(itemStack);
      if (!data.isEmpty()) {
         data.setScan((byte)(data.getScan() + 1));
      }
   }

   public static CropData getCropData(ItemStack stack) {
      return stack.getItem() instanceof ICropSeed seed ? seed.getCropFromStack(stack) : CropData.EMPTY;
   }
}
