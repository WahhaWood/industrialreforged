package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.IronFurnaceBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.IronFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IronFurnaceScreen extends IRScreenBase<IronFurnaceMenu> {
   public IronFurnaceScreen(IronFurnaceMenu container, Inventory inventory, Component component) {
      super(container, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableWidget(
         RatioGauge.asFuel(this.leftPos + 56, this.topPos + 36, ((IronFurnaceBlockEntity)((IronFurnaceMenu)this.menu).entity)::getFuelRatio)
      );
      this.addRenderableWidget(
         RatioGauge.asArrow(this.leftPos + 80, this.topPos + 35, ((IronFurnaceBlockEntity)((IronFurnaceMenu)this.menu).entity)::getProgress)
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/iron_furnace");
   }
}
