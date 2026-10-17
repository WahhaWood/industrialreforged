package com.industrialreforged.tech.core.integration.cablefacades;

import com.portingdeadmods.cable_facades.api.CableFacadesAPI;

/**
 * Cable Facades integration: lets facade covers attach to our wires and heat pipes.
 *
 * <p>Called only when {@code cable_facades} is present (see {@code IR}), so direct
 * references to its API are safe. Patterns follow their wildcard syntax from the
 * default whitelist (e.g. {@code mekanism:*_cable}): {@code industrialreforged:wire_*}
 * covers all 10 wire tiers, {@code industrialreforged:heat_pipe_*} covers the pipes.
 * Both are also hidden under facades, otherwise our baked wire model would stick
 * out through the cover (same reason their {@code hidden_when_facaded} list exists).
 */
public final class CableFacadesIntegration {
   private CableFacadesIntegration() {
   }

   public static void init() {
      CableFacadesAPI.enqueueAPICallback(api -> {
         api.registerAllowedBlocks("industrialreforged:wire_*", "industrialreforged:heat_pipe_*");
         api.registerHiddenBlocks("industrialreforged:wire_*", "industrialreforged:heat_pipe_*");
      });
   }
}
