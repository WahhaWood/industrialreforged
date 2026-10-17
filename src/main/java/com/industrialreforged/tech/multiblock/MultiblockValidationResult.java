package com.industrialreforged.tech.multiblock;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class MultiblockValidationResult {
   private final boolean success;
   private final Component message;
   @Nullable
   private final BlockPos errorPos;
   @Nullable
   private final BlockPos minPos;
   @Nullable
   private final BlockPos maxPos;
   private final int innerVolume;
   private final int capacity;
   private final List<BlockPos> valves;
   private final List<BlockPos> allBlocks;

   private MultiblockValidationResult(
      boolean success,
      Component message,
      @Nullable BlockPos errorPos,
      @Nullable BlockPos minPos,
      @Nullable BlockPos maxPos,
      int innerVolume,
      int capacity,
      List<BlockPos> valves,
      List<BlockPos> allBlocks
   ) {
      this.success = success;
      this.message = message;
      this.errorPos = errorPos;
      this.minPos = minPos;
      this.maxPos = maxPos;
      this.innerVolume = innerVolume;
      this.capacity = capacity;
      this.valves = valves;
      this.allBlocks = allBlocks;
   }

   public static MultiblockValidationResult success(
      BlockPos minPos, BlockPos maxPos, int innerVolume, int capacity, List<BlockPos> valves, List<BlockPos> allBlocks
   ) {
      Component msg = Component.translatable(
         "text.industrialreforged.multiblock.tank.success",
         String.format("%,d", capacity),
         innerVolume
      );
      return new MultiblockValidationResult(true, msg, null, minPos, maxPos, innerVolume, capacity, valves, allBlocks);
   }

   public static MultiblockValidationResult error(BlockPos errorPos, Component message) {
      return new MultiblockValidationResult(
         false, message, errorPos, null, null, 0, 0, Collections.emptyList(), Collections.emptyList()
      );
   }

   public static MultiblockValidationResult error(Component message) {
      return error(null, message);
   }

   public boolean isSuccess() {
      return this.success;
   }

   public Component getMessage() {
      return this.message;
   }

   @Nullable
   public BlockPos getErrorPos() {
      return this.errorPos;
   }

   @Nullable
   public BlockPos getMinPos() {
      return this.minPos;
   }

   @Nullable
   public BlockPos getMaxPos() {
      return this.maxPos;
   }

   public int getInnerVolume() {
      return this.innerVolume;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public List<BlockPos> getValves() {
      return this.valves;
   }

   public List<BlockPos> getAllBlocks() {
      return this.allBlocks;
   }
}
