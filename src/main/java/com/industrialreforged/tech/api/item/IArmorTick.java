package com.industrialreforged.tech.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IArmorTick {
   void onPreArmorTick(ItemStack var1, Level var2, Player var3);
}
