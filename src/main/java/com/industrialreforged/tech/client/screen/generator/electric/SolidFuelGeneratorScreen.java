package com.industrialreforged.tech.client.screen.generator.electric;

import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.generator.electric.SolidFuelGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.electric.SolidFuelGeneratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolidFuelGeneratorScreen extends IRScreenBase<SolidFuelGeneratorMenu> {
   public SolidFuelGeneratorScreen(SolidFuelGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.setBackgroundSlot(BackgroundSlot.CONTAINER);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.asBar(this.leftPos + 100, this.topPos + 39, ((SolidFuelGeneratorBlockEntity)((SolidFuelGeneratorMenu)this.menu).entity).energy)
      );
      this.addRenderableOnly(
         RatioGauge.asFuel(this.leftPos + 57, this.topPos + 36, ((SolidFuelGeneratorBlockEntity)((SolidFuelGeneratorMenu)this.menu).entity)::getFuelRatio)
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/basic");
   }
}
