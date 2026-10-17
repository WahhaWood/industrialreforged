package com.industrialreforged.tech.client.screen.generator.electric;

import com.industrialreforged.tech.blockentity.generator.electric.SemifluidGeneratorBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.FluidGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.electric.SemifluidGeneratorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SemifluidGeneratorScreen extends IRScreenBase<SemifluidGeneratorMenu> {
   public SemifluidGeneratorScreen(SemifluidGeneratorMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.asBar(this.leftPos + 112, this.topPos + 29, ((SemifluidGeneratorBlockEntity)((SemifluidGeneratorMenu)this.menu).entity).energy)
      );
      this.addRenderableOnly(
         FluidGauge.asNormal(this.leftPos + 70, this.topPos + 20, ((SemifluidGeneratorBlockEntity)((SemifluidGeneratorMenu)this.menu).entity).tank)
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/fluid_generator");
   }
}
