package com.industrialreforged.tech.menu.item;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import org.apache.commons.lang3.tuple.Pair;

public class ToolScannerMenu extends AbstractHandHeldMenu<HandHeldScanner> {
   public static final MenuType<ToolScannerMenu> TYPE = IMenuTypeExtension.create(ToolScannerMenu::new);
   public List<Pair<ItemStack, Integer>> scanResults;

   public ToolScannerMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
      this(containerId, inventory);
      this.scanResults = buf.readList(buffer -> Pair.of((ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readVarInt()));
   }

   public ToolScannerMenu(int containerId, Inventory inventory) {
      super(TYPE, containerId, inventory, new HandHeldScanner(inventory));
      this.addPlayerInventorySlots(inventory, 231);
   }
}
