package com.industrialreforged.tech.multiblock.tank;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.menu.multiblock.TankControllerMenu;
import com.industrialreforged.tech.multiblock.MultiblockValidationResult;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Контроллер резервуара-котла (по прототипу контроллера Extreme Reactors): часть оболочки
 * мультиблока, показывает состояние (вода/пар/тепло) и открывает GUI. Сам не хранит жидкость —
 * читает мастер-клапан структуры.
 */
public class TankControllerBlockEntity extends BaseBlockEntity implements IServerTick, MenuProvider {
   private boolean formed = false;
   @Nullable
   private BlockPos masterPos = null;
   private int capacity = 0;
   private int innerVolume = 0;
   @Nullable
   private BlockPos minPos = null;
   @Nullable
   private BlockPos maxPos = null;

   // Зеркало для GUI: сервер обновляет из мастера, меню синхронизирует клиенту.
   public int displayWater = 0;
   public int displaySteam = 0;
   public int displayHeat = 0;
   public int displayCapacity = 0;
   public int displayFormed = 0;

   public TankControllerBlockEntity(BlockPos pos, BlockState state) {
      super(IRBlockEntities.TANK_CONTROLLER.get(), pos, state);
   }

   public boolean isFormed() {
      return this.formed;
   }

   @Nullable
   public BlockPos getMasterPos() {
      return this.masterPos;
   }

   /** Мастер-клапан структуры (хранит воду/пар/тепло). */
   @Nullable
   public TankValveBlockEntity getMasterValve() {
      if (this.level == null || this.masterPos == null) {
         return null;
      }
      return this.level.getBlockEntity(this.masterPos) instanceof TankValveBlockEntity valve ? valve : null;
   }

   public int getCapacity() {
      return this.capacity;
   }

   public int getInnerVolume() {
      return this.innerVolume;
   }

   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      TankValveBlockEntity master = this.getMasterValve();
      if (master != null && master.isFormed()) {
         this.displayWater = master.waterTank.getFluidAmount();
         this.displaySteam = master.steamTank.getFluidAmount();
         this.displayHeat = master.getHeat();
         this.displayCapacity = master.getCapacity();
         this.displayFormed = 1;
      } else {
         this.displayFormed = 0;
      }
   }

   /** Валидация из контроллера: мастером становится уже собранный клапан, иначе первый в структуре. */
   public boolean validateStructure(@Nullable Player player) {
      if (this.level == null || this.level.isClientSide) {
         return false;
      }

      MultiblockValidationResult result = MultiblockTankValidator.validateWithFallback(this.level, this.worldPosition);
      if (!result.isSuccess()) {
         if (this.formed) {
            this.disbandStructure();
         }
         if (player != null) {
            player.sendSystemMessage(result.getMessage());
         }
         return false;
      }

      List<BlockPos> valves = result.getValves();
      BlockPos master = valves.isEmpty() ? null : valves.get(0);

      // Если в структуре уже есть собранный мастер-клапан — сохраняем его (не теряем содержимое)
      for (BlockPos vPos : valves) {
         if (this.level.getBlockEntity(vPos) instanceof TankValveBlockEntity v && v.isFormed() && v.isMaster()) {
            master = vPos;
            break;
         }
      }

      if (master != null && this.level.getBlockEntity(master) instanceof TankValveBlockEntity masterValve) {
         masterValve.applyValidation(result, master, player);
         return true;
      }

      if (player != null) {
         player.sendSystemMessage(Component.translatable("text.industrialreforged.multiblock.tank.error.no_valve"));
      }
      return false;
   }

   /** Вызывается мастер-клапаном при сборке структуры. */
   public void setFormed(
      boolean formed, @Nullable BlockPos masterPos, int capacity, int innerVolume, @Nullable BlockPos minPos, @Nullable BlockPos maxPos
   ) {
      this.formed = formed;
      this.masterPos = masterPos;
      this.capacity = capacity;
      this.innerVolume = innerVolume;
      this.minPos = minPos;
      this.maxPos = maxPos;
      if (this.level != null && !this.level.isClientSide) {
         ActiveBlock.setActive(this, formed);
      }
      this.markForSave();
      this.markForUpdate();
   }

   public void disbandStructure() {
      if (!this.formed) {
         return;
      }

      TankValveBlockEntity master = this.getMasterValve();
      this.formed = false;
      this.masterPos = null;
      if (this.level != null && !this.level.isClientSide) {
         ActiveBlock.setActive(this, false);
      }
      this.markForSave();
      this.markForUpdate();

      if (master != null && master.isFormed()) {
         master.disbandStructure();
      }
   }

   @Override
   public Component getDisplayName() {
      return Component.translatable("block.industrialreforged.tank_controller");
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
      return new TankControllerMenu(containerId, inventory, this);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.formed = tag.getBoolean("formed");
      this.capacity = tag.getInt("capacity");
      this.innerVolume = tag.getInt("innerVolume");
      this.masterPos = tag.contains("masterPos") ? BlockPos.of(tag.getLong("masterPos")) : null;
      this.minPos = tag.contains("minPos") ? BlockPos.of(tag.getLong("minPos")) : null;
      this.maxPos = tag.contains("maxPos") ? BlockPos.of(tag.getLong("maxPos")) : null;
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putBoolean("formed", this.formed);
      tag.putInt("capacity", this.capacity);
      tag.putInt("innerVolume", this.innerVolume);
      if (this.masterPos != null) {
         tag.putLong("masterPos", this.masterPos.asLong());
      }
      if (this.minPos != null) {
         tag.putLong("minPos", this.minPos.asLong());
      }
      if (this.maxPos != null) {
         tag.putLong("maxPos", this.maxPos.asLong());
      }
   }
}
