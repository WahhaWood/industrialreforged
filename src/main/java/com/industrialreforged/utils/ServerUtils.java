package com.industrialreforged.utils;

import com.industrialreforged.ServerInfo;

public class ServerUtils {
   public static ServerInfo serverInfo;

   public static ServerInfo getInfo() {
      return serverInfo;
   }

   public static int getId() {
      return serverInfo == null ? 0 : serverInfo.getId();
   }
}
