package com.industrialreforged.tech.blockentity.storage;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.storage.ChargepadBlockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import com.industrialreforged.block.ActiveBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class ChargepadBlockEntity extends ElectricBlockEntity implements IClientTick {
   private static final DustParticleOptions effect = new DustParticleOptions(new Vector3f(0.2F, 0.2F, 1.0F), 1.0F);
   private int updateTicker;
   private Player player = null;
   public static byte redstoneModes = 2;

   public ChargepadBlockEntity(
      int capacity, int receiveTick, int extractTick, BlockEntityType<? extends ChargepadBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState
   ) {
      super(capacity, receiveTick, extractTick, blockEntityType, blockPos, blockState);
      this.updateTicker = IR.random.nextInt(this.getTickRate());
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
   }

   public void entityInside(Entity entity) {
      if (entity instanceof ServerPlayer p) {
         this.player = p;
      }
   }

   protected int getTickRate() {
      return 2;
   }

   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      // getItems() was never called anywhere, so a chargepad did nothing at
      // all.  Charge whoever is standing on the pad, as intended.
      if (--this.updateTicker <= 0) {
         this.updateTicker = Math.max(1, this.getTickRate());
         // entityInside() runs every tick while somebody stands on the pad, so
         // dropping the reference here stops the charge immediately on step off
         Player player = this.player;
         this.player = null;
         if (player != null && player.isRemoved()) {
            player = null;
         }

         boolean inReach = player != null && player.distanceToSqr(
            this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5
         ) <= 1.5;
         if (!inReach) {
            player = null;
         }

         boolean active = player != null && this.energy.getStored() > 0 && this.shouldEmitEnergy();
         ActiveBlock.setActive(this, active);
         if (active) {
            this.getItems(player);
            this.markForSave();
         }
      }
   }

   public void clientTick() {
      Level level = this.getLevel();
      RandomSource random = level.random;
      if (random.nextInt(8) == 0) {
         if (this.isActive()) {
            for (int i = 20; i > 0; i--) {
               double d = this.worldPosition.getX() + 0.0F + random.nextFloat();
               double d2 = this.worldPosition.getY() + 0.9F + random.nextFloat();
               double d3 = this.worldPosition.getZ() + 0.0F + random.nextFloat();
               level.addParticle(effect, d, d2, d3, 0.0, 0.1, 0.0);
            }
         }
      }
   }

   /**
    * The pad used to offer the full transfer rate to every one of the 40 armour/inventory
    * slots, so it discharged up to 20× what its tooltip promised.  The rate is now a single
    * budget that the slots share (scaled by the tick rate, since the pad runs every other
    * tick), which keeps the advertised FE/t exactly.
    */
   protected void getItems(Player player) {
      if (player == null) {
         return;
      }

      long budget = (long)this.energy.getExtract() * Math.max(1, this.getTickRate());
      for (ItemStack itemStack : player.getInventory().armor) {
         budget = this.chargeItem(itemStack, budget);
      }

      for (ItemStack itemStack : player.getInventory().items) {
         budget = this.chargeItem(itemStack, budget);
      }
   }

   @Override
   protected boolean shouldEmitRedstone() {
      return this.redstoneMode == 0 && this.isActive() || this.redstoneMode == 1 && !this.isActive();
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new ChargepadBlockMenu(containerId, playerInventory, this);
   }

   @Override
   public void onNetworkEvent(Player player, int n) {
      this.redstoneMode++;
      if (this.redstoneMode >= redstoneModes) {
         this.redstoneMode = 0;
      }

      MsgUtils.send(player, this.getRedstoneMode());
   }

   @Override
   public Component getRedstoneMode() {
      return this.redstoneMode <= 1 && this.redstoneMode >= 0
         ? Component.translatable("text.industrialreforged.tooltip.charge_pad.mod.redstone." + this.redstoneMode)
         : Component.empty();
   }

   @Nullable
   @Override
   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return direction != Direction.UP ? this.energy : null;
   }

   /** Charges one slot from the shared budget and returns what is left of it. */
   protected long chargeItem(ItemStack itemStack, long budget) {
      if (budget <= 0L || itemStack == null) {
         return Math.max(0L, budget);
      }

      IEnergyStorage storage = (IEnergyStorage)itemStack.getCapability(EnergyStorage.ITEM);
      if (storage != null) {
         int moved = storage.receiveEnergy((int)Math.min(Math.min(this.energy.getEnergyStored(), budget), Integer.MAX_VALUE), false);
         if (moved > 0) {
            this.energy.extractForceEnergy(moved);
            return budget - moved;
         }
      }

      return budget;
   }
}
