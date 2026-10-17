package com.industrialreforged.tech.core.integration.jei.wrapper;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/** JEI page of the recycler: a few example inputs (it takes almost anything) and the scrap roll. */
public record RecyclerWrapper(List<ItemStack> examples) {
}
