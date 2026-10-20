<div align="center">

# IndustrialReforged

**An industrial tech mod for Minecraft 1.21.1 / NeoForge, in the spirit of IndustrialCraft 2.**

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1-E68C2D)
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![License](https://img.shields.io/badge/license-GPL--3.0-blue)

</div>

Power your world with energy from **1 FE/t up to 1 GFE/t**: generators, machines with upgrades,
cables and pipes, multiblock reactors, crops and a long tech tree to climb.

## Features

- **Energy tiers.** Generators from a hand-cranked kinetic generator and Stirling engine to geothermal,
  thermal and steam plants, solar panels across many levels, and battery blocks up to CESU, MFSU and EFSU.
- **Machines with upgrades.** Macerators, extractors, compressors, electric furnaces, recyclers, centrifuges, bottlers,
  cutters and more, each in several levels.
- **Logistics.** Universal wires, heat pipes, fluid tanks with valves, pumps and fluid regulators.
- **Multiblock reactors.** Reactor pressure vessel, access hatches and heat management.
- **Farming and resources.** Rubber trees and resin, molecular farming, new ores such as nickel, silver, tungsten,
  titan and uranium, ore washing and drilling rigs.
- **Building.** Scaffolds, reinforced doors, iridium casings and construction foam in many colours.
- **Translations.** English, Russian and Ukrainian.

## Integrations

All integrations are optional, the mod runs without any of them.

| Mod | What it adds |
| --- | --- |
| Applied Energistics 2 | ME machines: matter generator, recycler, electric furnace; FE cables to ME nodes |
| Botania | Detected at startup, integration hook in place |
| Cable Facades, Applied Flux | Compatibility with facades and FE in ME networks |
| JEI | Recipe viewing |
| KubeJS | Add and change recipes of our machine types from scripts, see [docs/kubejs.md](docs/kubejs.md) |

## Building

Java 21 is required.

```sh
./gradlew build    # build/libs/industrialreforged-<version>.jar
```

The `tools/` folder holds the dev-server test suite (`tests_*.py`) and texture and balance scripts.
They are only needed for development.

## Credits

Inspired by **IndustrialCraft 2** and the IC2 team. This is an independent mod, not affiliated with or
endorsed by the IndustrialCraft 2 authors.

## License

The code in this repository is licensed under the [GNU General Public License v3.0](LICENSE).
