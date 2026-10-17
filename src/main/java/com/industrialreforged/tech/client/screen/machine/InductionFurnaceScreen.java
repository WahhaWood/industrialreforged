package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.tech.blockentity.machine.InductionFurnaceBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.InductionFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InductionFurnaceScreen extends IRScreenBase<InductionFurnaceMenu> {
   public InductionFurnaceScreen(InductionFurnaceMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.asBolt(this.leftPos + 55, this.topPos + 36, ((InductionFurnaceBlockEntity)((InductionFurnaceMenu)this.menu).entity).energy)
      );
      this.addRenderableOnly(
         RatioGauge.asArrow(this.leftPos + 80, this.topPos + 35, ((InductionFurnaceBlockEntity)((InductionFurnaceMenu)this.menu).entity)::getProgress)
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/induction_furnace");
   }
}
