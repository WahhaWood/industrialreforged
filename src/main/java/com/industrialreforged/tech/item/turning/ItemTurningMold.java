package com.industrialreforged.tech.item.turning;

import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.ItemLike;

public class ItemTurningMold extends Item {
   private final byte width;
   private final float outputChance;
   private final TurningProgram[] types;

   @SafeVarargs
   public ItemTurningMold(byte width, float outputChance, Supplier<ItemTurningProduct>... types) {
      super(new Properties().stacksTo(1).component(IRDataComponents.TURNING_STATES, TurningProgram.toStates(new byte[]{width, width, width, width, width})));
      this.width = width;
      this.outputChance = outputChance;
      this.types = Arrays.stream(types).map(Supplier::get).map(ItemTurningProduct::getProgram).toArray(TurningProgram[]::new);
   }

   public TurningProgram[] getTypes() {
      return this.types;
   }

   public byte getWidth() {
      return this.width;
   }

   public long getCurrentState(ItemStack stack) {
      return (Long)stack.getOrDefault(IRDataComponents.TURNING_STATES, -1L);
   }

   public void setState(ItemStack stack, int position, byte value) {
      if (!stack.isEmpty() && position >= 0 && position < 5) {
         long states = this.getCurrentState(stack);
         if (states == -1L) {
            return;
         }

         int shift = 8 * position;
         long mask = 255L << shift;
         states &= ~mask;
         states |= (value & 255L) << shift;
         stack.set(IRDataComponents.TURNING_STATES, states);
      }
   }

   public ItemStack getOutputItem(ItemStack stack) {
      return switch (this.width) {
         case 3 -> new ItemStack(Items.STICK);
         default -> ItemStack.EMPTY;
         case 5, 7 -> new ItemStack((ItemLike)IRItems.SMALL_IRON_DUST.get());
      };
   }

   public float getOutputChance() {
      return this.outputChance;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      long states = this.getCurrentState(stack);
      int max = this.getWidth();
      StringBuilder sb = new StringBuilder();

      for (int j = 0; j < 5; j++) {
         sb.append(MsgUtils.translate("text.industrialreforged.tooltip.turning.ratio", new Object[]{TurningProgram.toState(states, j), max}));
         sb.append("   ");
      }

      tooltipComponents.add(Component.literal(sb.toString()));
   }
}
