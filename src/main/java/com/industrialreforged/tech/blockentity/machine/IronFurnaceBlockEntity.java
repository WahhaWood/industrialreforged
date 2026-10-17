package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.IClientTick;
import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotProcessableSmelting;
import com.industrialreforged.tech.container.SimpleFuelContainer;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.ParticleUtil;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.menu.machine.IronFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class IronFurnaceBlockEntity extends AbstractMachineBlockEntity implements IClientTick, ContainerData, IContainerWrapper {
   public final InvSlotProcessableSmelting<?> inputSlot = new InvSlotProcessableSmelting<>(this, "input", 1);
   public final InvSlotOutput outputSlot = new InvSlotOutput(this, 1);
   public final SimpleFuelContainer fuelSlot = new SimpleFuelContainer(this, true);
   public int fuel = 0;
   public int totalFuel = 0;
   public short progress = 0;
   protected Object audioSource;

   public IronFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.IRON_FURNACE.get(), blockPos, blockState);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.fuelSlot.load(tag, registries);
      // "fuel" is the fuel container's key.  The old build stored the burn counter under it
      // too, as an int, which overwrote the item list — the coal was gone after every reload.
      this.fuel = tag.contains("burnTime") ? tag.getInt("burnTime") : tag.getInt("fuel");
      this.totalFuel = tag.getInt("totalFuel");
      this.progress = tag.getShort("progress");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.fuelSlot.save(tag, registries);
      tag.putInt("burnTime", this.fuel);
      tag.putInt("totalFuel", this.totalFuel);
      tag.putShort("progress", this.progress);
   }

   public void clientTick() {
      if (this.isActive()) {
         Level level = this.getLevel();
         ParticleUtil.showFurnaceFlames(level, this.worldPosition, RotateBlock.getHorizontalFacing(this));
         if (level.random.nextDouble() < 0.1) {
            level.playLocalSound(
               this.worldPosition.getX() + 0.5,
               this.worldPosition.getY(),
               this.worldPosition.getZ() + 0.5,
               SoundEvents.FURNACE_FIRE_CRACKLE,
               SoundSource.BLOCKS,
               1.0F,
               1.0F,
               false
            );
         }

         if (this.audioSource == null) {
            this.audioSource = SoundUtil.loop(this, IRSounds.SOUND_MACHINE_ironfurnaceop);
         }

         if (SoundUtil.isStopped(this.audioSource)) {
            this.audioSource = SoundUtil.playSound(this.audioSource);
         }
      } else if (SoundUtil.isActive(this.audioSource)) {
         SoundUtil.stop(this.audioSource);
      }
   }

   public void setRemoved() {
      super.setRemoved();
      if (this.level.isClientSide) {
         SoundUtil.stop(this.audioSource);
      }
   }

   /** Плавит предметы на топливе (160 тиков на операцию). */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      super.serverTick();
      int fuelBefore = this.fuel;
      int progressBefore = this.progress;
      boolean active = false;
      if (this.canOperate()) {
         if (this.fuel <= 0) {
            int value = this.fuelSlot.consumeFuel();
            if (value > 0) {
               this.fuel = value;
               this.totalFuel = value;
            }
         }

         if (this.fuel > 0) {
            this.fuel--;
            this.progress++;
            active = true;
            if (this.progress >= 160) {
               this.progress = 0;
               this.inputSlot.assemble(0);
            }
         }
      } else {
         this.progress = 0;
      }

      com.industrialreforged.block.ActiveBlock.setActive(this, active);
      // Сборка слота сама метит через listener; здесь — только топливо/прогресс.
      if (this.fuel != fuelBefore || this.progress != progressBefore) {
         this.markForSave();
      }
   }

   private boolean canOperate() {
      SmeltingRecipe recipe = this.inputSlot.process(0);
      return recipe == null ? false : this.outputSlot.canAdd(recipe.getResultItem(RegistryAccess.EMPTY));
   }

   public float getProgress() {
      return this.progress == 0 ? 0.0F : this.progress / 160.0F;
   }

   public float getFuelRatio() {
      return this.fuel == 0 ? 0.0F : (float)this.fuel / this.totalFuel;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new IronFurnaceMenu(containerId, playerInventory, this);
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> this.fuel;
         case 1 -> this.totalFuel;
         case 2 -> this.progress;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.fuel = value;
            break;
         case 1:
            this.totalFuel = value;
            break;
         case 2:
            this.progress = (short)value;
      }
   }

   public int getCount() {
      return 3;
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         return new CombinedInvWrapper(
            new IItemHandlerModifiable[]{this.inputSlot.createWrapper(), this.fuelSlot.createWrapper(), this.outputSlot.createWrapper()}
         );
      }

      return switch (direction) {
         case UP -> this.inputSlot.createWrapper();
         case DOWN -> this.outputSlot.createWrapper();
         case NORTH, SOUTH, EAST, WEST -> this.fuelSlot.createWrapper();
      };
   }
}
