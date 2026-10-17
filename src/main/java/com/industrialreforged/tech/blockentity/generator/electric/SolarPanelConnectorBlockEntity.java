package com.industrialreforged.tech.blockentity.generator.electric;

import com.industrialreforged.tech.block.generator.electric.SolarPanelBlock;
import com.industrialreforged.tech.container.InvSlotSolarConnector;
import com.industrialreforged.tech.core.IRConfig;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.item.SolarPanelBlockItem;
import com.industrialreforged.tech.menu.generator.electric.SolarPanelConnectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarPanelConnectorBlockEntity extends SolarPanelBlockEntity {
   public InvSlotSolarConnector inventory = new InvSlotSolarConnector(this, "inventory", 9);
   public long staticProduction;
   private long staticCapacity;

   public SolarPanelConnectorBlockEntity(BlockPos pos, BlockState state) {
      super(IRBlockEntities.SOLAR_CONNECTOR.get(), pos, state);
      // the connector itself never produces; the installed panels do
      this.energy.setCapacity(0L);
      this.energy.setStored(0L);
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.inventory.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.inventory.save(tag, registries);
   }

   /**
    * Recomputes output and buffer from the solar panels installed in the nine
    * slots.  Without panels both are zero, so the connector generates 0 FE/t
    * and offers no buffer at all.
    */
   public void updatePanels() {
      long production = 0L;
      long capacity = 0L;

      for (int i = 0; i < this.inventory.getContainerSize(); i++) {
         ItemStack stack = this.inventory.getItem(i);
         if (stack.isEmpty()
            || !(stack.getItem() instanceof SolarPanelBlockItem item)
            || !(item.getBlock() instanceof SolarPanelBlock panel)) {
            continue;
         }

         int count = stack.getCount();
         long day = IRConfig.CONFIG.getProduction(panel.getLevel());
         long output = this.sunIsUp ? day : Math.max(day * 6L / 10L, 1L);
         production += output * (long)count;
         capacity += Math.max(day * 6400L, 64000L) * (long)count;
      }

      this.staticProduction = production;
      // The connector is a pure source: it must be able to push its whole
      // output out.  The base generator constructor created it with
      // production 0, so receive/extract stayed 0 and nothing could be drawn.
      this.energy.setReceive(0L);
      this.energy.setExtract(Math.max(2L * production, 0L));

      if (capacity != this.staticCapacity) {
         this.staticCapacity = capacity;
         this.energy.setCapacity(capacity);
         if (this.energy.getStored() > capacity) {
            this.energy.setStored(capacity);
         }
      }
   }

   @Override
   public void updateVisibility() {
      super.updateVisibility();
      this.updatePanels();
      this.production = this.sunIsVisible ? this.staticProduction : 0L;
   }

   @Override
   public void onLoad() {
      super.onLoad();
      this.updatePanels();
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new SolarPanelConnectorMenu(containerId, playerInventory, this);
   }
}
