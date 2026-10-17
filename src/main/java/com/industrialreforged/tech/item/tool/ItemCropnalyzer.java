package com.industrialreforged.tech.item.tool;

import com.industrialreforged.item.energy.BasicEnergyItem;
import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.crop.CropBlockEntity;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.crop.data.StorageData;
import com.industrialreforged.tech.menu.item.CropnalyzerMenu;
import com.industrialreforged.tech.menu.item.HandHeldCropnalyzer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ItemCropnalyzer extends BasicEnergyItem implements MenuProvider {
   public ItemCropnalyzer(Properties properties) {
      super(properties.stacksTo(1).component(DataComponents.CUSTOM_DATA, CustomData.EMPTY), 1000000, 48000);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!level.isClientSide && stack.is(this)) {
         player.openMenu(this);
      }

      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
   }

   public boolean onDroppedByPlayer(ItemStack stack, Player player) {
      if (player instanceof ServerPlayer && !stack.isEmpty() && player.containerMenu instanceof CropnalyzerMenu menu && menu.stillValid(player)) {
         player.closeContainer();
      }

      return true;
   }

   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
      Level level = context.getLevel();
      Player player = context.getPlayer();
      if (!level.isClientSide && !player.isShiftKeyDown()) {
         BlockPos pos = context.getClickedPos();
         if (level.getBlockEntity(pos) instanceof CropBlockEntity entity) {
            if (entity.getCrop() == null) {
               return InteractionResult.PASS;
            }

            if (this.use(stack, HandHeldCropnalyzer.energyForLevel(2), false)) {
               CropData crop = entity.getCrop();
               CropCard card = crop.getCard();
               StorageData storage = entity.getStorage();
               MutableComponent component = Component.literal("====================").append("\n");
               if (card != null) {
                  component.append(Component.translatable("crop.industrialreforged.name").append(card.getDisplayName())).append("\n");
                  component.append(Component.translatable("crop.industrialreforged.size", new Object[]{entity.getCurrentAge() + "/" + card.getMaxAge()})).append("\n");
                  component.append(
                        Component.translatable("crop.industrialreforged.growthpoints", new Object[]{entity.getGrowth().getPoints() + "/" + card.getGrowthDuration(entity)})
                     )
                     .append("\n");
                  component.append(Component.translatable("crop.industrialreforged.statresistance", new Object[]{Integer.toString(crop.getResistance())})).append("\n");
                  component.append(Component.translatable("crop.industrialreforged.statgain", new Object[]{Integer.toString(crop.getGain())})).append("\n");
                  component.append(Component.translatable("crop.industrialreforged.statgrowth", new Object[]{Integer.toString(crop.getGrowth())})).append("\n");
                  component.append(Component.empty()).append("\n");
               }

               component.append(Component.translatable("crop.industrialreforged.nutrstorage", new Object[]{storage.getNutrients() + "/100"})).append("\n");
               component.append(Component.translatable("crop.industrialreforged.waterstorage", new Object[]{storage.getWater() + "/200"})).append("\n");
               component.append(Component.translatable("crop.industrialreforged.weedexstorage", new Object[]{storage.getWeedEx() + "/100"}));
               player.sendSystemMessage(component.append("\n").append("===================="));
               return InteractionResult.SUCCESS;
            }
         }

         return InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public Component getDisplayName() {
      return Component.empty();
   }

   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new CropnalyzerMenu(containerId, playerInventory);
   }
}
