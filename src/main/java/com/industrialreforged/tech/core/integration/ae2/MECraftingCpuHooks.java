package com.industrialreforged.tech.core.integration.ae2;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.CraftingLink;
import appeng.crafting.inv.ListCraftingInventory;
import java.lang.reflect.Field;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

/**
 * Called from {@code CraftingCpuLogicMixin}: a crafting CPU hands an ME machine a whole task in one push.
 *
 * <p>AE2 pushes a pattern one craft at a time, and every craft is a full pass: inputs picked from the CPU
 * (tag substitutes searched one by one), network power checked and drawn, the expected outputs noted.
 * At {@value AbstractMENodeBlockEntity#OPERATIONS_PER_TICK} crafts per tick for each step of a chain that
 * cost the server tens of milliseconds a tick. Here AE2 makes its usual first push, then the machine gets
 * the rest of the task (as far as its queue, the CPU's stock and the network's power go) in one block, and
 * AE2 books it with its own code for that push, scaled. A tick costs the same for 1 or 10 000 crafts.
 * Pushes into an ME machine do not use up the CPU's co-processor operations either.
 */
public final class MECraftingCpuHooks {
   /** Id of the crafting job whose {@code pushPattern} call is running (server thread only), so a machine knows who pushed. */
   private static UUID pushingJob;

   private MECraftingCpuHooks() {
   }

   /** @param cpuLogic the CPU logic (AE2's or AdvancedAE's) that is about to push */
   public static void beginPush(Object cpuLogic) {
      pushingJob = jobId(cpuLogic);
   }

   public static void endPush() {
      pushingJob = null;
   }

   @Nullable
   static UUID pushingJob() {
      return pushingJob;
   }

   /** A CPU's job ended without success (cancel, stop): ME machines return what it had pushed and not run. */
   public static void jobAborted(Object cpuLogic) {
      UUID id = jobId(cpuLogic);
      if (id != null) {
         MEMachineBlockEntity.refundAll(id);
      }
   }

   /** The crafting id of the job the CPU runs, or null. The fields are package-private in AE2 and in AdvancedAE's copy. */
   @Nullable
   private static UUID jobId(Object cpuLogic) {
      try {
         Fields fields = FIELDS.get(cpuLogic.getClass());
         Object job = fields.job.get(cpuLogic);
         return job == null ? null : ((CraftingLink)fields.link(job).get(job)).getCraftingID();
      } catch (ReflectiveOperationException | RuntimeException e) {
         return null;
      }
   }

   private static final ClassValue<Fields> FIELDS = new ClassValue<>() {
      @Override
      protected Fields computeValue(Class<?> type) {
         try {
            Field job = type.getDeclaredField("job");
            job.setAccessible(true);
            return new Fields(job);
         } catch (ReflectiveOperationException | RuntimeException e) {
            throw new IllegalStateException(e);
         }
      }
   };

   private static final class Fields {
      final Field job;
      private Field link;

      Fields(Field job) {
         this.job = job;
      }

      Field link(Object jobInstance) throws ReflectiveOperationException {
         if (this.link == null) {
            Field field = jobInstance.getClass().getDeclaredField("link");
            field.setAccessible(true);
            this.link = field;
         }

         return this.link;
      }
   }

   public static boolean isMEMachine(ICraftingProvider provider) {
      return provider instanceof MEBulkMachine;
   }

   /**
    * After AE2 pushed one craft into the ME machine {@code provider}: moves up to {@code limit} more copies
    * of the same craft (same input keys) from the CPU inventory into the machine. {@code expectedOutputs} and
    * {@code expectedContainerItems} hold what AE2 awaits from that one craft (results; returned catalysts and
    * container leftovers); they are scaled to cover the extra crafts, and AE2 books them right after the push.
    * The caller scales the pattern power AE2 is about to draw and takes the returned count off the task.
    *
    * @return how many more crafts the machine took, 0..limit
    */
   public static long pushMore(
      ICraftingProvider provider, KeyCounter[] inputs, long limit, ListCraftingInventory cpu,
      IEnergyService energy, double patternPower, KeyCounter expectedOutputs, KeyCounter expectedContainerItems
   ) {
      if (!(provider instanceof MEBulkMachine machine) || limit <= 0) {
         return 0;
      }

      long extra = Math.min(limit, machine.freeCapacity());
      KeyCounter perCraft = new KeyCounter();
      for (KeyCounter slot : inputs) {
         perCraft.addAll(slot);
      }

      for (var input : perCraft) {
         if (input.getLongValue() > 0) {
            extra = Math.min(extra, cpu.extract(input.getKey(), Long.MAX_VALUE, Actionable.SIMULATE) / input.getLongValue());
         }
      }

      if (patternPower > 0 && extra > 0) {
         // AE2 checked the power of one craft and draws it after the push: this one and the extra ones must fit
         double available = energy.extractAEPower(patternPower * (extra + 1), Actionable.SIMULATE, PowerMultiplier.CONFIG);
         extra = Math.min(extra, (long)Math.floor(available / patternPower + 1.0E-9) - 1);
      }

      if (extra <= 0) {
         return 0;
      }

      for (var input : perCraft) {
         cpu.extract(input.getKey(), input.getLongValue() * extra, Actionable.MODULATE);
      }

      scale(expectedOutputs, extra);
      scale(expectedContainerItems, extra);
      machine.acceptMore(extra);
      return extra;
   }

   /** Adds {@code extra} more times what one craft put in {@code counter}. */
   private static void scale(KeyCounter counter, long extra) {
      KeyCounter one = new KeyCounter();
      one.addAll(counter);
      for (var entry : one) {
         counter.add(entry.getKey(), entry.getLongValue() * extra);
      }
   }
}
