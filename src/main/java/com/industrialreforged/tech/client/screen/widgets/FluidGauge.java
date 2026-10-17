package com.industrialreforged.tech.client.screen.widgets;

import com.industrialreforged.screen.ScreenApi;
import com.mojang.blaze3d.vertex.PoseStack;
import com.industrialreforged.tech.client.screen.IRScreenBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidGauge extends AbstractWidget {
   private final IFluidHandler handler;
   private final FluidGauge.TankGuiStyle style;
   private Fluid oldFluid;
   private int oldAmount;

   public static FluidGauge asNormal(int x, int y, IFluidHandler handler) {
      return new FluidGauge(x, y, 20, 55, handler, FluidGauge.TankGuiStyle.Normal);
   }

   public static FluidGauge asPlain(int x, int y, int w, int h, IFluidHandler handler) {
      return new FluidGauge(x, y, w, h, handler, FluidGauge.TankGuiStyle.Plain);
   }

   public static FluidGauge asBorderless(int x, int y, IFluidHandler handler, boolean bl) {
      return new FluidGauge(x, y, 12, 47, handler, bl ? FluidGauge.TankGuiStyle.BorderlessMirrored : FluidGauge.TankGuiStyle.Borderless);
   }

   private FluidGauge(int x, int y, int w, int h, IFluidHandler handler, FluidGauge.TankGuiStyle style) {
      super(x, y, w, h, Component.empty());
      this.handler = handler;
      this.style = style;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      PoseStack pose = graphics.pose();
      ScreenApi.setShaderTexture(IRScreenBase.COMMON);
      FluidStack fluidStack = this.handler.getFluidInTank(0);
      if (fluidStack.isEmpty()) {
         if (this.style.mirrorGauge) {
            pose.pushPose();
            pose.scale(-1.0F, 1.0F, 1.0F);
         }

         if (this.style.withBorder) {
            ScreenApi.rect(graphics, this.getX(), this.getY(), 70.0F, 100.0F, this.width, this.height);
         } else if (this.style.withGauge) {
            ScreenApi.rect(graphics, this.getX(), this.getY(), 74.0F, 104.0F, this.width, this.height);
         }

         if (this.style.mirrorGauge) {
            pose.popPose();
         }
      } else {
         if (this.style.withBorder) {
            ScreenApi.rect(graphics, this.getX(), this.getY(), 6.0F, 100.0F, this.width, this.height);
         }

         int x = this.getX();
         int y = this.getY();
         int w = this.width;
         int h = this.height;
         if (this.style.withBorder) {
            x += 4;
 y += 4;
 w = 12;
 h = 47;
         }

         ScreenApi.renderFluidTank(graphics, fluidStack, this.handler.getTankCapacity(0), x, y, w, h);
         if (this.style.withGauge) {
            int n8 = this.getX();
            int n9 = this.getY();
            if (!this.style.withBorder) {
               n8 -= 4;
               n9 -= 4;
            }

            if (this.style.mirrorGauge) {
               pose.pushPose();
               pose.scale(-1.0F, 1.0F, 1.0F);
            }

            ScreenApi.rect(graphics, IRScreenBase.COMMON, n8, n9, 38.0F, 100.0F, 20.0F, 55.0F);
            if (this.style.mirrorGauge) {
               pose.popPose();
            }
         }
      }

      if (this.active && this.isHovered() && this.isChange()) {
         this.setTooltip(
            Tooltip.create(
               fluidStack.isEmpty()
                  ? Component.translatable("text.industrialreforged.fluid.empty")
                  : Component.empty()
                     .append(fluidStack.getHoverName())
                     .append(": ")
                     .append(Component.translatable("text.industrialreforged.fluid.mb", new Object[]{fluidStack.getAmount()}))
            )
         );
      }
   }

   protected void updateWidgetNarration(NarrationElementOutput output) {
   }

   private boolean isChange() {
      FluidStack fluidStack = this.handler.getFluidInTank(0);
      if (fluidStack.is(this.oldFluid) && fluidStack.getAmount() == this.oldAmount) {
         return false;
      }

      this.oldFluid = fluidStack.getFluid();
      this.oldAmount = fluidStack.getAmount();
      return true;
   }

   private enum TankGuiStyle {
      Normal(true, true, false),
      Borderless(false, true, false),
      BorderlessMirrored(false, true, true),
      Plain(false, false, false);

      public final boolean withBorder;
      public final boolean withGauge;
      public final boolean mirrorGauge;

      TankGuiStyle(boolean bl, boolean bl2, boolean bl3) {
         this.withBorder = bl;
         this.withGauge = bl2;
         this.mirrorGauge = bl3;
      }
   }
}
