package com.industrialreforged.tech.core.util;

import com.industrialreforged.container.BasicSimpleContainer;
import com.industrialreforged.energy.BigEnergyStorage;
import com.industrialreforged.tech.container.EnergyContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Moves energy between a machine buffer and the batteries sitting in its slots.
 *
 * <p>Which direction(s) run is decided by the slot's {@link EnergyContainer}:
 * {@code receiveTick > 0} charges the battery from the machine, {@code
 * extractTick > 0} discharges the battery into the machine.  Both may be set.
 *
 * <p>Machines such as the storage blocks keep an extra charge container that is
 * not the default one of the machine base, so this is a standalone helper and
 * every block entity has to call it for each of its energy slots.
 */
public final class EnergySlotHelper {
   private EnergySlotHelper() {
   }

   public static void tick(BasicSimpleContainer container, IEnergyStorage machine) {
      int receiveTick = 0;
      int extractTick = 0;
      if (container instanceof EnergyContainer energyContainer) {
         receiveTick = energyContainer.receiveTick;
         extractTick = energyContainer.extractTick;
      }

      if (receiveTick <= 0 && extractTick <= 0) {
         return;
      }

      long stored = machine instanceof BigEnergyStorage big ? big.getStored() : machine.getEnergyStored();
      long space = machine instanceof BigEnergyStorage big ? big.getSpace() : machine.getMaxEnergyStored() - machine.getEnergyStored();

      for (int i = 0; i < container.getContainerSize(); i++) {
         ItemStack stack = container.getItem(i);
         if (stack.isEmpty()) {
            continue;
         }

         IEnergyStorage item = stack.getCapability(Capabilities.EnergyStorage.ITEM);
         if (item == null) {
            continue;
         }

         if (receiveTick > 0 && stored > 0L && item.canReceive()) {
            int moved = item.receiveEnergy((int)Math.min(stored, receiveTick), false);
            if (moved > 0) {
               if (machine instanceof BigEnergyStorage big) {
                  big.extractForceEnergy(moved);
               } else {
                  machine.extractEnergy(moved, false);
               }

               stored -= moved;
            }
         }

         if (extractTick > 0 && space > 0L && item.canExtract()) {
            int moved = item.extractEnergy((int)Math.min(space, extractTick), false);
            if (moved > 0) {
               if (machine instanceof BigEnergyStorage big) {
                  big.injectForceEnergy(moved);
               } else {
                  machine.receiveEnergy(moved, false);
               }

               space -= moved;
            }
         }

         if (stored <= 0L && space <= 0L) {
            return;
         }
      }
   }
}
