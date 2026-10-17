package com.industrialreforged.tech.core;

import com.industrialreforged.tech.core.util.Keyboard;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class SideProxyServer implements SideProxy {
   private static final Keyboard keyboard = new Keyboard();
   private static final Queue<Runnable> pendingTasks = new ArrayDeque<>();

   @Override
   public Keyboard getKeyboard() {
      return keyboard;
   }

   @Override
   public boolean isSimulating() {
      return true;
   }

   @Override
   public boolean isRendering() {
      return false;
   }

   @Override
   public void onServerAvailable(MinecraftServer minecraftServer) {
      synchronized (pendingTasks) {
         Runnable runnable;
         while ((runnable = pendingTasks.poll()) != null) {
            minecraftServer.execute(runnable);
         }
      }
   }

   @Override
   public Player getPlayerInstance() {
      return null;
   }

   @Override
   public Level getPlayerWorld() {
      return null;
   }

   @Override
   public RecipeManager getRecipeManager() {
      return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
   }
}
