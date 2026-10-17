package com.industrialreforged.tech.menu.item;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.menu.slots.OutputSlot;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.ItemInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.jetbrains.annotations.NotNull;

public class CropnalyzerMenu extends AbstractHandHeldMenu<HandHeldCropnalyzer> {
   public static final MenuType<CropnalyzerMenu> TYPE = IMenuTypeExtension.create(CropnalyzerMenu::new);

   public CropnalyzerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
      this(containerId, inventory);
   }

   public CropnalyzerMenu(int containerId, Inventory inventory) {
      super(TYPE, containerId, inventory, new HandHeldCropnalyzer(inventory));
      this.addSlot(new Slot(this.container, 0, 8, 7) {
         public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() == IRItems.CROP_SEED_BAG.get();
         }
      });
      this.addSlot(new OutputSlot(this.container, 1, 41, 7));
      this.addSlot(
         new Slot(this.container, 2, 152, 7) {
            public boolean mayPlace(@NotNull ItemStack stack) {
               return ItemInfo.INSTANCE.getEnergyValue(stack) > 0.0
                  || stack.getItem() instanceof IEnergyContainerItem energyItem && energyItem.use(stack, 1, true);
            }
         }
      );
      this.addPlayerInventorySlots(inventory, 223);
   }
}
