package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.CalculationStrategy;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingSimulationRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.google.gson.JsonObject;
import com.industrialreforged.tech.core.ref.IRFluids;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * Dev bridge endpoint {@code /ae2} (dev runs only): pokes the ME network around an AE2 node so the E2E
 * tests can drive the ME machines. {@code DevBridge} reaches it only when the integration is active and
 * passes nothing but JSON and opaque objects, so the bridge itself never sees an AE2 type.
 */
public final class MEDevBridge {
   private MEDevBridge() {
   }

   /** Server thread: every operation but the crafting request. */
   public static JsonObject handle(String op, ServerLevel level, BlockPos pos, JsonObject params) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (!(blockEntity instanceof IGridConnectedBlockEntity host)) {
         return error("no AE2 node at " + pos.toShortString());
      }

      IGridNode node = host.getGridNode();
      IGrid grid = node == null ? null : node.getGrid();
      JsonObject json = new JsonObject();
      json.addProperty("ok", true);
      switch (op) {
         case "info" -> {
            json.addProperty("active", host.getMainNode().isActive());
            json.addProperty("powered", host.getMainNode().isPowered());
            json.addProperty("grid", grid != null);
            if (grid != null) {
               json.addProperty("gridStoredPower", grid.getEnergyService().getStoredPower());
               json.addProperty("cpus", grid.getCraftingService().getCpus().size());
               long busy = 0;
               for (var cpu : grid.getCraftingService().getCpus()) {
                  var status = cpu.isBusy() ? cpu.getJobStatus() : null;
                  if (status != null) {
                     busy++;
                     json.addProperty("jobProgress", status.progress());
                     json.addProperty("jobTotal", status.totalItems());
                  }
               }

               json.addProperty("cpusBusy", busy);
            }

            if (blockEntity instanceof AbstractMENodeBlockEntity machine) {
               json.addProperty("buffer", machine.getAECurrentPower());
               json.addProperty("lastOperations", machine.lastOperations);
            }

            if (blockEntity instanceof MEMachineBlockEntity machine) {
               json.addProperty("kind", machine.kind.name());
               json.addProperty("recipes", machine.recipes().size());
               json.addProperty("patterns", machine.getAvailablePatterns().size());
               json.addProperty("queued", machine.queuedOperations());
               json.addProperty("pendingOutputs", machine.pendingOutputs());
               json.addProperty("busy", machine.isBusy());
            }

            if (blockEntity instanceof MEMatterGeneratorBlockEntity generator) {
               json.addProperty("produced", generator.produced);
               json.addProperty("saved", generator.saved);
               json.addProperty("pendingMb", generator.pendingMb());
            }

            if (blockEntity instanceof MERecyclerBlockEntity recycler) {
               json.addProperty("recycled", recycler.recycled);
               json.addProperty("sinceScrap", recycler.sinceScrap());
               json.addProperty("pendingScrap", recycler.pendingScrap());
            }
         }
         case "count", "insert", "extract", "craftable" -> {
            if (grid == null) {
               return error("node has no grid");
            }

            AEItemKey key = AEItemKey.of(item(params));
            if (key == null) {
               return error("unknown item");
            }

            long count = params.has("count") ? params.get("count").getAsLong() : 1;
            var storage = grid.getStorageService().getInventory();
            switch (op) {
               case "count" -> json.addProperty("count", storage.getAvailableStacks().get(key));
               case "insert" -> json.addProperty("inserted", storage.insert(key, count, Actionable.MODULATE, IActionSource.empty()));
               case "extract" -> json.addProperty("extracted", storage.extract(key, count, Actionable.MODULATE, IActionSource.empty()));
               default -> json.addProperty("craftable", grid.getCraftingService().isCraftable(key));
            }
         }
         case "uu" -> {
            // UU matter in the network, mB
            if (grid == null) {
               return error("node has no grid");
            }

            var uu = AEFluidKey.of(IRFluids.UU_MATTER.getFluidStill());
            var storage = grid.getStorageService().getInventory();
            if (params.has("take")) {
               json.addProperty("taken", storage.extract(uu, params.get("take").getAsLong(), Actionable.MODULATE, IActionSource.empty()));
            }

            json.addProperty("uu", storage.getAvailableStacks().get(uu));
         }
         case "fe" -> {
            // pushes FE into the node the way an energy cable does
            if (!(blockEntity instanceof AbstractMENodeBlockEntity machine)) {
               return error("not an ME node");
            }

            int amount = params.has("amount") ? params.get("amount").getAsInt() : 0;
            json.addProperty("accepted", machine.feInput().receiveEnergy(amount, false));
            json.addProperty("stored", machine.feInput().getEnergyStored());
         }
         case "cancel" -> {
            // stops every running job of the network's crafting CPUs, as the Cancel button does
            if (grid == null) {
               return error("node has no grid");
            }

            int cancelled = 0;
            for (var cpu : grid.getCraftingService().getCpus()) {
               if (cpu.isBusy()) {
                  cpu.cancelJob();
                  cancelled++;
               }
            }

            json.addProperty("cancelled", cancelled);
         }
         case "toggle" -> {
            if (!(blockEntity instanceof MEMachineBlockEntity machine)) {
               return error("not an ME machine");
            }

            MEMachineTogglePayload.Action action = MEMachineTogglePayload.Action.valueOf(params.get("action").getAsString());
            ResourceLocation recipe = params.has("recipe") ? ResourceLocation.parse(params.get("recipe").getAsString()) : null;
            machine.applyToggle(action, recipe);
            json.addProperty("patterns", machine.getAvailablePatterns().size());
         }
         case "push" -> {
            // throughput test: hand the machine N crafts directly, with the recipe's primary input
            if (!(blockEntity instanceof MEMachineBlockEntity machine)) {
               return error("not an ME machine");
            }

            MEMachineRecipe recipe = machine.recipes().get(ResourceLocation.parse(params.get("recipe").getAsString()));
            if (recipe == null) {
               return error("unknown recipe for this machine");
            }

            MEMachinePattern pattern = new MEMachinePattern(recipe);
            AEItemKey input = AEItemKey.of(recipe.input().getItems()[0]);
            long times = params.has("count") ? params.get("count").getAsLong() : 1;
            long pushed = 0;
            for (long i = 0; i < times; i++) {
               KeyCounter counter = new KeyCounter();
               counter.add(input, recipe.inputCount());
               if (!machine.pushPattern(pattern, new KeyCounter[]{counter})) {
                  break;
               }
               pushed++;
            }

            json.addProperty("pushed", pushed);
         }
         case "charge" -> {
            // fill (or empty) the machine's own AE buffer: energy tests run without a creative cell
            if (!(blockEntity instanceof AbstractMENodeBlockEntity machine)) {
               return error("not an ME machine");
            }

            double amount = params.get("amount").getAsDouble();
            machine.extractAEPower(Double.MAX_VALUE, Actionable.MODULATE, appeng.api.config.PowerMultiplier.ONE);
            machine.injectAEPower(amount, Actionable.MODULATE);
            json.addProperty("buffer", machine.getAECurrentPower());
         }
         case "put" -> {
            // e.g. a storage cell into a drive, through the block's item capability
            var handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (handler == null) {
               return error("no item handler at " + pos.toShortString());
            }

            ItemStack rest = ItemHandlerHelper.insertItem(handler, item(params), false);
            json.addProperty("inserted", rest.isEmpty());
         }
         case "sample" -> {
            if (!(blockEntity instanceof MERecyclerBlockEntity recycler)) {
               return error("not an ME recycler");
            }

            int slot = params.get("slot").getAsInt();
            recycler.samples.setItem(slot, params.has("item") ? item(params) : ItemStack.EMPTY);
            json.addProperty("sample", recycler.samples.getItem(slot).toString());
         }
         default -> {
            return error("unknown op " + op);
         }
      }

      return json;
   }

   /** Server thread: starts AE2's crafting calculation; returns the pending request or an error JSON. */
   public static Object beginCraft(ServerLevel level, BlockPos pos, JsonObject params) {
      if (!(level.getBlockEntity(pos) instanceof IGridConnectedBlockEntity host) || host.getGridNode() == null || host.getGridNode().getGrid() == null) {
         return error("no AE2 grid at " + pos.toShortString());
      }

      AEItemKey key = AEItemKey.of(item(params));
      if (key == null) {
         return error("unknown item");
      }

      IGrid grid = host.getGridNode().getGrid();
      IActionSource source = IActionSource.ofMachine(host);
      long count = params.has("count") ? params.get("count").getAsLong() : 1;
      ICraftingSimulationRequester requester = () -> source;
      Future<ICraftingPlan> future = grid.getCraftingService().beginCraftingCalculation(level, requester, key, count, CalculationStrategy.REPORT_MISSING_ITEMS);
      return new PendingCraft(grid, source, future);
   }

   /** Bridge thread: waits for the calculation. Returns the plan (opaque) or an error JSON. */
   public static Object awaitPlan(Object pending, long timeoutMs) {
      try {
         return ((PendingCraft)pending).future.get(timeoutMs, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         return error("calculation failed: " + e);
      }
   }

   /** Server thread: submits a finished plan to any free crafting CPU. */
   public static JsonObject submit(Object pending, Object plan) {
      PendingCraft craft = (PendingCraft)pending;
      ICraftingPlan job = (ICraftingPlan)plan;
      JsonObject json = new JsonObject();
      json.addProperty("simulation", job.simulation());
      json.addProperty("bytes", job.bytes());
      json.addProperty("missing", job.missingItems().size());
      if (job.simulation()) {
         json.addProperty("ok", false);
         return json;
      }

      var result = craft.grid.getCraftingService().submitJob(job, null, null, true, craft.source);
      json.addProperty("ok", result.successful());
      json.addProperty("error", String.valueOf(result.errorCode()));
      return json;
   }

   private static ItemStack item(JsonObject params) {
      return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(params.get("item").getAsString())));
   }

   private static JsonObject error(String message) {
      JsonObject json = new JsonObject();
      json.addProperty("ok", false);
      json.addProperty("error", message);
      return json;
   }

   private record PendingCraft(IGrid grid, IActionSource source, Future<ICraftingPlan> future) {
   }
}
