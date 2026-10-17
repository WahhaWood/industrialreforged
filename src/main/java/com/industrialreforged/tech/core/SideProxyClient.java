package com.industrialreforged.tech.core;

import com.industrialreforged.tech.core.util.Keyboard;
import com.industrialreforged.tech.core.util.KeyboardClient;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

public final class SideProxyClient implements SideProxy {
   public static final Minecraft mc = Minecraft.getInstance();
   private static final Keyboard keyboard = KeyboardClient.INSTANCE;

   @Override
   public boolean isSimulating() {
      return !this.isRendering();
   }

   @Override
   public Keyboard getKeyboard() {
      return keyboard;
   }

   @Override
   public boolean isRendering() {
      return Minecraft.getInstance().isSameThread();
   }

   @Override
   public void onServerAvailable(MinecraftServer minecraftServer) {
   }

   @Override
   public Player getPlayerInstance() {
      return mc.player;
   }

   @Override
   public Level getPlayerWorld() {
      return mc.level;
   }

   @Override
   public RecipeManager getRecipeManager() {
      return mc.level.getRecipeManager();
   }
}
