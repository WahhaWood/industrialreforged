package com.industrialreforged.tech.core.ref;

import com.industrialreforged.tech.core.IR;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IRSounds {
   public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, "industrialreforged");
   public static Supplier<SoundEvent> SOUND_MACHINE_INTERRUPT = registerSound("block.machine.interrupt");
   public static Supplier<SoundEvent> SOUND_MACERATOR_START = registerSound("block.macerator.start");
   public static Supplier<SoundEvent> SOUND_RECYCLER_START = registerSound("block.recycler.start");
   public static Supplier<SoundEvent> SOUND_EXTRACTOR_START = registerSound("block.extractor.start");
   public static Supplier<SoundEvent> SOUND_COMPRESSOR_START = registerSound("block.compressor.start");
   public static Supplier<SoundEvent> SOUND_ELECTRO_FURNACE_LOOP = registerSound("block.electro_furnace.loop");
   public static Supplier<SoundEvent> SOUND_ELECTRO_FURNACE_START = registerSound("block.electro_furnace.start");
   public static Supplier<SoundEvent> SOUND_ELECTRO_FURNACE_STOP = registerSound("block.electro_furnace.stop");
   public static Supplier<SoundEvent> SOUND_INDUCTION_LOOP = registerSound("block.induction.loop");
   public static Supplier<SoundEvent> SOUND_INDUCTION_START = registerSound("block.induction.start");
   public static Supplier<SoundEvent> SOUND_INDUCTION_STOP = registerSound("block.induction.stop");
   public static Supplier<SoundEvent> SOUND_MACHINE_telechargedloop = registerSound("block.machine_telechargedloop");
   public static Supplier<SoundEvent> SOUND_MACHINE_iteleuse = registerSound("block.machine_teleuse");
   public static Supplier<SoundEvent> SOUND_MACHINE_pumpop = registerSound("block.machine_pumpop");
   public static Supplier<SoundEvent> SOUND_MACHINE_ironfurnaceop = registerSound("block.machine_ironfurnaceop");
   public static Supplier<SoundEvent> SOUND_MACHINE_massfabloop = registerSound("block.machine_massfabloop");
   public static Supplier<SoundEvent> SOUND_GENERATOR_LOOP = registerSound("block.generator.loop");
   public static Supplier<SoundEvent> SOUND_GEOTHERMAL_LOOP = registerSound("block.geothermal.loop");
   public static Supplier<SoundEvent> SOUND_REACTOR_LOOP = registerSound("block.reactor.loop");
   public static Supplier<SoundEvent> SOUND_REACTOR_LOW = registerSound("block.reactor.low");
   public static Supplier<SoundEvent> SOUND_REACTOR_MED = registerSound("block.reactor.med");
   public static Supplier<SoundEvent> SOUND_REACTOR_HIGH = registerSound("block.reactor.high");
   public static Supplier<SoundEvent> SOUND_JETPACK_LOOP = registerSound("item.jetpack.loop");
   public static Supplier<SoundEvent> SOUND_JETPACK_FIRE = registerSound("item.jetpack.fire");
   public static Supplier<SoundEvent> SOUND_SABER_IDLE = registerSound("item.saber.idle");
   public static Supplier<SoundEvent> SOUND_SABER_POWER = registerSound("item.saber.power");
   public static Supplier<SoundEvent> SOUND_SABER_SWING = registerSound("item.saber.swing");
   public static Supplier<SoundEvent> SOUND_TOOL_SCANNER = registerSound("item.tool.scanner");
   public static Supplier<SoundEvent> SOUND_TOOL_WRENCH = registerSound("item.tool.wrench");
   public static Supplier<SoundEvent> SOUND_TOOL_TREETAP = registerSound("item.tool.treetap");

   public static Supplier<SoundEvent> registerSound(String soundID) {
      return REGISTRY.register(soundID, () -> SoundEvent.createVariableRangeEvent(IR.location(soundID)));
   }
}
