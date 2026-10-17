package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.base.AbstractStandardMachineBlockEntity;
import com.industrialreforged.tech.blockentity.machine.ElectricFurnace1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Extractor1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Macerator1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.Recycler1LevelBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.client.screen.widgets.builder.GaugeProperties;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.machine.StandardLevelMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StandardLevelMachineScreen extends IRScreenBase<StandardLevelMachineMenu> {
   public StandardLevelMachineScreen(StandardLevelMachineMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.setBackgroundSlot(BackgroundSlot.CONTAINER);
      this.imageWidth = 206;
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/level_machine");
   }

   @Override
   public void init() {
      super.init();
      this.titleLabelX -= 12;
      AbstractStandardMachineBlockEntity<?> machine = (AbstractStandardMachineBlockEntity)((StandardLevelMachineMenu)this.menu).entity;
      this.addRenderableOnly(
         EnergyGauge.asBolt(this.leftPos + 7, this.topPos + 46, machine.energy)
      );
      // тепло рисуется над молнией энергии (раньше пламя было справа от неё)
      this.addRenderableOnly(
         RatioGauge.create(this.leftPos + 7, this.topPos + 28, machine::getHeatRatio, Gauge.Fuel)
            .withTooltip(machine::getHeatTooltip)
      );
      GaugeProperties properties = this.getStyle();
      switch (((AbstractStandardMachineBlockEntity)((StandardLevelMachineMenu)this.menu).entity).inputSlot.getContainerSize()) {
         case 1:
            this.addProgress(80, 35, 0, properties);
            break;
         case 2:
            this.addProgress(80, 24, 0, properties);
            this.addProgress(80, 52, 1, properties);
            break;
         case 3:
            this.addProgress(75, 36, 0, properties);
            this.addProgress(85, 22, 1, properties);
            this.addProgress(85, 50, 2, properties);
            break;
         case 4:
            this.addProgress(69, 24, 0, properties);
            this.addProgress(69, 52, 1, properties);
            this.addProgress(94, 24, 2, properties);
            this.addProgress(94, 52, 3, properties);
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      switch (((AbstractStandardMachineBlockEntity)((StandardLevelMachineMenu)this.menu).entity).inputSlot.getContainerSize()) {
         case 2:
            ScreenApi.rect(graphics, this.texture, this.leftPos + 178, this.topPos + 77, 178.0F, 59.0F, 28.0F, 23.0F);
            break;
         case 3:
            ScreenApi.rect(graphics, this.texture, this.leftPos + 178, this.topPos + 77, 178.0F, 41.0F, 28.0F, 41.0F);
            break;
         case 4:
            ScreenApi.rect(graphics, this.texture, this.leftPos + 178, this.topPos + 77, 178.0F, 5.0F, 28.0F, 77.0F);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   private void addProgress(int x, int y, int index, GaugeProperties properties) {
      this.addRenderableOnly(
         RatioGauge.create(
            this.leftPos + x,
            this.topPos + y,
            () -> ((AbstractStandardMachineBlockEntity)((StandardLevelMachineMenu)this.menu).entity).getProgress(index),
            properties
         )
      );
   }

   private GaugeProperties getStyle() {
      BlockEntity entity = ((StandardLevelMachineMenu)this.menu).entity;
      if (entity instanceof Macerator1LevelBlockEntity) {
         return Gauge.ProgressCrush;
      } else if (entity instanceof Extractor1LevelBlockEntity) {
         return Gauge.ProgressDrop;
      } else if (entity instanceof Recycler1LevelBlockEntity) {
         return Gauge.ProgressArrow;
      } else {
         return entity instanceof ElectricFurnace1LevelBlockEntity ? Gauge.ProgressArrow : Gauge.ProgressTriangle;
      }
   }
}
