package com.industrialreforged.utils;

import com.industrialreforged.IndustrialReforged;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PlayerUtils {
   public static Optional<String> getUsername(@Nullable UUID uuid) {
      if (uuid == null) {
         return Optional.empty();
      }

      {
         for (PlayerInfo __ : Minecraft.getInstance().player.connection.getOnlinePlayers()) {
            if (__.getProfile().getId().equals(uuid)) {
               Optional result = Optional.ofNullable(__.getProfile().getName());
               return result;
            }
         }
      }

      Optional __ = Optional.empty();
      return __;
   }

   public static Optional<UUID> getUUID(@Nullable String username) {
      if (username == null) {
         return Optional.empty();
      }

      {
         for (PlayerInfo __ : Minecraft.getInstance().player.connection.getOnlinePlayers()) {
            if (username.equalsIgnoreCase(__.getProfile().getName())) {
               Optional result = Optional.ofNullable(__.getProfile().getId());
               return result;
            }
         }
      }

      Optional __ = Optional.empty();
      return __;
   }
}
