package com.industrialreforged.tech.item;

import com.industrialreforged.utils.NumberUtils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

/**
 * Block item of an energy storage (BatBox, CESU, MFE, MFSU, EFSU and their
 * chargepads).  The tooltip spells out how much energy the block can hold and
 * how fast it takes and gives energy.
 */
public class ElectricStorageBlockItem extends BlockItem {
   private final long capacity;
   private final long receive;
   private final long extract;

   public ElectricStorageBlockItem(Block block, long capacity, long receive, long extract, Properties properties) {
      super(block, properties);
      this.capacity = capacity;
      this.receive = receive;
      this.extract = extract;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("text.industrialreforged.fe.capacity", new Object[]{NumberUtils.formatToSi(this.capacity)}));
      tooltipComponents.add(Component.translatable("text.industrialreforged.fe.extract.tick", new Object[]{NumberUtils.formatToSi(this.extract)}));
   }
}
