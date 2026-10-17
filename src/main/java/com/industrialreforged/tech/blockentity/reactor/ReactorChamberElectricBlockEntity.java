package com.industrialreforged.tech.blockentity.reactor;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.tech.api.reactor.IReactorChamber;
import com.industrialreforged.tech.api.tile.IHeatSource;
import com.industrialreforged.tech.block.heat.HeatPipeBlock;
import com.industrialreforged.tech.blockentity.base.AbstractBlockEntity;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class ReactorChamberElectricBlockEntity extends AbstractBlockEntity
   implements IClientTick,
   IReactorChamber,
   IHeatSource,
   IContainerWrapper,
   IEnergyWrapper {
   private final EnumMap<Direction, BlockEntity> cacheTiles = new EnumMap<>(Direction.class);
   private NuclearReactorElectricBlockEntity reactor;

   public ReactorChamberElectricBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.REACTOR_CHAMBER.get(), blockPos, blockState);
      this.enableRedstoneSignal();
   }

   public InteractionResult onActivated(Player player, BlockHitResult result) {
      NuclearReactorElectricBlockEntity reactor = this.getReactor();
      return reactor != null ? reactor.getBlockState().useWithoutItem(this.level, player, result.withPosition(reactor.getBlockPos())) : InteractionResult.PASS;
   }

   public void onLoad() {
      super.onLoad();
      this.updateRedstoneSignal(this.getBlockState(), this.worldPosition);
   }

   public void clientTick() {
      NuclearReactorElectricBlockEntity entity = this.getReactor();
      if (entity != null) {
         NuclearReactorElectricBlockEntity.showHeatEffects(this.getLevel(), this.worldPosition, entity.getHeat());
      }
   }

   @Override
   public void updateRedstoneSignal(BlockState state, BlockPos neighbor) {
      super.updateRedstoneSignal(state, neighbor);
      // камера без ядра рядом просто остаётся на месте (раньше самоуничтожалась)
      NuclearReactorElectricBlockEntity reactor = this.getReactor();
      if (reactor != null) {
         reactor.updateRedstoneSignal(state, neighbor);

         for (Direction direction : Direction.values()) {
            this.cacheTiles.put(direction, this.level.getBlockEntity(this.worldPosition.relative(direction)));
         }
      }

      this.refreshPipeConnections();
   }

   /** Камера в структуре реактора — источник тепла: трубы рядом должны пересчитать отводы. */
   private void refreshPipeConnections() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      for (Direction direction : Direction.values()) {
         BlockPos pipePos = this.worldPosition.relative(direction);
         BlockState pipeState = this.level.getBlockState(pipePos);
         if (pipeState.getBlock() instanceof HeatPipeBlock pipe) {
            BlockState updated = pipe.connectionState(pipeState, this.level, pipePos);
            if (updated != pipeState) {
               this.level.setBlock(pipePos, updated, Block.UPDATE_ALL);
            }
         }
      }
   }

   /** Тепло берётся у ядра, к которому пристроена камера. */
   @Override
   public int getHeat(Direction side) {
      NuclearReactorElectricBlockEntity reactor = this.getReactor();
      return reactor != null ? reactor.getHeat(side) : 0;
   }

   @Override
   public int extractHeat(Direction side, int amount, boolean simulate) {
      NuclearReactorElectricBlockEntity reactor = this.getReactor();
      return reactor != null ? reactor.extractHeat(side, amount, simulate) : 0;
   }

   @Nullable
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      NuclearReactorElectricBlockEntity entity = this.getReactor();
      return entity != null ? entity.getContainerWrapper(direction) : null;
   }

   @Nullable
   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      NuclearReactorElectricBlockEntity entity = this.getReactor();
      return entity != null ? entity.getEnergyWrapper(direction) : null;
   }

   @Nullable
   public NuclearReactorElectricBlockEntity getReactorInstance() {
      return this.getReactor();
   }

   @Override
   public boolean isWall() {
      return false;
   }

   @Nullable
   private NuclearReactorElectricBlockEntity getReactor() {
      if (this.reactor == null || this.reactor.isRemoved()) {
         this.reactor = null;

         for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(direction));
            if (blockEntity instanceof NuclearReactorElectricBlockEntity) {
               this.reactor = (NuclearReactorElectricBlockEntity)blockEntity;
               break;
            }
         }
      }

      return this.reactor;
   }
}
