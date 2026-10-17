package com.industrialreforged.utils;

import java.util.IllegalFormatException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class MsgUtils {
   private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)§[0-9A-FK-OR]");

   public static String getTextWithoutFormatting(String text) {
      return text == null ? null : FORMATTING_CODE.matcher(text).replaceAll("");
   }

   public static String translate(String key) {
      return Language.getInstance().getOrDefault(key);
   }

   public static String translate(@Nonnull String key, @Nonnull Object... args) {
      String s = translate(key);

      try {
         return String.format(s, args);
      } catch (IllegalFormatException illegalformatexception) {
         return "Format error: " + s;
      }
   }

   public static void print(@Nonnull CommandSource target, @Nonnull String text) {
      send(target, Component.literal(text));
   }

   public static void send(@Nonnull CommandSource target, @Nonnull String key) {
      send(target, Component.translatable(key));
   }

   public static void send(@Nonnull CommandSource target, @Nonnull String key, @Nonnull Object... args) {
      send(target, Component.translatable(key, args));
   }

   public static void send(@Nonnull CommandSource target, @Nonnull Component component) {
      target.sendSystemMessage(component);
   }

   public static String toString(Component component) {
      StringBuilder out = new StringBuilder();
      Style[] last = new Style[]{Style.EMPTY};
      component.visit((style, text) -> {
         if (!style.equals(last[0])) {
            appendStyleDiff(out, last[0], style);
            last[0] = style;
         }

         out.append(text);
         return Optional.empty();
      }, Style.EMPTY);
      return out.toString().replaceAll("(?i)&([0-9A-FK-OR])", "§$1");
   }

   private static void appendStyleDiff(StringBuilder out, Style oldS, Style newS) {
      if (!Objects.equals(oldS.getColor(), newS.getColor())) {
         if (newS.getColor() != null) {
            for (ChatFormatting value : ChatFormatting.values()) {
               if (value.getColor() != null && value.getColor() == newS.getColor().getValue()) {
                  out.append(value);
                  break;
               }
            }

            ChatFormatting formatting = ChatFormatting.getById(newS.getColor().getValue());
            if (formatting != null) {
               out.append('§').append(formatting.getChar());
            }
         } else {
            out.append("§r");
         }
      }

      toggle(out, oldS.isBold(), newS.isBold(), ChatFormatting.BOLD);
      toggle(out, oldS.isItalic(), newS.isItalic(), ChatFormatting.ITALIC);
      toggle(out, oldS.isUnderlined(), newS.isUnderlined(), ChatFormatting.UNDERLINE);
      toggle(out, oldS.isStrikethrough(), newS.isStrikethrough(), ChatFormatting.STRIKETHROUGH);
      toggle(out, oldS.isObfuscated(), newS.isObfuscated(), ChatFormatting.OBFUSCATED);
   }

   private static void toggle(StringBuilder out, boolean oldV, boolean newV, ChatFormatting fmt) {
      if (oldV != newV) {
         if (newV) {
            out.append('§').append(fmt.getChar());
         } else {
            out.append("§r");
         }
      }
   }
}
