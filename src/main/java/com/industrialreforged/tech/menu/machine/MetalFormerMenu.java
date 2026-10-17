package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.blockentity.machine.MetalFormer1LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer2LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer3LevelBlockEntity;
import com.industrialreforged.tech.blockentity.machine.MetalFormer4LevelBlockEntity;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.menu.base.AbstractElectricMachineMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class MetalFormerMenu extends AbstractElectricMachineMenu<MetalFormer1LevelBlockEntity> {
   public static final MenuType<MetalFormerMenu> TYPE = createType(MetalFormerMenu::new);

   public MetalFormerMenu(int containerId, Inventory inventory, MetalFormer1LevelBlockEntity tile) {
      super(
         TYPE,
         containerId,
         inventory,
         tile,
         tile.inputSlot.getContainerSize() > 1 ? 12 : 8,
         84,
         tile.inputSlot.getContainerSize() > 1 ? 8 : 17,
         tile.inputSlot.getContainerSize() > 1 ? 55 : 53
      );
      this.constructionSlots(tile, tile.inputSlot.getContainerSize() == 1);
      this.addDataSlots(tile);
   }

   public boolean clickMenuButton(Player player, int id) {
      ((MetalFormer1LevelBlockEntity)this.entity)
         .setMode(MetalFormerRecipe.Mode.values()[(((MetalFormer1LevelBlockEntity)this.entity).getMode().ordinal() + 1) % 3]);
      return true;
   }

   private void constructionSlots(MetalFormer1LevelBlockEntity tile, boolean def) {
      if (def) {
         this.addSlot(new InputSlot(tile.inputSlot, 0, 17, 17));
         this.addSlot(new OutputSlot(tile.outputSlot, 0, 116, 35));
      } else if (tile instanceof MetalFormer2LevelBlockEntity) {
         this.addSlot(new InputSlot(tile.inputSlot, 0, 41, 21));
         this.addSlot(new OutputSlot(tile.outputSlot, 0, 125, 21));
         this.addSlot(new InputSlot(tile.inputSlot, 1, 41, 49));
         this.addSlot(new OutputSlot(tile.outputSlot, 1, 125, 49));
      } else if (tile instanceof MetalFormer3LevelBlockEntity) {
         this.addSlot(new InputSlot(tile.inputSlot, 0, 31, 35));
         this.addSlot(new OutputSlot(tile.outputSlot, 0, 127, 35));
         this.addSlot(new InputSlot(tile.inputSlot, 1, 51, 21));
         this.addSlot(new OutputSlot(tile.outputSlot, 1, 155, 21));
         this.addSlot(new InputSlot(tile.inputSlot, 2, 51, 49));
         this.addSlot(new OutputSlot(tile.outputSlot, 2, 155, 49));
      } else if (tile instanceof MetalFormer4LevelBlockEntity) {
         this.addSlot(new InputSlot(tile.inputSlot, 0, 31, 21));
         this.addSlot(new OutputSlot(tile.outputSlot, 0, 127, 21));
         this.addSlot(new InputSlot(tile.inputSlot, 1, 51, 21));
         this.addSlot(new OutputSlot(tile.outputSlot, 1, 155, 21));
         this.addSlot(new InputSlot(tile.inputSlot, 2, 31, 49));
         this.addSlot(new OutputSlot(tile.outputSlot, 2, 127, 49));
         this.addSlot(new InputSlot(tile.inputSlot, 3, 51, 49));
         this.addSlot(new OutputSlot(tile.outputSlot, 3, 155, 49));
      }

      for (int i = 0; i < tile.upgradeSlot.getContainerSize(); i++) {
         this.addSlot(new InputSlot(tile.upgradeSlot, i, def ? 152 : 182, 8 + i * 18));
      }
   }
}
