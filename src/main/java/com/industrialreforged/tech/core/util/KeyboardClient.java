package com.industrialreforged.tech.core.util;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.SideProxyClient;
import com.industrialreforged.tech.network.KeyUpdatePacket;
import java.util.EnumSet;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class KeyboardClient extends Keyboard {
   public static final KeyboardClient INSTANCE = new KeyboardClient();
   private static final String keyCategory = "industrialreforged";
   private final Minecraft mc = Minecraft.getInstance();
   private final KeyMapping altKey = new KeyMapping("key.industrialreforged.alt", 342, "industrialreforged");
   private final KeyMapping boostKey = new KeyMapping("key.industrialreforged.boost", 341, "industrialreforged");
   private final KeyMapping modeSwitchKey = new KeyMapping("key.industrialreforged.switch", 77, "industrialreforged");
   private final KeyMapping flyKey = new KeyMapping("key.industrialreforged.fly", 70, "industrialreforged");
   private int lastKeyState = 0;

   @Override
   public Component getAltKeyName() {
      return this.altKey.getTranslatedKeyMessage();
   }

   @Override
   public Component getBoostKeyName() {
      return this.boostKey.getTranslatedKeyMessage();
   }

   @Override
   public Component getModeSwitchKeyName() {
      return this.modeSwitchKey.getTranslatedKeyMessage();
   }

   @Override
   public Component getFlyKeyName() {
      return this.flyKey.getTranslatedKeyMessage();
   }

   @Override
   public void sendKeyUpdate() {
      EnumSet<Keyboard.Key> enumSet = EnumSet.noneOf(Keyboard.Key.class);
      Screen screen = SideProxyClient.mc.screen;
      if (screen == null) {
         if (this.altKey.isDown()) {
            enumSet.add(Keyboard.Key.alt);
         }

         if (this.boostKey.isDown()) {
            enumSet.add(Keyboard.Key.boost);
         }

         if (this.mc.options.keyUp.isDown()) {
            enumSet.add(Keyboard.Key.forward);
         }

         if (this.modeSwitchKey.isDown()) {
            enumSet.add(Keyboard.Key.modeSwitch);
         }

         if (this.flyKey.isDown()) {
            enumSet.add(Keyboard.Key.fly);
         }

         if (this.mc.options.keyJump.isDown()) {
            enumSet.add(Keyboard.Key.jump);
         }
      }

      int n;
      if ((n = Keyboard.Key.toInt(enumSet)) != this.lastKeyState) {
         IndustrialReforged.sendToServer(new KeyUpdatePacket(n), new CustomPacketPayload[0]);
         super.processKeyUpdate(IR.sideProxy.getPlayerInstance(), n);
         this.lastKeyState = n;
      }
   }

   @SubscribeEvent
   public void onRegistryKeyBinding(RegisterKeyMappingsEvent event) {
      event.register(this.altKey);
      event.register(this.boostKey);
      event.register(this.modeSwitchKey);
      event.register(this.flyKey);
   }
}
