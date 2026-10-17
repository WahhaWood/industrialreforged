package com.industrialreforged.tech.client.screen.reactor;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.reactor.NuclearReactorElectricBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.reactor.NuclearReactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NuclearReactorScreen extends IRScreenBase<NuclearReactorMenu> {
   private RatioGauge heat;

   public NuclearReactorScreen(NuclearReactorMenu containerNuclearReactor, Inventory inventory, Component component) {
      super(containerNuclearReactor, inventory, component);
      this.imageWidth = 212;
      this.imageHeight = 243;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableWidget(new HelpButton(this.leftPos + 190, this.topPos + 4));
      this.heat = ((RatioGauge)this.addRenderableOnly(RatioGauge.create(this.leftPos + 7, this.topPos + 136, () -> {
         float maxHeat = ((NuclearReactorElectricBlockEntity)((NuclearReactorMenu)this.menu).entity).get(1);
         return maxHeat == 0.0F ? 0.0 : ((NuclearReactorElectricBlockEntity)((NuclearReactorMenu)this.menu).entity).get(0) / maxHeat;
      }, Gauge.HeatNuclearReactor))).withTooltip(() -> {
         float heat = ((NuclearReactorElectricBlockEntity)((NuclearReactorMenu)this.menu).entity).get(0);
         float maxHeat = ((NuclearReactorElectricBlockEntity)((NuclearReactorMenu)this.menu).entity).get(1);
         return Component.translatable("text.industrialreforged.tooltip.reactor.temp", new Object[]{maxHeat == 0.0F ? 0.0F : 100.0F * heat / maxHeat});
      });
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      graphics.drawCenteredString(
         this.font,
         Component.translatable("text.industrialreforged.fe.tick", new Object[]{((NuclearReactorElectricBlockEntity)((NuclearReactorMenu)this.menu).entity).getGeneration()}),
         160,
         139,
         5752026
      );
      if (this.isHovering(6, 161, 16, 16, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.reactor.mode.electric"));
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      int reactorSize = ((NuclearReactorElectricBlockEntity)((NuclearReactorMenu)this.menu).entity).getReactorSize();

      for (int y = 0; y < 6; y++) {
         for (int x = reactorSize; x < 9; x++) {
            ScreenApi.rect(graphics, this.leftPos + 26 + x * 18, this.topPos + 25 + y * 18, 213.0F, 1.0F, 16.0F, 16.0F);
         }
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/nuclear_reactor");
   }

   /** Кнопка «как собрать»: открывает страницу реактора в JEI. */
   private final class HelpButton extends AbstractButton {
      private HelpButton(int x, int y) {
         super(x, y, 16, 16, Component.translatable("text.industrialreforged.reactor.help"));
      }

      @Override
      public void onPress() {
         NuclearReactorScreen.this.handleInventoryButtonClick(NuclearReactorMenu.BUTTON_HELP);
      }

      @Override
      protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         int x = this.getX();
         int y = this.getY();
         graphics.fill(x, y, x + 16, y + 16, 0xFFB9C4D0);
         graphics.fill(x, y, x + 16, y + 1, 0xFF20304A);
         graphics.fill(x, y + 15, x + 16, y + 16, 0xFF20304A);
         graphics.fill(x, y, x + 1, y + 16, 0xFF20304A);
         graphics.fill(x + 15, y, x + 16, y + 16, 0xFF20304A);
         if (this.isHovered()) {
            graphics.fill(x + 1, y + 1, x + 15, y + 15, 0x40FFFFFF);
         }

         graphics.drawString(NuclearReactorScreen.this.font, "?", x + 6, y + 4, 0xFF20304A, false);
      }

      @Override
      protected void updateWidgetNarration(NarrationElementOutput output) {
         this.defaultButtonNarrationText(output);
      }
   }
}
