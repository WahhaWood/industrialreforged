package com.industrialreforged.tech.client.screen.generator.electric;

import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.generator.electric.RTGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.electric.RTGeneratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RTGeneratorScreen extends IRScreenBase<RTGeneratorMenu> {
   public RTGeneratorScreen(RTGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.setBackgroundSlot(BackgroundSlot.CONTAINER);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBar(this.leftPos + 115, this.topPos + 39, ((RTGeneratorBlockEntity)((RTGeneratorMenu)this.menu).entity).energy));
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/basic");
   }
}
