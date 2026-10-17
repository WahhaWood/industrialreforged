package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.ReplicatorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.CustomButton;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.menu.machine.ReplicatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ReplicatorScreen extends IRScreenBase<ReplicatorMenu> {
   public ReplicatorScreen(ReplicatorMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 184;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 132, this.topPos + 83, ((ReplicatorBlockEntity)((ReplicatorMenu)this.menu).entity).energy));
      this.addRenderableOnly(FluidGauge.asNormal(this.leftPos + 27, this.topPos + 30, ((ReplicatorBlockEntity)((ReplicatorMenu)this.menu).entity).tank));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 80, this.topPos + 16, 9, 18, o -> this.handleInventoryButtonClick(0))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.replicator.prev")));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 109, this.topPos + 16, 9, 18, o -> this.handleInventoryButtonClick(1))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.replicator.next")));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 75, this.topPos + 82, 16, 16, o -> this.handleInventoryButtonClick(3))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.replicator.stop")));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 92, this.topPos + 82, 16, 16, o -> this.handleInventoryButtonClick(4))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.replicator.single")));
      ((CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 109, this.topPos + 82, 16, 16, o -> this.handleInventoryButtonClick(5))))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.replicator.repeat")));
   }

   @Override
   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
      ItemStack pattern = ((ReplicatorBlockEntity)((ReplicatorMenu)this.menu).entity).pattern;
      if (!pattern.isEmpty()) {
         ScreenApi.renderItem(guiGraphics, pattern, this.leftPos + 91, this.topPos + 17, 1.0F);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      super.renderLabels(graphics, mouseX, mouseY);
      ReplicatorBlockEntity entity = (ReplicatorBlockEntity)((ReplicatorMenu)this.menu).entity;
      graphics.drawString(this.font, this.getStatus(), 51, 41, entity.getMode() == ReplicatorBlockEntity.Mode.STOPPED ? 15461152 : 2157374);
      if (entity.pattern != null && this.isHovering(90, 16, 16, 16, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(Component.literal(entity.pattern.getHoverName().getString() + " UU: " + Util.toSiStringUU(entity.patternUu)));
      }
   }

   public ResourceLocation getTexture() {
      return IR.texture("screen/replicator");
   }

   private Component getStatus() {
      ReplicatorBlockEntity entity = (ReplicatorBlockEntity)((ReplicatorMenu)this.menu).entity;
      if (entity.getMode() == ReplicatorBlockEntity.Mode.STOPPED) {
         return Component.translatable("text.industrialreforged.tooltip.replicator.waiting");
      }

      int n = 0;
      if (entity.patternUu != 0) {
 n = Math.min(Math.round(100.0F * entity.uuProcessed / entity.patternUu), 100);
      }

      return Component.literal(String.format("UU:%d%%  >%s", n, entity.getMode() == ReplicatorBlockEntity.Mode.SINGLE ? "" : ">"));
   }
}
