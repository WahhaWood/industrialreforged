package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.menu.generator.electric.GeothermalGeneratorMenu;
import com.industrialreforged.tech.menu.generator.electric.KineticGeneratorMenu;
import com.industrialreforged.tech.menu.generator.electric.RTGeneratorMenu;
import com.industrialreforged.tech.menu.generator.electric.SemifluidGeneratorMenu;
import com.industrialreforged.tech.menu.generator.electric.SolarPanelConnectorMenu;
import com.industrialreforged.tech.menu.generator.electric.SolarPanelMenu;
import com.industrialreforged.tech.menu.generator.electric.SolidFuelGeneratorMenu;
import com.industrialreforged.tech.menu.generator.electric.StirlingGeneratorMenu;
import com.industrialreforged.tech.menu.generator.heat.ElectricHeatGeneratorMenu;
import com.industrialreforged.tech.menu.generator.heat.FluidHeatGeneratorMenu;
import com.industrialreforged.tech.menu.generator.heat.RTHeatGeneratorMenu;
import com.industrialreforged.tech.menu.generator.heat.SolidHeatGeneratorMenu;
import com.industrialreforged.tech.menu.generator.kinetic.ElectricKineticGeneratorMenu;
import com.industrialreforged.tech.menu.generator.kinetic.SteamKineticGeneratorMenu;
import com.industrialreforged.tech.menu.generator.kinetic.StirlingKineticGeneratorMenu;
import com.industrialreforged.tech.menu.generator.kinetic.WaterKineticGeneratorMenu;
import com.industrialreforged.tech.menu.generator.kinetic.WindKineticGeneratorMenu;
import com.industrialreforged.tech.menu.item.CropnalyzerMenu;
import com.industrialreforged.tech.menu.item.ToolScannerMenu;
import com.industrialreforged.tech.menu.machine.BlastFurnaceMenu;
import com.industrialreforged.tech.menu.machine.BlockCutterMenu;
import com.industrialreforged.tech.menu.machine.CannerMenu;
import com.industrialreforged.tech.menu.machine.CentrifugeMenu;
import com.industrialreforged.tech.menu.machine.CondenserMenu;
import com.industrialreforged.tech.menu.multiblock.TankControllerMenu;
import com.industrialreforged.tech.menu.machine.OreBreakerMenu;
import com.industrialreforged.tech.menu.machine.QuantumQuarryMenu;
import com.industrialreforged.tech.menu.tool.FilterMenu;
import com.industrialreforged.tech.menu.machine.CropHarvesterMenu;
import com.industrialreforged.tech.menu.machine.CropmatronMenu;
import com.industrialreforged.tech.menu.machine.FermenterMenu;
import com.industrialreforged.tech.menu.machine.FluidBottlerMenu;
import com.industrialreforged.tech.menu.machine.InductionFurnaceMenu;
import com.industrialreforged.tech.menu.machine.IronFurnaceMenu;
import com.industrialreforged.tech.menu.machine.LatheMenu;
import com.industrialreforged.tech.menu.machine.LiquidHeatExchangerMenu;
import com.industrialreforged.tech.menu.machine.MatterGeneratorMenu;
import com.industrialreforged.tech.menu.machine.MetalFormerMenu;
import com.industrialreforged.tech.menu.machine.MolecularFarmingMenu;
import com.industrialreforged.tech.menu.machine.MolecularTransformerMenu;
import com.industrialreforged.tech.menu.machine.OreWashingPlantMenu;
import com.industrialreforged.tech.menu.machine.PatternStorageMenu;
import com.industrialreforged.tech.menu.machine.PumpMenu;
import com.industrialreforged.tech.menu.machine.ReplicatorMenu;
import com.industrialreforged.tech.menu.machine.ScannerMenu;
import com.industrialreforged.tech.menu.machine.SolarDestillerMenu;
import com.industrialreforged.tech.menu.machine.SolidCannerMenu;
import com.industrialreforged.tech.menu.machine.StandardLevelMachineMenu;
import com.industrialreforged.tech.menu.reactor.NuclearReactorMenu;
import com.industrialreforged.tech.menu.storage.ChargepadBlockMenu;
import com.industrialreforged.tech.menu.storage.ElectricBlockMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.RegisterEvent;

/** Common (both sides) registration of IR menu types. */
public final class IRMenuRegistration {
   public static void registerMenuTypes(RegisterEvent event) {
      if (event.getRegistryKey().equals(Registries.MENU)) {
         register(event, "geothermal_generator", GeothermalGeneratorMenu.TYPE);
         register(event, "kinetic_generator", KineticGeneratorMenu.TYPE);
         register(event, "rt_generator", RTGeneratorMenu.TYPE);
         register(event, "semifluid_generator", SemifluidGeneratorMenu.TYPE);
         register(event, "solar_panel_connector", SolarPanelConnectorMenu.TYPE);
         register(event, "solar_panel", SolarPanelMenu.TYPE);
         register(event, "solid_fuel_generator", SolidFuelGeneratorMenu.TYPE);
         register(event, "stirling_generator", StirlingGeneratorMenu.TYPE);
         register(event, "electric_heat_generator", ElectricHeatGeneratorMenu.TYPE);
         register(event, "fluid_heat_generator", FluidHeatGeneratorMenu.TYPE);
         register(event, "rt_heat_generator", RTHeatGeneratorMenu.TYPE);
         register(event, "solid_heat_generator", SolidHeatGeneratorMenu.TYPE);
         register(event, "electric_kinetic_generator", ElectricKineticGeneratorMenu.TYPE);
         register(event, "steam_kinetic_generator", SteamKineticGeneratorMenu.TYPE);
         register(event, "stirling_kinetic_generator", StirlingKineticGeneratorMenu.TYPE);
         register(event, "water_kinetic_generator", WaterKineticGeneratorMenu.TYPE);
         register(event, "wind_kinetic_generator", WindKineticGeneratorMenu.TYPE);
         register(event, "blast_furnace", BlastFurnaceMenu.TYPE);
         register(event, "block_cutter", BlockCutterMenu.TYPE);
         register(event, "canner", CannerMenu.TYPE);
         register(event, "centrifuge", CentrifugeMenu.TYPE);
         register(event, "condenser", CondenserMenu.TYPE);
         register(event, "tank_controller", TankControllerMenu.TYPE);
         register(event, "quantum_quarry", QuantumQuarryMenu.TYPE);
         register(event, "ore_breaker", OreBreakerMenu.TYPE);
         register(event, "item_filter", FilterMenu.TYPE);
         register(event, "crop_harvester", CropHarvesterMenu.TYPE);
         register(event, "cropmatron", CropmatronMenu.TYPE);
         register(event, "fermenter", FermenterMenu.TYPE);
         register(event, "fluid_bottler", FluidBottlerMenu.TYPE);
         register(event, "induction_furnace", InductionFurnaceMenu.TYPE);
         register(event, "iron_furnace", IronFurnaceMenu.TYPE);
         register(event, "lathe", LatheMenu.TYPE);
         register(event, "liquid_heat_exchanger", LiquidHeatExchangerMenu.TYPE);
         register(event, "matter_generator", MatterGeneratorMenu.TYPE);
         register(event, "metal_former", MetalFormerMenu.TYPE);
         register(event, "molecular_farming", MolecularFarmingMenu.TYPE);
         register(event, "molecular_transformer", MolecularTransformerMenu.TYPE);
         register(event, "ore_washing", OreWashingPlantMenu.TYPE);
         register(event, "pattern_storage", PatternStorageMenu.TYPE);
         register(event, "pump", PumpMenu.TYPE);
         register(event, "replicator", ReplicatorMenu.TYPE);
         register(event, "scanner", ScannerMenu.TYPE);
         register(event, "solar_destiller", SolarDestillerMenu.TYPE);
         register(event, "solid_canner", SolidCannerMenu.TYPE);
         register(event, "standard_level_machine", StandardLevelMachineMenu.TYPE);
         register(event, "nuclear_reactor", NuclearReactorMenu.TYPE);
         register(event, "chargepad_block", ChargepadBlockMenu.TYPE);
         register(event, "electric_block", ElectricBlockMenu.TYPE);
         register(event, "cropnalyzer", CropnalyzerMenu.TYPE);
         register(event, "tool_scanner", ToolScannerMenu.TYPE);
      }
   }


   private static void register(RegisterEvent event, String name, MenuType<?> type) {
      event.register(Registries.MENU, IR.location(name), () -> type);
   }
}
