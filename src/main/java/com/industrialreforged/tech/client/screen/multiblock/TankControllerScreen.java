package com.industrialreforged.tech.client.screen.multiblock;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.multiblock.TankControllerMenu;
import com.industrialreforged.tech.multiblock.tank.TankControllerBlockEntity;
import com.industrialreforged.tech.multiblock.tank.TankValveBlockEntity;
import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.screen.ScreenBase;
import com.industrialreforged.utils.NumberUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * GUI контроллера резервуара: готовая панель Extreme Reactors (перекрашена в бело-голубой)
 * со шкалами, делениями и иконками ER2. Без слотов — только показания вода/тепло/пар.
 */
public class TankControllerScreen extends ScreenBase<TankControllerMenu> {
   private static final ResourceLocation GUI = IR.texture("screen/tank_controller");
   private static final ResourceLocation ICONS = IR.texture("screen/tank_gui");

   private static final int WIDTH = 224;
   private static final int HEIGHT = 166;

   /** Полоса шкалы (рамка 18×66), заливка внутри 16×64. */
   private static final int BAR_Y = 48;
   private static final int BAR_W = 18;
   private static final int BAR_H = 66;
   private static final int FILL_H = 64;
   private static final int WATER_X = 36;
   private static final int HEAT_X = 100;
   private static final int STEAM_X = 164;
   private static final int SCALE_DX = 20;
   private static final int SCALE_Y = 50;

   private static final int LABEL_Y = 118;
   private static final int VALUE_Y = 129;
   private static final int STATUS_Y = 146;

   private static final int TEXT_DARK = 0x1E2A38;
   private static final int TEXT_LIGHT = 0xF2F8FF;
   private static final int STATUS_FORMED = 0x2E8B3E;
   private static final int STATUS_UNFORMED = 0xA83030;

   public TankControllerScreen(TankControllerMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = WIDTH;
      this.imageHeight = HEIGHT;
   }

   @Override
   protected ResourceLocation getTexture() {
      return GUI;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableWidget(new HelpButton(this.leftPos + 200, this.topPos + 22));
   }

   private TankControllerBlockEntity entity() {
      return this.menu.entity;
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      super.renderBg(graphics, partialTick, mouseX, mouseY);

      TankControllerBlockEntity entity = this.entity();
      int x = this.leftPos;
      int y = this.topPos;

      // рамки шкал и деления
      for (int bar : new int[]{WATER_X, HEAT_X, STEAM_X}) {
         ScreenApi.rect(graphics, ICONS, x + bar, y + BAR_Y, 0, 0, BAR_W, BAR_H);
         ScreenApi.rect(graphics, ICONS, x + bar + SCALE_DX, y + SCALE_Y, 66, 0, 5, 59);
      }

      // заливки: вода — синяя, тепло — родная ER2 (красная), пар — бело-голубая
      this.drawFill(graphics, WATER_X, entity.displayWater, entity.displayCapacity, 18);
      this.drawFill(graphics, HEAT_X, entity.displayHeat, TankValveBlockEntity.HEAT_CAPACITY, 34);
      this.drawFill(graphics, STEAM_X, entity.displaySteam, entity.displayCapacity, 50);

      // иконки ER2 над шкалами
      ScreenApi.rect(graphics, ICONS, x + WATER_X + 2, y + 30, 0, 66, 13, 14);
      ScreenApi.rect(graphics, ICONS, x + HEAT_X + 4, y + 16, 16, 66, 9, 30);
      ScreenApi.rect(graphics, ICONS, x + STEAM_X + 2, y + 32, 32, 66, 13, 12);
   }

   private void drawFill(GuiGraphics graphics, int barX, int amount, int capacity, int sourceX) {
      if (capacity <= 0 || amount <= 0) {
         return;
      }

      int filled = (int)Math.round(FILL_H * Math.min(1.0, amount / (double)capacity));
      if (filled <= 0) {
         return;
      }

      ScreenApi.rect(
         graphics, ICONS, this.leftPos + barX + 1, this.topPos + BAR_Y + 1 + (FILL_H - filled), sourceX, FILL_H - filled, 16, filled
      );
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);

      TankControllerBlockEntity entity = this.entity();
      boolean formed = entity.displayFormed != 0;

      this.drawCentered(graphics, Component.translatable("text.industrialreforged.tank_controller.water"), WATER_X + BAR_W / 2, LABEL_Y);
      this.drawCentered(graphics, Component.translatable("text.industrialreforged.tank_controller.heat"), HEAT_X + BAR_W / 2, LABEL_Y);
      this.drawCentered(graphics, Component.translatable("text.industrialreforged.tank_controller.steam"), STEAM_X + BAR_W / 2, LABEL_Y);

      this.drawCentered(graphics, Component.literal(NumberUtils.formatToSi(entity.displayWater)), WATER_X + BAR_W / 2, VALUE_Y);
      this.drawCentered(graphics, Component.literal(NumberUtils.formatToSi(entity.displayHeat)), HEAT_X + BAR_W / 2, VALUE_Y);
      this.drawCentered(graphics, Component.literal(NumberUtils.formatToSi(entity.displaySteam)), STEAM_X + BAR_W / 2, VALUE_Y);

      Component status = formed
         ? Component.translatable("text.industrialreforged.tank_controller.status.formed", this.format(entity.displayCapacity))
         : Component.translatable("text.industrialreforged.tank_controller.status.unformed");
      int color = formed ? STATUS_FORMED : STATUS_UNFORMED;
      // индикатор + текст центрируются вместе, чтобы группа стояла ровно по центру панели
      int groupWidth = 10 + 4 + this.font.width(status);
      int startX = (WIDTH - groupWidth) / 2;
      ScreenApi.rect(graphics, ICONS, startX, STATUS_Y + 1, formed ? 48 : 58, 66, 10, 10);
      graphics.drawString(this.font, status, startX + 14, STATUS_Y, color, true);

      // Подсказки по шкалам
      if (this.isHovering(WATER_X, BAR_Y, BAR_W, BAR_H, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(
            Component.translatable("text.industrialreforged.tank_controller.water.tooltip", this.format(entity.displayWater), this.format(entity.displayCapacity))
         );
      } else if (this.isHovering(HEAT_X, BAR_Y, BAR_W, BAR_H, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(
            Component.translatable(
               "text.industrialreforged.tank_controller.heat.tooltip", this.format(entity.displayHeat), this.format(TankValveBlockEntity.HEAT_CAPACITY)
            )
         );
      } else if (this.isHovering(STEAM_X, BAR_Y, BAR_W, BAR_H, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(
            Component.translatable("text.industrialreforged.tank_controller.steam.tooltip", this.format(entity.displaySteam), this.format(entity.displayCapacity))
         );
      }
   }

   private void drawCentered(GuiGraphics graphics, Component text, int centerX, int y) {
      graphics.drawString(this.font, text, centerX - this.font.width(text) / 2, y, TEXT_LIGHT, true);
   }

   private String format(int value) {
      return String.format("%,d", value);
   }

   /** Кнопка «как собрать»: открывает страницу мультиблока в JEI. */
   private final class HelpButton extends AbstractButton {
      private HelpButton(int x, int y) {
         super(x, y, 16, 16, Component.translatable("text.industrialreforged.tank_controller.help"));
      }

      @Override
      public void onPress() {
         TankControllerScreen.this.handleInventoryButtonClick(TankControllerMenu.BUTTON_HELP);
      }

      @Override
      protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
         int x = this.getX();
         int y = this.getY();
         ScreenApi.rect(graphics, ICONS, x, y, 32, 82, 16, 16);
         graphics.fill(x, y, x + 16, y + 1, 0xFF20304A);
         graphics.fill(x, y + 15, x + 16, y + 16, 0xFF20304A);
         graphics.fill(x, y, x + 1, y + 16, 0xFF20304A);
         graphics.fill(x + 15, y, x + 16, y + 16, 0xFF20304A);
         if (this.isHovered()) {
            graphics.fill(x + 1, y + 1, x + 15, y + 15, 0x40FFFFFF);
         }

         graphics.drawString(TankControllerScreen.this.font, "?", x + 6, y + 4, TEXT_DARK, false);
      }

      @Override
      protected void updateWidgetNarration(NarrationElementOutput output) {
         this.defaultButtonNarrationText(output);
      }
   }
}
