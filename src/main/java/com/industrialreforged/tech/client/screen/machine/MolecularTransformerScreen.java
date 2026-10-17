package com.industrialreforged.tech.client.screen.machine;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.utils.NumberUtils;
import com.industrialreforged.tech.blockentity.machine.MolecularTransformerBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import com.industrialreforged.tech.menu.machine.MolecularTransformerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class MolecularTransformerScreen extends IRScreenBase<MolecularTransformerMenu> {
   public MolecularTransformerScreen(MolecularTransformerMenu container, Inventory inventory, Component component) {
      super(container, inventory, component);
      this.imageWidth = 220;
      this.imageHeight = 213;
   }

   @Override
   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      if (((MolecularTransformerBlockEntity)((MolecularTransformerMenu)this.menu).entity).recipe != null) {
         ScreenApi.rect(graphics, this.getTexture(), this.leftPos + 23, this.topPos + 48, 221.0F, 7.0F, 10.0F, this.getProgress(15) + 1);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      int xOffset = 56;
      int yOffset = 26;
      MolecularTransformerBlockEntity entity = (MolecularTransformerBlockEntity)((MolecularTransformerMenu)this.menu).entity;
      if (entity.recipe == null) {
         // Idle: show no input/output/energy/progress lines at all.
         return;
      }

      MolecularTransformerRecipe recipe = (MolecularTransformerRecipe)entity.recipe.value();
      graphics.drawString(
         this.font,
         Component.translatable(
            "text.industrialreforged.tooltip.molecular_transformer.input", new Object[]{recipe.getIngredient().ingredient().getItems()[0].getHoverName(), entity.count}
         ),
         xOffset,
         yOffset,
         16777215
      );
      graphics.drawString(
         this.font,
         Component.translatable(
            "text.industrialreforged.tooltip.molecular_transformer.output", new Object[]{recipe.getResultItem(RegistryAccess.EMPTY).getHoverName(), entity.count}
         ),
         xOffset,
         yOffset + 9,
         16777215
      );
      graphics.drawString(
         this.font,
         Component.translatable("text.industrialreforged.tooltip.molecular_transformer.cost", new Object[]{NumberUtils.formatToSi(recipe.energy), entity.count}),
         xOffset,
         yOffset + 9 * 2,
         16777215
      );
      graphics.drawString(
         this.font,
         Component.translatable("text.industrialreforged.fe.receive.tick", new Object[]{NumberUtils.formatToSi(entity.energyPerTick)}),
         xOffset,
         yOffset + 9 * 3,
         16777215
      );
      graphics.drawString(
         this.font,
         Component.translatable("text.industrialreforged.tooltip.molecular_transformer.progress", new Object[]{this.getProgress(100)}),
         xOffset,
         yOffset + 9 * 4,
         16777215
      );
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/molecular_transformer");
   }

   private int getProgress(int value) {
      float total = (float)((MolecularTransformerRecipe)((MolecularTransformerBlockEntity)((MolecularTransformerMenu)this.menu).entity).recipe.value()).energy
         * ((MolecularTransformerBlockEntity)((MolecularTransformerMenu)this.menu).entity).count;
      float used = total - (float)((MolecularTransformerBlockEntity)((MolecularTransformerMenu)this.menu).entity).energyLeft;
      return total == 0.0F ? 0 : (int)Mth.clamp(used / total * value, 0.0F, value);
   }
}
