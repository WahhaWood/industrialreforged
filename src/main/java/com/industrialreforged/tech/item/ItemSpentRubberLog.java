package com.industrialreforged.tech.item;

import com.industrialreforged.tech.core.ref.IRBlocks;
import net.minecraft.world.item.BlockItem;

/** Отработанное каучуковое бревно: ставится «сухим», латекс больше не даёт. */
public class ItemSpentRubberLog extends BlockItem {
   public ItemSpentRubberLog() {
      super(IRBlocks.RUBBER_LOG.get(), new Properties());
   }

   @Override
   public String getDescriptionId() {
      return "item.industrialreforged.spent_rubber_log";
   }
}
