package com.industrialreforged.tech.core;

import com.industrialreforged.tech.core.util.Keyboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

public interface SideProxy {
   Keyboard getKeyboard();

   boolean isSimulating();

   boolean isRendering();

   void onServerAvailable(MinecraftServer var1);

   Player getPlayerInstance();

   Level getPlayerWorld();

   RecipeManager getRecipeManager();
}
