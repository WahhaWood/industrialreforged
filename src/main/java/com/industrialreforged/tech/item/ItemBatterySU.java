package com.industrialreforged.tech.item;

import com.industrialreforged.tech.core.util.StackUtil;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class ItemBatterySU extends Item {
   public int capacity;
   public int tier;

   public ItemBatterySU(Properties properties, int n, int n2) {
      super(properties);
      this.capacity = n;
      this.tier = n2;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemStack = StackUtil.get(player, interactionHand);
      return new InteractionResultHolder(InteractionResult.PASS, itemStack);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.literal(this.capacity + " FE"));
   }
}
