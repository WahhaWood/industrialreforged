package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.CannerBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.CustomButton;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.CannerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CannerScreen extends IRScreenBase<CannerMenu> {
   private CustomButton mode;
   private CannerBlockEntity.Mode lastMode;

   public CannerScreen(CannerMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 184;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 8, this.topPos + 61, ((CannerBlockEntity)((CannerMenu)this.menu).entity).energy));
      this.addRenderableOnly(FluidGauge.asNormal(this.leftPos + 39, this.topPos + 42, ((CannerBlockEntity)((CannerMenu)this.menu).entity).inputTank));
      this.addRenderableOnly(FluidGauge.asNormal(this.leftPos + 117, this.topPos + 42, ((CannerBlockEntity)((CannerMenu)this.menu).entity).outputTank));
      this.mode = (CustomButton)this.addRenderableWidget(new CustomButton(this.leftPos + 63, this.topPos + 81, 50, 14, 176, 18, this.getTexture(), var1 -> {
         int ordinal = ((CannerBlockEntity)((CannerMenu)this.menu).entity).getMode().ordinal();
         ordinal += var1 == 0 ? 1 : -1;
         ordinal = ordinal < 0 ? 3 : ordinal % 4;
         ((CannerBlockEntity)((CannerMenu)this.menu).entity).setMode(CannerBlockEntity.Mode.values[ordinal]);
         this.handleInventoryButtonClick(ordinal);
      }));
      ((CustomButton)this.addRenderableWidget(
            new CustomButton(this.leftPos + 77, this.topPos + 64, 22, 13, o -> this.handleInventoryButtonClick(CannerBlockEntity.eventSwapTanks))
         ))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.canner.switch")));
   }

   protected void containerTick() {
      super.containerTick();
      if (this.lastMode != ((CannerBlockEntity)((CannerMenu)this.menu).entity).getMode()) {
         this.lastMode = ((CannerBlockEntity)((CannerMenu)this.menu).entity).getMode();
         this.mode.setTexturePos(176, 18 + this.lastMode.ordinal() * 14);

         this.mode.setTooltip(Tooltip.create(Component.translatable(switch (this.lastMode) {
            case BottleSolid -> "text.industrialreforged.tooltip.canner.switch.bottle_solid";
            case EmptyLiquid -> "text.industrialreforged.tooltip.canner.switch.empty_liquid";
            case BottleLiquid -> "text.industrialreforged.tooltip.canner.switch.bottle_liquid";
            case EnrichLiquid -> "text.industrialreforged.tooltip.canner.switch.enrich_liquid";
         })));
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      switch (((CannerBlockEntity)((CannerMenu)this.menu).entity).getMode()) {
         case BottleSolid:
            ScreenApi.rect(graphics, this.texture, this.leftPos + 59, this.topPos + 53, 3.0F, 4.0F, 9.0F, 18.0F);
            ScreenApi.rect(graphics, this.texture, this.leftPos + 99, this.topPos + 53, 3.0F, 4.0F, 18.0F, 23.0F);
            break;
         case EmptyLiquid:
            ScreenApi.rect(graphics, this.texture, this.leftPos + 71, this.topPos + 43, 196.0F, 0.0F, 26.0F, 18.0F);
            ScreenApi.rect(graphics, this.texture, this.leftPos + 59, this.topPos + 53, 3.0F, 4.0F, 9.0F, 18.0F);
            break;
         case BottleLiquid:
            ScreenApi.rect(graphics, this.texture, this.leftPos + 99, this.topPos + 53, 3.0F, 4.0F, 18.0F, 23.0F);
            ScreenApi.rect(graphics, this.texture, this.leftPos + 71, this.topPos + 43, 196.0F, 0.0F, 26.0F, 18.0F);
      }

      int n3 = Math.round(((CannerBlockEntity)((CannerMenu)this.menu).entity).getProgress() * 23.0F);
      if (n3 > 0) {
         ScreenApi.rect(graphics, this.texture, this.leftPos + 74, this.topPos + 22, 233.0F, 0.0F, n3, 14.0F);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/canner");
   }
}
