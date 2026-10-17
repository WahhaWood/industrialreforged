package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.PatternStorageBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.CustomButton;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.menu.machine.PatternStorageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PatternStorageScreen extends IRScreenBase<PatternStorageMenu> {
   public PatternStorageScreen(PatternStorageMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 7, this.topPos + 19, 9, 18, o -> this.handleInventoryButtonClick(0))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.pattern_storage.prev")));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 36, this.topPos + 19, 9, 18, o -> this.handleInventoryButtonClick(1))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.pattern_storage.next")));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 10, this.topPos + 37, 16, 8, o -> this.handleInventoryButtonClick(2))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.pattern_storage.export")));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 26, this.topPos + 37, 16, 8, o -> this.handleInventoryButtonClick(3))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.pattern_storage.import")));
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      PatternStorageBlockEntity entity = (PatternStorageBlockEntity)((PatternStorageMenu)this.menu).entity;
      String idxStr = Math.min(entity.index + 1, entity.maxIndex) + " / " + entity.maxIndex;
      graphics.drawString(this.font, idxStr, (this.imageWidth - this.font.width(idxStr)) / 2, 30, 4210752, false);
      graphics.drawString(this.font, Component.translatable("text.industrialreforged.name"), 10, 48, 16777215, false);
      graphics.drawString(this.font, Component.translatable("text.industrialreforged.uu.mater"), 10, 59, 16777215, false);
      if (!entity.pattern.isEmpty()) {
         ScreenApi.renderItem(graphics, entity.pattern, 152.0F, 29.0F, 1.0F);
         graphics.drawString(this.font, Util.toSiStringUU(entity.patternUu), 80, 59, 16777215, false);
         graphics.drawString(this.font, entity.pattern.getHoverName(), 40, 48, 16777215, false);
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/pattern_storage");
   }
}
