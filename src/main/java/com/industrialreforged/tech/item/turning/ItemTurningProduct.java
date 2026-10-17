package com.industrialreforged.tech.item.turning;

import com.industrialreforged.utils.MsgUtils;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class ItemTurningProduct extends Item {
   public static final LinkedHashMap<TurningProgram, ItemTurningProduct> PROGRAMS = new LinkedHashMap<>();
   private final TurningProgram program;

   public ItemTurningProduct(int width, byte[] states, String unlocalizedName) {
      super(new Properties());
      this.program = new TurningProgram((byte)width, TurningProgram.toStates(states), unlocalizedName);
      PROGRAMS.put(this.program, this);
   }

   public TurningProgram getProgram() {
      return this.program;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      TurningProgram program = this.getProgram();
      StringBuilder sb = new StringBuilder();

      for (int j = 0; j < 5; j++) {
         sb.append(MsgUtils.translate("text.industrialreforged.tooltip.turning.ratio", new Object[]{program.state(j), program.width()}));
         sb.append("   ");
      }

      tooltipComponents.add(Component.literal(sb.toString()));
   }
}
