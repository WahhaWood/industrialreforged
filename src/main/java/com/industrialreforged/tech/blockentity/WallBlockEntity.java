package com.industrialreforged.tech.blockentity;

import com.industrialreforged.tech.block.WallBlock;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.blockentity.comp.Obscuration;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WallBlockEntity extends AbstractBlockEntity {
   public final Obscuration obscuration;
   private DyeColor color = WallBlock.DEFAULT_COLOR;
   private volatile WallBlockEntity.WallRenderState renderState;

   public WallBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.WALL.get(), blockPos, blockState);
      this.obscuration = new Obscuration(this::markForUpdate);
   }

   public void setColor(DyeColor color) {
      this.color = color;
   }

   @Override
   public void loadAdditional(CompoundTag tag, @NotNull Provider registries) {
      super.loadAdditional(tag, registries);
      this.color = DyeColor.byId(tag.getByte("color"));
   }

   @Override
   public void saveAdditional(CompoundTag tag, @NotNull Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putByte("color", (byte)this.color.getId());
   }

   public void onLoad() {
      super.onLoad();
      if (this.getLevel().isClientSide) {
         this.updateRenderState();
      }
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putInt("color", this.color.getId());
      // Obscuration.save() returns null while nothing is applied, and CompoundTag.put
      // rejects null — the server threw on every update of a plain wall.
      CompoundTag obscuration = this.obscuration.save();
      if (obscuration != null) {
         tag.put("obscuration", obscuration);
      }
      return tag;
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      if (tag.contains("color")) {
         DyeColor color = DyeColor.byId(tag.getInt("color"));
         if (color != null) {
            this.recolor(color);
         }
      }
      if (tag.contains("obscuration")) {
         this.obscuration.load(tag.getCompound("obscuration"));
      }
   }

   protected boolean recolor(DyeColor dyeColor) {
      if (dyeColor == this.color) {
         return false;
      }

      this.color = dyeColor;
      if (!this.getLevel().isClientSide) {
         this.markForUpdate();
         this.markForSave();
      } else if (this.updateRenderState()) {
      }

      return true;
   }

   private boolean updateRenderState() {
      WallBlockEntity.WallRenderState renderState = new WallBlockEntity.WallRenderState(this.color, this.obscuration.getRenderState());
      if (renderState.equals(this.renderState)) {
         return false;
      }

      this.renderState = renderState;
      return true;
   }

   public record WallRenderState(DyeColor color, Obscuration.ObscurationData[] obscurations) {
      @Override
      public boolean equals(Object o) {
         return o == this
            ? true
            : o instanceof WallBlockEntity.WallRenderState(DyeColor color1, Obscuration.ObscurationData[] obscurations1)
               && color1 == this.color
               && Arrays.equals(obscurations1, this.obscurations);
      }

      @Override
      public int hashCode() {
         return this.color.hashCode() * 31 + Arrays.hashCode(this.obscurations);
      }

      @Override
      public String toString() {
         return "WallState<" + this.color + ", " + Arrays.toString(this.obscurations);
      }
   }
}
