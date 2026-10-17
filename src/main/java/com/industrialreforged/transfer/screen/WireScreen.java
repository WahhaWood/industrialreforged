package com.industrialreforged.transfer.screen;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.screen.ScreenBase;
import com.industrialreforged.screen.widget.EditLongBox;
import com.industrialreforged.utils.NumberUtils;
import com.industrialreforged.transfer.block.WireBlockEntity;
import com.industrialreforged.transfer.core.WireMenu;
import com.industrialreforged.transfer.core.WireLimitPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Wire GUI.  Shows the real input/output flow and the tier limit, and lets the
 * player cap the flow straight from the screen (no resistor item needed).
 */
public class WireScreen extends ScreenBase<WireMenu> {
   private boolean remove;
   private boolean oldReplace;
   private boolean oldRemove;
   private int resistorMode;
   private long resistorValue;

   public WireScreen(WireMenu container, Inventory inventory, Component component) {
      super(container, inventory);
   }

   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("industrialreforged", "textures/screen/base.png");
   }

   public void init() {
      super.init();
      this.oldReplace = ((WireMenu)this.menu).replaceFlag;
      this.oldRemove = this.remove;
      this.resistorMode = ((WireMenu)this.menu).resistor.mode;
      this.resistorValue = ((WireMenu)this.menu).resistor.value;
      int x = this.leftPos;
      int y = this.topPos;

      if (this.remove) {
         this.addRenderableWidget(
            new Builder(Component.translatable("gui.industrialreforged.grid.break.yes"), this.onPress(0)).bounds(x + 24, y + 56, 56, 20).build()
         );
         this.addRenderableWidget(
            new Builder(Component.translatable("gui.industrialreforged.grid.break.no"), p_93751_ -> this.remove = false).bounds(x + 96, y + 56, 56, 20).build()
         );
         return;
      }

      if (!((WireMenu)this.menu).replaceFlag) {
         this.addRenderableWidget(
            new Builder(Component.translatable("gui.industrialreforged.grid.break"), p_93751_ -> this.remove = true).bounds(x + 8, y + 8, 76, 20).build()
         );
         this.addRenderableWidget(
            new Builder(Component.translatable("gui.industrialreforged.grid.replace"), this.onPress(1)).bounds(x + 92, y + 8, 76, 20).build()
         );

         // flow limit controls exist on the universal wire only
         if (((WireMenu)this.menu).resistor.mode != -1) {
            EditLongBox longBox = new EditLongBox(this.font, x + 8, y + 64, 110, 18);
            longBox.setHint(Component.translatable("gui.industrialreforged.text.infinity"));
            longBox.setConsumer(value -> IndustrialReforged.sendToServer(new WireLimitPayload(value), new CustomPacketPayload[0]));
            longBox.setMaxLength(25);
            longBox.setValue(((WireMenu)this.menu).resistor.value == -1L ? "" : String.valueOf(((WireMenu)this.menu).resistor.value));
            this.addRenderableWidget(longBox);

            String mode = ((WireMenu)this.menu).resistor.mode == 0 ? "FE/t" : "%";
            this.addRenderableWidget(new Builder(Component.literal(mode), this.onPress(2)).bounds(x + 122, y + 64, 46, 18).build());
         }
      }
   }

   public void renderBackground(GuiGraphics graphics, int mx, int my, float pt) {
      super.renderBackground(graphics, mx, my, pt);
      int x = this.leftPos;
      int y = this.topPos;
      int centerX = x + 88;
      if (this.remove) {
         this.drawCenterString(graphics, Component.translatable("gui.industrialreforged.text.remove.wire"), centerX, y + 34);
         this.drawCenterString(
            graphics, Component.translatable("gui.industrialreforged.text.wire.count.2", new Object[]{((WireMenu)this.menu).throwThisInt}), centerX, y + 43
         );
      } else if (((WireMenu)this.menu).replaceFlag) {
         this.drawCenterString(graphics, Component.translatable("gui.industrialreforged.text.set.wire"), centerX, y + 40);
         this.drawCenterString(
            graphics, Component.translatable("gui.industrialreforged.text.wire.count.0", new Object[]{((WireMenu)this.menu).throwThisInt}), centerX, y + 49
         );
         this.drawCenterString(
            graphics,
            Component.translatable("gui.industrialreforged.text.wire.count.1", new Object[]{((WireBlockEntity)((WireMenu)this.menu).entity).getBlock().level}),
            centerX,
            y + 58
         );
      } else {
         this.drawCenterString(
            graphics,
            Component.translatable("gui.industrialreforged.text.input", new Object[]{NumberUtils.formatToSi(((WireMenu)this.menu).throwThisLong)}),
            centerX,
            y + 33
         );
         this.drawCenterString(
            graphics, Component.translatable("gui.industrialreforged.text.output", new Object[]{NumberUtils.formatToSi(((WireMenu)this.menu).outputValue)}), centerX, y + 43
         );
         int transfer = ((WireMenu)this.menu).entity.getBlock().transfer;
         String limit = transfer < 0 ? I18n.get("gui.industrialreforged.text.infinity", new Object[0]) : NumberUtils.formatToSi(transfer);
         this.drawCenterString(
            graphics,
            Component.translatable("gui.industrialreforged.wire.limit", new Object[]{limit, ((WireMenu)this.menu).entity.getBlock().level}),
            centerX,
            y + 53
         );
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      if (this.oldRemove != this.remove
         || this.oldReplace != ((WireMenu)this.menu).replaceFlag
         || this.resistorMode != ((WireMenu)this.menu).resistor.mode
         || this.resistorValue != ((WireMenu)this.menu).resistor.value) {
         this.resize(this.minecraft, this.width, this.height);
      }
   }

   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
   }

   public boolean mouseClicked(double mx, double my, int button) {
      if (this.getFocused() instanceof EditLongBox box) {
         if (!box.isHovered()) {
            this.setFocused(null);
            return true;
         }

         if (button == 1) {
            box.setValue("");
            return true;
         }
      }

      return super.mouseClicked(mx, my, button);
   }

   public void onClose() {
      if (this.getFocused() instanceof EditLongBox) {
         this.setFocused(null);
      }

      super.onClose();
   }

   private void drawCenterString(GuiGraphics graphics, Component component, int x, int y) {
      graphics.drawString(this.font, component, x - this.font.width(component) / 2, y, -16777216, false);
   }

   private void drawCenterString(GuiGraphics graphics, String text, int x, int y) {
      graphics.drawString(this.font, text, x - this.font.width(text) / 2, y, -16777216, false);
   }

   private OnPress onPress(int value) {
      return p_93751_ -> this.minecraft.gameMode.handleInventoryButtonClick(((WireMenu)this.menu).containerId, value);
   }
}
