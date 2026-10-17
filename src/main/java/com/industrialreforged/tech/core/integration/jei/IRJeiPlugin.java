package com.industrialreforged.tech.core.integration.jei;

import com.industrialreforged.item.api.IEnergyContainerItem;
import com.industrialreforged.tech.client.screen.generator.electric.SolidFuelGeneratorScreen;
import com.industrialreforged.tech.client.screen.generator.heat.SolidHeatGeneratorScreen;
import com.industrialreforged.tech.client.screen.machine.BlastFurnaceScreen;
import com.industrialreforged.tech.client.screen.machine.BlockCutterScreen;
import com.industrialreforged.tech.client.screen.machine.CannerScreen;
import com.industrialreforged.tech.client.screen.machine.CentrifugeScreen;
import com.industrialreforged.tech.client.screen.machine.InductionFurnaceScreen;
import com.industrialreforged.tech.client.screen.machine.IronFurnaceScreen;
import com.industrialreforged.tech.client.screen.machine.MetalFormerScreen;
import com.industrialreforged.tech.client.screen.machine.MolecularTransformerScreen;
import com.industrialreforged.tech.client.screen.machine.OreWashingPlantScreen;
import com.industrialreforged.tech.client.screen.machine.ScannerScreen;
import com.industrialreforged.tech.client.screen.machine.SolidCannerScreen;
import com.industrialreforged.tech.client.screen.machine.StandardLevelMachineScreen;
import com.industrialreforged.tech.client.screen.widgets.Gauge;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.integration.jei.category.BlastFurnaceCategory;
import com.industrialreforged.tech.core.integration.jei.category.BlockCutterCategory;
import com.industrialreforged.tech.core.integration.jei.category.CannerCategory;
import com.industrialreforged.tech.core.integration.jei.category.CentrifugeCategory;
import com.industrialreforged.tech.core.integration.jei.category.MetalFormerCategory;
import com.industrialreforged.tech.core.integration.jei.category.MolecularTransformerCategory;
import com.industrialreforged.tech.core.integration.jei.category.OreWashingPlantCategory;
import com.industrialreforged.tech.core.integration.jei.category.ScannerCategory;
import com.industrialreforged.tech.core.integration.jei.category.SolidCannerCategory;
import com.industrialreforged.tech.core.integration.jei.category.StandardMachineCategory;
import com.industrialreforged.tech.core.integration.jei.category.TankMultiblockCategory;
import com.industrialreforged.tech.core.integration.jei.category.ReactorMultiblockCategory;
import com.industrialreforged.tech.core.integration.jei.category.TurningCategory;
import com.industrialreforged.tech.core.integration.jei.TankMultiblockRecipe;
import com.industrialreforged.tech.core.integration.jei.ReactorMultiblockRecipe;
import com.industrialreforged.tech.core.integration.jei.handler.BlockCutterHandler;
import com.industrialreforged.tech.core.integration.jei.handler.IronFurnaceHandler;
import com.industrialreforged.tech.core.integration.jei.handler.MetalFormerHandler;
import com.industrialreforged.tech.core.integration.jei.handler.StandardLevelMachineHandler;
import com.industrialreforged.tech.core.integration.jei.wrapper.CannerWrapper;
import com.industrialreforged.tech.core.integration.jei.wrapper.TurningWrapper;
import com.industrialreforged.tech.core.integration.jei.wrapper.RecyclerWrapper;
import com.industrialreforged.tech.core.integration.jei.category.RecyclerCategory;
import com.industrialreforged.tech.core.recipe.BlastFurnaceRecipe;
import com.industrialreforged.tech.core.recipe.BlockCutterRecipe;
import com.industrialreforged.tech.core.recipe.CentrifugeRecipe;
import com.industrialreforged.tech.core.recipe.CompressorRecipe;
import com.industrialreforged.tech.core.recipe.ExtractorRecipe;
import com.industrialreforged.tech.core.recipe.MaceratorRecipe;
import com.industrialreforged.tech.core.recipe.MetalFormerRecipe;
import com.industrialreforged.tech.core.recipe.MolecularTransformerRecipe;
import com.industrialreforged.tech.core.recipe.OreWashingRecipe;
import com.industrialreforged.tech.core.recipe.ScannerRecipe;
import com.industrialreforged.tech.core.recipe.SolidCannerRecipe;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.core.ref.IRItemTags;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRRecipeTypes;
import com.industrialreforged.tech.item.ItemCell;
import com.industrialreforged.tech.item.ItemCropSeed;
import com.industrialreforged.tech.item.armor.base.AbstractFluidArmorItem;
import com.industrialreforged.tech.item.tool.ItemSprayer;
import com.industrialreforged.tech.item.tool.ItemToolPainter;
import com.industrialreforged.tech.item.turning.ItemTurningMold;
import com.industrialreforged.tech.item.turning.TurningProgram;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredHolder;

@JeiPlugin
public class IRJeiPlugin implements IModPlugin {
   private static IRJeiPlugin instance;
   private final Minecraft mc = Minecraft.getInstance();
   private final RecipeType<BlastFurnaceRecipe> blastFurnace = RecipeType.create("industrialreforged", "blast_furnace", BlastFurnaceRecipe.class);
   private final RecipeType<BlockCutterRecipe> blockCutter = RecipeType.create("industrialreforged", "block_cutter", BlockCutterRecipe.class);
   private final RecipeType<CentrifugeRecipe> centrifuge = RecipeType.create("industrialreforged", "centrifuge", CentrifugeRecipe.class);
   private final RecipeType<CompressorRecipe> compressor = RecipeType.create("industrialreforged", "compressor", CompressorRecipe.class);
   private final RecipeType<ExtractorRecipe> extractor = RecipeType.create("industrialreforged", "extractor", ExtractorRecipe.class);
   private final RecipeType<MaceratorRecipe> macerator = RecipeType.create("industrialreforged", "macerator", MaceratorRecipe.class);
   private final RecipeType<RecyclerWrapper> recycler = RecipeType.create("industrialreforged", "recycler", RecyclerWrapper.class);
   private final RecipeType<SolidCannerRecipe> solidCanner = RecipeType.create("industrialreforged", "solid_canner", SolidCannerRecipe.class);
   private final RecipeType<CannerWrapper> canner = RecipeType.create("industrialreforged", "canner", CannerWrapper.class);
   private final RecipeType<MetalFormerRecipe> metalFormer = RecipeType.create("industrialreforged", "metal_former", MetalFormerRecipe.class);
   private final RecipeType<OreWashingRecipe> oreWasher = RecipeType.create("industrialreforged", "ore_washer", OreWashingRecipe.class);
   private final RecipeType<MolecularTransformerRecipe> molecularTransformer = RecipeType.create(
      "industrialreforged", "molecular_transformer", MolecularTransformerRecipe.class
   );
   private final RecipeType<TurningWrapper> turning = RecipeType.create("industrialreforged", "turning", TurningWrapper.class);
   private final RecipeType<ScannerRecipe> scanner = RecipeType.create("industrialreforged", "scanner", ScannerRecipe.class);
   private final RecipeType<TankMultiblockRecipe> tankMultiblock = RecipeType.create(
      "industrialreforged", "tank_multiblock", TankMultiblockRecipe.class
   );
   private final RecipeType<ReactorMultiblockRecipe> reactorMultiblock = RecipeType.create(
      "industrialreforged", "reactor_multiblock", ReactorMultiblockRecipe.class
   );
   private static IJeiRuntime jeiRuntime;
   /** The recycler takes almost anything; JEI shows a few typical inputs. */
   private static final List<Item> RECYCLER_EXAMPLES = List.of(
      Items.COBBLESTONE, Items.DIRT, Items.GRAVEL, Items.SAND, Items.NETHERRACK, Items.ROTTEN_FLESH, Items.WOODEN_SWORD, Items.STRING
   );

   public IRJeiPlugin() {
      instance = this;
   }

   public ResourceLocation getPluginUid() {
      return IR.location("plugin");
   }

   /** Открыть страницу мультиблока (dev-команда /irjei tank). */
   public static boolean openTankMultiblock() {
      if (instance == null || jeiRuntime == null) {
         return false;
      }
      jeiRuntime.getRecipesGui().showTypes(List.of(instance.tankMultiblock));
      return true;
   }

   /** Открыть страницу реактора (dev-команда /irjei reactor). */
   public static boolean openReactorMultiblock() {
      if (instance == null || jeiRuntime == null) {
         return false;
      }
      jeiRuntime.getRecipesGui().showTypes(List.of(instance.reactorMultiblock));
      return true;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
      registration.addRecipeCategories(new IRecipeCategory[]{new BlastFurnaceCategory(this.blastFurnace, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new BlockCutterCategory(this.blockCutter, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new CentrifugeCategory(this.centrifuge, helper)});
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new StandardMachineCategory(this.compressor, "text.industrialreforged.compressor", IRBlocks.COMPRESSOR_1_LEVEL, Gauge.ProgressTriangle, 300, helper)
         }
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{new StandardMachineCategory(this.extractor, "text.industrialreforged.extractor", IRBlocks.EXTRACTOR_1_LEVEL, Gauge.ProgressDrop, 300, helper)}
      );
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new StandardMachineCategory(this.macerator, "text.industrialreforged.macerator", IRBlocks.MACERATOR_1_LEVEL, Gauge.ProgressCrush, 300, helper)
         }
      );
      registration.addRecipeCategories(new IRecipeCategory[]{new RecyclerCategory(this.recycler, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new MetalFormerCategory(this.metalFormer, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new OreWashingPlantCategory(this.oreWasher, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new CannerCategory(this.canner, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new SolidCannerCategory(this.solidCanner, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new MolecularTransformerCategory(this.molecularTransformer, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new TurningCategory(this.turning, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new ScannerCategory(this.scanner, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new TankMultiblockCategory(this.tankMultiblock, helper)});
      registration.addRecipeCategories(new IRecipeCategory[]{new ReactorMultiblockCategory(this.reactorMultiblock, helper)});
   }

   public void onRuntimeAvailable(IJeiRuntime jei) {
      jeiRuntime = jei;
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addRecipeClickArea(BlastFurnaceScreen.class, 80, 35, 22, 15, new RecipeType[]{this.blastFurnace});
      registration.addGuiContainerHandler(BlockCutterScreen.class, new BlockCutterHandler(this.blockCutter));
      registration.addRecipeClickArea(CentrifugeScreen.class, 84, 25, 3, 28, new RecipeType[]{this.centrifuge});
      registration.addGuiContainerHandler(StandardLevelMachineScreen.class, new StandardLevelMachineHandler(this.compressor, this.extractor, this.macerator, this.recycler));
      registration.addGuiContainerHandler(MetalFormerScreen.class, new MetalFormerHandler(this.metalFormer));
      registration.addRecipeClickArea(SolidFuelGeneratorScreen.class, 57, 36, 13, 13, new RecipeType[]{RecipeTypes.FUELING});
      registration.addRecipeClickArea(InductionFurnaceScreen.class, 81, 25, 22, 15, new RecipeType[]{RecipeTypes.SMELTING});
      registration.addGuiContainerHandler(IronFurnaceScreen.class, new IronFurnaceHandler());
      registration.addRecipeClickArea(OreWashingPlantScreen.class, 103, 39, 18, 18, new RecipeType[]{this.oreWasher});
      registration.addRecipeClickArea(CannerScreen.class, 74, 22, 23, 14, new RecipeType[]{this.canner, this.solidCanner});
      registration.addRecipeClickArea(SolidCannerScreen.class, 88, 35, 22, 15, new RecipeType[]{this.solidCanner});
      registration.addRecipeClickArea(SolidHeatGeneratorScreen.class, 81, 29, 13, 13, new RecipeType[]{RecipeTypes.FUELING});
      registration.addRecipeClickArea(ScannerScreen.class, 6, 4, 18, 18, new RecipeType[]{this.scanner});
      registration.addRecipeClickArea(MolecularTransformerScreen.class, 20, 40, 13, 23, new RecipeType[]{this.molecularTransformer});
   }

   public void registerItemSubtypes(ISubtypeRegistration registration) {
      for (DeferredHolder<Item, ? extends Item> object : IRItems.REGISTRY.getEntries()) {
         if (object != null) {
            Item item = (Item)object.get();
            if (item instanceof ItemCropSeed) {
               registration.registerSubtypeInterpreter(item, SubtypeInterpreters.CROPS);
            } else if (item instanceof ItemToolPainter) {
               registration.registerSubtypeInterpreter(item, SubtypeInterpreters.PAINTS);
            } else if (item instanceof ItemCell || item instanceof AbstractFluidArmorItem || item instanceof ItemSprayer) {
               registration.registerSubtypeInterpreter(item, SubtypeInterpreters.FLUIDS);
            } else if (item instanceof IEnergyContainerItem) {
               registration.registerSubtypeInterpreter(item, SubtypeInterpreters.ENERGY);
            }
         }
      }
   }

   public static void openCategory(RecipeType<?> recipeType) {
      if (jeiRuntime != null) {
         IRecipesGui recipesGui = jeiRuntime.getRecipesGui();
         recipesGui.showTypes(List.of(recipeType));
      }
   }

   public static Optional<RecipeType<?>> getRecipeCategory(String type) {
      return jeiRuntime != null ? jeiRuntime.getRecipeManager().getRecipeType(IR.location(type)) : Optional.empty();
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(IRBlocks.BLAST_FURNACE, new RecipeType[]{this.blastFurnace});
      registration.addRecipeCatalyst(IRBlocks.BLOCK_CUTTER, new RecipeType[]{this.blockCutter});
      registration.addRecipeCatalyst(IRBlocks.CENTRIFUGE, new RecipeType[]{this.centrifuge});
      registration.addRecipeCatalyst(IRBlocks.COMPRESSOR_1_LEVEL, new RecipeType[]{this.compressor});
      registration.addRecipeCatalyst(IRBlocks.COMPRESSOR_2_LEVEL, new RecipeType[]{this.compressor});
      registration.addRecipeCatalyst(IRBlocks.COMPRESSOR_3_LEVEL, new RecipeType[]{this.compressor});
      registration.addRecipeCatalyst(IRBlocks.COMPRESSOR_4_LEVEL, new RecipeType[]{this.compressor});
      registration.addRecipeCatalyst(IRBlocks.EXTRACTOR_1_LEVEL, new RecipeType[]{this.extractor});
      registration.addRecipeCatalyst(IRBlocks.EXTRACTOR_2_LEVEL, new RecipeType[]{this.extractor});
      registration.addRecipeCatalyst(IRBlocks.EXTRACTOR_3_LEVEL, new RecipeType[]{this.extractor});
      registration.addRecipeCatalyst(IRBlocks.EXTRACTOR_4_LEVEL, new RecipeType[]{this.extractor});
      registration.addRecipeCatalyst(IRBlocks.MACERATOR_1_LEVEL, new RecipeType[]{this.macerator});
      registration.addRecipeCatalyst(IRBlocks.MACERATOR_2_LEVEL, new RecipeType[]{this.macerator});
      registration.addRecipeCatalyst(IRBlocks.MACERATOR_3_LEVEL, new RecipeType[]{this.macerator});
      registration.addRecipeCatalyst(IRBlocks.MACERATOR_4_LEVEL, new RecipeType[]{this.macerator});
      registration.addRecipeCatalyst(IRBlocks.RECYCLER_1_LEVEL, new RecipeType[]{this.recycler});
      registration.addRecipeCatalyst(IRBlocks.RECYCLER_2_LEVEL, new RecipeType[]{this.recycler});
      registration.addRecipeCatalyst(IRBlocks.RECYCLER_3_LEVEL, new RecipeType[]{this.recycler});
      registration.addRecipeCatalyst(IRBlocks.RECYCLER_4_LEVEL, new RecipeType[]{this.recycler});
      registration.addRecipeCatalyst(IRBlocks.SOLID_CANNER, new RecipeType[]{this.solidCanner});
      registration.addRecipeCatalyst(IRBlocks.CANNER, new RecipeType[]{this.solidCanner});
      registration.addRecipeCatalyst(IRBlocks.CANNER, new RecipeType[]{this.canner});
      registration.addRecipeCatalyst(IRBlocks.METAL_FORMER_1_LEVEL, new RecipeType[]{this.metalFormer});
      registration.addRecipeCatalyst(IRBlocks.METAL_FORMER_2_LEVEL, new RecipeType[]{this.metalFormer});
      registration.addRecipeCatalyst(IRBlocks.METAL_FORMER_3_LEVEL, new RecipeType[]{this.metalFormer});
      registration.addRecipeCatalyst(IRBlocks.METAL_FORMER_4_LEVEL, new RecipeType[]{this.metalFormer});
      registration.addRecipeCatalyst(IRBlocks.ORE_WASHING_PLANT, new RecipeType[]{this.oreWasher});
      registration.addRecipeCatalyst(IRBlocks.MOLECULAR_TRANSFORMER, new RecipeType[]{this.molecularTransformer});
      registration.addRecipeCatalyst(IRBlocks.TURNING, new RecipeType[]{this.turning});
      registration.addRecipeCatalyst(IRBlocks.UU_SCANNER, new RecipeType[]{this.scanner});
      registration.addRecipeCatalyst(IRBlocks.TANK_VALVE, new RecipeType[]{this.tankMultiblock});
      registration.addRecipeCatalyst(IRBlocks.TANK_CONTROLLER, new RecipeType[]{this.tankMultiblock});
      registration.addRecipeCatalyst(IRBlocks.TANK_WALL, new RecipeType[]{this.tankMultiblock});
      registration.addRecipeCatalyst(IRBlocks.ELECTRIC_FURNACE_1_LEVEL, new RecipeType[]{RecipeTypes.SMELTING});
      registration.addRecipeCatalyst(IRBlocks.ELECTRIC_FURNACE_2_LEVEL, new RecipeType[]{RecipeTypes.SMELTING});
      registration.addRecipeCatalyst(IRBlocks.ELECTRIC_FURNACE_3_LEVEL, new RecipeType[]{RecipeTypes.SMELTING});
      registration.addRecipeCatalyst(IRBlocks.ELECTRIC_FURNACE_4_LEVEL, new RecipeType[]{RecipeTypes.SMELTING});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      registration.addRecipes(this.blastFurnace, this.getAllRecipesFor(IRRecipeTypes.BLAST_FURNACE));
      registration.addRecipes(this.blockCutter, this.getAllRecipesFor(IRRecipeTypes.BLOCK_CUTTER));
      registration.addRecipes(this.centrifuge, this.getAllRecipesFor(IRRecipeTypes.CENTRIFUGE));
      registration.addRecipes(this.compressor, this.getAllRecipesFor(IRRecipeTypes.COMPRESSOR));
      registration.addRecipes(this.extractor, this.getAllRecipesFor(IRRecipeTypes.EXTRACTOR));
      registration.addRecipes(this.macerator, this.getAllRecipesFor(IRRecipeTypes.MACERATOR));
      registration.addRecipes(this.recycler, List.of(new RecyclerWrapper(RECYCLER_EXAMPLES.stream().map(ItemStack::new).toList())));
      registration.addRecipes(this.metalFormer, this.getAllRecipesFor(IRRecipeTypes.METAL_FORMER));
      registration.addRecipes(this.oreWasher, this.getAllRecipesFor(IRRecipeTypes.ORE_WASHER));
      registration.addRecipes(this.solidCanner, SolidCannerRecipe.gets());
      registration.addRecipes(this.molecularTransformer, this.getAllRecipesFor(IRRecipeTypes.MOLECULAR_TRANSFORMER));
      registration.addRecipes(this.turning, this.getTurningWrapper());
      registration.addRecipes(this.scanner, this.getAllRecipesFor(IRRecipeTypes.SCANNER));
      registration.addRecipes(this.tankMultiblock, List.of(new TankMultiblockRecipe(false)));
      registration.addRecipes(this.reactorMultiblock, List.of(new ReactorMultiblockRecipe(false)));
      registration.addRecipes(
         this.canner,
         List.of(
            new CannerWrapper(Fluids.WATER, 1000, Ingredient.of(new ItemLike[]{IRItems.CF_POWDER}), 1, IRFluids.CONSTRUCTION_FOAM.getFluidStill(), 1000),
            new CannerWrapper(Fluids.WATER, 1000, Ingredient.of(IRItemTags.DUSTS_LAPIS), 8, IRFluids.COOLANT.getFluidStill(), 1000),
            new CannerWrapper(
               IRFluids.DISTILLED_WATER.getFluidStill(), 1000, Ingredient.of(IRItemTags.DUSTS_LAPIS), 1, IRFluids.COOLANT.getFluidStill(), 1000
            ),
            new CannerWrapper(Fluids.WATER, 1000, Ingredient.of(new ItemLike[]{IRItems.BIO_CHAFF}), 1, IRFluids.BIOMASS.getFluidStill(), 1000),
            new CannerWrapper(Fluids.WATER, 6000, Ingredient.of(new ItemLike[]{Items.STICK}), 1, IRFluids.HOT_WATER.getFluidStill(), 6000)
         )
      );
   }

   private <T extends Recipe<RecipeInput>> List<T> getAllRecipesFor(Supplier<net.minecraft.world.item.crafting.RecipeType<T>> supplier) {
      return this.mc.level.getRecipeManager().getAllRecipesFor(supplier.get()).stream().<T>map(RecipeHolder::value).toList();
   }

   private List<TurningWrapper> getTurningWrapper() {
      List<TurningWrapper> turningWrappers = new ArrayList<>();
      ItemTurningMold mold = (ItemTurningMold)IRItems.WOODEN_TURNING_MOLD.get();

      for (TurningProgram type : mold.getTypes()) {
         turningWrappers.add(new TurningWrapper(mold, type));
      }

      mold = (ItemTurningMold)IRItems.IRON_TURNING_MOLD.get();

      for (TurningProgram type : mold.getTypes()) {
         turningWrappers.add(new TurningWrapper(mold, type));
      }

      mold = (ItemTurningMold)IRItems.STEEL_TURNING_MOLD.get();

      for (TurningProgram type : mold.getTypes()) {
         turningWrappers.add(new TurningWrapper(mold, type));
      }

      return turningWrappers;
   }
}
