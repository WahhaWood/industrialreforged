package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.OreBreakerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Экран рудокрушителя: текстура и полоска прогресса под киркой. */
public class OreBreakerScreen extends IRScreenBase<OreBreakerMenu> {
   private static final ResourceLocation GAUGE = IR.texture("screen/ore_breaker_gauge");
   public OreBreakerScreen(OreBreakerMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = 200;
      this.inventoryLabelX = 20;
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      super.renderBg(graphics, partialTick, mouseX, mouseY);
      int opTime = Math.max(1, this.menu.entity == null ? 1 : this.menu.entity.opTime);
      int progress = this.menu.entity == null ? 0 : this.menu.entity.guiProgress;
      int height = 19;
      int filled = (int)Math.min(height, (long)height * progress / opTime);
      if (filled > 0) {
         // заполняем кирочку по мере работы (снизу вверх), как в других машинах
         graphics.blit(
            GAUGE,
            this.leftPos + 91,
            this.topPos + 44 + (height - filled),
            0,
            height - filled,
            17,
            filled,
            17,
            height
         );
      }
   }

   @Override
   protected ResourceLocation getTexture() {
      return IR.texture("screen/ore_breaker");
   }
}
