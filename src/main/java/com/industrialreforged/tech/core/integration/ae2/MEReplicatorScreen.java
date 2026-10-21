package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.utils.NumberUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

/** ME replicator GUI: two rows of samples and the inventory (a 9×2 chest); buffer and counters in the left panel. */
public class MEReplicatorScreen extends AbstractContainerScreen<MEReplicatorMenu> {
   private static final ResourceLocation TEXTURE = IR.texture("screen/me_recycler");

   public MEReplicatorScreen(MEReplicatorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = 176;
      this.imageHeight = 150;
      this.inventoryLabelY = this.imageHeight - 94;
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(new MEPowerButton(this.leftPos + this.imageWidth - 42, this.topPos + 2, () -> this.menu.enabled,
         button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0)));
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      MEMachineScreen.renderStatusPanel(
         graphics,
         this.font,
         this.leftPos,
         this.topPos,
         List.of(
            Component.translatable("gui.industrialreforged.me_machine.buffer"),
            MEMachineScreen.bufferValue(this.menu.buffer, this.menu.entity.getAEMaxPower()),
            Component.translatable("gui.industrialreforged.me_replicator.replicated"),
            Component.literal(NumberUtils.formatToSi(this.menu.replicated)),
            Component.translatable("gui.industrialreforged.me_replicator.per_tick"),
            Component.literal(this.menu.lastOperations + " / " + MEReplicatorBlockEntity.ITEMS_PER_TICK)
         )
      );
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, MEMachineScreen.fitTitle(this.font, this.title, this.imageWidth - 54), this.titleLabelX, this.titleLabelY, 0x404040, false);
      graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      if (this.hoveredSlot != null && this.hoveredSlot.index < MEReplicatorBlockEntity.SAMPLES && !this.hoveredSlot.hasItem() && this.menu.getCarried().isEmpty()) {
         // the hint is a long sentence: wrap it instead of running off a narrow screen
         List<FormattedCharSequence> lines = new ArrayList<>();
         lines.add(Component.translatable("gui.industrialreforged.me_replicator.sample").getVisualOrderText());
         lines.addAll(this.font.split(Component.translatable("gui.industrialreforged.me_replicator.sample.hint").withStyle(ChatFormatting.GRAY), 200));
         graphics.renderTooltip(this.font, lines, mouseX, mouseY);
      } else {
         this.renderTooltip(graphics, mouseX, mouseY);
      }
   }
}
