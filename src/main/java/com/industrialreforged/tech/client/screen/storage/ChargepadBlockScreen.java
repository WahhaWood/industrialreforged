package com.industrialreforged.tech.client.screen.storage;

import com.industrialreforged.screen.ScreenBase.BackgroundSlot;
import com.industrialreforged.tech.blockentity.storage.ChargepadBlockEntity;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import com.industrialreforged.tech.client.screen.widgets.EnergyGauge;
import com.industrialreforged.tech.client.screen.widgets.VanillaButton;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.storage.ChargepadBlockMenu;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ChargepadBlockScreen extends IRScreenBase<ChargepadBlockMenu> {
   private VanillaButton button;

   public ChargepadBlockScreen(ChargepadBlockMenu menu, Inventory inventory, Component component) {
      super(menu, inventory, component);
      this.setBackgroundSlot(BackgroundSlot.CONTAINER);
   }

   @Override
   public void init() {
      super.init();
      this.addRenderableOnly(EnergyGauge.asBar(this.leftPos + 79, this.topPos + 38, ((ChargepadBlockEntity)((ChargepadBlockMenu)this.menu).entity).energy));
      this.button = (VanillaButton)this.addRenderableWidget(
         new VanillaButton(this.leftPos + 152, this.topPos + 4, 20, 20, var1 -> this.handleInventoryButtonClick(0))
            .withIcon(() -> new ItemStack(Items.REDSTONE))
      );
   }

   protected void containerTick() {
      super.containerTick();
      Component s = ((ChargepadBlockEntity)((ChargepadBlockMenu)this.menu).entity).getRedstoneMode();
      if (s.getContents() != PlainTextContents.EMPTY) {
         this.button.setTooltip(Tooltip.create(s));
      } else {
         this.button.setTooltip(null);
      }
   }

   protected ResourceLocation getTexture() {
      return IR.texture("screen/basic");
   }
}
