package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.ScannerBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.CustomButton;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.IconButton;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.menu.machine.ScannerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

public class ScannerScreen extends IRScreenBase<ScannerMenu> {
   private CustomButton delete;
   private CustomButton save;

   public ScannerScreen(ScannerMenu containerScanner, Inventory inventory, Component component) {
      super(containerScanner, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 8, this.topPos + 24, ((ScannerBlockEntity)((ScannerMenu)this.menu).entity).energy));
      ResourceLocation location = this.getTexture();
      this.delete = (CustomButton)this.addRenderableWidget(
         new CustomButton(this.leftPos + 102, this.topPos + 49, 12, 12, 176, 57, location, var1x -> this.handleInventoryButtonClick(0))
      );
      this.delete.setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.scanner.delete")));
      this.save = (CustomButton)this.addRenderableWidget(
         new CustomButton(this.leftPos + 143, this.topPos + 49, 24, 12, 176, 69, location, var1x -> this.handleInventoryButtonClick(1))
      );
      this.save.setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.scanner.save")));
      this.addRenderableOnly(new IconButton(this.leftPos + 6, this.topPos + 4, 18, 18, Items.BOOK));
   }

   protected void containerTick() {
      super.containerTick();
      ScannerBlockEntity.State state = ((ScannerBlockEntity)((ScannerMenu)this.menu).entity).getState();
      this.delete.visible = state.isAllowDelete();
      this.save.visible = state.isAllowSave();
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      int n3 = ((ScannerBlockEntity)((ScannerMenu)this.menu).entity).getSubPercentageDoneScaled(66);
      if (n3 > 0) {
         ScreenApi.rect(graphics, this.leftPos + 30, this.topPos + 20, 176.0F, 14.0F, n3, 43.0F);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      ScannerBlockEntity entity = (ScannerBlockEntity)((ScannerMenu)this.menu).entity;
      switch (entity.getState()) {
         case IDLE:
            graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.waiting"), 10, 69, 15461152);
            break;
         case NO_STORAGE:
            graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.waiting.storage"), 10, 69, 15461152);
            break;
         case SCANNING:
            graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.scanning"), 10, 69, 2157374);
            graphics.drawString(this.font, String.format("%d%%", entity.getPercentageDone()), 125, 69, 2157374);
            break;
         case NO_ENERGY:
            graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.error.power"), 10, 69, 14094352);
            break;
         case ALREADY_RECORDED:
            graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.error.already"), 10, 69, 14094352);
            break;
         case FAILED:
            graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.complete"), 10, 69, 2157374);
            graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.error.mistake"), 110, 30, 14094352);
            break;
         case COMPLETED:
         case TRANSFER_ERROR:
            if (entity.getState() == ScannerBlockEntity.State.COMPLETED) {
               graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.complete"), 10, 69, 2157374);
            }

            if (entity.getState() == ScannerBlockEntity.State.TRANSFER_ERROR) {
               graphics.drawString(this.font, Component.translatable("text.industrialreforged.tooltip.scanner.error.storage"), 10, 69, 14094352);
            }

            graphics.drawString(this.font, Util.toSiStringUU(entity.patternUu) + " UU", 105, 25, 16777215);
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/scanner");
   }
}
