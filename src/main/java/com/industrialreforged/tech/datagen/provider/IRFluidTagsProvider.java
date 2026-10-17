package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.core.ref.IRFluidTags;
import com.industrialreforged.tech.core.ref.IRFluids;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IRFluidTagsProvider extends FluidTagsProvider {
   public IRFluidTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "industrialreforged", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.tag(IRFluidTags.STEAM).add(IRFluids.STEAM.getFluidStill());
   }
}
