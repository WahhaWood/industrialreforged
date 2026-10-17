package com.industrialreforged.tech.core;

import net.minecraft.world.level.Level;

public class TickHandler {
   public static void onWorldTickStart(Level level) {
      if (!level.isClientSide) {
         WorldData worldData = WorldData.get(level, false);
         if (worldData != null) {
            level.getProfiler().push("updates");
            processUpdates(level, worldData);
            level.getProfiler().pop();
         }
      }
   }

   public static void onClientTick() {
      Level level = IR.sideProxy.getPlayerWorld();
      if (level != null) {
         IR.keyboard.sendKeyUpdate();
         processUpdates(level, WorldData.get(level));
      }
   }

   public static void requestContinuousWorldTick(Level level, IWorldTickCallback update) {
      WorldData worldData = WorldData.get(level);
      if (!worldData.continuousUpdatesInUse) {
         worldData.continuousUpdates.add(update);
      } else {
         worldData.continuousUpdatesToRemove.remove(update);
         worldData.continuousUpdatesToAdd.add(update);
      }
   }

   public static void removeContinuousWorldTick(Level level, IWorldTickCallback update) {
      WorldData worldData = WorldData.get(level);
      if (!worldData.continuousUpdatesInUse) {
         worldData.continuousUpdates.remove(update);
      } else {
         worldData.continuousUpdatesToAdd.remove(update);
         worldData.continuousUpdatesToRemove.add(update);
      }
   }

   private static void processUpdates(Level level, WorldData worldData) {
      level.getProfiler().push("cont-update");
      worldData.continuousUpdatesInUse = true;

      for (IWorldTickCallback update : worldData.continuousUpdates) {
         update.onTick(level);
      }

      worldData.continuousUpdatesInUse = false;
      worldData.continuousUpdates.addAll(worldData.continuousUpdatesToAdd);
      worldData.continuousUpdatesToAdd.clear();
      worldData.continuousUpdates.removeAll(worldData.continuousUpdatesToRemove);
      worldData.continuousUpdatesToRemove.clear();
      level.getProfiler().pop();
   }
}
