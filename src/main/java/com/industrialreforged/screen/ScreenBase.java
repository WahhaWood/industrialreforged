package com.industrialreforged.screen;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.menu.MenuBase;
import com.industrialreforged.menu.slots.BaseSlot;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public abstract class ScreenBase<T extends MenuBase<?>> extends AbstractContainerScreen<T> {
   protected static final int W_INV = 176;
   protected static final int H_INV = 90;
   protected static final ResourceLocation SLOT = IndustrialReforged.location("textures/screen/slot.png");
   protected static final ResourceLocation LARGE_SLOT = IndustrialReforged.location("textures/screen/large_slot.png");
   protected ResourceLocation texture;
   private ScreenBase.BackgroundSlot backgroundSlot = ScreenBase.BackgroundSlot.NONE;

   public ScreenBase(T container, Inventory inventory) {
      super(container, inventory, Component.empty());
   }

   public ScreenBase(T container, Inventory inventory, Component title) {
      super(container, inventory, title);
   }

   protected abstract ResourceLocation getTexture();

   public void init() {
      super.init();
      this.titleLabelX = this.imageWidth / 2;
      this.inventoryLabelY = this.imageHeight - 94;
      this.texture = this.getTexture();
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      GlStateManager._enableBlend();
      if (this.texture != null) {
         ScreenApi.rect(graphics, this.texture, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight);
      }

      for (Slot slot : ((MenuBase)this.menu).slots) {
         if (this.isBackgroundSlot(slot)) {
            int size = slot instanceof BaseSlot o && o.large ? 26 : 18;
            int offset = (size - 16) / 2;
            ScreenApi.quad(graphics, size == 26 ? LARGE_SLOT : SLOT, this.leftPos + slot.x - offset, this.topPos + slot.y - offset, size, size);
         }

         if (slot instanceof BaseSlot baseSlot && !baseSlot.hasItem()) {
            ScreenApi.renderItem(graphics, baseSlot.getGhostItem(), this.leftPos + baseSlot.x, this.topPos + baseSlot.y, 1.0F, 0.5F);
         }
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
   }

   protected void renderTitle(GuiGraphics graphics) {
      graphics.drawString(this.font, this.title, this.titleLabelX - this.font.width(this.title) / 2, this.titleLabelY, 4210752, false);
   }

   protected void setBackgroundSlot(ScreenBase.BackgroundSlot value) {
      this.backgroundSlot = value;
   }

   protected boolean isBackgroundSlot(Slot slot) {
      switch (this.backgroundSlot) {
         case ALL:
            break;
         case CONTAINER:
            if (slot.container == ((MenuBase)this.menu).player.getInventory()) {
               return false;
            }
            break;
         case INVENTORY:
            if (slot.container != ((MenuBase)this.menu).player.getInventory()) {
               return false;
            }
            break;
         default:
            return false;
      }

      return !(slot instanceof BaseSlot slot1) || slot1.background;
   }

   protected void handleInventoryButtonClick(int buttonId) {
      if (this.minecraft != null && this.minecraft.gameMode != null) {
         this.minecraft.gameMode.handleInventoryButtonClick(((MenuBase)this.menu).containerId, buttonId);
      }
   }

   public enum BackgroundSlot {
      NONE,
      ALL,
      CONTAINER,
      INVENTORY;
   }
}
