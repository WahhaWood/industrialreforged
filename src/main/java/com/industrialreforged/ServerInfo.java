package com.industrialreforged;

import com.industrialreforged.utils.TimeUtils;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerInfo {
   private final int id;
   private final String group;
   private final String name;
   private final String version;
   private final boolean active;
   private final boolean donate;
   private final ServerInfo.Wipe wipe;
   private final ServerInfo.Discount discount;

   public ServerInfo(FriendlyByteBuf buf) {
      this.id = buf.readVarInt();
      this.group = buf.readUtf();
      this.name = buf.readUtf();
      this.version = buf.readUtf();
      this.active = buf.readBoolean();
      this.donate = buf.readBoolean();
      this.wipe = new ServerInfo.Wipe(buf);
      this.discount = new ServerInfo.Discount(buf);
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(this.id);
      buf.writeUtf(this.group);
      buf.writeUtf(this.name);
      buf.writeUtf(this.version);
      buf.writeBoolean(this.active);
      buf.writeBoolean(this.donate);
      this.wipe.write(buf);
      this.discount.write(buf);
   }

   public int getId() {
      return this.id;
   }

   public String getGroup() {
      return this.group;
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }

   public boolean isActive() {
      return this.active;
   }

   public boolean isDonate() {
      return this.donate;
   }

   public ServerInfo.Wipe getWipe() {
      return this.wipe;
   }

   public ServerInfo.Discount getDiscount() {
      return this.discount;
   }

   public static class Discount {
      private final double shop;
      private final double group;
      @Nullable
      private final Instant begins;
      @Nullable
      private final Instant ends;

      public Discount(FriendlyByteBuf buf) {
         this(
            buf.readDouble(),
            buf.readDouble(),
            (Instant)buf.readNullable(FriendlyByteBuf::readInstant),
            (Instant)buf.readNullable(FriendlyByteBuf::readInstant)
         );
      }

      public Discount(double shop, double group, @Nullable Instant begins, @Nullable Instant ends) {
         this.shop = shop;
         this.group = group;
         this.begins = begins;
         this.ends = ends;
      }

      public double getShop() {
         return this.shop;
      }

      public double getGroup() {
         return this.group;
      }

      @NotNull
      public Optional<Instant> getBegins() {
         return Optional.ofNullable(this.begins);
      }

      @NotNull
      public Optional<Instant> getEnds() {
         return Optional.ofNullable(this.ends);
      }

      public boolean isWork() {
         if (this.ends == null) {
            return false;
         }

         Instant now = Instant.now();
         return (this.begins == null || this.begins.isBefore(now)) && this.ends.isAfter(now);
      }

      @NotNull
      public String getBegins(@NotNull DateTimeFormatter formatter) {
         return this.getBegins().map(i -> formatter.withZone(TimeUtils.moscowZone()).format(i)).orElse("-");
      }

      @NotNull
      public String getEnds(@NotNull DateTimeFormatter formatter) {
         return this.getEnds().map(i -> formatter.withZone(TimeUtils.moscowZone()).format(i)).orElse("-");
      }

      @Nullable
      public Duration getElapsedTimeBegins() {
         return TimeUtils.elapsedTime(this.begins);
      }

      @Nullable
      public Duration getElapsedTimeEnds() {
         return TimeUtils.elapsedTime(this.ends);
      }

      public void write(FriendlyByteBuf buf) {
         buf.writeDouble(this.shop);
         buf.writeDouble(this.group);
         buf.writeNullable(this.begins, FriendlyByteBuf::writeInstant);
         buf.writeNullable(this.ends, FriendlyByteBuf::writeInstant);
      }
   }

   public static class Wipe {
      @Nullable
      private final Instant time;
      private final boolean badge;
      private final int badgeDays;
      private final boolean openBeta;

      public Wipe(FriendlyByteBuf buf) {
         this((Instant)buf.readNullable(FriendlyByteBuf::readInstant), buf.readBoolean(), buf.readVarInt(), buf.readBoolean());
      }

      public Wipe(@Nullable Instant time, boolean badge, int badgeDays, boolean openBeta) {
         this.time = time;
         this.badge = badge;
         this.badgeDays = badgeDays;
         this.openBeta = openBeta;
      }

      @NotNull
      public Optional<Instant> getTime() {
         return Optional.ofNullable(this.time);
      }

      public boolean isBadge() {
         return this.badge;
      }

      public int getBadgeDays() {
         return this.badgeDays;
      }

      public boolean isOpenBeta() {
         return this.openBeta;
      }

      @Nullable
      public Duration getElapsedTime() {
         return TimeUtils.elapsedTime(this.time);
      }

      public int getDayPassed() {
         Duration duration = this.getElapsedTime();
         return duration != null ? (int)duration.toDays() : 0;
      }

      public void write(FriendlyByteBuf buf) {
         buf.writeNullable(this.time, FriendlyByteBuf::writeInstant);
         buf.writeBoolean(this.badge);
         buf.writeVarInt(this.badgeDays);
         buf.writeBoolean(this.openBeta);
      }
   }
}
