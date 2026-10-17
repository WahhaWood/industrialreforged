package com.industrialreforged.tech.client.screen.item;

import com.industrialreforged.utils.MsgUtils;
import com.google.common.base.Supplier;
import com.industrialreforged.tech.client.screen.widgets.TextLabel;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.item.CropnalyzerMenu;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CropnalyzerScreen extends AbstractContainerScreen<CropnalyzerMenu> {
   private final ResourceLocation background = IR.texture("screen/cropnalyzer");

   public CropnalyzerScreen(CropnalyzerMenu container, Inventory inventory, Component component) {
      super(container, inventory, component);
      this.imageHeight = 223;
   }

   protected void init() {
      super.init();
      this.addRenderableOnly(TextLabel.create(this.leftPos + 74, this.topPos + 11, MsgUtils.translate("item.industrialreforged.cropnalyzer"), 0, false));
      this.addRenderableOnly(
         TextLabel.create(this.leftPos + 8, this.topPos + 37, "UNKNOWN", 16777215, false)
            .withEnableHandler(() -> ((CropnalyzerMenu)this.menu).container.getScannedLevel() == 0)
      );
      this.addRenderableOnly(
         TextLabel.create(this.leftPos + 8, this.topPos + 37, this.cropSensitiveText(((CropnalyzerMenu)this.menu).container::getSeedName), 16777215, false)
            .withEnableHandler(this.atLeastLevel(1))
      );
      BooleanSupplier atLeast2 = this.atLeastLevel(2);
      this.addRenderableOnly(
         TextLabel.create(
               this.leftPos + 8,
               this.topPos + 50,
               this.cropSensitiveText(() -> "Tier: " + ((CropnalyzerMenu)this.menu).container.getSeedTier()),
               16777215,
               false
            )
            .withEnableHandler(atLeast2)
      );
      this.addRenderableOnly(TextLabel.create(this.leftPos + 8, this.topPos + 73, "Discovered by:", 16777215, false).withEnableHandler(atLeast2));
      this.addRenderableOnly(
         TextLabel.create(
               this.leftPos + 8, this.topPos + 86, this.cropSensitiveText(((CropnalyzerMenu)this.menu).container::getSeedDiscovered), 16777215, false
            )
            .withEnableHandler(atLeast2)
      );
      BooleanSupplier atLeast3 = this.atLeastLevel(3);
      this.addRenderableOnly(
         TextLabel.create(
               this.leftPos + 8, this.topPos + 109, this.cropSensitiveText(() -> ((CropnalyzerMenu)this.menu).container.getSeedDesc(0)), 16777215, false
            )
            .withEnableHandler(atLeast3)
      );
      this.addRenderableOnly(
         TextLabel.create(
               this.leftPos + 8, this.topPos + 122, this.cropSensitiveText(() -> ((CropnalyzerMenu)this.menu).container.getSeedDesc(1)), 16777215, false
            )
            .withEnableHandler(atLeast3)
      );
      BooleanSupplier atLeast4 = this.atLeastLevel(4);
      this.addRenderableOnly(TextLabel.create(this.leftPos + 118, this.topPos + 37, "Growth:", 11403055, false).withEnableHandler(atLeast4));
      this.addRenderableOnly(
         TextLabel.create(
               this.leftPos + 118,
               this.topPos + 50,
               this.cropSensitiveText(() -> Integer.toString(((CropnalyzerMenu)this.menu).container.getSeedGrowth())),
               11403055,
               false
            )
            .withEnableHandler(atLeast4)
      );
      this.addRenderableOnly(TextLabel.create(this.leftPos + 118, this.topPos + 73, "Gain:", 15649024, false).withEnableHandler(atLeast4));
      this.addRenderableOnly(
         TextLabel.create(
               this.leftPos + 118,
               this.topPos + 86,
               this.cropSensitiveText(() -> Integer.toString(((CropnalyzerMenu)this.menu).container.getSeedGain())),
               15649024,
               false
            )
            .withEnableHandler(atLeast4)
      );
      this.addRenderableOnly(TextLabel.create(this.leftPos + 118, this.topPos + 109, "Resis.:", 52945, false).withEnableHandler(atLeast4));
      this.addRenderableOnly(
         TextLabel.create(
               this.leftPos + 118,
               this.topPos + 122,
               this.cropSensitiveText(() -> Integer.toString(((CropnalyzerMenu)this.menu).container.getSeedResistence())),
               52945,
               false
            )
            .withEnableHandler(atLeast4)
      );
   }

   private BooleanSupplier atLeastLevel(int level) {
      return () -> ((CropnalyzerMenu)this.menu).container.getScannedLevel() >= level;
   }

   private Supplier<String> cropSensitiveText(Supplier<String> text) {
      return () -> ((CropnalyzerMenu)this.menu).container.getScannedLevel() > -1 ? (String)text.get() : "";
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      this.renderTooltip(graphics, mouseX, mouseY);
   }

   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      graphics.blit(this.background, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
   }

   protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
   }
}
