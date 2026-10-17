package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.FermenterBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.FermenterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FermenterScreen extends IRScreenBase<FermenterMenu> {
   public FermenterScreen(FermenterMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 184;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         FluidGauge.asPlain(this.leftPos + 38, this.topPos + 49, 48, 30, ((FermenterBlockEntity)((FermenterMenu)this.menu).entity).getInputTank())
      );
      this.addRenderableOnly(
         FluidGauge.asNormal(this.leftPos + 125, this.topPos + 22, ((FermenterBlockEntity)((FermenterMenu)this.menu).entity).getOutputTank())
      );
      this.addRenderableOnly(
         RatioGauge.create(
               this.leftPos + 42, this.topPos + 41, () -> ((FermenterBlockEntity)((FermenterMenu)this.menu).entity).get(0) / 4000.0F, Gauge.HeatFermenter
            )
            .withTooltip(
               () -> Component.translatable(
                  "text.industrialreforged.tooltip.fermenter.conversion", new Object[]{((FermenterBlockEntity)((FermenterMenu)this.menu).entity).get(0) / 40.0F}
               )
            )
      );
      ((RatioGauge)this.addRenderableOnly(
            RatioGauge.create(
               this.leftPos + 38, this.topPos + 88, () -> ((FermenterBlockEntity)((FermenterMenu)this.menu).entity).get(1) / 500.0F, Gauge.ProgressFermenter
            )
         ))
         .setTooltip(Tooltip.create(Component.translatable("text.industrialreforged.tooltip.fermenter.waste")));
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/fermenter");
   }
}
