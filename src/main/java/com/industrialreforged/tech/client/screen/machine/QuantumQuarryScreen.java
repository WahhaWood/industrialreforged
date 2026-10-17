package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.QuantumQuarryBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.QuantumQuarryMenu;
import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.utils.NumberUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Экран квантового карьера.
 *
 * Слева сверху слот сканера, правее кнопка режима (размером со слот), ниже 2 улучшения и 2 фильтра,
 * правее — выход 3x3. Слева от окна — панель энергии (рисует базовый класс), справа — панель
 * с режимом, счётчиком, требованием сканера и списком подходящих улучшений.
 */
public class QuantumQuarryScreen extends IRScreenBase<QuantumQuarryMenu> {
   private static final int PANEL_BG = -16777216;
   private static final int PANEL_TEXT = 0x55FF55;
   private Button modeButton;

   public QuantumQuarryScreen(QuantumQuarryMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   protected void setInitialFocus() {
      // без начального фокуса: иначе кнопка рисуется так, будто на неё наведён курсор
      this.setFocused(null);
   }

   @Override
   public void init() {
      super.init();
      this.modeButton = this.addRenderableWidget(
         Button.builder(
               Component.empty(),
               button -> this.handleInventoryButtonClick(0)
            )
            .bounds(this.leftPos + this.imageWidth + 4, this.topPos + 72, 20, 20)
            .tooltip(Tooltip.create(Component.translatable("text.industrialreforged.quantum_quarry.toggle")))
            .build()
      );
      this.modeButton.setFocused(false);
   }

   private Component modeName() {
      return Component.translatable(
         this.menu.entity != null && this.menu.entity.silkMode
            ? "text.industrialreforged.quantum_quarry.silk"
            : "text.industrialreforged.quantum_quarry.fortune"
      );
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (this.modeButton != null && this.menu.entity != null) {
         this.modeButton.setMessage(
            Component.translatable(
               this.menu.entity.silkMode
                  ? "text.industrialreforged.quantum_quarry.letter.silk"
                  : "text.industrialreforged.quantum_quarry.letter.fortune"
            )
         );
         this.modeButton.setFocused(false);
      }

      super.render(graphics, mouseX, mouseY, partialTick);
   }

   /** Правая информационная панель. */
   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      QuantumQuarryBlockEntity entity = this.menu.entity;
      if (entity == null) {
         return;
      }

      List<Component> lines = new ArrayList<>();
      lines.add(Component.translatable("text.industrialreforged.quantum_quarry.panel.mode", this.modeName()));
      lines.add(Component.translatable("text.industrialreforged.quantum_quarry.panel.mined", NumberUtils.formatToSi(entity.blockMined)));
      lines.add(Component.translatable("text.industrialreforged.quantum_quarry.panel.price"));
      lines.add(
         Component.translatable(
            "text.industrialreforged.quantum_quarry.panel.rate",
            NumberUtils.formatToSi(entity.opsLastTick),
            NumberUtils.formatToSi((long)entity.opsLastTick * 4_000_000L)
         )
      );
      lines.add(Component.translatable("text.industrialreforged.quantum_quarry.panel.scanner"));
      lines.add(Component.translatable("text.industrialreforged.quantum_quarry.panel.upgrades"));
      lines.add(Component.translatable("text.industrialreforged.quantum_quarry.panel.filters"));
      int width = 0;

      for (Component line : lines) {
         width = Math.max(width, this.font.width(line));
      }

      width += 8;
      int height = lines.size() * 10 + 6;
      int x = this.leftPos + this.imageWidth + 4;
      int y = this.topPos + 4;
      ScreenApi.fill(graphics, x, y, width, height, PANEL_BG);
      int lineY = y + 3;

      for (Component line : lines) {
         graphics.drawString(this.font, line, x + 4, lineY, PANEL_TEXT, false);
         lineY += 10;
      }
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      // без заголовка: только стандартная подпись инвентаря
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
   }

   @Override
   protected ResourceLocation getTexture() {
      return IR.texture("screen/quantum_quarry");
   }
}
