package com.industrialreforged.tech.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.IntConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class VanillaButton extends Button<VanillaButton> {
   public VanillaButton(int x, int y, int w, int h, @Nullable IntConsumer clickHandler) {
      super(x, y, w, h, clickHandler);
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      Minecraft minecraft = Minecraft.getInstance();
      graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      graphics.blitSprite(SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.getFGColor();
      this.renderString(graphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
      super.renderWidget(graphics, mouseX, mouseY, partialTick);
   }

   @Override
   protected int getTextColor(int mouseX, int mouseY) {
      return this.isMouseOver(mouseX, mouseY) ? 16777120 : 14737632;
   }
}
