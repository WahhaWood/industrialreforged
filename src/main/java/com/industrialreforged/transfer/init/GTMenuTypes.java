package com.industrialreforged.transfer.init;

import com.industrialreforged.transfer.core.WireMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GTMenuTypes {
   public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, "industrialreforged");
   public static final Supplier<MenuType<WireMenu>> ENERGY_CABLE = REGISTRY.register("cable", () -> IMenuTypeExtension.create(WireMenu::new));
}
