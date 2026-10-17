package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.machine.BlockCutterBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.BlockCutterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlockCutterScreen extends IRScreenBase<BlockCutterMenu> {
   public BlockCutterScreen(BlockCutterMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.setBackgroundSlot(BackgroundSlot.CONTAINER);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 25, this.topPos + 36, ((BlockCutterBlockEntity)((BlockCutterMenu)this.menu).entity).energy));
      this.addRenderableOnly(
         RatioGauge.create(
            this.leftPos + 55, this.topPos + 33, ((BlockCutterBlockEntity)((BlockCutterMenu)this.menu).entity)::getProgress, Gauge.ProgressBlockCutter
         )
      );
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      boolean emptyCutter = ((BlockCutterBlockEntity)((BlockCutterMenu)this.menu).entity).cutterSlot.isEmpty();
      if (emptyCutter
         || !((BlockCutterBlockEntity)((BlockCutterMenu)this.menu).entity).inputSlot.isEmpty()
            && ((BlockCutterBlockEntity)((BlockCutterMenu)this.menu).entity).getRecipe(0) == null) {
         ScreenApi.rect(graphics, IRScreenBase.COMMON, this.leftPos + 63, this.topPos + 54, 192.0F, 76.0F, 30.0F, 26.0F);
         if (this.isHovering(64, 55, 28, 24, mouseX, mouseY)) {
            this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.cutter.weak"));
         }
      }

      if (emptyCutter && this.isHovering(70, 34, 16, 16, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.cutter.slot"));
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      super.renderLabels(graphics, mouseX, mouseY);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/basic");
   }
}
