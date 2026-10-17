package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.capabilities.IEnergyWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByList;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.core.util.ItemMoveHelper;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.menu.machine.MatterGeneratorMenu;
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
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class MatterGeneratorBlockEntity extends AbstractMachineBlockEntity implements IUpgradableBlock, IFluidWrapper, IEnergyWrapper, IClientTick, IContainerWrapper {
   /** Price of 1 mB of UU-matter without scrap. The tank holds exactly one cycle's price (ADR 0004). */
   public static final int ENERGY_PER_MB = 4_000_000;
   /** Scrap takes at most this much off the price of each mB (a quarter). */
   public static final int MAX_DISCOUNT_PER_MB = 1_000_000;
   /** Discount stored per scrap: 32 scrap make the full discount for 1 mB. */
   public static final int SCRAP_VALUE = 31_250;
   /** A scrap box is nine scrap. */
   public static final int SCRAP_BOX_VALUE = 9 * SCRAP_VALUE;
   private int lastEnergy;
   /** Discount stored from scrap, FE. Spent at most MAX_DISCOUNT_PER_MB per generated mB. */
   public long amplifier;
   /** Energy the current cycle needs (price minus usable discount); synced to the GUI. */
   public long cycleCost = ENERGY_PER_MB;
   private Object audioSource;
   public final EnergyStorage energy;
   public final InvSlotConsumableLiquid inputSlot;
   public final InvSlotOutput outputSlot;
   public final InvSlotUpgrade upgradeSlot;
   public final InvSlotConsumableItem amplifierSlot;
   public final FluidTank tank;

   public MatterGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.MATTER_GENERATOR.get(), blockPos, blockState);
      this.enableRedstoneSignal();
      this.energy = new EnergyStorage(ENERGY_PER_MB, 16000000, 0);
      this.inputSlot = new InvSlotConsumableLiquidByList(this, "container", 1, InvSlotConsumableLiquid.OpType.Fill, IRFluids.UU_MATTER.getFluidStill());
      this.outputSlot = new InvSlotOutput(this, 1);
      this.upgradeSlot = new InvSlotUpgrade(this, 4);
      this.amplifierSlot = new InvSlotConsumableItem(this, "amplifier", 1, IRItems.SCRAP.get(), IRItems.SCRAP_BOX.get());
      this.upgradeSlot.addListener(container -> this.setUpgradestat());
      this.tank = new FluidTank(8000, false, true, stack -> stack.is(IRFluids.UU_MATTER.getFluidStill()));
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.energy.load(tag, registries);
      this.inputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.amplifierSlot.load(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.lastEnergy = tag.getInt("lastEnergy");
      this.amplifier = tag.getLong("amplifier");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.energy.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.inputSlot.save(tag, registries);
      this.amplifierSlot.save(tag, registries);
      this.tank.save(registries, tag, "tank");
      tag.putInt("lastEnergy", this.lastEnergy);
      tag.putLong("amplifier", this.amplifier);
   }

   @Override
   public void updateRedstoneSignal(BlockState state, BlockPos neighbor) {
      boolean hasSignal = this.redstoneSignal;
      super.updateRedstoneSignal(state, neighbor);
      if (hasSignal != this.redstoneSignal) {
         this.energy.setReceive(this.upgradeSlot.invertRedstonePower != this.redstoneSignal ? 0 : this.energy.getDefReceive());
         this.setUpgradestat();
      }
   }

   public void onLoad() {
      super.onLoad();
      this.upgradeSlot.setChanged();
      this.setUpgradestat();
   }

   public void onChunkUnloaded() {
      if (IR.sideProxy.isRendering() && this.audioSource != null) {
         SoundUtil.stop(this.audioSource);
      }

      super.onChunkUnloaded();
   }

   public void clientTick() {
      if (!this.upgradeSlot.soundproofing && this.isActive()) {
         if (SoundUtil.isActive(this.audioSource)) {
            this.audioSource = SoundUtil.playLoop(this, IRSounds.SOUND_MACHINE_massfabloop.get(), this.audioSource);
         }
      } else if (this.audioSource != null) {
         SoundUtil.stop(this.audioSource);
         this.audioSource = null;
      }
   }

   /**
    * Копит энергию и, набрав цену цикла, делает 1 мБ жидкой материи (или 8 мБ за каждый ускоритель
    * частиц). Утильсырьё из слота усиления снимает с цены до четверти (IC2: массфабрикатор + scrap).
    */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      ItemMoveHelper.tick(this.level, this.worldPosition, this.upgradeSlot, this.amplifierSlot, this.outputSlot);
      this.loadAmplifier();
      this.cycleCost = this.getCycleCost();
      boolean active = false;
      if (this.energy.getStored() > 0) {
         active = true;
         if (this.energy.getStored() >= this.cycleCost) {
            this.attemptGeneration();
         }
      }

      this.setActive(active);
   }

   public boolean attemptGeneration() {
      int input = this.getMbPerCycle();
      if (this.tank.getFluidAmount() + input > this.tank.getCapacity()) {
         return false;
      }

      long discount = this.getUsableDiscount();
      long cost = (long)input * ENERGY_PER_MB - discount;
      if (this.energy.getStored() < cost) {
         return false;
      }

      this.tank.fillForce(new FluidStack(IRFluids.UU_MATTER.getFluidStill(), input), FluidAction.EXECUTE);
      this.energy.extractForceEnergy((int)cost);
      this.amplifier -= discount;
      this.cycleCost = this.getCycleCost();
      this.setChanged();
      return true;
   }

   /** mB per cycle: 1, or 8 per particle accelerator; capped so that a cycle's price fits the int tank. */
   public int getMbPerCycle() {
      int mb = this.upgradeSlot.particleAccelerator == 0 ? 1 : this.upgradeSlot.particleAccelerator * 8;
      return Math.min(mb, Integer.MAX_VALUE / ENERGY_PER_MB);
   }

   private long getUsableDiscount() {
      return Math.min(this.amplifier, (long)MAX_DISCOUNT_PER_MB * this.getMbPerCycle());
   }

   /** Energy the next cycle needs: full price minus the discount scrap can cover. */
   public long getCycleCost() {
      return (long)this.getMbPerCycle() * ENERGY_PER_MB - this.getUsableDiscount();
   }

   /** Feeds scrap from the amplifier slot until this cycle's discount is covered. */
   private void loadAmplifier() {
      ItemStack stack = this.amplifierSlot.getItem(0);
      if (stack.isEmpty()) {
         return;
      }

      long missing = (long)MAX_DISCOUNT_PER_MB * this.getMbPerCycle() - this.amplifier;
      if (missing <= 0) {
         return;
      }

      int value = getAmplifierValue(stack);
      if (value <= 0) {
         return;
      }

      int take = (int)Math.min(stack.getCount(), (missing + value - 1) / value);
      this.amplifierSlot.shrink(0, take);
      this.amplifier += (long)take * value;
      this.setChanged();
   }

   /** Discount a scrap item gives, FE (0 for anything else). */
   public static int getAmplifierValue(ItemStack stack) {
      if (stack.is(IRItems.SCRAP.get())) {
         return SCRAP_VALUE;
      }
      return stack.is(IRItems.SCRAP_BOX.get()) ? SCRAP_BOX_VALUE : 0;
   }

   public String getProgressAsString() {
      long cost = Math.max(1L, this.cycleCost);
      return String.format("%.0f%%", Math.min(100.0 * this.energy.getStored() / cost, 100.0));
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new MatterGeneratorMenu(containerId, playerInventory, this);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putBoolean("soundproofing", this.upgradeSlot.soundproofing);
      return tag;
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.upgradeSlot.soundproofing = tag.getBoolean("soundproofing");
   }

   public void setUpgradestat() {
      // the tank is one cycle's full price; mB per cycle is capped (see getMbPerCycle), so a
      // pile of accelerators no longer hits the int ceiling and gets matter below price
      int value = this.getMbPerCycle() * this.energy.getDefCapacity();
      this.energy.setReceive(value);
      this.energy.setCapacity(value);
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.REDSTONE_SENSITIVE,
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.SOUNDPROOFING,
         UpgradableProperty.ITEM_CONSUMING,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.FLUID_PRODUCING,
         UpgradableProperty.PARTICLE_ACCELERATOR
      );
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.inputSlot, this.outputSlot, this.tank);
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }

   public IEnergyStorage getEnergyWrapper(@Nullable Direction direction) {
      return this.energy;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         return new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.amplifierSlot.createWrapper(), this.outputSlot.createWrapper()}
         );
      }

      return switch (direction) {
         case DOWN -> this.outputSlot.createWrapper();
         case UP, NORTH, SOUTH, EAST, WEST -> new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.amplifierSlot.createWrapper()}
         );
      };
   }
}
