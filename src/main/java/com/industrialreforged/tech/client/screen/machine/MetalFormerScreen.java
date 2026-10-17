package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.client.screen.widgets.RatioGauge;
import com.industrialreforged.tech.client.screen.widgets.VanillaButton;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.menu.machine.MetalFormerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class MetalFormerScreen extends IRScreenBase<MetalFormerMenu> {
   private VanillaButton button;
   private MetalFormerRecipe.Mode lastMode;

   public MetalFormerScreen(MetalFormerMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      if (((MetalFormer1LevelBlockEntity)menu.entity).inputSlot.getContainerSize() > 1) {
         this.setBackgroundSlot(BackgroundSlot.CONTAINER);
         this.imageWidth = 206;
      }
   }

   @Override
   public void init() {
      super.init();
      MetalFormer1LevelBlockEntity entity = (MetalFormer1LevelBlockEntity)((MetalFormerMenu)this.menu).entity;
      if (entity.inputSlot.getContainerSize() == 1) {
         this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 16, this.topPos + 36, entity.energy));
         this.addButtonChangeMode(entity, 65, 53);
      } else {
         this.imageWidth = 206;
         this.addRenderableOnly(EnergyGauge.asBolt(this.leftPos + 7, this.topPos + 38, entity.energy));
         this.addButtonChangeMode(entity, 7, 16);
      }

      switch (entity.inputSlot.getContainerSize()) {
         case 1:
            this.addProgress(52, 39, 0);
            break;
         case 2:
            this.addProgress(65, 24, 0);
            this.addProgress(65, 52, 1);
            break;
         case 3:
            this.addProgress(65, 38, 0);
            this.addProgress(75, 24, 1);
            this.addProgress(75, 52, 2);
            break;
         case 4:
            this.addProgress(73, 20, 0);
            this.addProgress(73, 34, 1);
            this.addProgress(73, 48, 2);
            this.addProgress(73, 62, 3);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
   }

   protected void containerTick() {
      super.containerTick();
      if (this.lastMode != ((MetalFormer1LevelBlockEntity)((MetalFormerMenu)this.menu).entity).getMode()) {
         this.lastMode = ((MetalFormer1LevelBlockEntity)((MetalFormerMenu)this.menu).entity).getMode();

         this.button.setTooltip(Tooltip.create(switch (this.lastMode) {
            case EXTRUDING -> Component.translatable("text.industrialreforged.tooltip.metal_former.switch.extruding");
            case CUTTING -> Component.translatable("text.industrialreforged.tooltip.metal_former.switch.cutting");
            case ROLLING -> Component.translatable("text.industrialreforged.tooltip.metal_former.switch.rolling");
         }));
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture(
         ((MetalFormer1LevelBlockEntity)((MetalFormerMenu)this.menu).entity).getClass() == MetalFormer1LevelBlockEntity.class
            ? "screen/metal_former"
            : "screen/metal_former_extended"
      );
   }

   private void addButtonChangeMode(MetalFormer1LevelBlockEntity tile, int x, int y) {
      this.button = (VanillaButton)this.addRenderableWidget(
         new VanillaButton(this.leftPos + x, this.topPos + y, 20, 20, o -> this.handleInventoryButtonClick(0)).withIcon(() -> {
            return switch (tile.getMode()) {
               case EXTRUDING -> new ItemStack((ItemLike)com.industrialreforged.transfer.init.GTItems.WIRE_2_LEVEL.get());
               case CUTTING -> new ItemStack((ItemLike)IRItems.CUTTER.get());
               case ROLLING -> new ItemStack((ItemLike)IRItems.FORGE_HAMMER.get());
            };
         })
      );
   }

   private void addProgress(int x, int y, int index) {
      this.addRenderableOnly(
         RatioGauge.create(
            this.leftPos + x,
            this.topPos + y,
            () -> ((MetalFormer1LevelBlockEntity)((MetalFormerMenu)this.menu).entity).getProgress(index),
            Gauge.ProgressMetalFormer
         )
      );
   }
}
