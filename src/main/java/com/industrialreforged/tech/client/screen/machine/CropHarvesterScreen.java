package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.machine.CropHarvesterBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.CropHarvesterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CropHarvesterScreen extends IRScreenBase<CropHarvesterMenu> {
   public CropHarvesterScreen(CropHarvesterMenu containerCropHarvester, Inventory inventory, Component component) {
      super(containerCropHarvester, inventory, component);
      this.setBackgroundSlot(BackgroundSlot.CONTAINER);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 15, this.topPos + 36, ((CropHarvesterBlockEntity)((CropHarvesterMenu)this.menu).entity).energy));
   }

   public ResourceLocation getTexture() {
      return IR.texture("screen/basic");
   }
}
