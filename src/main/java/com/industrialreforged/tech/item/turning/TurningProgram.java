package com.industrialreforged.tech.item.turning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TurningProgram(byte width, long states, String unlocalizedName) {
   public static final TurningProgram EMPTY = new TurningProgram((byte)0, 0L, "empty");
   public static final Codec<TurningProgram> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            Codec.BYTE.fieldOf("width").forGetter(TurningProgram::width),
            Codec.LONG.fieldOf("states").forGetter(TurningProgram::states),
            Codec.STRING.fieldOf("unlocalizedName").forGetter(TurningProgram::unlocalizedName)
         )
         .apply(inst, TurningProgram::new)
   );
   public static final StreamCodec<ByteBuf, TurningProgram> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BYTE,
      TurningProgram::width,
      ByteBufCodecs.VAR_LONG,
      TurningProgram::states,
      ByteBufCodecs.STRING_UTF8,
      TurningProgram::unlocalizedName,
      TurningProgram::new
   );

   public boolean isEmpty() {
      return this.width == 0 || this == EMPTY;
   }

   public byte state(int index) {
      return toState(this.states, index);
   }

   @Override
   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         TurningProgram that = (TurningProgram)o;
         return Objects.equals(this.unlocalizedName, that.unlocalizedName);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.unlocalizedName);
   }

   public static long toStates(byte[] bytes) {
      long states = 0L;

      for (int i = 0; i < bytes.length; i++) {
         states |= (long)bytes[i] << 8 * i;
      }

      return states;
   }

   public static byte toState(long value, int index) {
      return (byte)(value >> 8 * index & 255L);
   }
}
