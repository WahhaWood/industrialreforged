package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.ICommonTick;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class TeleporterBlockEntity extends AbstractBlockEntity implements ICommonTick {
   private static final Vector3f BLUE = Vec3.fromRGB24(1673925).toVector3f();
   private static final Vector3f GREEN = Vec3.fromRGB24(5814085).toVector3f();
   private BlockPos target;
   /** Измерение цели; null — то же измерение (обычный режим). */
   private ResourceLocation targetDimension;
   private Object audioSource = null;
   private int cooldown = 0;

   public TeleporterBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.TELEPORTER.get(), blockPos, blockState);
      this.enableRedstoneSignal();
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.contains("targetX")) {
         this.target = new BlockPos(tag.getInt("targetX"), tag.getInt("targetY"), tag.getInt("targetZ"));
      }

      if (tag.contains("targetDim")) {
         this.targetDimension = ResourceLocation.tryParse(tag.getString("targetDim"));
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (this.target != null) {
         tag.putInt("targetX", this.target.getX());
         tag.putInt("targetY", this.target.getY());
         tag.putInt("targetZ", this.target.getZ());
      }

      if (this.targetDimension != null) {
         tag.putString("targetDim", this.targetDimension.toString());
      }
   }

   public void onChunkUnloaded() {
      if (IR.sideProxy.isRendering() && this.audioSource != null) {
         SoundUtil.stop(this.audioSource);
      }

      super.onChunkUnloaded();
   }

   /**
    * Серверный тик: телепортер светится при наличии цели; сущности (кроме игроков),
    * стоящие на площадке, уезжают к цели по редстоун-сигналу. Игрок телепортируется
    * ПКМ пустой рукой (редстоун для этого не нужен).
    */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (this.cooldown > 0) {
         this.cooldown--;
      }

      this.setActive(this.target != null);
      if (this.cooldown > 0 || this.target == null || !this.isRedstoneSignal()) {
         return;
      }

      AABB pad = new AABB(this.worldPosition).move(0.0, 1.0, 0.0);
      for (Entity entity : this.level.getEntitiesOfClass(Entity.class, pad)) {
         if (!(entity instanceof Player) && entity.onGround()) {
            this.teleport(entity);
            break;
         }
      }
   }

   public void clientTick() {
      if (this.cooldown > 0) {
         this.cooldown--;
      }

      boolean active = this.isActive();
      if (this.isActive()) {
         this.spawnParticles(this.level, 2, this.worldPosition, this.cooldown > 0 ? GREEN : BLUE);
      }

      boolean play = SoundUtil.isActive(this.audioSource);
      if (this.audioSource == null) {
         this.audioSource = SoundUtil.loop(this, IRSounds.SOUND_MACHINE_telechargedloop);
      }

      if (play != active) {
         this.audioSource = active ? SoundUtil.playSound(this.audioSource) : SoundUtil.stop(this.audioSource);
      }
   }

   public void teleport(Entity entity) {
      if (this.target == null) {
         return;
      }

      double x = this.target.getX() + 0.5;
      double y = this.target.getY() + 1.5;
      double z = this.target.getZ() + 0.5;
      ServerLevel targetLevel = this.resolveTargetLevel();
      if (targetLevel != null && targetLevel != this.level) {
         // межмировой режим
         entity.teleportTo(targetLevel, x, y, z, Set.of(), entity.getYRot(), entity.getXRot());
      } else {
         entity.teleportTo(x, y, z);
      }

      this.cooldown = 60;
      this.level.playSound(null, entity, IRSounds.SOUND_MACHINE_iteleuse.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
      this.spawnParticles(this.level, 20, this.worldPosition, BLUE);
      this.spawnParticles(targetLevel != null ? targetLevel : this.level, 20, this.target, BLUE);
   }

   /** Целевое измерение, если связь межмировая и оно загружено. */
   private ServerLevel resolveTargetLevel() {
      if (this.targetDimension == null || this.level == null || this.level.getServer() == null) {
         return null;
      }

      return this.level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, this.targetDimension));
   }

   private void spawnParticles(Level level, int n, BlockPos blockPos, Vector3f color) {
      RandomSource random = level.random;
      DustParticleOptions dustParticleOptions = new DustParticleOptions(color, 1.0F);

      for (int i = 0; i < n; i++) {
         level.addParticle(
            dustParticleOptions,
            blockPos.getX() + random.nextFloat(),
            blockPos.getY() + 1 + random.nextFloat(),
            blockPos.getZ() + random.nextFloat(),
            0.0,
            0.0,
            0.0
         );
         level.addParticle(
            dustParticleOptions,
            blockPos.getX() + random.nextFloat(),
            blockPos.getY() + 2 + random.nextFloat(),
            blockPos.getZ() + random.nextFloat(),
            0.0,
            0.0,
            0.0
         );
      }
   }

   public boolean hasTarget() {
      return this.target != null;
   }

   public BlockPos getTarget() {
      return this.target;
   }

   @org.jetbrains.annotations.Nullable
   public ResourceLocation getTargetDimension() {
      return this.targetDimension;
   }

   public boolean isInterdimensional() {
      return this.targetDimension != null && this.level != null && !this.targetDimension.equals(this.level.dimension().location());
   }

   public void setTarget(BlockPos blockPos) {
      this.target = blockPos;
      this.targetDimension = null;
   }

   public void setTarget(BlockPos blockPos, @org.jetbrains.annotations.Nullable ResourceLocation dimension) {
      this.target = blockPos;
      this.targetDimension = dimension;
   }
}
