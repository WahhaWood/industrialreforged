package com.industrialreforged.tech.menu.machine;

import com.industrialreforged.menu.slots.InputSlot;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.menu.sync.RecipeDataSlot;
import com.industrialreforged.tech.blockentity.machine.MolecularTransformerBlockEntity;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.menu.IRMenuBase;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeInput;

public class MolecularTransformerMenu extends IRMenuBase<MolecularTransformerBlockEntity> {
   public static final MenuType<MolecularTransformerMenu> TYPE = createType(MolecularTransformerMenu::new);

   public MolecularTransformerMenu(int containerId, Inventory inventory, MolecularTransformerBlockEntity entity) {
      super(TYPE, containerId, inventory, entity);
      this.addSlot(new InputSlot(entity.inputSlot, 0, 20, 27));
      this.addSlot(new OutputSlot(entity.outputSlot, 0, 20, 68));
      this.addGrid(entity.upgradeSlot, 0, 30, 195, 1, 2, 22, 0, InputSlot::new);
      this.addGrid(entity.upgradeSlot, 2, 152, 195, 1, 2, 22, 0, InputSlot::new);
      this.addPlayerSlots(inventory, 18, 98, 21, 21);
      this.addToSync(() -> entity.count, value -> entity.count = value);
      this.addLongToSync(() -> entity.energyPerTick, value -> entity.energyPerTick = value);
      this.addLongToSync(() -> entity.energyLeft, value -> entity.energyLeft = value);
      this.addCustomSync(new RecipeDataSlot<RecipeInput, MolecularTransformerRecipe>(entity, IRRecipeTypes.MOLECULAR_TRANSFORMER.get(), () -> entity.recipe, holder -> entity.recipe = holder));
   }
}
