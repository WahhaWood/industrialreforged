package com.industrialreforged.tech.menu;

import com.industrialreforged.menu.MenuBase;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class IRMenuBase<T extends BlockEntity> extends MenuBase<T> {
   public IRMenuBase(Supplier<MenuType<?>> supplierType, int containerId, Inventory inv, T entity) {
      this(supplierType.get(), containerId, inv, entity);
   }

   public IRMenuBase(MenuType<?> type, int containerId, Inventory inv, T entity) {
      super(type, containerId, inv, entity);
      if (entity instanceof IUpgradableBlock upgradableBlock) {
         this.addToSync(upgradableBlock::getConsumption, upgradableBlock::setConsumption);
         this.addToSync(upgradableBlock::getTimeCreation, upgradableBlock::setTimeCreation);
         this.addToSync(upgradableBlock::getDisplayConsumption, upgradableBlock::setDisplayConsumption);
         this.addToSync(() -> upgradableBlock.isStarved() ? 1 : 0, value -> upgradableBlock.setStarved(value != 0));
      }
   }
}
