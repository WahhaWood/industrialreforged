package com.industrialreforged.debug.bridge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Personal dev bridge: a tiny HTTP server inside the mod that lets an agent
 * drive the game autonomously.  Available in development builds only, bound to
 * localhost.
 *
 * Everything game related is executed on the server thread; HTTP requests wait
 * for the result, so a script can run command -> read state -> click slots
 * without guessing at timings.
 */
public final class DevBridge {
   private static final Logger LOGGER = LoggerFactory.getLogger("IR-Bridge");
   private static final Gson GSON = new Gson();
   private static final int DEFAULT_PORT = 9877;
   private static final long DEFAULT_TIMEOUT_MS = 10_000L;
   private static HttpServer server;
   private static long startedAt;
   private static int port;

   private DevBridge() {
   }

   public static void start() {
      if (FMLEnvironment.production || server != null) {
         return;
      }

      try {
         port = Integer.getInteger("irbridge.port", DEFAULT_PORT);
         server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
         server.createContext("/", DevBridge::handle);
         server.setExecutor(Executors.newCachedThreadPool());
         server.start();
         TickTimer.register();
         startedAt = System.currentTimeMillis();
         LOGGER.info("[IR-Bridge] listening on http://127.0.0.1:{} (dev only)", port);
      } catch (Exception e) {
         LOGGER.error("[IR-Bridge] could not start", e);
      }
   }

   // ------------------------------------------------------------------
   // routing
   // ------------------------------------------------------------------

   private static void handle(HttpExchange exchange) throws IOException {
      try {
         String path = exchange.getRequestURI().getPath();
         JsonObject params = params(exchange);
         JsonElement result;

         switch (path) {
            case "/status" -> result = status();
            case "/help" -> result = help();
            case "/exec" -> result = exec(params);
            case "/wait" -> result = wait(params);
            case "/block" -> result = block(params);
            case "/be" -> result = be(params);
            case "/energy" -> result = energy(params);
            case "/fluids" -> result = fluids(params);
            case "/items" -> result = items(params);
            case "/players" -> result = players();
            case "/player" -> result = movePlayer(params);
            case "/break" -> result = breakBlock(params);
            case "/use" -> result = useItemOnBlock(params);
            case "/entities" -> result = entities(params);
            case "/drops" -> result = drops(params);
            case "/recipe" -> result = recipes(params);
            case "/menu" -> result = menu(params);
            case "/menu/open" -> result = openMenu(params);
            case "/menu/click" -> result = clickMenu(params);
            case "/menu/button" -> result = clickMenuButton(params);
            case "/menu/close" -> result = closeMenu(params);
            case "/map" -> result = map(params);
            case "/screenshot" -> result = screenshot(params);
            case "/clientbe" -> result = clientBlockEntity(params);
            case "/advancements" -> result = advancements();
            case "/ae2" -> result = ae2(params);
            case "/perf" -> result = TickTimer.report(params.has("reset") && params.get("reset").getAsBoolean());
            default -> {
               send(exchange, 404, error("unknown endpoint, see /help"));
               return;
            }
         }

         send(exchange, 200, result);
      } catch (Exception e) {
         Throwable cause = e;
         while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
         }

         if (cause instanceof IllegalArgumentException || cause instanceof IllegalStateException) {
            send(exchange, 400, error(describe(cause)));
         } else {
            // Строка "NullPointerException" без стека бесполезна: такие баги уже приходилось
            // ловить вслепую. Пишем в лог и отдаём верхние кадры.
            LOGGER.error("[IR-Bridge] {} {} -> {}", exchange.getRequestMethod(),
               exchange.getRequestURI().getPath(), cause.toString(), cause);
            send(exchange, 500, error(describe(cause)));
         }
      }
   }

   /** Сообщение об ошибке с верхними кадрами стека — иначе NPE невозможно локализовать. */
   private static String describe(Throwable cause) {
      StringBuilder sb = new StringBuilder(String.valueOf(cause));
      StackTraceElement[] frames = cause.getStackTrace();
      for (int i = 0; i < Math.min(frames.length, 6); i++) {
         sb.append("\n    at ").append(frames[i]);
      }

      return sb.toString();
   }

   private static JsonElement help() {
      JsonObject object = new JsonObject();
      JsonArray endpoints = new JsonArray();
      endpoints.add("GET  /status");
      endpoints.add("GET  /help");
      endpoints.add("POST /exec    {\"command\":\"/time set day\"}  or {\"commands\":[...]}");
      endpoints.add("POST /wait    {\"ticks\":20}");
      endpoints.add("GET  /block   ?x=..&y=..&z=..");
      endpoints.add("GET  /energy  ?x=..&y=..&z=..[&side=north]");
      endpoints.add("GET  /fluids  ?x=..&y=..&z=..");
      endpoints.add("GET  /items   ?x=..&y=..&z=..   (or ?player)");
      endpoints.add("GET  /players");
      endpoints.add("GET  /drops   ?x=..&y=..&z=..[&item=..&count=..]   — дроп блока с инструментом");
      endpoints.add("GET  /menu");
      endpoints.add("POST /menu/open  {\"x\":..,\"y\":..,\"z\":..}");
      endpoints.add("POST /menu/click {\"slot\":3,\"button\":0,\"type\":\"PICKUP\"}");
      endpoints.add("POST /menu/button {\"id\":1}   (menu buttons: scanner save, replicator mode, ...)");
      endpoints.add("POST /menu/close");
      endpoints.add("POST /use     {\"x\":..,\"y\":..,\"z\":..,\"item\":\"industrialreforged:wrench\"} — ПКМ по блоку");
      endpoints.add("GET  /be      ?x=..&y=..&z=..   — все поля block entity (тепло, флаги, танки, слоты)");
      endpoints.add("POST /be      {\"x\":..,\"y\":..,\"z\":..,\"setField\":\"machineHeat\",\"value\":9000}");
      endpoints.add("POST /be      {\"x\":..,\"y\":..,\"z\":..,\"setField\":\"rotorSlot\",\"item\":\"id\",\"damage\":100}");
      endpoints.add("GET  /map     ?x1=..&z1=..&x2=..&z2=..&y=..");
      endpoints.add("POST /screenshot {\"path\":\"/tmp/shot.png\"}");
      endpoints.add("GET  /clientbe  ?x=..&y=..&z=..   — NBT блок-сущности на стороне КЛИЕНТА");
      endpoints.add("POST /perf    {\"reset\":true} — время тика сервера (avg/p50/p95/max, мс) с прошлого сброса");
      endpoints.add("POST /ae2     {\"op\":\"info|count|insert|extract|craftable|toggle|push|put|sample|craft\",\"x\":..,\"y\":..,\"z\":..} — ME-сеть у узла AE2");
      object.add("endpoints", endpoints);
      return object;
   }

   /**
    * ME network around an AE2 node (ME machines E2E). Only with the AE2 integration active; all AE2
    * types stay in {@code MEDevBridge}, this class must load without AE2.
    */
   private static JsonElement ae2(JsonObject params) {
      if (!com.industrialreforged.tech.core.IR.ae2Integration) {
         return error("AE2 integration is not active");
      }

      String op = params.has("op") ? params.get("op").getAsString() : "info";
      if (!"craft".equals(op)) {
         return onServer(() -> com.industrialreforged.tech.core.integration.ae2.MEDevBridge.handle(op, level(params), pos(params), params));
      }

      // the calculation runs on AE2's thread: wait for it here, never on the server thread
      Object pending = onServer(() -> com.industrialreforged.tech.core.integration.ae2.MEDevBridge.beginCraft(level(params), pos(params), params));
      if (pending instanceof JsonObject failed) {
         return failed;
      }

      Object plan = com.industrialreforged.tech.core.integration.ae2.MEDevBridge.awaitPlan(pending, DEFAULT_TIMEOUT_MS);
      if (plan instanceof JsonObject failed) {
         return failed;
      }

      return onServer(() -> com.industrialreforged.tech.core.integration.ae2.MEDevBridge.submit(pending, plan));
   }

   private static JsonObject advancements() {
      JsonObject object = new JsonObject();
      MinecraftServer minecraftServer = gameServer();
      if (minecraftServer == null) {
         object.addProperty("server", false);
         return object;
      }

      JsonArray list = new JsonArray();
      for (var holder : minecraftServer.getAdvancements().getAllAdvancements()) {
         ResourceLocation id = holder.id();
         if (id.getNamespace().equals("industrialreforged")) {
            list.add(id.toString());
         }
      }

      object.addProperty("server", true);
      object.addProperty("count", list.size());
      object.add("advancements", list);
      return object;
   }

   private static JsonObject status() {
      JsonObject object = new JsonObject();
      object.addProperty("mod", "industrialreforged");
      object.addProperty("dist", FMLEnvironment.dist.name());
      object.addProperty("port", port);
      object.addProperty("uptimeMs", System.currentTimeMillis() - startedAt);

      MinecraftServer minecraftServer = gameServer();
      if (minecraftServer == null) {
         object.addProperty("server", false);
         return object;
      }

      object.addProperty("server", true);
      object.addProperty("tick", minecraftServer.getTickCount());
      object.addProperty("gameTime", minecraftServer.overworld().getGameTime());
      object.addProperty("dayTime", minecraftServer.overworld().dayTime());
      object.addProperty("frozen", minecraftServer.tickRateManager().isFrozen());
      object.addProperty("paused", minecraftServer.isPaused());
      object.addProperty("players", minecraftServer.getPlayerList().getPlayers().size());

      JsonArray players = new JsonArray();
      for (ServerPlayer player : minecraftServer.getPlayerList().getPlayers()) {
         players.add(playerJson(player));
      }

      object.add("playerList", players);
      return object;
   }

   private static JsonObject playerJson(ServerPlayer player) {
      JsonObject json = new JsonObject();
      json.addProperty("name", player.getName().getString());
      json.addProperty("dimension", player.level().dimension().location().toString());
      json.addProperty("x", player.getX());
      json.addProperty("y", player.getY());
      json.addProperty("z", player.getZ());
      json.addProperty("yaw", player.getYRot());
      json.addProperty("pitch", player.getXRot());
      json.addProperty("health", player.getHealth());
      json.addProperty("food", player.getFoodData().getFoodLevel());
      json.addProperty("gameMode", player.gameMode.getGameModeForPlayer().getName());
      json.addProperty("creative", player.isCreative());
      return json;
   }

   // ------------------------------------------------------------------
   // commands
   // ------------------------------------------------------------------

   private static JsonElement exec(JsonObject params) {
      List<String> commands = new ArrayList<>();
      if (params.has("commands")) {
         for (JsonElement element : params.getAsJsonArray("commands")) {
            commands.add(element.getAsString());
         }
      } else if (params.has("command")) {
         commands.add(params.get("command").getAsString());
      }

      if (commands.isEmpty()) {
         return error("missing command");
      }

      return onServer(() -> {
         MinecraftServer minecraftServer = gameServer();
         ServerPlayer player = firstPlayerOrNull();
         JsonObject result = new JsonObject();
         result.addProperty("ok", true);
         JsonArray output = new JsonArray();

         for (String command : commands) {
            try {
               CommandSourceStack source = commandSource(minecraftServer, player, output);
               String prefixed = command.startsWith("/") ? command.substring(1) : command;
               minecraftServer.getCommands().performPrefixedCommand(source, prefixed);
               output.add("> " + command);
            } catch (Exception e) {
               result.addProperty("ok", false);
               output.add("> " + command + "  !! " + e);
            }
         }

         result.add("output", output);
         return result;
      });
   }

   private static CommandSourceStack commandSource(MinecraftServer server, @Nullable ServerPlayer player, JsonArray output) {
      CommandSource collector = new CommandSource() {
         @Override
         public void sendSystemMessage(Component component) {
            output.add(component.getString());
         }

         @Override
         public boolean acceptsSuccess() {
            return true;
         }

         @Override
         public boolean acceptsFailure() {
            return true;
         }

         @Override
         public boolean shouldInformAdmins() {
            return false;
         }
      };

      ServerLevel level = player != null ? player.serverLevel() : server.overworld();
      Vec3 position = player != null ? player.position() : Vec3.atLowerCornerOf(level.getSharedSpawnPos());
      Vec2 rotation = player != null ? new Vec2(player.getXRot(), player.getYRot()) : Vec2.ZERO;
      return new CommandSourceStack(
         collector,
         position,
         rotation,
         level,
         4,
         player != null ? player.getName().getString() : "bridge",
         player != null ? player.getDisplayName() : Component.literal("bridge"),
         server,
         player
      );
   }

   private static JsonElement wait(JsonObject params) throws Exception {
      int ticks = params.has("ticks") ? params.get("ticks").getAsInt() : 20;
      long timeout = params.has("timeoutMs") ? params.get("timeoutMs").getAsLong() : Math.max(DEFAULT_TIMEOUT_MS, ticks * 500L);
      MinecraftServer server = gameServer();
      if (server == null) {
         return error("no server");
      }

      long target = onServer(() -> server.getTickCount() + ticks);
      long deadline = System.currentTimeMillis() + timeout;

      while (System.currentTimeMillis() < deadline) {
         if (onServer(server::getTickCount) >= target) {
            JsonObject result = new JsonObject();
            result.addProperty("ok", true);
            result.addProperty("tick", onServer(server::getTickCount));
            return result;
         }

         Thread.sleep(5L);
      }

      return error("timeout waiting " + ticks + " ticks");
   }

   // ------------------------------------------------------------------
   // world inspection
   // ------------------------------------------------------------------

   private static JsonElement block(JsonObject params) {
      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerLevel level = level(params);
         BlockState state = level.getBlockState(pos);
         JsonObject json = new JsonObject();
         json.addProperty("block", BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
         json.addProperty("state", state.toString());
         json.addProperty("light", level.getMaxLocalRawBrightness(pos));
         json.addProperty("skyLight", level.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos));
         json.addProperty("power", level.getBestNeighborSignal(pos));
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity != null) {
            json.addProperty("beClass", blockEntity.getClass().getName());
            try {
               json.addProperty("be", blockEntity.saveWithFullMetadata(level.registryAccess()).toString());
            } catch (Exception e) {
               json.addProperty("beError", String.valueOf(e));
            }
         }

         return json;
      });
   }

   private static JsonElement energy(JsonObject params) {
      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerLevel level = level(params);
         Direction side = side(params);
         IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
         JsonObject json = new JsonObject();

         if (storage == null) {
            // fall back to any public energy field of our own block entities
            BlockEntity blockEntity = level.getBlockEntity(pos);
            storage = findField(blockEntity, IEnergyStorage.class);
         }

         if (storage == null) {
            json.addProperty("found", false);
            return json;
         }

         // write mode: set the buffer directly from tests
         if (params.has("set")) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            Object raw = findField(blockEntity, Object.class, "energy");
            long value = params.get("set").getAsLong();
            if (raw instanceof com.industrialreforged.energy.BigEnergyStorage big) {
               big.setStored(value);
            } else if (raw instanceof com.industrialreforged.energy.EnergyStorage basic) {
               basic.setStored((int)Math.min(value, Integer.MAX_VALUE));
            } else if (storage instanceof net.neoforged.neoforge.energy.IEnergyStorage ext) {
               int current = ext.getEnergyStored();
               if (value > current) {
                  ext.receiveEnergy((int)Math.min(value - current, Integer.MAX_VALUE), false);
               } else if (value < current) {
                  ext.extractEnergy(current - (int)value, false);
               }
            }

            json.addProperty("ok", true);
            json.addProperty("set", value);
            return json;
         }

         json.addProperty("found", true);
         json.addProperty("stored", storage.getEnergyStored());
         json.addProperty("capacity", storage.getMaxEnergyStored());
         json.addProperty("canReceive", storage.canReceive());
         json.addProperty("canExtract", storage.canExtract());
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity != null) {
            Object raw = findField(blockEntity, Object.class, "energy");
            if (raw instanceof com.industrialreforged.energy.BigEnergyStorage big) {
               json.addProperty("storedLong", big.getStored());
               json.addProperty("capacityLong", big.getCapacity());
               json.addProperty("receive", big.getReceive());
               json.addProperty("extract", big.getExtract());
            } else if (raw instanceof com.industrialreforged.energy.EnergyStorage basic) {
               json.addProperty("receive", basic.getReceive());
               json.addProperty("extract", basic.getExtract());
            }
         }

         return json;
      });
   }

   private static JsonElement fluids(JsonObject params) {
      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerLevel level = level(params);
         IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side(params));
         if (handler == null) {
            handler = findField(level.getBlockEntity(pos), IFluidHandler.class);
         }

         JsonObject json = new JsonObject();
         if (handler == null) {
            json.addProperty("found", false);
            return json;
         }

         // write mode: fill the tank from tests (steam, water, coolant...)
         if (params.has("fluid")) {
            var fluid = BuiltInRegistries.FLUID.get(net.minecraft.resources.ResourceLocation.parse(params.get("fluid").getAsString()));
            int amount = params.has("amount") ? params.get("amount").getAsInt() : 1000;
            json.addProperty("filled", handler.fill(new net.neoforged.neoforge.fluids.FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE));
            return json;
         }

         // drain mode: слить жидкость из танка (для тестов режимов клапана)
         if (params.has("drain")) {
            int amount = params.has("amount") ? params.get("amount").getAsInt() : 1000;
            net.neoforged.neoforge.fluids.FluidStack drained;
            if (params.has("fluid") && !params.get("fluid").getAsString().isBlank()) {
               var fluid = BuiltInRegistries.FLUID.get(net.minecraft.resources.ResourceLocation.parse(params.get("fluid").getAsString()));
               drained = handler.drain(new net.neoforged.neoforge.fluids.FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE);
            } else {
               drained = handler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
            }

            json.addProperty("drained", drained.getAmount());
            json.addProperty("fluid", drained.isEmpty() ? "empty" : BuiltInRegistries.FLUID.getKey(drained.getFluid()).toString());
            return json;
         }

         json.addProperty("found", true);
         JsonArray tanks = new JsonArray();
         for (int i = 0; i < handler.getTanks(); i++) {
            JsonObject tank = new JsonObject();
            var fluid = handler.getFluidInTank(i);
            tank.addProperty("fluid", fluid.isEmpty() ? "empty" : BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString());
            tank.addProperty("amount", fluid.getAmount());
            tank.addProperty("capacity", handler.getTankCapacity(i));
            tanks.add(tank);
         }

         json.add("tanks", tanks);
         return json;
      });
   }

   private static JsonElement items(JsonObject params) {
      if (params.has("slot")) {
         // write an item into a player slot (real or fake player)
         return onServer(() -> {
            ServerPlayer player = playerFor(params);
            int slot = params.get("slot").getAsInt();
            int count = params.has("count") ? params.get("count").getAsInt() : 1;
            ItemStack stack = ItemStack.EMPTY;
            if (params.has("item") && !params.get("item").getAsString().isBlank()) {
               net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.get(
                  net.minecraft.resources.ResourceLocation.parse(params.get("item").getAsString())
               );
               stack = new ItemStack(item, Math.max(1, count));
            }

            player.getInventory().setItem(slot, stack);
            player.inventoryMenu.broadcastChanges();
            JsonObject json = new JsonObject();
            json.addProperty("ok", true);
            json.addProperty("slot", slot);
            json.addProperty("count", stack.getCount());
            return json;
         });
      }

      if (params.has("player")) {
         return onServer(() -> {
            ServerPlayer player = playerFor(params);
            JsonObject json = new JsonObject();
            JsonArray slots = new JsonArray();
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
               ItemStack stack = player.getInventory().getItem(i);
               if (!stack.isEmpty()) {
                  JsonObject entry = itemJson(i, stack);
                  slots.add(entry);
               }
            }

            json.add("slots", slots);
            return json;
         });
      }

      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerLevel level = level(params);
         IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side(params));
         if (handler == null) {
            handler = findField(level.getBlockEntity(pos), IItemHandler.class);
         }

         JsonObject json = new JsonObject();
         if (handler == null) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Container container) {
               JsonArray slots = new JsonArray();
               for (int i = 0; i < container.getContainerSize(); i++) {
                  ItemStack stack = container.getItem(i);
                  if (!stack.isEmpty()) {
                     slots.add(itemJson(i, stack));
                  }
               }

               json.add("slots", slots);
               return json;
            }

            json.addProperty("found", false);
            return json;
         }

         JsonArray slots = new JsonArray();
         for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
               slots.add(itemJson(i, stack));
            }
         }

         json.add("slots", slots);
         json.add("containers", containerFields(level.getBlockEntity(pos)));
         return json;
      });
   }

   /**
    * Walks the block entity fields for our own containers and reports them by
    * field name, so tests no longer depend on the capability layout.
    */
   private static JsonObject containerFields(@Nullable BlockEntity blockEntity) {
      JsonObject containers = new JsonObject();
      if (blockEntity == null) {
         return containers;
      }

      for (Class<?> clazz = blockEntity.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
         for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            if (!Container.class.isAssignableFrom(field.getType())) {
               continue;
            }

            try {
               field.setAccessible(true);
               if (!(field.get(blockEntity) instanceof Container container)) {
                  continue;
               }

               JsonArray slots = new JsonArray();
               for (int i = 0; i < container.getContainerSize(); i++) {
                  ItemStack stack = container.getItem(i);
                  if (!stack.isEmpty()) {
                     slots.add(itemJson(i, stack));
                  }
               }

               containers.add(field.getName(), slots);
            } catch (Exception ignored) {
               // try the next field
            }
         }
      }

      return containers;
   }

   private static JsonObject itemJson(int slot, ItemStack stack) {
      JsonObject entry = new JsonObject();
      entry.addProperty("slot", slot);
      entry.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
      entry.addProperty("count", stack.getCount());
      if (stack.getMaxDamage() > 0) {
         entry.addProperty("damage", stack.getDamageValue());
         entry.addProperty("maxDamage", stack.getMaxDamage());
      }

      // энергия предмета (электрические инструменты, броня, батарейки) — для e2e-тестов
      var energy = stack.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.ITEM);
      if (energy != null) {
         entry.addProperty("energy", energy.getEnergyStored());
         entry.addProperty("energyCapacity", energy.getMaxEnergyStored());
      }

      return entry;
   }

   /** Loaded recipes producing the given item (verifies the datapack in game). */
   private static JsonElement recipes(JsonObject params) {
      String itemId = params.get("item").getAsString();
      return onServer(() -> {
         var level = level(params);
         var item = BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.parse(itemId));
         JsonObject json = new JsonObject();
         JsonArray array = new JsonArray();

         for (var holder : level.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)) {
            var recipe = holder.value();
            var result = recipe.getResultItem(level.registryAccess());
            if (result.isEmpty() || result.getItem() != item) {
               continue;
            }

            JsonObject entry = new JsonObject();
            entry.addProperty("id", holder.id().toString());
            entry.addProperty("shaped", recipe instanceof net.minecraft.world.item.crafting.ShapedRecipe);
            JsonArray ingredients = new JsonArray();
            for (var ingredient : recipe.getIngredients()) {
               if (ingredient.isEmpty()) {
                  ingredients.add(" ");
                  continue;
               }

               var stacks = ingredient.getItems();
               ingredients.add(stacks.length == 0 ? "?" : BuiltInRegistries.ITEM.getKey(stacks[0].getItem()).toString());
            }

            entry.add("ingredients", ingredients);
            entry.addProperty("result", BuiltInRegistries.ITEM.getKey(result.getItem()) + " x" + result.getCount());
            array.add(entry);
         }

         level.getRecipeManager().byType(com.industrialreforged.tech.core.ref.IRRecipeTypes.MOLECULAR_TRANSFORMER.get()).forEach(holder -> {
            var recipe = holder.value();
            var result = recipe.getResultItem(level.registryAccess());
            if (result.isEmpty() || result.getItem() != item) {
               return;
            }

            JsonObject entry = new JsonObject();
            entry.addProperty("id", holder.id().toString() + " (MT)");
            entry.addProperty("shaped", false);
            JsonArray ingredients = new JsonArray();
            ingredients.add(BuiltInRegistries.ITEM.getKey(recipe.getIngredient().ingredient().getItems()[0].getItem()).toString());
            entry.add("ingredients", ingredients);
            entry.addProperty("result", BuiltInRegistries.ITEM.getKey(result.getItem()) + " x" + result.getCount());
            array.add(entry);
         });

         // Машинные рецепты остальных станков: раньше эндпоинт показывал только крафт и MT,
         // из-за чего «рецепта нет» можно было спутать с «рецепт не загрузился» — а это
         // разные вещи (например, у доменной печи).
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.MACERATOR.get(), "macerator", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.EXTRACTOR.get(), "extractor", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.COMPRESSOR.get(), "compressor", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.CENTRIFUGE.get(), "centrifuge", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.BLOCK_CUTTER.get(), "block_cutter", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.BLAST_FURNACE.get(), "blast_furnace", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.METAL_FORMER.get(), "metal_former", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.ORE_WASHER.get(), "ore_washing", item, array);
         machineRecipes(level, com.industrialreforged.tech.core.ref.IRRecipeTypes.SCANNER.get(), "scanner", item, array);

         json.add("recipes", array);
         return json;
      });
   }

   private static <I extends net.minecraft.world.item.crafting.RecipeInput, T extends net.minecraft.world.item.crafting.Recipe<I>> void machineRecipes(
      net.minecraft.world.level.Level level, net.minecraft.world.item.crafting.RecipeType<T> type, String typeName,
      net.minecraft.world.item.Item item, JsonArray array
   ) {
      for (var holder : level.getRecipeManager().getAllRecipesFor(type)) {
         var recipe = holder.value();
         // getResultItem() у рецептов с несколькими продуктами всегда пустой (BasicRecipe
         // возвращает EMPTY), поэтому смотрим весь список результатов
         // у рецептов мода своя форма: getResultItems() вместо ванильного getResultItem()
         net.minecraft.core.NonNullList<net.minecraft.world.item.ItemStack> results =
            recipe instanceof com.industrialreforged.tech.core.recipe.basic.BasicRecipe basic
               ? basic.getResultItems()
               : net.minecraft.core.NonNullList.withSize(0, net.minecraft.world.item.ItemStack.EMPTY);
         boolean matches = false;
         for (var stack : results) {
            if (!stack.isEmpty() && stack.getItem() == item) {
               matches = true;
            }
         }

         if (!matches) {
            continue;
         }

         JsonArray outputs = new JsonArray();
         for (var stack : results) {
            if (!stack.isEmpty()) {
               outputs.add(BuiltInRegistries.ITEM.getKey(stack.getItem()) + " x" + stack.getCount());
            }
         }

         JsonObject entry = new JsonObject();
         entry.addProperty("id", holder.id().toString());
         entry.addProperty("type", typeName);
         entry.add("results", outputs);
         array.add(entry);
      }
   }

   /**
    * Universal block entity inspector/mutator for tests: dumps all scalar fields
    * (heat, production, flags), energy storage, fluid tanks and container slots;
    * can write a numeric field or place an item into a container field.
    */
   private static JsonElement be(JsonObject params) {
      return onServer(() -> {
         ServerLevel level = level(params);
         BlockPos pos = pos(params);
         BlockEntity blockEntity = level.getBlockEntity(pos);
         JsonObject json = new JsonObject();
         if (blockEntity == null) {
            json.addProperty("found", false);
            return json;
         }

         json.addProperty("found", true);
         json.addProperty("class", blockEntity.getClass().getSimpleName());

         // ---- write mode -------------------------------------------------
         if (params.has("action")) {
            String act = params.get("action").getAsString();
            if ("validate".equalsIgnoreCase(act) && blockEntity instanceof com.industrialreforged.tech.multiblock.tank.TankValveBlockEntity valve) {
               json.addProperty("validated", valve.validateStructure(null));
               json.addProperty("formed", valve.isFormed());
               json.addProperty("capacity", valve.getCapacity());
               return json;
            }
            if ("validate".equalsIgnoreCase(act)
               && blockEntity instanceof com.industrialreforged.tech.multiblock.tank.TankControllerBlockEntity controller) {
               json.addProperty("validated", controller.validateStructure(null));
               json.addProperty("formed", controller.isFormed());
               json.addProperty("capacity", controller.getCapacity());
               return json;
            }
         }

         if (params.has("setField")) {
            json.addProperty("ok", beSetField(blockEntity, level, params));
            return json;
         }

         // ---- read mode --------------------------------------------------
         JsonObject fields = new JsonObject();
         for (Class<?> clazz = blockEntity.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
               if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || fields.has(field.getName())) {
                  continue;
               }

               try {
                  field.setAccessible(true);
                  Object value = field.get(blockEntity);
                  JsonElement encoded = encodeBeValue(value);
                  if (encoded != null) {
                     fields.add(field.getName(), encoded);
                  }
               } catch (Exception ignored) {
               }
            }
         }

         json.add("fields", fields);
         return json;
      });
   }

   /** Encodes a field value for the /be dump; null = skip the field. */
   @Nullable
   private static JsonElement encodeBeValue(Object value) {
      if (value == null
         || value instanceof net.minecraft.world.level.Level
         || value instanceof BlockEntity) {
         return null;
      }

      if (value instanceof ItemStack stack) {
         return itemJson(-1, stack);
      }

      if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte
         || value instanceof Float || value instanceof Double || value instanceof Boolean
         || value instanceof String || value instanceof Enum<?>) {
         JsonObject json = new JsonObject();
         json.addProperty("value", value instanceof Enum<?> e ? e.name() : String.valueOf(value));
         return json;
      }

      if (value instanceof com.industrialreforged.energy.BigEnergyStorage big) {
         JsonObject json = new JsonObject();
         json.addProperty("stored", big.getStored());
         json.addProperty("capacity", big.getCapacity());
         json.addProperty("extract", big.getExtract());
         return json;
      }

      if (value instanceof IEnergyStorage storage) {
         JsonObject json = new JsonObject();
         json.addProperty("stored", storage.getEnergyStored());
         json.addProperty("capacity", storage.getMaxEnergyStored());
         return json;
      }

      if (value instanceof IFluidHandler handler) {
         JsonObject json = new JsonObject();
         var fluid = handler.getFluidInTank(0);
         json.addProperty("fluid", fluid.isEmpty() ? "empty" : BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString());
         json.addProperty("amount", fluid.getAmount());
         json.addProperty("capacity", handler.getTankCapacity(0));
         return json;
      }

      if (value instanceof Container container) {
         JsonArray slots = new JsonArray();
         for (int i = 0; i < container.getContainerSize(); i++) {
            slots.add(itemJson(i, container.getItem(i)));
         }

         JsonObject json = new JsonObject();
         json.add("slots", slots);
         return json;
      }

      return null;
   }

   /** Writes a field of the block entity for /be; returns true on success. */
   private static boolean beSetField(BlockEntity blockEntity, ServerLevel level, JsonObject params) {
      String name = params.get("setField").getAsString();
      java.lang.reflect.Field reflected = findFieldHandle(blockEntity, name);
      if (reflected == null) {
         return false;
      }

      try {
         reflected.setAccessible(true);
         Object field;
         try {
            field = reflected.get(blockEntity);
         } catch (Exception e) {
            field = null;
         }

         if (params.has("value")) {
            String raw = params.get("value").getAsString();
            Class<?> type = reflected.getType();
            if (type == int.class || type == Integer.class) {
               reflected.set(blockEntity, Integer.parseInt(raw));
            } else if (type == long.class || type == Long.class) {
               reflected.set(blockEntity, Long.parseLong(raw));
            } else if (type == short.class || type == Short.class) {
               reflected.set(blockEntity, Short.parseShort(raw));
            } else if (type == byte.class || type == Byte.class) {
               reflected.set(blockEntity, Byte.parseByte(raw));
            } else if (type == float.class || type == Float.class) {
               reflected.set(blockEntity, Float.parseFloat(raw));
            } else if (type == double.class || type == Double.class) {
               reflected.set(blockEntity, Double.parseDouble(raw));
            } else if (type == boolean.class || type == Boolean.class) {
               reflected.set(blockEntity, Boolean.parseBoolean(raw));
            } else if (type == String.class) {
               reflected.set(blockEntity, raw);
            } else {
               return false;
            }
         } else if (params.has("item")) {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.get(
               net.minecraft.resources.ResourceLocation.parse(params.get("item").getAsString())
            );
            int count = params.has("count") ? params.get("count").getAsInt() : 1;
            ItemStack stack = count <= 0 || item == net.minecraft.world.item.Items.AIR
               ? ItemStack.EMPTY
               : new ItemStack(item, count);
            if (params.has("damage") && stack.getMaxDamage() > 0) {
               stack.setDamageValue(Math.min(params.get("damage").getAsInt(), stack.getMaxDamage() - 1));
            }

            if (field instanceof ItemStack slot) {
               reflected.set(blockEntity, stack);
            } else if (field instanceof Container container) {
               int slotIndex = params.has("slot") ? params.get("slot").getAsInt() : 0;
               container.setItem(slotIndex, stack);
            } else if (field instanceof ItemStack[] array) {
               int slotIndex = params.has("slot") ? params.get("slot").getAsInt() : 0;
               if (slotIndex >= 0 && slotIndex < array.length) {
                  array[slotIndex] = stack;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }

         if (blockEntity instanceof com.industrialreforged.blockentity.BaseBlockEntity base) {
            base.markForUpdate();
            base.markForSave();
         }

         return true;
      } catch (Exception e) {
         return false;
      }
   }

   @Nullable
   private static java.lang.reflect.Field findFieldHandle(Object target, String name) {
      for (Class<?> clazz = target.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
         try {
            return clazz.getDeclaredField(name);
         } catch (Exception ignored) {
         }
      }

      return null;
   }

   /** Breaks a block as the player, so loot/wrench behaviour can be tested. */
   private static JsonElement breakBlock(JsonObject params) {
      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerPlayer player = playerFor(params);
         boolean destroyed = player.gameMode.destroyBlock(pos);
         JsonObject json = new JsonObject();
         json.addProperty("ok", destroyed);
         json.addProperty("player", playerKey(params));
         return json;
      });
   }

   /** Симуляция ПКМ игроком по блоку (в т.ч. onItemUseFirst — ключи, вёдра, инструменты). */
   private static JsonElement useItemOnBlock(JsonObject params) {
      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerLevel level = level(params);
         ServerPlayer player = playerFor(params);
         ItemStack stack = ItemStack.EMPTY;
         if (params.has("item") && !params.get("item").getAsString().isBlank()) {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.get(
               net.minecraft.resources.ResourceLocation.parse(params.get("item").getAsString())
            );
            stack = new ItemStack(item, params.has("count") ? params.get("count").getAsInt() : 1);
         }

         player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stack);
         Direction side = side(params);
         BlockHitResult hit = new BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos), side, pos, false);
         InteractionResult result = player.gameMode.useItemOn(
            player, level, player.getMainHandItem(), net.minecraft.world.InteractionHand.MAIN_HAND, hit
         );
         JsonObject json = new JsonObject();
         json.addProperty("ok", true);
         json.addProperty("player", playerKey(params));
         json.addProperty("result", result.toString());
         json.addProperty("state", level.getBlockState(pos).toString());
         json.addProperty("held", BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString());
         return json;
      });
   }

   /** Дроп блока с учётом инструмента — проверка «ключ/без ключа» без ломания. */
   private static JsonElement drops(JsonObject params) {
      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerLevel level = level(params);
         BlockState state = level.getBlockState(pos);
         ItemStack tool = ItemStack.EMPTY;
         if (params.has("item") && !params.get("item").getAsString().isBlank()) {
            net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.get(
               net.minecraft.resources.ResourceLocation.parse(params.get("item").getAsString())
            );
            tool = new ItemStack(item, params.has("count") ? params.get("count").getAsInt() : 1);
         }

         net.minecraft.world.level.storage.loot.LootParams.Builder builder = new net.minecraft.world.level.storage.loot.LootParams.Builder(level)
            .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN, net.minecraft.world.phys.Vec3.atCenterOf(pos))
            .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL, tool)
            .withOptionalParameter(
               net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos)
            );
         JsonObject json = new JsonObject();
         JsonArray array = new JsonArray();
         for (ItemStack stack : state.getDrops(builder)) {
            array.add(itemJson(0, stack));
         }

         json.add("drops", array);
         return json;
      });
   }

   /** Entities around a point (item entities carry their stack). */
   private static JsonElement entities(JsonObject params) {
      return onServer(() -> {
         ServerLevel level = level(params);
         double x = params.get("x").getAsDouble();
         double y = params.get("y").getAsDouble();
         double z = params.get("z").getAsDouble();
         double radius = params.has("r") ? params.get("r").getAsDouble() : 8.0;
         var box = new net.minecraft.world.phys.AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);

         JsonObject json = new JsonObject();
         JsonArray array = new JsonArray();
         for (net.minecraft.world.entity.Entity entity : level.getEntitiesOfClass(net.minecraft.world.entity.Entity.class, box)) {
            JsonObject entry = new JsonObject();
            entry.addProperty("type", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
            entry.addProperty("x", entity.getX());
            entry.addProperty("y", entity.getY());
            entry.addProperty("z", entity.getZ());
            if (entity instanceof net.minecraft.world.entity.item.ItemEntity item) {
               entry.addProperty("item", BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString());
               entry.addProperty("count", item.getItem().getCount());
            }

            array.add(entry);
         }

         json.add("entities", array);
         return json;
      });
   }

   /** Teleports the real or fake player (needed to test pads/step-on logic). */
   private static JsonElement movePlayer(JsonObject params) {
      return onServer(() -> {
         ServerPlayer player = playerFor(params);
         double x = params.get("x").getAsDouble();
         double y = params.get("y").getAsDouble();
         double z = params.get("z").getAsDouble();
         float yaw = params.has("yaw") ? params.get("yaw").getAsFloat() : player.getYRot();
         float pitch = params.has("pitch") ? params.get("pitch").getAsFloat() : player.getXRot();
         // FakePlayer overrides teleportTo() to a no-op, so position it directly
         player.setPos(x, y, z);
         player.setYRot(yaw);
         player.setXRot(pitch);
         JsonObject json = new JsonObject();
         json.addProperty("ok", true);
         json.addProperty("player", playerKey(params));
         json.addProperty("x", player.getX());
         json.addProperty("y", player.getY());
         json.addProperty("z", player.getZ());
         return json;
      });
   }

   private static JsonElement players() {
      return onServer(() -> {
         JsonArray array = new JsonArray();
         for (ServerPlayer player : gameServer().getPlayerList().getPlayers()) {
            array.add(playerJson(player));
         }

         return array;
      });
   }

   // ------------------------------------------------------------------
   // menus
   // ------------------------------------------------------------------

   private static JsonElement menu(JsonObject params) {
      return onServer(() -> {
         AbstractContainerMenu menu = activeMenu(params);
         JsonObject json = new JsonObject();
         json.addProperty("containerId", menu.containerId);
         json.addProperty("player", playerKey(params));
         JsonArray slots = new JsonArray();
         for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            JsonObject entry = itemJson(i, slot.getItem());
            entry.addProperty("x", slot.x);
            entry.addProperty("y", slot.y);
            slots.add(entry);
         }

         json.add("slots", slots);
         json.addProperty("playerInventoryStart", menu.slots.size() - 36);
         return json;
      });
   }

   private static JsonElement openMenu(JsonObject params) {
      BlockPos pos = pos(params);
      return onServer(() -> {
         ServerLevel level = level(params);
         BlockEntity blockEntity = level.getBlockEntity(pos);
         JsonObject json = new JsonObject();
         // blocks without a block entity (crafting table, stonecutter, ...) still
         // expose a menu through the block state
         net.minecraft.world.MenuProvider provider = blockEntity instanceof net.minecraft.world.MenuProvider beMenu
            ? beMenu
            : level.getBlockState(pos).getMenuProvider(level, pos);
         if (provider != null) {
            if (playerKey(params).equals("fake")) {
               ServerPlayer fake = fakePlayer();
               AbstractContainerMenu menu = provider.createMenu(nextContainerId++, fake.getInventory(), fake);
               FAKE_MENUS.put("fake", menu);
               fake.containerMenu = menu;
               json.addProperty("ok", true);
               json.addProperty("player", "fake");
               json.addProperty("containerId", menu.containerId);
               json.addProperty("slotCount", menu.slots.size());
            } else {
               ServerPlayer player = firstPlayer();
               player.openMenu(provider, pos);
               json.addProperty("ok", true);
               json.addProperty("player", player.getName().getString());
            }
         } else {
            json.addProperty("ok", false);
            json.addProperty("error", "no menu provider at this block");
         }

         return json;
      });
   }

   private static JsonElement clickMenu(JsonObject params) {
      int slot = params.get("slot").getAsInt();
      int button = params.has("button") ? params.get("button").getAsInt() : 0;
      String typeName = params.has("type") ? params.get("type").getAsString() : "PICKUP";

      return onServer(() -> {
         ServerPlayer player = playerFor(params);
         AbstractContainerMenu menu = activeMenu(params);
         ClickType type = ClickType.valueOf(typeName);
         menu.clicked(slot, button, type, player);
         menu.broadcastChanges();

         JsonObject json = new JsonObject();
         json.addProperty("ok", true);
         json.addProperty("slot", slot);
         json.addProperty("type", typeName);
         ItemStack carried = menu.getCarried();
         json.addProperty("carried", carried.isEmpty() ? "" : BuiltInRegistries.ITEM.getKey(carried.getItem()) + " x" + carried.getCount());

         // report the slot contents afterwards so a script can follow along
         JsonArray after = new JsonArray();
         for (int i = 0; i < menu.slots.size(); i++) {
            ItemStack stack = menu.slots.get(i).getItem();
            if (!stack.isEmpty()) {
               after.add(itemJson(i, stack));
            }
         }

         json.add("slots", after);
         return json;
      });
   }

   private static JsonElement clickMenuButton(JsonObject params) {
      int id = params.get("id").getAsInt();
      return onServer(() -> {
         ServerPlayer player = playerFor(params);
         AbstractContainerMenu menu = activeMenu(params);
         boolean handled = menu.clickMenuButton(player, id);
         menu.broadcastChanges();
         JsonObject json = new JsonObject();
         json.addProperty("ok", true);
         json.addProperty("id", id);
         json.addProperty("handled", handled);
         return json;
      });
   }

   private static JsonElement closeMenu(JsonObject params) {
      return onServer(() -> {
         if (playerKey(params).equals("fake")) {
            FAKE_MENUS.remove("fake");
            ServerPlayer fake = fakePlayer();
            fake.containerMenu = fake.inventoryMenu;
         } else {
            firstPlayer().closeContainer();
         }

         JsonObject json = new JsonObject();
         json.addProperty("ok", true);
         return json;
      });
   }

   private static final Map<String, AbstractContainerMenu> FAKE_MENUS = new java.util.concurrent.ConcurrentHashMap<>();
   private static int nextContainerId = 200;

   private static String playerKey(JsonObject params) {
      return params.has("player") && params.get("player").getAsString().equalsIgnoreCase("fake") ? "fake" : "real";
   }

   private static ServerPlayer playerFor(JsonObject params) {
      return playerKey(params).equals("fake") ? fakePlayer() : firstPlayer();
   }

   private static ServerPlayer fakePlayer() {
      return net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(gameServer().overworld());
   }

   private static AbstractContainerMenu activeMenu(JsonObject params) {
      if (playerKey(params).equals("fake")) {
         AbstractContainerMenu menu = FAKE_MENUS.get("fake");
         if (menu == null) {
            throw new IllegalStateException("no fake menu open; call /menu/open with \"player\":\"fake\"");
         }

         return menu;
      }

      return firstPlayer().containerMenu;
   }

   // ------------------------------------------------------------------
   // map, screenshots
   // ------------------------------------------------------------------

   private static JsonElement map(JsonObject params) {
      return onServer(() -> {
         ServerLevel level = level(params);
         int x1 = params.get("x1").getAsInt();
         int z1 = params.get("z1").getAsInt();
         int x2 = params.get("x2").getAsInt();
         int z2 = params.get("z2").getAsInt();
         int y = params.get("y").getAsInt();
         int fromX = Math.min(x1, x2);
         int toX = Math.max(x1, x2);
         int fromZ = Math.min(z1, z2);
         int toZ = Math.max(z1, z2);
         if (toX - fromX > 127 || toZ - fromZ > 127) {
            throw new IllegalArgumentException("map too large, max 128x128 blocks");
         }

         Map<String, Character> legend = new LinkedHashMap<>();
         List<String> rows = new ArrayList<>();
         StringBuilder header = new StringBuilder("     ");
         for (int x = fromX; x <= toX; x++) {
            header.append(x % 10 == 0 ? String.valueOf(Math.floorMod(x, 100) / 10) : ' ');
         }

         rows.add(header.toString());

         for (int z = fromZ; z <= toZ; z++) {
            StringBuilder row = new StringBuilder(String.format("%4d ", z));
            for (int x = fromX; x <= toX; x++) {
               BlockPos pos = new BlockPos(x, y, z);
               BlockState state = level.getBlockState(pos);
               boolean hasEntity = level.getBlockEntity(pos) != null;
               String name = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
               if (state.isAir()) {
                  row.append('.');
                  continue;
               }

               Character symbol = legend.get(name);
               if (symbol == null) {
                  symbol = symbolFor(legend.size());
                  legend.put(name, symbol);
               }

               row.append(hasEntity ? Character.toUpperCase(symbol) : symbol);
            }

            rows.add(row.toString());
         }

         JsonObject json = new JsonObject();
         json.addProperty("y", y);
         JsonArray lines = new JsonArray();
         rows.forEach(lines::add);
         json.add("rows", lines);
         JsonObject legendJson = new JsonObject();
         legend.forEach((name, symbol) -> legendJson.addProperty(String.valueOf(symbol), name));
         json.add("legend", legendJson);
         json.addProperty("note", "upper case = block entity present");
         return json;
      });
   }

   /**
    * NBT блок-сущности на стороне клиента: проверка того, что значение реально доехало
    * до экрана. Серверная копия всегда права, поэтому без этого «клиент слеп» не видно.
    */
   private static JsonElement clientBlockEntity(JsonObject params) {
      if (FMLEnvironment.dist.isDedicatedServer()) {
         throw new IllegalStateException("clientBlockEntity needs the client");
      }

      int x = params.get("x").getAsInt();
      int y = params.get("y").getAsInt();
      int z = params.get("z").getAsInt();
      JsonObject json = new JsonObject();
      try {
         json.addProperty("ok", true);
         json.addProperty("nbt", DevBridgeClient.clientBlockEntity(x, y, z));
      } catch (Exception e) {
         json.addProperty("ok", false);
         json.addProperty("error", String.valueOf(e));
      }
      return json;
   }

   private static char symbolFor(int index) {
      String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
      return alphabet.charAt(index % alphabet.length());
   }

   private static JsonElement screenshot(JsonObject params) {
      if (FMLEnvironment.dist.isDedicatedServer()) {
         throw new IllegalStateException("screenshots need the client");
      }

      String path = params.has("path") ? params.get("path").getAsString() : "bridge-shot.png";
      try {
         String absolute = DevBridgeClient.screenshot(path);
         JsonObject json = new JsonObject();
         json.addProperty("ok", true);
         json.addProperty("path", absolute);
         return json;
      } catch (Exception e) {
         return error(String.valueOf(e));
      }
   }

   // ------------------------------------------------------------------
   // helpers
   // ------------------------------------------------------------------

   private static JsonObject params(HttpExchange exchange) throws IOException {
      JsonObject params = new JsonObject();

      // query string
      String query = exchange.getRequestURI().getQuery();
      if (query != null) {
         for (String pair : query.split("&")) {
            int index = pair.indexOf('=');
            if (index > 0) {
               params.addProperty(
                  java.net.URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8),
                  java.net.URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8)
               );
            }
         }
      }

      // json body
      if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
         InputStream stream = exchange.getRequestBody();
         String body = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
         if (!body.isBlank()) {
            JsonElement parsed = com.google.gson.JsonParser.parseString(body);
            if (parsed.isJsonObject()) {
               for (Map.Entry<String, JsonElement> entry : parsed.getAsJsonObject().entrySet()) {
                  params.add(entry.getKey(), entry.getValue());
               }
            }
         }
      }

      return params;
   }

   private static void send(HttpExchange exchange, int code, JsonElement body) throws IOException {
      byte[] bytes = GSON.toJson(body).getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
      exchange.sendResponseHeaders(code, bytes.length);
      try (OutputStream stream = exchange.getResponseBody()) {
         stream.write(bytes);
      }
   }

   private static JsonObject error(String message) {
      JsonObject json = new JsonObject();
      json.addProperty("ok", false);
      json.addProperty("error", message);
      return json;
   }

   @Nullable
   private static MinecraftServer gameServer() {
      return ServerLifecycleHooks.getCurrentServer();
   }

   @Nullable
   private static ServerPlayer firstPlayerOrNull() {
      List<ServerPlayer> players = gameServer().getPlayerList().getPlayers();
      return players.isEmpty() ? null : players.get(0);
   }

   private static ServerPlayer firstPlayer() {
      List<ServerPlayer> players = gameServer().getPlayerList().getPlayers();
      if (players.isEmpty()) {
         throw new IllegalStateException("no player online");
      }

      return players.get(0);
   }

   private static ServerLevel level(JsonObject params) {
      if (params.has("dim") && !params.get("dim").getAsString().isBlank()) {
         net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> key = net.minecraft.resources.ResourceKey.create(
            net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.ResourceLocation.parse(params.get("dim").getAsString())
         );
         ServerLevel level = gameServer().getLevel(key);
         if (level != null) {
            return level;
         }
      }

      ServerPlayer player = firstPlayerOrNull();
      return player != null ? player.serverLevel() : gameServer().overworld();
   }

   private static BlockPos pos(JsonObject params) {
      return new BlockPos(params.get("x").getAsInt(), params.get("y").getAsInt(), params.get("z").getAsInt());
   }

   @Nullable
   /**
    * Сторона удара для /use и /drops.
    * <p>
    * Раньше при отсутствии параметра возвращался null, и ванильные предметы, читающие
    * сторону (костьная мука, мотыга, установка блоков), падали с
    * NullPointerException внутри BlockPos.relative(direction=null). По умолчанию даём
    * верхнюю грань — как если бы игрок смотрел сверху вниз.
    */
   private static Direction side(JsonObject params) {
      if (!params.has("side")) {
         return Direction.UP;
      }

      String side = params.get("side").getAsString();
      if (side.isBlank() || side.equalsIgnoreCase("null")) {
         return Direction.UP;
      }

      Direction parsed = Direction.byName(side);
      return parsed == null ? Direction.UP : parsed;
   }

   @Nullable
   @SuppressWarnings("unchecked")
   private static <T> T findField(@Nullable Object target, Class<T> type) {
      return findField(target, type, null);
   }

   /** Finds a public field of the given type (optionally by name) for capability-less block entities. */
   @Nullable
   @SuppressWarnings("unchecked")
   private static <T> T findField(@Nullable Object target, Class<T> type, @Nullable String name) {
      if (target == null) {
         return null;
      }

      for (Class<?> clazz = target.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
         for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            if (name != null && !field.getName().equals(name)) {
               continue;
            }

            if (!type.isAssignableFrom(field.getType())) {
               continue;
            }

            try {
               field.setAccessible(true);
               return (T)field.get(target);
            } catch (Exception ignored) {
               // try the next one
            }
         }
      }

      return null;
   }

   private static <T> T onServer(java.util.concurrent.Callable<T> action) {
      MinecraftServer minecraftServer = gameServer();
      if (minecraftServer == null) {
         throw new IllegalStateException("no server running");
      }

      if (minecraftServer.isSameThread()) {
         try {
            return action.call();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      CompletableFuture<T> future = new CompletableFuture<>();
      minecraftServer.execute(() -> {
         try {
            future.complete(action.call());
         } catch (Throwable t) {
            future.completeExceptionally(t);
         }
      });

      try {
         return future.get(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         throw new RuntimeException("server task failed: " + e, e);
      }
   }
}
