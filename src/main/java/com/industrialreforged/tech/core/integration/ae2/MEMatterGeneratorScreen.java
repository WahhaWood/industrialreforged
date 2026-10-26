package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.tech.blockentity.machine.MatterGeneratorBlockEntity;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.utils.NumberUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** ME matter generator GUI: a small window with the status lines (no slots). */
public class MEMatterGeneratorScreen extends AbstractContainerScreen<MEMatterGeneratorMenu> {
   private static final ResourceLocation TEXTURE = IR.texture("screen/me_matter_generator");

   public MEMatterGeneratorScreen(MEMatterGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = 176;
      this.imageHeight = 78;
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(new MEPowerButton(this.leftPos + this.imageWidth - 42, this.topPos + 2, () -> this.menu.enabled,
         button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0)));

      if (this.menu.entity.supportsCableOnly()) {
         this.addRenderableWidget(new MEPowerSourceButton(this.leftPos + this.imageWidth - 68, this.topPos + 2, () -> this.menu.cableOnly,
            button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1)));
      }
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, MEMachineScreen.fitTitle(this.font, this.title, this.imageWidth - 80), 8, 6, 0x404040, false);
      this.line(graphics, 20, "gui.industrialreforged.me_machine.buffer", MEMachineScreen.bufferValue(this.menu.buffer, this.menu.entity.getAEMaxPower()).getString());
      this.line(graphics, 32, "gui.industrialreforged.me_matter.produced", NumberUtils.formatToSi(this.menu.produced) + " mB");
      this.line(graphics, 44, "gui.industrialreforged.me_matter.per_tick", this.menu.lastOperations + " / \u221e");
      this.line(graphics, 56, "gui.industrialreforged.me_matter.saved", NumberUtils.formatToSi(this.menu.saved) + " FE");
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      boolean overBuffer = mouseX >= this.leftPos + 8 && mouseX < this.leftPos + this.imageWidth - 8 && mouseY >= this.topPos + 19 && mouseY < this.topPos + 30;
      if (overBuffer) {
         graphics.renderTooltip(this.font, this.bufferLasts(), mouseX, mouseY);
      }
   }

   /** How long the own buffer pays for the current rate at the full price (scrap only lengthens it); the network pays beyond that. */
   private Component bufferLasts() {
      double aePerTick = this.menu.lastOperations * (double)MatterGeneratorBlockEntity.ENERGY_PER_MB / MEMachineKind.FE_PER_AE;
      if (!this.menu.enabled) {
         return Component.translatable("gui.industrialreforged.me_matter.lasts.off");
      }

      if (aePerTick <= 0) {
         return Component.translatable("gui.industrialreforged.me_matter.lasts.idle");
      }

      long seconds = (long)(this.menu.buffer / aePerTick / 20.0);
      String time = seconds >= 3600 ? seconds / 3600 + " h " + seconds % 3600 / 60 + " min" : seconds >= 60 ? seconds / 60 + " min " + seconds % 60 + " s" : seconds + " s";
      return Component.translatable("gui.industrialreforged.me_matter.lasts", time);
   }

   /** Label and value on one row, the value pressed to the right edge; a value too long for the row cuts the label's end off. */
   private void line(GuiGraphics graphics, int y, String labelKey, String value) {
      int valueWidth = this.font.width(value);
      String label = MEMachineScreen.fitTitle(this.font, Component.translatable(labelKey), this.imageWidth - 24 - valueWidth);
      graphics.drawString(this.font, label, 8, y, 0x404040, false);
      graphics.drawString(this.font, value, this.imageWidth - 8 - valueWidth, y, 0x202020, false);
   }
}
