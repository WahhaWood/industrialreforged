package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.LatheBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.item.turning.ItemTurningMold;
import com.industrialreforged.tech.item.turning.TurningProgram;
import com.industrialreforged.tech.menu.machine.LatheMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class LatheScreen extends IRScreenBase<LatheMenu> {
   public LatheScreen(LatheMenu container, Inventory inventory, Component component) {
      super(container, inventory, component);
   }

   @Override
   public void init() {
      super.init();

      for (int i = 0; i < 5; i++) {
         int finalI = i;
         this.addRenderableWidget(
            Button.builder(Component.literal(""), button -> this.handleInventoryButtonClick(finalI))
               .bounds(this.leftPos + 40 + i * 24, this.topPos + 15, 24, 20)
               .build()
         );
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      LatheBlockEntity entity = (LatheBlockEntity)((LatheMenu)this.menu).entity;
      if (!entity.latheSlot.isEmpty() && entity.latheSlot.getItem().getItem() instanceof ItemTurningMold) {
         ItemStack stack = entity.latheSlot.getItem();
         renderILatheItemIntoGUI(graphics, stack, 40, 45);
         ItemTurningMold i = (ItemTurningMold)stack.getItem();
         int segLength = 24;
         long state = i.getCurrentState(stack);
         int max = i.getWidth();

         for (int j = 0; j < 5; j++) {
            graphics.drawString(
               this.font,
               Component.translatable("text.industrialreforged.tooltip.turning.ratio", new Object[]{TurningProgram.toState(state, j), max}),
               40 + segLength * j,
               37,
               4210752,
               false
            );
         }
      }

      if (!entity.toolSlot.isEmpty()) {
         for (int i = 0; i < 5; i++) {
            ScreenApi.renderItem(graphics, entity.toolSlot.getItem(), 45 + i * 24, 18.0F, 1.0F);
         }
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/turning");
   }

   public static void renderILatheItemIntoGUI(GuiGraphics graphics, ItemStack stack, int x, int y) {
      ItemTurningMold i = (ItemTurningMold)stack.getItem();
      int segLength = 24;
      long state = i.getCurrentState(stack);

      ScreenApi.setShaderTexture(switch (i.getWidth()) {
         case 5 -> IR.texture("screen/overlay/iron_turning_mold");
         case 7 -> IR.texture("screen/overlay/steel_turning_mold");
         default -> IR.texture("screen/overlay/wooden_turning_mold");
      });

      for (int j = 0; j < 5; j++) {
         int segWidth = (int)(32.0F / i.getWidth() * TurningProgram.toState(state, j) + 0.5F);
         int offset = (int)((32 - segWidth) / 2.0F + 0.5F);
         ScreenApi.rect(graphics, x + segLength * j, y + offset, segLength * j, offset, segLength, segWidth);
      }
   }
}
