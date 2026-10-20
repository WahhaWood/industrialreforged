package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * ME machine: offers every enabled recipe of its kind to the ME network as a pattern, takes the inputs a
 * crafting CPU pushes, and processes up to {@value #OPERATIONS_PER_TICK} operations per tick, paying
 * the regular machine's FE per operation as AE (2 FE = 1 AE). Results go straight back into the network.
 */
public class MEMachineBlockEntity extends AbstractMENodeBlockEntity implements ICraftingProvider {
   /** Pushed operations the machine accepts before it reports busy: two work passes, so it never runs dry between CPU pushes. */
   private static final long MAX_QUEUED = 2L * OPERATIONS_PER_TICK * WORK_INTERVAL;
   /** Machines loaded on the server, refreshed when datapacks reload. */
   private static final Set<MEMachineBlockEntity> LOADED = Collections.newSetFromMap(new WeakHashMap<>());

   public final MEMachineKind kind;
   private final MERecipeToggles toggles;
   private final Deque<Job> queue = new ArrayDeque<>();
   private final KeyCounter outputBuffer = new KeyCounter();
   private long queued;
   private long lastSaveTick = -1L;
   @Nullable
   private Map<ResourceLocation, MEMachineRecipe> recipes;
   private int recipesGeneration;
   @Nullable
   private List<IPatternDetails> patterns;

   public MEMachineBlockEntity(MEMachineKind kind, BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state, kind == MEMachineKind.MOLECULAR_TRANSFORMER ? MOLECULAR_BUFFER_AE : BUFFER_AE);
      this.kind = kind;
      this.toggles = new MERecipeToggles(kind.enabledByDefault);
      this.cableOnly = kind == MEMachineKind.MOLECULAR_TRANSFORMER;
      this.getMainNode().addService(ICraftingProvider.class, this);
   }

   /** Called after a datapack reload: every loaded machine re-reads its recipes and re-offers patterns. */
   static void refreshAll() {
      MEMachineRecipe.invalidate();
      for (MEMachineBlockEntity machine : List.copyOf(LOADED)) {
         machine.patterns = null;
         ICraftingProvider.requestUpdate(machine.getMainNode());
      }
   }

   /** A crafting job ended unfinished: every loaded machine hands back what that job pushed and has not run. */
   static void refundAll(UUID jobId) {
      for (MEMachineBlockEntity machine : List.copyOf(LOADED)) {
         machine.refundFor(jobId);
      }
   }

   @Override
   public void onReady() {
      super.onReady();
      if (!this.isClientSide()) {
         LOADED.add(this);
      }
   }

   @Override
   public void setRemoved() {
      super.setRemoved();
      LOADED.remove(this);
   }

   @Override
   public void onChunkUnloaded() {
      super.onChunkUnloaded();
      LOADED.remove(this);
   }

   // ---- recipes and toggles -------------------------------------------------------------------

   /** All usable recipes of this machine's kind, by id, in GUI order. */
   public Map<ResourceLocation, MEMachineRecipe> recipes() {
      if (this.level == null) {
         return Map.of();
      }

      int generation = MEMachineRecipe.generation();
      if (this.recipes == null || this.recipesGeneration != generation) {
         this.recipes = MEMachineRecipe.collect(this.kind, this.level);
         this.recipesGeneration = generation;
         this.patterns = null;
      }

      return this.recipes;
   }

   public boolean isEnabled(ResourceLocation id) {
      return this.toggles.isEnabled(id);
   }

   /** Server side: flips one recipe, or all of them, and re-offers the patterns. */
   public void applyToggle(MEMachineTogglePayload.Action action, @Nullable ResourceLocation id) {
      switch (action) {
         case TOGGLE -> {
            if (id == null || !this.recipes().containsKey(id)) {
               return;
            }
            this.toggles.toggle(id);
         }
         case ENABLE_ALL -> this.toggles.setAll(true);
         case DISABLE_ALL -> this.toggles.setAll(false);
      }

      this.patterns = null;
      ICraftingProvider.requestUpdate(this.getMainNode());
      this.saveChanges();
      this.markForClientUpdate();
   }

   @Override
   protected void writeToStream(RegistryFriendlyByteBuf data) {
      super.writeToStream(data);
      this.toggles.write(data);
   }

   @Override
   protected boolean readFromStream(RegistryFriendlyByteBuf data) {
      boolean changed = super.readFromStream(data);
      this.toggles.read(data);
      return changed;
   }

   // ---- ICraftingProvider ---------------------------------------------------------------------

   @Override
   public List<IPatternDetails> getAvailablePatterns() {
      Map<ResourceLocation, MEMachineRecipe> all = this.recipes();
      if (this.patterns == null) {
         List<IPatternDetails> list = new ArrayList<>();
         for (MEMachineRecipe recipe : all.values()) {
            if (this.toggles.isEnabled(recipe.id())) {
               list.add(new MEMachinePattern(recipe));
            }
         }

         this.patterns = List.copyOf(list);
      }

      return this.patterns;
   }

   @Override
   public boolean pushPattern(IPatternDetails details, KeyCounter[] inputHolder) {
      if (!(details instanceof MEMachinePattern pattern) || this.isBusy() || !this.getMainNode().isActive()) {
         return false;
      }

      MEMachineRecipe recipe = this.recipes().get(pattern.recipeId());
      if (recipe == null || !this.toggles.isEnabled(recipe.id())) {
         return false;
      }

      List<GenericStack> inputs = new ArrayList<>();
      for (KeyCounter counter : inputHolder) {
         for (var entry : counter) {
            inputs.add(new GenericStack(entry.getKey(), entry.getLongValue()));
         }
      }

      UUID owner = MECraftingCpuHooks.pushingJob();
      Job last = this.queue.peekLast();
      if (last != null && Objects.equals(last.owner, owner) && last.recipe.equals(recipe.id()) && last.inputs.equals(inputs)) {
         last.crafts++;
      } else {
         this.queue.addLast(new Job(recipe.id(), inputs, 1, owner));
      }

      // several CPUs and direct pushes may come in one tick: wake and save once per tick, not per push
      boolean idle = this.queued == 0;
      this.queued++;
      long now = this.level != null ? this.level.getGameTime() : 0L;
      if (now != this.lastSaveTick) {
         this.lastSaveTick = now;
         this.saveChanges();
      }

      if (idle) {
         this.wake();
      }

      return true;
   }

   long queuedOperations() {
      return this.queued;
   }

   /** Operations the queue still takes. */
   long freeCapacity() {
      return Math.max(0, MAX_QUEUED - this.queued);
   }

   /** More copies of the push just accepted, inputs already taken from the CPU ({@link MECraftingCpuHooks#pushMore}). */
   void acceptMore(long crafts) {
      Job last = this.queue.peekLast();
      if (last != null && crafts > 0) {
         last.crafts += crafts;
         this.queued += crafts;
      }
   }

   /**
    * A crafting job ended unfinished (cancelled, stopped): the operations it pushed and the machine has not
    * run yet are not worth running, their inputs go back to the network. Only jobs pushed through a crafting
    * CPU have an owner; the id is saved, so this works after a reload and after the CPU was rebuilt.
    */
   void refundFor(UUID jobId) {
      boolean refunded = false;
      for (var it = this.queue.iterator(); it.hasNext();) {
         Job job = it.next();
         if (jobId.equals(job.owner)) {
            for (GenericStack input : job.inputs) {
               this.outputBuffer.add(input.what(), input.amount() * job.crafts);
            }

            this.queued -= job.crafts;
            it.remove();
            refunded = true;
         }
      }

      if (refunded) {
         this.saveChanges();
         this.wake();
      }
   }

   int pendingOutputs() {
      return this.outputBuffer.size();
   }

   @Override
   public boolean isBusy() {
      return this.queued >= MAX_QUEUED || !this.outputBuffer.isEmpty();
   }

   // ---- work ----------------------------------------------------------------------------------

   @Override
   public boolean supportsCableOnly() {
      return this.kind == MEMachineKind.MOLECULAR_TRANSFORMER;
   }

   @Override
   protected boolean hasWork() {
      return !this.queue.isEmpty() || !this.outputBuffer.isEmpty();
   }

   @Override
   protected TickRateModulation doWork(IGrid grid, int ticks) {
      MEStorage storage = grid.getStorageService().getInventory();
      IEnergyService energy = grid.getEnergyService();
      this.flushOutputs(storage, energy);
      if (!this.outputBuffer.isEmpty()) {
         return TickRateModulation.SLOWER; // network full: retry before taking more work
      }

      Map<ResourceLocation, MEMachineRecipe> all = this.recipes();
      long budget = (long)OPERATIONS_PER_TICK * ticks;
      int done = 0;
      while (budget > 0 && !this.queue.isEmpty()) {
         Job job = this.queue.peekFirst();
         MEMachineRecipe recipe = all.get(job.recipe);
         if (recipe == null) {
            // recipe gone after a datapack change: hand the inputs back to the network
            for (GenericStack input : job.inputs) {
               this.outputBuffer.add(input.what(), input.amount() * job.crafts);
            }
            this.queued -= job.crafts;
            this.queue.pollFirst();
            continue;
         }

         long crafts = Math.min(budget, job.crafts);
         double price = Math.max(recipe.aeCost(), 1.0E-6); // a free recipe must not divide by zero
         double available = this.availableAE(energy, price * crafts);
         crafts = Math.min(crafts, (long)Math.floor(available / price + 1.0E-6));
         if (crafts <= 0) {
            break; // not enough AE: the job waits for power
         }

         this.payAE(energy, price * crafts);
         for (ItemStack output : recipe.outputs()) {
            AEItemKey key = AEItemKey.of(output);
            if (key != null) {
               this.outputBuffer.add(key, output.getCount() * crafts);
            }
         }

         job.crafts -= crafts;
         this.queued -= crafts;
         budget -= crafts;
         done += (int)crafts;
         if (job.crafts <= 0) {
            this.queue.pollFirst();
         }
      }

      this.lastOperations = done / ticks;
      this.flushOutputs(storage, energy);
      if (done > 0) {
         this.saveChanges();
      }

      if (!this.hasWork()) {
         return TickRateModulation.SLEEP;
      }

      // still queued: either the budget ran out (go on next tick) or AE ran short (back off)
      return done > 0 ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
   }

   private void flushOutputs(MEStorage storage, IEnergyService energy) {
      if (this.outputBuffer.isEmpty()) {
         return;
      }

      List<GenericStack> pending = new ArrayList<>(this.outputBuffer.size());
      for (var entry : this.outputBuffer) {
         pending.add(new GenericStack(entry.getKey(), entry.getLongValue()));
      }

      for (GenericStack stack : pending) {
         long inserted = this.insertResult(energy, storage, stack.what(), stack.amount());
         if (inserted > 0) {
            this.outputBuffer.remove(stack.what(), inserted);
         }
      }

      this.outputBuffer.removeZeros();
      this.saveChanges();
   }

   // ---- persistence and drops -----------------------------------------------------------------

   @Override
   public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
      super.saveAdditional(tag, registries);
      this.toggles.save(tag);
      ListTag jobs = new ListTag();
      for (Job job : this.queue) {
         CompoundTag jobTag = new CompoundTag();
         jobTag.putString("recipe", job.recipe.toString());
         jobTag.putLong("crafts", job.crafts);
         if (job.owner != null) {
            jobTag.putUUID("owner", job.owner);
         }

         jobTag.put("inputs", writeStacks(job.inputs, registries));
         jobs.add(jobTag);
      }

      tag.put("me_queue", jobs);
      List<GenericStack> outputs = new ArrayList<>();
      for (var entry : this.outputBuffer) {
         outputs.add(new GenericStack(entry.getKey(), entry.getLongValue()));
      }

      tag.put("me_outputs", writeStacks(outputs, registries));
   }

   @Override
   public void loadTag(CompoundTag tag, HolderLookup.Provider registries) {
      super.loadTag(tag, registries);
      this.toggles.load(tag);
      this.queue.clear();
      this.queued = 0;
      ListTag jobs = tag.getList("me_queue", Tag.TAG_COMPOUND);
      for (int i = 0; i < jobs.size(); i++) {
         CompoundTag jobTag = jobs.getCompound(i);
         ResourceLocation id = ResourceLocation.tryParse(jobTag.getString("recipe"));
         long crafts = jobTag.getLong("crafts");
         List<GenericStack> inputs = readStacks(jobTag.getList("inputs", Tag.TAG_COMPOUND), registries);
         if (id != null && crafts > 0) {
            this.queue.addLast(new Job(id, inputs, crafts, jobTag.hasUUID("owner") ? jobTag.getUUID("owner") : null));
            this.queued += crafts;
         }
      }

      this.outputBuffer.clear();
      for (GenericStack stack : readStacks(tag.getList("me_outputs", Tag.TAG_COMPOUND), registries)) {
         this.outputBuffer.add(stack.what(), stack.amount());
      }
   }

   private static ListTag writeStacks(List<GenericStack> stacks, HolderLookup.Provider registries) {
      ListTag list = new ListTag();
      for (GenericStack stack : stacks) {
         list.add(GenericStack.writeTag(registries, stack));
      }

      return list;
   }

   private static List<GenericStack> readStacks(ListTag list, HolderLookup.Provider registries) {
      List<GenericStack> stacks = new ArrayList<>(list.size());
      for (int i = 0; i < list.size(); i++) {
         GenericStack stack = GenericStack.readTag(registries, list.getCompound(i));
         if (stack != null && stack.amount() > 0) {
            stacks.add(stack);
         }
      }

      return stacks;
   }

   @Override
   public void addContentDrops(List<ItemStack> drops) {
      if (this.level == null) {
         return;
      }

      for (Job job : this.queue) {
         for (GenericStack input : job.inputs) {
            input.what().addDrops(input.amount() * job.crafts, drops, this.level, this.worldPosition);
         }
      }

      for (var entry : this.outputBuffer) {
         AEKey key = entry.getKey();
         key.addDrops(entry.getLongValue(), drops, this.level, this.worldPosition);
      }
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
      return new MEMachineMenu(containerId, inventory, this);
   }

   /** A run of identical pushes: same recipe, same actual input items (tag substitutes included). */
   private static final class Job {
      final ResourceLocation recipe;
      /** Inputs of one craft. */
      final List<GenericStack> inputs;
      /** Id of the crafting job that pushed it (AE2 saves it with the job); null for a direct push. */
      @Nullable
      final UUID owner;
      long crafts;

      Job(ResourceLocation recipe, List<GenericStack> inputs, long crafts, @Nullable UUID owner) {
         this.recipe = recipe;
         this.inputs = List.copyOf(inputs);
         this.crafts = crafts;
         this.owner = owner;
      }
   }
}
