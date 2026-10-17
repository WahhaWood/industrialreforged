package com.industrialreforged.tech.client.screen.widgets;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.core.util.SoundUtil;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class Button<T extends Button<T>> extends AbstractButton {
   @Nullable
   private final IntConsumer handler;
   private Supplier<String> textProvider;
   private Supplier<ItemStack> iconProvider;

   protected Button(int x, int y, int w, int h, @Nullable IntConsumer clickHandler) {
      super(x, y, w, h, Component.empty());
      this.handler = clickHandler;
   }

   public T withText(String string) {
      return this.withText(() -> string);
   }

   public T withText(Supplier<String> supplier) {
      this.textProvider = supplier;
      return (T)this;
   }

   public T withIcon(Supplier<ItemStack> supplier) {
      this.iconProvider = supplier;
      return (T)this;
   }

   protected int getTextColor(int n, int n2) {
      return 14540253;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      if (this.textProvider != null) {
         String string = this.textProvider.get();
         if (string != null && !string.isEmpty()) {
            string = MsgUtils.translate(string);
            graphics.drawString(
               Minecraft.getInstance().font, string, this.getX() + this.width / 2, this.getY() + this.height / 2, this.getTextColor(mouseX, mouseY), true
            );
         }
      } else {
         ItemStack itemStack;
         if (this.iconProvider != null && (itemStack = this.iconProvider.get()) != null && itemStack.getItem() != null) {
            ScreenApi.renderItem(graphics, itemStack, this.getX() + (this.width - 16) / 2.0F, this.getY() + (this.height - 16) / 2.0F, 1.0F);
         }
      }
   }

   public void playDownSound(SoundManager handler) {
      SoundUtil.play((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 0.25F, 1.0F);
   }

   public void onClick(double mouseX, double mouseY, int button) {
      if (this.handler != null) {
         this.handler.accept(button);
      }
   }

   public void onPress() {
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }
}
