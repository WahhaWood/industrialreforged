package com.industrialreforged.render.avatar;

import com.industrialreforged.screen.ScreenApi;
import com.industrialreforged.utils.ImageUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class PlayerIconGif implements IPlayerIcon {
   private final IntList delays;
   private final List<ResourceLocation> resourceLocations;
   private final long startTime;

   public PlayerIconGif(String username, IntList delays, List<BufferedImage> frames) throws IOException {
      this.delays = delays;
      this.resourceLocations = new ArrayList<>();
      this.startTime = System.currentTimeMillis();
      frames.stream()
         .map(ImageUtils::convert)
         .forEach(frame -> this.resourceLocations.add(Minecraft.getInstance().getTextureManager().register(username.toLowerCase(), new DynamicTexture(frame))));
   }

   @Override
   public void draw(GuiGraphics graphics, float x, float y, float sx, float sy, float alpha) {
      long currentTime = System.currentTimeMillis();
      long elapsedTime = currentTime - this.startTime;
      int totalTime = this.delays.intStream().sum();
      int currentTimeSlot = (int)(elapsedTime % totalTime);
      int sumDelay = 0;
      int frameToRender = 0;

      for (int i = 0; i < this.delays.size(); i++) {
         sumDelay += this.delays.get(i);
         if (currentTimeSlot < sumDelay) {
            frameToRender = i;
            break;
         }
      }

      graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
      ScreenApi.quad(graphics, this.resourceLocations.get(frameToRender), x, y, sx, sy);
   }
}
