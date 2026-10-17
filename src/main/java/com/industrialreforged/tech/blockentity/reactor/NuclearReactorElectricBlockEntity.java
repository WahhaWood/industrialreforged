package com.industrialreforged.tech.blockentity.reactor;

import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.energy.EnergyStorage;
import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import com.industrialreforged.tech.api.tile.IHeatSource;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotReactor;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.menu.reactor.NuclearReactorMenu;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorElectricBlockEntity extends AbstractElectricMachineBlockEntity implements IReactor, IHeatSource, IContainerWrapper, IClientTick, ContainerData {
   /** Сколько FE даёт один импульс урана. */
   public static final int ENERGY_PER_PULSE = 500;
   public Object audioSourceMain;
   public Object audioSourceGeiger;
   private float lastOutput = 0.0F;
   public final InvSlotReactor reactorSlot;
   public float output = 0.0F;
   public int updateTicker = IR.random.nextInt(this.getTickRate());
   public int heat = 0;
   public int maxHeat = 10000;
   public float hem = 1.0F;
   public int EmitHeat = 0;
   private final EnumMap<Direction, BlockEntity> cacheEntities = new EnumMap<>(Direction.class);
   private int reactorSize = 3;
   private int lastReactorSize = 3;
   private InvWrapper containerWrapper;

   public NuclearReactorElectricBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.NUCLEAR_REACTOR.get(), blockPos, blockState, new EnergyStorage(64_000_000, 0, 65_536), false, false);
      this.enableRedstoneSignal();
      this.reactorSlot = new InvSlotReactor(this, "reactor", 54);
      this.reactorSlot.setMaxStackSize(1);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.reactorSlot.load(tag, registries);
      this.heat = tag.getInt("heat");
      this.output = tag.getShort("output");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.reactorSlot.save(tag, registries);
      tag.putInt("heat", this.heat);
      tag.putShort("output", (short)this.getReactorEnergyOutput());
   }

   public void onLoad() {
      super.onLoad();
   }

   public void onChunkUnloaded() {
      if (IR.sideProxy.isRendering()) {
         SoundUtil.stop(this.audioSourceMain);
         SoundUtil.stop(this.audioSourceGeiger);
         this.audioSourceMain = null;
         this.audioSourceGeiger = null;
      }

      super.onChunkUnloaded();
   }

   @Override
   public void updateRedstoneSignal(BlockState state, BlockPos neighbor) {
      super.updateRedstoneSignal(state, neighbor);

      for (Direction direction : Direction.values()) {
         this.cacheEntities.put(direction, this.level.getBlockEntity(this.worldPosition.relative(direction)));
      }

      this.reactorSize = 3;

      for (BlockEntity value : this.cacheEntities.values()) {
         if (value instanceof ReactorChamberElectricBlockEntity chamber) {
            this.redstoneSignal = this.redstoneSignal | chamber.isRedstoneSignal();
            this.reactorSize++;
         }
      }
   }

   public void clientTick() {
      showHeatEffects(this.getLevel(), this.worldPosition, this.heat);
   }

   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.containerWrapper == null) {
         this.containerWrapper = new InvWrapper(this.reactorSlot);
      }

      return this.containerWrapper;
   }

   public int getGeneration() {
      return (int)(this.getReactorEnergyOutput() * ENERGY_PER_PULSE / 20.0F);
   }

   public static void showHeatEffects(Level level, BlockPos blockPos, int n) {
      RandomSource random = level.random;
      if (random.nextInt(8) == 0) {
         int n2 = n / 1000;
         if (n2 > 0) {
            n2 = random.nextInt(n2);

            for (int n3 = 0; n3 < n2; n3++) {
               level.addParticle(
                  ParticleTypes.SMOKE, blockPos.getX() + random.nextFloat(), blockPos.getY() + 0.95F, blockPos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0
               );
            }

            for (int var7 = 0; var7 < (n2 -= random.nextInt(4) + 3); var7++) {
               level.addParticle(
                  ParticleTypes.FLAME, blockPos.getX() + random.nextFloat(), blockPos.getY() + 1, blockPos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0
               );
            }
         }
      }
   }

   /** Раз в секунду обрабатывает активную зону: топливо, тепло, энергия, перегрев. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      if (--this.updateTicker > 0) {
         return;
      }

      this.updateTicker = this.getTickRate();
      int heatBefore = this.heat;
      int storedBefore = this.energy.getStored();
      this.dropAllUnfittingStuff();
      this.output = 0.0F;
      this.processChambers();
      if (this.produceEnergy()) {
         int eu = (int)Math.min(this.output * ENERGY_PER_PULSE, 4_000_000.0F);
         if (eu > 0) {
            this.energy.injectForceEnergy(Math.min(eu, this.energy.getSpace()));
         }
      }

      if (this.heat >= this.maxHeat) {
         this.level.explode(
            null,
            this.worldPosition.getX() + 0.5,
            this.worldPosition.getY() + 0.5,
            this.worldPosition.getZ() + 0.5,
            8.0F,
            true,
            net.minecraft.world.level.Level.ExplosionInteraction.BLOCK
         );
         this.level.removeBlock(this.worldPosition, false);
         return;
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, this.produceEnergy());
      // Перекладка стержней сама метит через listener; здесь — тепло/энергия.
      if (this.heat != heatBefore || this.energy.getStored() != storedBefore) {
         this.markForSave();
      }
   }

   public void dropAllUnfittingStuff() {
      int reactorSize = this.getReactorSize();
      if (reactorSize != this.lastReactorSize) {
         if (this.lastReactorSize > reactorSize) {
            for (int row = 0; row < 6; row++) {
               for (int column = reactorSize; column < this.lastReactorSize; column++) {
                  ItemStack itemStack = this.reactorSlot.get(column, row);
                  if (!itemStack.isEmpty()) {
                     this.reactorSlot.put(column, row, ItemStack.EMPTY);
                     StackUtil.dropAsEntity(this.level, this.worldPosition, itemStack);
                  }
               }
            }
         }

         this.lastReactorSize = reactorSize;
      }
   }

   public void processChambers() {
      int reactorSize = this.getReactorSize();

      for (int i = 0; i < 2; i++) {
         for (int row = 0; row < 6; row++) {
            for (int column = 0; column < reactorSize; column++) {
               ItemStack itemStack = this.reactorSlot.get(column, row);
               if (itemStack.getItem() instanceof IReactorComponent component) {
                  component.processChamber(this, itemStack, column, row, i == 0);
               }
            }
         }
      }
   }

   @Override
   public boolean produceEnergy() {
      return this.redstoneSignal;
   }

   public int getReactorSize() {
      Level level = this.getLevel();
      return level == null ? 9 : this.reactorSize;
   }

   @Override
   public int getTickRate() {
      return 20;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new NuclearReactorMenu(containerId, playerInventory, this);
   }

   public void onNetworkUpdate(String string) {
      if (string.equals("output")) {
         if (this.output > 0.0F) {
            if (this.lastOutput <= 0.0F && SoundUtil.isStopped(this.audioSourceMain)) {
               this.audioSourceMain = SoundUtil.playLoop(this, IRSounds.SOUND_REACTOR_LOOP, this.audioSourceMain);
            }

            if (this.output < 40.0F) {
               if (this.lastOutput <= 0.0F || this.lastOutput >= 40.0F) {
                  this.audioSourceGeiger = SoundUtil.play(this, IRSounds.SOUND_REACTOR_LOW, this.audioSourceGeiger);
               }
            } else if (this.output < 80.0F) {
               if (this.lastOutput < 40.0F || this.lastOutput >= 80.0F) {
                  this.audioSourceGeiger = SoundUtil.play(this, IRSounds.SOUND_REACTOR_MED, this.audioSourceGeiger);
               }
            } else if (this.output >= 80.0F && this.lastOutput < 80.0F) {
               this.audioSourceGeiger = SoundUtil.play(this, IRSounds.SOUND_REACTOR_HIGH, this.audioSourceGeiger);
            }
         } else if (this.lastOutput > 0.0F) {
            SoundUtil.stop(this.audioSourceMain);
            SoundUtil.stop(this.audioSourceGeiger);
         }

         this.lastOutput = this.output;
      }
   }

   @Override
   public BlockPos getPosition() {
      return this.worldPosition;
   }

   @Override
   public Level getWorldObj() {
      return this.getLevel();
   }

   @Override
   public int getHeat() {
      return this.heat;
   }

   @Override
   public void setHeat(int value) {
      this.heat = value;
   }

   @Override
   public void addHeat(int value) {
      this.heat += value;
   }

   @Override
   public ItemStack getItemAt(int column, int row) {
      return column >= 0 && column < this.getReactorSize() && row >= 0 && row < 6 ? this.reactorSlot.get(column, row) : ItemStack.EMPTY;
   }

   @Override
   public void setItemAt(int column, int row, ItemStack stack) {
      if (column >= 0 && column < this.getReactorSize() && row >= 0 && row < 6) {
         this.reactorSlot.put(column, row, stack);
      }
   }

   @Override
   public int getMaxHeat() {
      return this.maxHeat;
   }

   @Override
   public void setMaxHeat(int value) {
      this.maxHeat = value;
   }

   @Override
   public float getHeatEffectModifier() {
      return this.hem;
   }

   @Override
   public void setHeatEffectModifier(float value) {
      this.hem = value;
   }

   @Override
   public float getReactorEnergyOutput() {
      return this.output;
   }

   /** Тепло реактора можно отводить тепловыми трубами (охлаждение / паровой реактор). */
   @Override
   public int getHeat(Direction side) {
      return this.heat;
   }

   @Override
   public int extractHeat(Direction side, int amount, boolean simulate) {
      int take = Math.min(amount, this.heat);
      if (take > 0 && !simulate) {
         this.heat -= take;
         this.markForSave();
      }

      return take;
   }

   @Override
   public void addOutput(float value) {
      this.output += value;
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> this.heat;
         case 1 -> this.maxHeat;
         case 2 -> this.EmitHeat;
         case 3 -> this.reactorSize;
         case 4 -> (int)(this.output * ENERGY_PER_PULSE / 20.0F);
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.heat = value;
            break;
         case 1:
            this.maxHeat = value;
            break;
         case 2:
            this.EmitHeat = value;
            break;
         case 3:
            this.reactorSize = value;
            break;
         case 4:
            this.output = value / 20.0F;
      }
   }

   public int getCount() {
      return 5;
   }
}
