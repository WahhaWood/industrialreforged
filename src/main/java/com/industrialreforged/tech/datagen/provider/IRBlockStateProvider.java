package com.industrialreforged.tech.datagen.provider;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class IRBlockStateProvider extends BlockStateProvider {
   public IRBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
      super(output, "industrialreforged", exFileHelper);
   }

   protected void registerStatesAndModels() {
   }
}
