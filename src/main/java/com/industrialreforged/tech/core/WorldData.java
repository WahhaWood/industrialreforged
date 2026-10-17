package com.industrialreforged.tech.core;

import com.industrialreforged.tech.core.util.Util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;

public class WorldData {
   private static final ConcurrentMap<ResourceLocation, WorldData> idxClient = FMLEnvironment.dist.isClient() ? new ConcurrentHashMap<>() : null;
   private static final ConcurrentMap<ResourceLocation, WorldData> idxServer = new ConcurrentHashMap<>();
   final Set<IWorldTickCallback> continuousUpdates = new HashSet<>();
   boolean continuousUpdatesInUse;
   final List<IWorldTickCallback> continuousUpdatesToAdd = new ArrayList<>();
   final List<IWorldTickCallback> continuousUpdatesToRemove = new ArrayList<>();

   private WorldData() {
   }

   public static WorldData get(Level level) {
      return get(level, true);
   }

   public static WorldData get(Level level, boolean bl) {
      if (level == null) {
         throw new IllegalArgumentException("world is null");
      }

      ConcurrentMap<ResourceLocation, WorldData> concurrentMap = getIndex(!level.isClientSide);
      WorldData worldData = concurrentMap.get(getKey(level));
      if (worldData == null && bl) {
         worldData = new WorldData();
         WorldData worldData2 = concurrentMap.putIfAbsent(getKey(level), worldData);
         if (worldData2 != null) {
            worldData = worldData2;
         }

         return worldData;
      } else {
         return worldData;
      }
   }

   public static void onWorldUnload(Level level) {
      getIndex(!level.isClientSide).remove(getKey(level));
   }

   private static ResourceLocation getKey(Level level) {
      return Util.getDimId(level);
   }

   private static ConcurrentMap<ResourceLocation, WorldData> getIndex(boolean bl) {
      return bl ? idxServer : idxClient;
   }
}
