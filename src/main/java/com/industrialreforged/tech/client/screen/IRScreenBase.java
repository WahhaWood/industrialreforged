package com.industrialreforged.tech.client.screen;

import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.screen.ScreenBase;
import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.utils.NumberUtils;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.IUpgradeItem;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.api.upgrade.UpgradeRegistry;
import com.industrialreforged.tech.client.screen.widgets.CustomButton;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.IRMenuBase;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class IRScreenBase<T extends IRMenuBase<?>> extends ScreenBase<T> {
   public static final ResourceLocation COMMON = IR.texture("screen/common");

   public IRScreenBase(T menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   public void init() {
      super.init();
      if (((IRMenuBase)this.menu).entity instanceof IUpgradableBlock upgradableBlock) {
         MutableComponent component = Component.translatable("text.industrialreforged.upgrade");
         Set<UpgradableProperty> set = upgradableBlock.getUpgradableProperties();

         for (IUpgradeItem item : UpgradeRegistry.getUpgradeItems()) {
            ItemStack stack = new ItemStack(item);
            if (item.isSuitableFor(stack, set)) {
               component.append("\n- ").append(stack.getHoverName());
            }
         }

         ((CustomButton)this.addRenderableOnly(new CustomButton(this.leftPos + 3, this.topPos + 3, 10, 10, 96, 128, COMMON, null)))
            .setTooltip(Tooltip.create(component));
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      if (((IRMenuBase)this.menu).entity instanceof IUpgradableBlock upgradableBlock && ((IRMenuBase)this.menu).entity instanceof IEnergyWrapper wrapper) {
         IEnergyStorage handler = wrapper.getEnergyWrapper(null);
         if (handler != null) {
            long storage = 0L;
            long capacity = -1L;
            if (handler instanceof BigEnergyStorage big) {
               storage = big.getStored();
               capacity = big.getCapacity();
            } else if (handler instanceof EnergyStorage basic) {
               storage = basic.getStored();
               capacity = basic.getCapacity();
            }

            if (capacity <= 0L) {
               return;
            }

            int consumption = upgradableBlock.getDisplayConsumption();
            int timeCreation = upgradableBlock.getTimeCreation();
            boolean isConsumption = upgradableBlock.getDisplayConsumption() != -1;
            boolean isTimeCreation = timeCreation != -1;
            boolean isStuck = isConsumption && consumption > capacity;
            // Голод, а не клин: работа начата, но на полный тик не хватает
            // (ускорения + укрепление умножают расход). Показываем жёлтым,
            // поверх красного клина не рисуем — это разные состояния.
            boolean isStarved = isConsumption && !isStuck && upgradableBlock.isStarved();
            String storageStr = MsgUtils.translate("text.industrialreforged.tooltip.upgrade.status.storage", new Object[]{NumberUtils.formatToSi(storage)});
            String capacityStr = MsgUtils.translate("text.industrialreforged.tooltip.upgrade.status.capacity", new Object[]{NumberUtils.formatToSi(capacity)});
            String consumptionStr = isConsumption
               ? MsgUtils.translate("text.industrialreforged.tooltip.upgrade.status.consumption", new Object[]{NumberUtils.formatInt(consumption)})
               : "";
            String timeCreationStr = isTimeCreation
               ? MsgUtils.translate("text.industrialreforged.tooltip.upgrade.status.time.creation", new Object[]{NumberUtils.formatInt(timeCreation)})
               : "";
            int line = 9;
            int height = line * 2;
            int width = Math.max(this.font.width(storageStr), this.font.width(capacityStr));
            if (isConsumption) {
               width = Math.max(width, this.font.width(consumptionStr));
               height += line;
            }

            if (isTimeCreation) {
               width = Math.max(width, this.font.width(timeCreationStr));
               height += line;
            }

            width += line;
            height += line;
            int x = -width - 3 + this.leftPos;
            int y = 9 / 2 + this.topPos;
            int green = 46622;
            int red = 13634315;
            int yellow = 16776960;
            ScreenApi.fill(graphics, x, this.topPos, width, height, -16777216);
            x += 9 / 2;
            graphics.drawString(this.font, storageStr, x, y, green);
            int var48;
            graphics.drawString(this.font, capacityStr, x, var48 = y + 9, isStuck ? red : green);
            y = var48;
            if (isConsumption) {
               graphics.drawString(this.font, consumptionStr, x, y += 9, isStarved ? yellow : green);
            }

            if (isTimeCreation) {
               graphics.drawString(this.font, timeCreationStr, x, y += 9, green);
            }

 y += 9 + 6;
            x -= width / 3;
            width += width / 3;
            if (isStuck) {
               String msgStuck = MsgUtils.translate("text.industrialreforged.tooltip.upgrade.status.error.stuck");
               List<FormattedText> rows = this.font.getSplitter().splitLines(msgStuck, width - 9, Style.EMPTY);
               height = 9 * (rows.size() + 1);
               graphics.fill(x - 9 / 2, y, x + width, y + height, -16777216);
 y += 9 / 2;

               for (FormattedText row : rows) {
                  graphics.drawString(this.font, row.getString(), x, y, red);
 y += 9;
               }

 y += 9 / 2 + 6;
            }

            if (isStarved) {
               String msgHunger = MsgUtils.translate("text.industrialreforged.tooltip.upgrade.status.warn.hunger");
               List<FormattedText> hungerRows = this.font.getSplitter().splitLines(msgHunger, width - 9, Style.EMPTY);
               height = 9 * (hungerRows.size() + 1);
               graphics.fill(x - 9 / 2, y, x + width, y + height, -16777216);
 y += 9 / 2;

               for (FormattedText row : hungerRows) {
                  graphics.drawString(this.font, row.getString(), x, y, yellow);
 y += 9;
               }

 y += 9 / 2 + 6;
            }

         }
      }
   }
}
