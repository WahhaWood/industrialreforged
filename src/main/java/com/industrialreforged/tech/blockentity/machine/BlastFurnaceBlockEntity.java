package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.tech.api.tile.IHeatSource;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotProcessableStandard;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.recipe.BlastFurnaceRecipe;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.menu.machine.BlastFurnaceMenu;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class BlastFurnaceBlockEntity extends AbstractMachineBlockEntity implements IUpgradableBlock, RecipeInput, ContainerData, IContainerWrapper {
   public int heat = 0;
   public static int maxHeat = 50000;
   protected int progress = 0;
   protected int progressNeeded = 300;
   public final InvSlotProcessableStandard<?, BlastFurnaceRecipe> inputSlot;
   public final InvSlotOutput outputSlot;
   public final InvSlotConsumableItem tankInputSlot;
   public final InvSlotOutput tankOutputSlot;
   public final InvSlotUpgrade upgradeSlot;
   private boolean emptyCell;
   private float guiHeat;
   private float guiProgress;
   private CombinedInvWrapper wrapper;

   public BlastFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.BLAST_FURNACE.get(), blockPos, blockState);
      this.enableRedstoneSignal();
      this.inputSlot = new InvSlotProcessableStandard<>(this, "input", 1, IRRecipeTypes.BLAST_FURNACE.get());
      this.outputSlot = new InvSlotOutput(this, 2);
      this.tankInputSlot = new InvSlotConsumableItem(this, "cellInput", 1, IRItems.COMPRESSED_AIR_CELL);
      this.tankOutputSlot = new InvSlotOutput(this, "cellOutput", 1);
      this.upgradeSlot = new InvSlotUpgrade(this, 2);
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
   protected boolean hasWork() {
      return this.progress > 0;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.tankInputSlot.load(tag, registries);
      this.tankOutputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.heat = tag.getInt("heat");
      this.progress = tag.getInt("progress");
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inputSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.tankInputSlot.save(tag, registries);
      this.tankOutputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      tag.putInt("heat", this.heat);
      tag.putInt("progress", this.progress);
   }

   /**
    * The furnace had heat intake and a recipe that knows how to assemble, but
    * nothing ever drove the progress: heatup() was never called and no tick
    * advanced the operation.
    */
   @Override
   public void serverTick() {
      super.serverTick();

      if (this.level == null || this.level.isClientSide) {
         return;
      }

      int before = this.heat;
      this.heatup();
      if (this.heat != before) {
         this.markForSave();
      }

      boolean working = false;

      if (this.isHot() && !this.inputSlot.isEmpty() && !this.tankInputSlot.isEmpty()) {
         BlastFurnaceRecipe recipe = this.inputSlot.process(0);
         if (recipe != null) {
            int cells = Math.max(1, recipe.getCells());
            if (this.tankInputSlot.getItem(0).getCount() >= cells) {
               this.progressNeeded = Math.max(1, recipe.getDuration());
               this.progress++;
               working = true;
               if (this.progress >= this.progressNeeded) {
                  this.progress = 0;
                  working = this.inputSlot.assemble(0);
               }

               this.markForSave();
            }
         }
      }

      if (!working && this.inputSlot.isEmpty()) {
         // Недостаток тепла или занятый выход — это пауза, а не сброс: остальные машины мода
         // в такой ситуации просто ждут.  Сбрасываем прогресс, только когда работать не над чем.
         this.progress = 0;
      }

      this.guiHeat = maxHeat <= 0 ? 0.0F : (float)this.heat / maxHeat;
      this.guiProgress = this.progressNeeded <= 0 ? 0.0F : (float)this.progress / this.progressNeeded;
      com.industrialreforged.block.ActiveBlock.setActive(this, working);
   }

   private void heatup() {
      int heatRequested = 0;
      int gainhU = 0;
      // Тянем ровно недостающее до потолка — без лишних «+100».  ПереборHeat был
      // не мусором, а гистерезисом: он позволял печи работать ещё 100 тиков после того, как
      // источник иссяк.  Но и без него нельзя: если на потолке печь сама остывает на 1, а
      // условие работы «heat >= maxHeat», то прогресс (6000 тиков) обнуляется каждый второй
      // тик и печь не может завершить ни одной операции.  Поэтому на потолке мы не остываем
      // вовсе, а остываем только когда источник не дал ничего (нет соседа, он пуст, сломан).
      if (this.heat < maxHeat) {
         if (this.upgradeSlot.invertRedstonePower != this.redstoneSignal) {
            heatRequested = maxHeat - this.heat;
         } else if (!this.inputSlot.isEmpty()) {
            heatRequested = maxHeat - this.heat;
         }
      }

      if (heatRequested > 0) {
         Direction direction = RotateBlock.getHorizontalFacing(this);
         BlockEntity blockEntity = this.getLevel().getBlockEntity(this.worldPosition.relative(direction));
         if (blockEntity instanceof IHeatSource) {
            gainhU = ((IHeatSource)blockEntity).extractHeat(direction.getOpposite(), heatRequested, false);
            this.heat = Math.min(maxHeat, this.heat + gainhU);
         }

         if (gainhU == 0) {
            this.heat = this.heat - Math.min(this.heat, 1);
         }
      }
   }

   public boolean isHot() {
      return this.heat >= maxHeat;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new BlastFurnaceMenu(containerId, playerInventory, this);
   }

   public float getGuiProgress() {
      return this.guiProgress;
   }

   public float getGuiHeat() {
      return this.guiHeat;
   }

   public boolean isEmptyCell() {
      return this.emptyCell;
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.REDSTONE_SENSITIVE, UpgradableProperty.ITEM_CONSUMING, UpgradableProperty.ITEM_PRODUCING, UpgradableProperty.FLUID_CONSUMING
      );
   }

   @Override
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (direction == null) {
         if (this.wrapper == null) {
            this.wrapper = new CombinedInvWrapper(
               new IItemHandlerModifiable[]{
                  this.inputSlot.createWrapper(), this.tankInputSlot.createWrapper(), this.outputSlot.createWrapper(), this.tankOutputSlot.createWrapper()
               }
            );
         }
         return this.wrapper;
      }

      return switch (direction) {
         case UP -> this.inputSlot.createWrapper();
         case DOWN -> new CombinedInvWrapper(new IItemHandlerModifiable[]{this.outputSlot.createWrapper(), this.tankOutputSlot.createWrapper()});
         case NORTH, SOUTH, EAST, WEST -> new CombinedInvWrapper(
            new IItemHandlerModifiable[]{
               this.tankInputSlot.createWrapper(), this.inputSlot.createWrapper(), this.outputSlot.createWrapper(), this.tankOutputSlot.createWrapper()
            }
         );
      };
   }

   public ItemStack getItem(int index) {
      return this.inputSlot.getItem(index);
   }

   public int size() {
      return this.inputSlot.getContainerSize();
   }

   public int get(int index) {
      return switch (index) {
         case 0 -> (int)(this.guiHeat * 1000.0F);
         case 1 -> (int)(this.guiProgress * 1000.0F);
         case 2 -> this.emptyCell ? 1 : 0;
         default -> throw new IllegalStateException("Unexpected value: " + index);
      };
   }

   public void set(int index, int value) {
      switch (index) {
         case 0:
            this.guiHeat = value / 1000.0F;
            break;
         case 1:
            this.guiProgress = value / 1000.0F;
            break;
         case 2:
            this.emptyCell = value == 1;
      }
   }

   public int getCount() {
      return 3;
   }
}
