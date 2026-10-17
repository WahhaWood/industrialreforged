package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.CondenserBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.CondenserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CondenserScreen extends IRScreenBase<CondenserMenu> {
   public CondenserScreen(CondenserMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 184;
   }

   @Override
   public void init() {
      super.init();
      // энергия конденсатору не нужна — полоску не показываем
      this.addRenderableOnly(
         FluidGauge.asPlain(this.leftPos + 46, this.topPos + 27, 84, 33, ((CondenserBlockEntity)((CondenserMenu)this.menu).entity).getInputTank())
      );
      this.addRenderableOnly(
         FluidGauge.asPlain(this.leftPos + 46, this.topPos + 74, 84, 15, ((CondenserBlockEntity)((CondenserMenu)this.menu).entity).getOutputTank())
      );
      this.addRenderableOnly(
         RatioGauge.create(
            this.leftPos + 47, this.topPos + 63, () -> ((CondenserBlockEntity)((CondenserMenu)this.menu).entity).progress / 10000.0F, Gauge.ProgressCondenser
         )
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      // Верхний танк: вход пара
      if (this.isHovering(46, 27, 84, 33, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.condenser.steam"));
      }
      // Нижний танк: выход воды
      if (this.isHovering(46, 74, 84, 15, mouseX, mouseY)) {
         this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.tooltip.condenser.water"));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/condenser");
   }
}
