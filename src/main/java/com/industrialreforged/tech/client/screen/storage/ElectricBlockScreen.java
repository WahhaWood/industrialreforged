package com.industrialreforged.tech.client.screen.storage;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.storage.ElectricBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.VanillaButton;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.storage.ElectricBlockMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ElectricBlockScreen extends IRScreenBase<ElectricBlockMenu> {
   private VanillaButton button;

   public ElectricBlockScreen(ElectricBlockMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.setBackgroundSlot(BackgroundSlot.CONTAINER);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBar(this.leftPos + 79, this.topPos + 38, ((ElectricBlockEntity)((ElectricBlockMenu)this.menu).entity).energy));
      this.button = (VanillaButton)this.addRenderableWidget(
         new VanillaButton(this.leftPos + 152, this.topPos + 4, 20, 20, var1 -> this.handleInventoryButtonClick(0))
            .withIcon(() -> new ItemStack(Items.REDSTONE))
      );
   }

   protected void containerTick() {
      super.containerTick();
      Component s = ((ElectricBlockEntity)((ElectricBlockMenu)this.menu).entity).getRedstoneMode();
      if (s.getContents() != PlainTextContents.EMPTY) {
         this.button.setTooltip(Tooltip.create(s));
      } else {
         this.button.setTooltip(null);
      }
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      super.renderBg(graphics, partialTick, mouseX, mouseY);

      for (int i = 0; i < 4; i++) {
         ScreenApi.quad(graphics, SLOT, this.leftPos + 7, this.topPos + 7 + 18 * i, 18.0F, 18.0F);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/basic");
   }
}
