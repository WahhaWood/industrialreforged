package com.industrialreforged.tech.datagen.provider;

import com.industrialreforged.tech.core.ref.IRDamageTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class IRDamageTypeTagsProvider extends DamageTypeTagsProvider {
   public IRDamageTypeTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "industrialreforged", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.tag(DamageTypeTags.BYPASSES_COOLDOWN).add(IRDamageTypes.ENERGY_ARROW);
   }
}
