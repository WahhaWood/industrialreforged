package com.industrialreforged.tech.menu.generator.heat;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.generator.heat.FluidHeatGeneratorBlockEntity;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class FluidHeatGeneratorMenu extends IRMenuBase<FluidHeatGeneratorBlockEntity> {
   public static final MenuType<FluidHeatGeneratorMenu> TYPE = createType(FluidHeatGeneratorMenu::new);

   public FluidHeatGeneratorMenu(int containerId, Inventory inventory, FluidHeatGeneratorBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addSlot(new InputSlot(entity.fluidSlot, 0, 27, 21));
      this.addSlot(new OutputSlot(entity.outputSlot, 0, 27, 54));
      this.addPlayerSlots(inventory, 8, 84);
      this.addSync(entity.tank);
      this.addToSync(entity::getHeat, entity::setHeat);
   }
}
