package com.industrialreforged.tech.client.screen.item;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.item.ToolScannerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public class ToolScannerScreen extends AbstractContainerScreen<ToolScannerMenu> {
   private final ResourceLocation background = IR.texture("screen/tool_scanner");

   public ToolScannerScreen(ToolScannerMenu containerToolScanner, Inventory inventory, Component component) {
      super(containerToolScanner, inventory, component);
      this.imageHeight = 230;
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.result"), 10, 20, 2157374);
      if (((ToolScannerMenu)this.menu).scanResults != null) {
         int n3 = 0;

         for (Pair<ItemStack, Integer> t2 : ((ToolScannerMenu)this.menu).scanResults) {
            String string = ((ItemStack)t2.getKey()).getItem().getName((ItemStack)t2.getKey()).getString();
            graphics.drawString(this.font, t2.getValue() + "x " + string, 10, 35 + n3 * 11, 5752026);
            if (++n3 == 10) {
               break;
            }
         }
      }
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      ScreenApi.rect(graphics, this.background, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight);
      if (((ToolScannerMenu)this.menu).scanResults != null) {
         int n3 = 0;

         for (Pair<ItemStack, Integer> t2 : ((ToolScannerMenu)this.menu).scanResults) {
            ScreenApi.renderItem(graphics, (ItemStack)t2.getKey(), this.leftPos - 32 + (n3 & 1) * 15, this.topPos + 11 * n3 + 30, 1.0F);
            if (++n3 == 10) {
               break;
            }
         }
      }
   }
}
