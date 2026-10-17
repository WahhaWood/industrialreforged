package com.industrialreforged.tech.item.tool;

import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.menu.tool.FilterMenu;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Фильтр для квантового карьера: ПКМ открывает сетку, куда кладутся образцы руд.
 * Режим — белый список (добывать только эти) или чёрный (всё, кроме этих).
 */
public class ItemFilter extends Item {
   public static final int SIZE = 48;

   public ItemFilter(Properties properties) {
      super(properties);
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (player instanceof ServerPlayer serverPlayer) {
         serverPlayer.openMenu(new ItemFilter.Provider(hand), buffer -> buffer.writeEnum(hand));
      }

      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
   }

   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      FilterData data = stack.getOrDefault(IRDataComponents.ITEM_FILTER.get(), FilterData.EMPTY);
      tooltip.add(Component.translatable(data.blacklist() ? "text.industrialreforged.filter.blacklist" : "text.industrialreforged.filter.whitelist"));
      int count = 0;
      for (ItemStack sample : data.items()) {
         if (!sample.isEmpty()) {
            count++;
         }
      }

      tooltip.add(Component.translatable("text.industrialreforged.filter.samples", count));
      tooltip.add(Component.translatable("text.industrialreforged.filter.hint"));
      super.appendHoverText(stack, context, tooltip, flag);
   }

   /** Провайдер для открытия меню фильтра (нужен, чтобы передать руку в меню). */
   public record Provider(InteractionHand hand) implements MenuProvider {
      public Component getDisplayName() {
         return Component.translatable("item.industrialreforged.item_filter");
      }

      public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
         return new FilterMenu(containerId, inventory, this.hand);
      }
   }
}
