package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.OreWashingPlantBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.OreWashingPlantMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OreWashingPlantScreen extends IRScreenBase<OreWashingPlantMenu> {
   public OreWashingPlantScreen(OreWashingPlantMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.asBolt(this.leftPos + 8, this.topPos + 43, ((OreWashingPlantBlockEntity)((OreWashingPlantMenu)this.menu).entity).energy)
      );
      this.addRenderableOnly(
         FluidGauge.asNormal(this.leftPos + 60, this.topPos + 20, ((OreWashingPlantBlockEntity)((OreWashingPlantMenu)this.menu).entity).fluidTank)
      );
      this.addRenderableOnly(
         RatioGauge.create(
            this.leftPos + 103, this.topPos + 39, ((OreWashingPlantBlockEntity)((OreWashingPlantMenu)this.menu).entity)::getProgress, Gauge.ProgressOreWasher
         )
      );
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      ScreenApi.rect(graphics, this.texture, this.leftPos + 37, this.topPos + 16, 37.0F, 16.0F, 87.0F, 63.0F);
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/ore_washing_plant");
   }
}
