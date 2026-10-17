package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.tech.blockentity.machine.MolecularFarmingBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.crop.IRCrops;
import com.industrialreforged.tech.crop.cropcard.CropBaseSapling;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.item.ItemCropSeed;
import com.industrialreforged.tech.menu.machine.MolecularFarmingMenu;
import java.awt.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class MolecularFarmingScreen extends IRScreenBase<MolecularFarmingMenu> {
   public MolecularFarmingScreen(MolecularFarmingMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.imageHeight = 192;
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(
         EnergyGauge.asBolt(this.leftPos + 135, this.topPos + 90, ((MolecularFarmingBlockEntity)((MolecularFarmingMenu)this.menu).entity).energy)
      );
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      this.renderTitle(graphics);
      if (this.isHovering(63, 76, 47, 24, mouseX, mouseY)) {
         FluidStack fluidStack = ((MolecularFarmingBlockEntity)((MolecularFarmingMenu)this.menu).entity).tank.getFluidInTank(0);
         if (fluidStack.isEmpty()) {
            this.setTooltipForNextRenderPass(Component.translatable("text.industrialreforged.fluid.empty"));
         } else {
            this.setTooltipForNextRenderPass(
               Component.empty()
                  .append(fluidStack.getHoverName())
                  .append(": ")
                  .append(Component.translatable("text.industrialreforged.fluid.mb", new Object[]{fluidStack.getAmount()}))
            );
         }
      }

      ItemStack stack = ((MolecularFarmingBlockEntity)((MolecularFarmingMenu)this.menu).entity).inputSlot.getItem();
      if (stack.getItem() instanceof ItemCropSeed) {
         CropData data = ItemCropSeed.getCropData(stack);
         if (data.isEmpty()) {
            graphics.drawCenteredString(
               this.font, Component.translatable("text.industrialreforged.tooltip.molecular_farming.unknown"), this.imageWidth / 2, 52, Color.RED.getRGB()
            );
         } else if (data.getScan() < 4) {
            graphics.drawCenteredString(
               this.font, Component.translatable("text.industrialreforged.tooltip.molecular_farming.analyze"), this.imageWidth / 2, 52, Color.RED.getRGB()
            );
         } else if (IRCrops.INSTANCE.getCropCard(stack) instanceof CropBaseSapling) {
            graphics.drawCenteredString(
               this.font, Component.translatable("text.industrialreforged.tooltip.molecular_farming.sapling"), this.imageWidth / 2, 52, Color.RED.getRGB()
            );
         } else {
            int speed = Mth.clamp(data.getGrowth() + data.getGain() + 1, 1, 63) * 100 / 63;
            graphics.drawCenteredString(
               this.font,
               Component.translatable("text.industrialreforged.tooltip.molecular_farming.status", new Object[]{speed + "%"}),
               this.imageWidth / 2,
               52,
               Color.GREEN.getRGB()
            );
         }
      } else if (MolecularFarmingBlockEntity.isVegetation(stack)) {
         graphics.drawCenteredString(
            this.font,
            Component.translatable("text.industrialreforged.tooltip.molecular_farming.vegetation", stack.getHoverName()),
            this.imageWidth / 2,
            52,
            Color.GREEN.getRGB()
         );
      }
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      MolecularFarmingBlockEntity farming = (MolecularFarmingBlockEntity)((MolecularFarmingMenu)this.menu).entity;
      int progress = (int)Math.min(23.0F * farming.progress / Math.max(1, farming.getTimeCreation()), 23.0F);
      if (progress >= 0) {
         graphics.blit(this.getTexture(), this.leftPos + 56, this.topPos + 44 - progress, 176, 37 - progress, 16, progress);
      }

      ScreenApi.renderFluidTank(
         graphics, ((MolecularFarmingBlockEntity)((MolecularFarmingMenu)this.menu).entity).tank, this.leftPos + 63, this.topPos + 76, 47, 24
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/molecular_farming");
   }
}
