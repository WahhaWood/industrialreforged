package com.industrialreforged.tech.api.upgrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.Item;

public class UpgradeRegistry {
   private static final List<IUpgradeItem> UPGRADE_ITEMS = new ArrayList<>();

   public static <T extends Item & IUpgradeItem> void register(T t) {
      UPGRADE_ITEMS.add(t);
   }

   public static Iterable<IUpgradeItem> getUpgradeItems() {
      return Collections.unmodifiableCollection(UPGRADE_ITEMS);
   }
}
