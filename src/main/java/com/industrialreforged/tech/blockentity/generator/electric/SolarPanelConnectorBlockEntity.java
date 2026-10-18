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
    * Recomputes output and buffer from the solar panels installed in the nine slots and the modules in the
    * four module slots. The modules act on the connector as a whole, as they do on one panel: day, night and
    * rain lenses scale the output of every panel by the weather, storage and output modules the buffer and the
    * transfer rate. Without panels all of it is zero, so the connector generates 0 FE/t and offers no buffer.
    */
   public void updatePanels() {
      long dayOutput = 0L;
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
         dayOutput += day * (long)count;
         capacity += Math.max(day * 6400L, 64000L) * (long)count;
      }

      // the same rules as one panel: rain gives the rain share, otherwise day or 60% of it at night
      double rate = this.rainWeather ? this.rainRate : this.sunIsUp ? this.dayRate : 0.6 * this.nightRate;
      this.staticProduction = dayOutput > 0L ? Math.max(Math.round((double)dayOutput * rate), 1L) : 0L;
      // The connector is a pure source: it must be able to push its whole
      // output out.  The base generator constructor created it with
      // production 0, so receive/extract stayed 0 and nothing could be drawn.
      this.energy.setReceive(0L);
      this.energy.setExtract(Math.max(Math.round((double)dayOutput * 2.0 * this.outputMult), 0L));

      long target = Math.round((double)capacity * this.capacityMult);
      if (target != this.energy.getCapacity()) {
         this.energy.setCapacity(target);
         if (this.energy.getStored() > target) {
            this.energy.setStored(target);
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
