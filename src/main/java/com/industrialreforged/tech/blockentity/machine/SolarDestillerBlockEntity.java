package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.tech.api.tile.FluidHandler;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.block.machine.SolarDestillerBlock;
import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByList;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByTank;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.menu.machine.SolarDestillerMenu;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class SolarDestillerBlockEntity extends AbstractMachineBlockEntity implements IUpgradableBlock, IFluidWrapper {
   public final SolarDestillerTank inputTank = new SolarDestillerTank(this, false, SolarDestillerGroup.CAPACITY_PER_BLOCK, stack -> stack.is(Fluids.WATER));
   public final SolarDestillerTank outputTank = new SolarDestillerTank(this, true, SolarDestillerGroup.CAPACITY_PER_BLOCK, stack -> true);
   public final InvSlotConsumableLiquidByList waterInputSlot = new InvSlotConsumableLiquidByList(
      this, "waterInput", 1, InvSlotConsumableLiquid.OpType.Drain, Fluids.WATER
   );
   public final InvSlotConsumableLiquidByTank destilledWaterInputSlot = new InvSlotConsumableLiquidByTank(
      this, "destilledWaterInput", 1, InvSlotConsumableLiquid.OpType.Fill, this.outputTank
   );
   public final InvSlotOutput waterOutputSlot = new InvSlotOutput(this, "waterOutput", 1);
   public final InvSlotOutput destilledWaterOutputSlot = new InvSlotOutput(this, "destilledWaterOutput", 1);
   public final InvSlotUpgrade upgradeSlot = new InvSlotUpgrade(this, 3);
   private int tickrate;
   private int updateTicker;
   private float skyLight;
   private FluidHandler fluidHandler;

   /** Shared buffers; null while this block is on its own. */
   @Nullable
   private SolarDestillerGroup group;
   private boolean groupDirty = true;

   public SolarDestillerBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.SOLAR_DISTILLER.get(), blockPos, blockState);
   }

   @Nullable
   public SolarDestillerGroup getDistillerGroup() {
      return this.group;
   }

   /** Marks a distiller (and therefore its whole group) for a rebuild. */
   public static void markGroupDirty(Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof SolarDestillerBlockEntity distiller) {
         distiller.groupDirty = true;
      }
   }

   private static void markNeighboursDirty(Level level, BlockPos pos) {
      for (Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos neighbour = pos.relative(direction);
         // never force a chunk load here: during chunk unload the main thread
         // would wait for a chunk that can no longer load and deadlock
         if (level.getChunkSource().getChunkNow(neighbour.getX() >> 4, neighbour.getZ() >> 4) == null) {
            continue;
         }

         if (level.getBlockState(neighbour).getBlock() instanceof SolarDestillerBlock) {
            markGroupDirty(level, neighbour);
         }
      }
   }

   /**
    * Rebuilds the shared buffers for the whole connected wall of distillers.
    * The contents of every member are collected first, so merging or splitting
    * groups never duplicates (or voids) fluid.
    */
   private static void rebuildGroup(SolarDestillerBlockEntity origin) {
      Level level = origin.level;
      if (level == null || level.isClientSide) {
         origin.groupDirty = false;
         return;
      }

      List<SolarDestillerBlockEntity> members = new ArrayList<>();
      Set<BlockPos> seen = new HashSet<>();
      Deque<BlockPos> queue = new ArrayDeque<>();
      seen.add(origin.worldPosition);
      queue.add(origin.worldPosition);
      BlockPos leader = origin.worldPosition;

      while (!queue.isEmpty()) {
         BlockPos pos = queue.poll();
         if (!(level.getBlockEntity(pos) instanceof SolarDestillerBlockEntity distiller)) {
            continue;
         }

         members.add(distiller);
         if (pos.compareTo(leader) < 0) {
            leader = pos;
         }

         for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos next = pos.relative(direction);
            if (seen.add(next) && level.getBlockState(next).getBlock() instanceof SolarDestillerBlock) {
               queue.add(next);
            }
         }
      }

      int water = 0;
      int distilled = 0;
      for (SolarDestillerBlockEntity member : members) {
         water += member.inputTank.target().getFluidAmount();
         distilled += member.outputTank.target().getFluidAmount();
         member.inputTank.target().setFluid(FluidStack.EMPTY);
         member.outputTank.target().setFluid(FluidStack.EMPTY);
      }

      SolarDestillerGroup group = new SolarDestillerGroup(members.size(), leader);
      group.input.setFluid(water > 0 ? new FluidStack(Fluids.WATER, Math.min(water, group.input.getCapacity())) : FluidStack.EMPTY);
      group.output.setFluid(
         distilled > 0 ? new FluidStack(IRFluids.DISTILLED_WATER.getFluidStill(), Math.min(distilled, group.output.getCapacity())) : FluidStack.EMPTY
      );

      for (SolarDestillerBlockEntity member : members) {
         member.group = group;
         member.groupDirty = false;
         member.markForSave();
      }
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_ELECTRO_FURNACE_LOOP.get();
   }

   @Override
   protected boolean isSoundproofed() {
      return this.upgradeSlot.soundproofing;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputTank.load(registries, tag, "input_tank");
      this.outputTank.load(registries, tag, "output_tank");
      this.waterInputSlot.load(tag, registries);
      this.destilledWaterInputSlot.load(tag, registries);
      this.waterOutputSlot.load(tag, registries);
      this.destilledWaterOutputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      // Only the leader writes the shared buffers, otherwise every member of a
      // group would save the same fluid and loading would duplicate it.
      if (this.group == null || this.group.isLeader(this.worldPosition)) {
         this.inputTank.save(registries, tag, "input_tank");
         this.outputTank.save(registries, tag, "output_tank");
      }

      this.waterInputSlot.save(tag, registries);
      this.destilledWaterInputSlot.save(tag, registries);
      this.waterOutputSlot.save(tag, registries);
      this.destilledWaterOutputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
   }

   public void onLoad() {
      super.onLoad();
      this.tickrate = this.getTickRate();
      this.updateTicker = IR.random.nextInt(this.tickrate);
      this.groupDirty = true;
   }

   @Override
   public void setRemoved() {
      if (this.level != null && !this.level.isClientSide) {
         markNeighboursDirty(this.level, this.worldPosition);
      }

      super.setRemoved();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SolarDestillerMenu(containerId, playerInventory, this);
   }

   public int getTickRate() {
      Holder<Biome> holder = this.level.getBiome(this.worldPosition);
      float temperature = ((Biome)holder.value()).getBaseTemperature();
      if (temperature >= 1.0F) {
         return 36;
      } else {
         return temperature <= 0.15F ? 144 : 72;
      }
   }

   public boolean canWork() {
      return this.inputTank.getFluidAmount() > 0 && this.outputTank.hasSpace() && this.skyLight > 0.5;
   }

   public float getSkyLightValue() {
      return this.skyLight;
   }

   public void setSkyLightValue(float value) {
      this.skyLight = value;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.FLUID_PRODUCING);
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.waterInputSlot, this.waterOutputSlot, this.inputTank);
      FluidSlotHelper.tick(this.destilledWaterInputSlot, this.destilledWaterOutputSlot, this.outputTank);
   }

   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (this.groupDirty) {
         rebuildGroup(this);
      }

      if (--this.updateTicker <= 0) {
         this.updateTicker = Math.max(1, this.tickrate);
         this.skyLight = getSkyLight(this.level, this.worldPosition);
      }

      SolarDestillerGroup group = this.group;
      if (group != null && !group.isLeader(this.worldPosition)) {
         return;
      }

      if (!this.canWork()) {
         return;
      }

      // hotter biomes distil faster: tickrate 36 -> 4 mB/t, 72 -> 2, 144 -> 1.
      // The whole wall distils as one machine, so the rate and the shared
      // buffers both scale with the number of joined distillers.
      int amount = Math.max(1, 144 / Math.max(1, this.tickrate));
      if (group != null) {
         amount *= group.size;
      }

      FluidStack drained = this.inputTank.drainForce(amount, FluidAction.SIMULATE);
      if (drained.isEmpty()) {
         return;
      }

      FluidStack distilled = new FluidStack(IRFluids.DISTILLED_WATER.getFluidStill(), drained.getAmount());
      int accepted = this.outputTank.fillForce(distilled, FluidAction.SIMULATE);
      if (accepted <= 0) {
         return;
      }

      this.inputTank.drainForce(accepted, FluidAction.EXECUTE);
      this.outputTank.fillForce(new FluidStack(distilled.getFluid(), accepted), FluidAction.EXECUTE);
      this.markForSave();
   }

   @Override
   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      if (direction == Direction.UP) {
         return this.inputTank;
      }
      if (direction == Direction.DOWN) {
         return this.outputTank;
      }
      if (this.fluidHandler == null) {
         this.fluidHandler = new FluidHandler(this.inputTank, this.outputTank);
      }

      return this.fluidHandler;
   }

   @Override
   public int getEnergyStored() {
      return 40;
   }

   @Override
   public boolean useEnergy(int d) {
      return true;
   }

   public static float getSkyLight(@Nullable Level level, BlockPos blockPos) {
      if (level != null && level.dimensionType().hasSkyLight()) {
         blockPos = blockPos.above();
         float f = Util.limit((float)Math.cos(level.getSunAngle(1.0F)) * 2.0F + 0.2F, 0.0F, 1.0F);
         if (!level.getBiome(blockPos).is(BiomeTags.IS_SAVANNA)) {
            f *= 1.0F - level.getRainLevel(1.0F) * 5.0F / 16.0F;
            f *= 1.0F - level.getThunderLevel(1.0F) * 5.0F / 16.0F;
            f = Util.limit(f, 0.0F, 1.0F);
         }

         return level.getBrightness(LightLayer.SKY, blockPos) / 15.0F * f;
      } else {
         return 0.0F;
      }
   }
}
