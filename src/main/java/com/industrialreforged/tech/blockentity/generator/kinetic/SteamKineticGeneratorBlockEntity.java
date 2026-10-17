package com.industrialreforged.tech.blockentity.generator.kinetic;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.tile.FluidHandler;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractKineticSourceBlockEntity;
import com.industrialreforged.tech.blockentity.machine.CondenserBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableClass;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.item.resources.ItemSteamTurbine;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.LiquidUtil;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.menu.generator.kinetic.SteamKineticGeneratorMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

public class SteamKineticGeneratorBlockEntity extends AbstractKineticSourceBlockEntity implements IUpgradableBlock, IFluidWrapper {
   public final FluidTank steamTank;
   public final FluidTank waterTank;
   public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, 1);
   public final InvSlotConsumable turbineSlot = new InvSlotConsumableClass(this, "turbine", 1, ItemSteamTurbine.class);
   private boolean ventingSteam;
   private boolean throttled;
   private boolean isTurbineFilledWithWater = false;
   private int progress = 0;
   private int updateTicker;
   private FluidHandler fluidHandler;

   public SteamKineticGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.STEAM_KINETIC_GENERATOR.get(), blockPos, blockState);
      this.turbineSlot.setMaxStackSize(1);
      this.steamTank = new FluidTank(
         21000, true, false, stack -> stack.is(IRFluids.STEAM.getFluidStill())
      );
      this.waterTank = new FluidTank(1000, false, true, stack -> stack.is(IRFluids.DISTILLED_WATER.getFluidStill()) || stack.is(Fluids.WATER));
      this.updateTicker = IR.random.nextInt(this.getTickRate());
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.steamTank.load(registries, tag, "steam");
      this.waterTank.load(registries, tag, "water");
      this.upgradeSlot.load(tag, registries);
      this.turbineSlot.load(tag, registries);
      this.progress = tag.getInt("progress");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.steamTank.save(registries, tag, "steam");
      this.waterTank.save(registries, tag, "water");
      this.upgradeSlot.save(tag, registries);
      this.turbineSlot.save(tag, registries);
      tag.putInt("progress", this.progress);
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SteamKineticGeneratorMenu(containerId, playerInventory, this);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.FLUID_CONSUMING, UpgradableProperty.FLUID_PRODUCING);
   }

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      if (direction == Direction.UP) {
         return this.steamTank;
      }
      if (direction == Direction.DOWN) {
         return this.waterTank;
      }
      if (this.fluidHandler == null) {
         this.fluidHandler = new FluidHandler(this.steamTank, this.waterTank);
      }

      return this.fluidHandler;
   }

   /** Крутит турбину паром; без турбины пар просто стравливается. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      if (!this.hasTurbine()) {
         int amount = this.steamTank.getFluidAmount();
         if (amount > 0) {
            this.steamTank.drainForce(amount, net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
            this.outputSteam(amount, true);
         }

         this.setPower(0);
         com.industrialreforged.block.ActiveBlock.setActive(this, false);
         return;
      }

      int powerBefore = this.getPower();
      int steamBefore = this.steamTank.getFluidAmount();
      if (this.steamTank.getFluidAmount() > 0) {
         this.turbineDoWork(false);
      } else {
         this.setPower(0);
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, this.getPower() > 0);
      // Турбина крутилась (пар/мощность изменились) — по делу; пустой простой — не пачкаем.
      if (this.getPower() != powerBefore || this.steamTank.getFluidAmount() != steamBefore) {
         this.markForSave();
      }
   }

   public boolean hasTurbine() {
      return !this.turbineSlot.isEmpty();
   }

   public boolean isVentingSteam() {
      return this.ventingSteam;
   }

   public boolean isThrottled() {
      return this.throttled;
   }

   public boolean isTurbineBlockedByWater() {
      return this.isTurbineFilledWithWater;
   }

   public int getTickRate() {
      return 20;
   }

   private int handleSteam(boolean bl) {
      int n = this.steamTank.getFluidAmount();
      assert n > 0;
      this.steamTank.drainForce(n, FluidAction.EXECUTE);
      int n2 = n * 2 * (bl ? 2 : 1);
      if (bl) {
         this.outputSteam(n, true);
      } else {
         int n3 = n / 10;
         this.progress += n3;
         this.outputSteam(n - n3, false);
      }

      return n2;
   }

   private boolean turbineDoWork(boolean bl) {
      ItemStack turbine = this.turbineSlot.getItem();
      float efficiency = turbine.getItem() instanceof ItemSteamTurbine steamTurbine ? steamTurbine.getEfficiency() : 1.0F;
      int n = this.handleSteam(bl);
      int n2 = this.waterTank.getFluidAmount();
      float f;
      if (n2 == 0) {
         this.throttled = false;
         // Сбрасываем здесь, а не в ветке ниже: флаг означает «турбину залило водой»,
         // и он должен сниматься, как только в баке появилось место. Раньше он
         // выставлялся в true и больше не менялся — слил воду, а статус «заблокировано
         // водой» остался навсегда, даже на сервере.
         this.isTurbineFilledWithWater = false;
         f = 1.0F;
      } else {
         this.throttled = true;
         f = 1.0F - (float)n2 / this.waterTank.getCapacity();
      }

      this.power = (int)(n * f * efficiency);
      if (this.power > 0) {
         this.damageTurbine(turbine);
      }

      if (this.progress >= 100) {
         if (this.waterTank.fillForce(new FluidStack(IRFluids.DISTILLED_WATER.getFluidStill(), 1), FluidAction.SIMULATE) == 1) {
            this.progress -= 100;
            this.waterTank.fillForce(new FluidStack(IRFluids.DISTILLED_WATER.getFluidStill(), 1), FluidAction.EXECUTE);
         } else {
            this.isTurbineFilledWithWater = true;
         }
      }

      return this.power > 0;
   }

   /** Турбина изнашивается, пока крутится; сломанная — исчезает из слота. */
   private void damageTurbine(ItemStack turbine) {
      if (turbine.getMaxDamage() <= 0) {
         return; // вечная турбина (иридий)
      }

      int damage = turbine.getDamageValue() + 1;
      if (damage >= turbine.getMaxDamage()) {
         this.turbineSlot.setItem(ItemStack.EMPTY);
      } else {
         turbine.setDamageValue(damage);
      }
   }

   private void outputSteam(int n, boolean bl) {
      Level level = this.getLevel();
      Direction[] arrdirection = Util.ALL_DIRS;
      int n3 = arrdirection.length;
      int i = 0;

      int n2;
      Direction direction;
      BlockEntity blockEntity;
      while (
         i < n3
            && (
               !((blockEntity = level.getBlockEntity(this.worldPosition.relative(direction = arrdirection[i]))) instanceof CondenserBlockEntity)
                     && (!bl || !(blockEntity instanceof SteamKineticGeneratorBlockEntity))
                  || (n2 = LiquidUtil.fillTile(blockEntity, direction.getOpposite(), new FluidStack(IRFluids.STEAM.getFluidStill(), n), FluidAction.EXECUTE))
                     <= 0
                  || (n -= n2) > 0
            )
      ) {
         i++;
      }

      this.ventingSteam = n > 0;
   }

   // ------------------------------------------------------------------
   // Синхронизация с клиентом
   //
   // Экран показывает isThrottled/isTurbineBlockedByWater/isVentingSteam. Без
   // getUpdatePacket клиентская копия вечно видит false по всем трём, и игрок
   // читает «жду пар, всё нормально» на турбине, которая на самом деле забита
   // водой и стоит колом. Баки синхронизируются через меню, флаги — здесь.
   // ------------------------------------------------------------------

   @Override
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putBoolean("ventingSteam", this.ventingSteam);
      tag.putBoolean("throttled", this.throttled);
      tag.putBoolean("turbineFilledWithWater", this.isTurbineFilledWithWater);
      return tag;
   }

   @Override
   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   @Override
   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.ventingSteam = tag.getBoolean("ventingSteam");
      this.throttled = tag.getBoolean("throttled");
      this.isTurbineFilledWithWater = tag.getBoolean("turbineFilledWithWater");
   }
}
