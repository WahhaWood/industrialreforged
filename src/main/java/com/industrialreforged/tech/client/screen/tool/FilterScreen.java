package com.industrialreforged.tech.client.screen.tool;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.tool.FilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Экран фильтра: внутри — только слоты (48 штук, 8x6), а переключатель режима вынесен за пределы окна
 * и показывает белую или чёрную бумагу вместо надписей.
 */
public class FilterScreen extends AbstractContainerScreen<FilterMenu> {
   private static final ResourceLocation TEXTURE = IR.texture("screen/filter");
   private static final ResourceLocation PAPER_WHITE = IR.texture("screen/filter_whitelist");
   private static final ResourceLocation PAPER_BLACK = IR.texture("screen/filter_blacklist");
   private Button modeButton;

   public FilterScreen(FilterMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = 176;
      this.imageHeight = 226;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   @Override
   protected void setInitialFocus() {
      this.setFocused(null);
   }

   @Override
   public void init() {
      super.init();
      this.modeButton = this.addRenderableWidget(
         Button.builder(
               Component.empty(),
               button -> {
                  if (this.minecraft != null && this.minecraft.gameMode != null) {
                     this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                  }
               }
            )
            .bounds(this.leftPos + this.imageWidth - 1, this.topPos + 17, 20, 20)
            .tooltip(Tooltip.create(Component.translatable("text.industrialreforged.filter.mode.tooltip")))
            .build()
      );
      this.modeButton.setFocused(false);
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(graphics, mouseX, mouseY, partialTick);
      super.render(graphics, mouseX, mouseY, partialTick);
      if (this.modeButton != null) {
         this.modeButton.setFocused(false);
         ResourceLocation paper = this.menu.blacklist ? PAPER_BLACK : PAPER_WHITE;
         graphics.blit(paper, this.modeButton.getX() + 2, this.modeButton.getY() + 2, 0, 0, 16, 16, 16, 16);
      }

      this.renderTooltip(graphics, mouseX, mouseY);
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      // заголовок слева, текущий режим справа, подпись инвентаря как обычно
      graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
      Component mode = Component.translatable(
         "text.industrialreforged.filter.mode",
         Component.translatable(this.menu.blacklist ? "text.industrialreforged.filter.blacklist" : "text.industrialreforged.filter.whitelist")
      );
      int modeColor = this.menu.blacklist ? 0xB71C1C : 0x2E7D32;
      graphics.drawString(this.font, mode, this.imageWidth - 8 - this.font.width(mode), this.titleLabelY, modeColor, false);
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
   }
}
