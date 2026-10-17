package com.industrialreforged.tech.blockentity.machine;

import com.industrialreforged.blockentity.api.capabilities.IContainerWrapper;
import com.industrialreforged.blockentity.api.capabilities.IFluidWrapper;
import com.industrialreforged.fluid.FluidTank;
import com.industrialreforged.tech.api.upgrade.IUpgradableBlock;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.blockentity.base.AbstractElectricMachineBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumable;
import com.industrialreforged.tech.container.InvSlotConsumableItem;
import com.industrialreforged.tech.container.InvSlotConsumableLiquid;
import com.industrialreforged.tech.container.InvSlotConsumableLiquidByList;
import com.industrialreforged.tech.container.InvSlotOutput;
import com.industrialreforged.tech.container.InvSlotUpgrade;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.FluidSlotHelper;
import com.industrialreforged.tech.crop.cropcard.CropBaseSapling;
import com.industrialreforged.tech.crop.cropcard.CropStickyReed;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.item.ItemCropSeed;
import com.industrialreforged.tech.menu.machine.MolecularFarmingMenu;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class MolecularFarmingBlockEntity extends AbstractElectricMachineBlockEntity implements IUpgradableBlock, IFluidWrapper, IContainerWrapper {
   public final InvSlotConsumableLiquidByList fluidInputSlot;
   public final InvSlotOutput fluidOutputSlot;
   public static final int WATER_PER_OP = 10;
   private static final TagKey<Item> SAPLINGS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("saplings"));
   private static final TagKey<Item> FLOWERS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("flowers"));
   private static final TagKey<Item> TALL_FLOWERS = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("tall_flowers"));
   /** Саженцы: ванильные деревья дают брёвна, гевея — латекс. */
   private static final Map<Item, Item> SAPLING_PRODUCTS = Map.ofEntries(
      Map.entry(Items.OAK_SAPLING, Items.OAK_LOG),
      Map.entry(Items.SPRUCE_SAPLING, Items.SPRUCE_LOG),
      Map.entry(Items.BIRCH_SAPLING, Items.BIRCH_LOG),
      Map.entry(Items.JUNGLE_SAPLING, Items.JUNGLE_LOG),
      Map.entry(Items.ACACIA_SAPLING, Items.ACACIA_LOG),
      Map.entry(Items.DARK_OAK_SAPLING, Items.DARK_OAK_LOG),
      Map.entry(Items.CHERRY_SAPLING, Items.CHERRY_LOG),
      Map.entry(Items.MANGROVE_PROPAGULE, Items.MANGROVE_LOG),
      Map.entry(IRItems.RUBBER_SAPLING.get(), IRItems.RESIN.get())
   );

   /** Семена -> то, что из них вырастает. */
   private static final Map<Item, Item> SEED_PRODUCTS = Map.of(
      Items.WHEAT_SEEDS, Items.WHEAT,
      Items.BEETROOT_SEEDS, Items.BEETROOT,
      Items.MELON_SEEDS, Items.MELON,
      Items.PUMPKIN_SEEDS, Items.PUMPKIN
   );
   /** Прочая растительность, которую ферма просто размножает. */
   private static final Set<Item> VEGETATION = Set.of(
      Items.CACTUS, Items.SUGAR_CANE, Items.BAMBOO, Items.KELP, Items.VINE, Items.LILY_PAD,
      Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.RED_MUSHROOM, Items.BROWN_MUSHROOM,
      Items.CARROT, Items.POTATO, Items.COCOA_BEANS, Items.NETHER_WART
   );
   public final InvSlotConsumable inputSlot;
   public final InvSlotOutput outputSlot;
   public final InvSlotUpgrade upgradeSlot;
   public final FluidTank tank = new FluidTank(8000, true, false, stack -> stack.is(Fluids.WATER));
   public int progress;
   public NonNullList<ItemStack> stuckItem;
   private IItemHandlerModifiable wrapper;
   private int consumption;
   private int timeCreation;
   private boolean starved;

   public MolecularFarmingBlockEntity(BlockPos pos, BlockState state) {
      super(4096000, 32768, IRBlockEntities.MOLECULAR_FARMING.get(), pos, state);
      this.enableRedstoneSignal();
      this.fluidInputSlot = new InvSlotConsumableLiquidByList(this, "fluidInputSlot", 1, InvSlotConsumableLiquid.OpType.Drain, Fluids.WATER);
      this.fluidOutputSlot = new InvSlotOutput(this, "fluidOutputSlot", 1);
      this.inputSlot = new InvSlotConsumable(this, "seeds", 1) {
         @Override
         public boolean canPlaceItem(int index, ItemStack stack) {
            return stack.is(IRItems.CROP_SEED_BAG.get())
               || stack.getItem() instanceof ItemCropSeed
               || isVegetation(stack);
         }
      };
      this.outputSlot = new InvSlotOutput(this, 1);
      this.upgradeSlot = new InvSlotUpgrade(this, 4);
   }


   @Override
   public SoundEvent getStartSoundFile() {
      return IRSounds.SOUND_MACHINE_massfabloop.get();
   }

   @Override
   protected boolean isSoundproofed() {
      return this.upgradeSlot.soundproofing;
   }

   @Override
   protected boolean hasWork() {
      return this.progress > 0;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.fluidInputSlot.load(tag, registries);
      this.fluidOutputSlot.load(tag, registries);
      this.inputSlot.load(tag, registries);
      this.outputSlot.load(tag, registries);
      this.upgradeSlot.load(tag, registries);
      this.tank.load(registries, tag, "tank");
      this.progress = tag.getInt("progress");
      if (tag.contains("stuckItem")) {
         this.stuckItem = NonNullList.withSize(tag.getInt("stuckSize"), ItemStack.EMPTY);
         ContainerHelper.loadAllItems(tag.getCompound("stuckItem"), this.stuckItem, registries);
      }
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.fluidInputSlot.save(tag, registries);
      this.fluidOutputSlot.save(tag, registries);
      this.inputSlot.save(tag, registries);
      this.outputSlot.save(tag, registries);
      this.upgradeSlot.save(tag, registries);
      this.tank.save(registries, tag, "tank");
      tag.putInt("progress", this.progress);
      if (this.stuckItem != null) {
         tag.putInt("stuckSize", this.stuckItem.size());
         tag.put("stuckItem", ContainerHelper.saveAllItems(new CompoundTag(), this.stuckItem, registries));
      }
   }

   public void stop() {
      if (this.inputSlot.isEmpty()) {
         this.progress = 0;
      }

      this.setActive(false);
   }

   public void onLoad() {
      super.onLoad();
      this.upgradeSlot.setChanged();
   }

   @Override
   public Set<UpgradableProperty> getUpgradableProperties() {
      return EnumSet.of(
         UpgradableProperty.TRANSFORMER,
         UpgradableProperty.PROCESSING,
         UpgradableProperty.ENERGY_STORAGE,
         UpgradableProperty.ITEM_PRODUCING,
         UpgradableProperty.STRENGTHENING,
         UpgradableProperty.SOUNDPROOFING,
         UpgradableProperty.FLUID_CONSUMING
      );
   }

   /** Можно ли считать предмет растительностью для размножения. */
   public static boolean isVegetation(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }

      Item item = stack.getItem();
      return SAPLING_PRODUCTS.containsKey(item)
         || SEED_PRODUCTS.containsKey(item)
         || VEGETATION.contains(item)
         || stack.is(SAPLINGS)
         || stack.is(FLOWERS)
         || stack.is(TALL_FLOWERS);
   }

   /**
    * Что ферма «выращивает» из катализатора: сам катализатор остаётся в слоте,
    * а за операцию появляется один предмет справа.
    */
   public static ItemStack productFor(ItemStack input) {
      if (input.isEmpty()) {
         return ItemStack.EMPTY;
      }

      CropData data = ItemCropSeed.getCropData(input);
      if (!data.isEmpty() && data.getScan() >= 4 && !(data.getCard() instanceof CropBaseSapling)) {
         if (data.getCard() instanceof CropStickyReed) {
            return new ItemStack(IRItems.RESIN.get());
         }

         ItemStack[] gains = data.getCard().getGains(null);
         return gains.length > 0 ? gains[0].copyWithCount(1) : ItemStack.EMPTY;
      }

      Item log = SAPLING_PRODUCTS.get(input.getItem());
      if (log != null) {
         return new ItemStack(log);
      }

      Item grown = SEED_PRODUCTS.get(input.getItem());
      if (grown != null) {
         return new ItemStack(grown);
      }

      return isVegetation(input) ? input.copyWithCount(1) : ItemStack.EMPTY;
   }

   private static int baseTicksFor(ItemStack input) {
      CropData data = ItemCropSeed.getCropData(input);
      return !data.isEmpty() && data.getScan() >= 4 && !(data.getCard() instanceof CropBaseSapling)
         ? Math.max(1, 100 / Mth.clamp(data.getGrowth() + data.getGain() + 1, 1, 63))
         : 100;
   }

   @Override
   public void serverTick() {
      super.serverTick();
      ItemStack input = this.inputSlot.getItem(0);
      int multiplier = Math.max(1, this.upgradeSlot.strengthening * 4);
      ItemStack output = productFor(input);
      if (!output.isEmpty()) {
         output.setCount(Math.min(output.getMaxStackSize(), output.getCount() * multiplier));
      }

      if (output.isEmpty() || !this.outputSlot.canAdd(output)) {
         this.progress = 0;
         this.setActive(false);
         return;
      }

      int time = Math.max(1, this.upgradeSlot.getOperationLength(baseTicksFor(input)));
      int energy = Math.max(1, (int)Math.round(4096.0 * this.upgradeSlot.energyDemandMultiplier * multiplier));
      if (this.energy.getStored() < energy || this.tank.getFluidAmount() < WATER_PER_OP) {
         this.starved = this.progress > 0 && this.energy.getStored() < energy && this.tank.getFluidAmount() >= WATER_PER_OP;
         this.setActive(false);
         return;
      }

      this.starved = false;

      this.energy.extractForceEnergy(energy);
      this.progress++;
      this.setActive(true);
      if (this.progress >= time) {
         this.progress -= time;
         this.tank.drainForce(WATER_PER_OP * multiplier, IFluidHandler.FluidAction.EXECUTE);
         this.outputSlot.add(output);
         this.markForSave();
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new MolecularFarmingMenu(containerId, playerInventory, this);
   }

   @Override
   protected void tickFluidSlots() {
      FluidSlotHelper.tick(this.fluidInputSlot, this.fluidOutputSlot, this.tank);
   }

   public IFluidHandler getFluidWrapper(@Nullable Direction direction) {
      return this.tank;
   }

   @Nullable
   public IItemHandlerModifiable getContainerWrapper(@Nullable Direction direction) {
      if (this.wrapper == null) {
         this.wrapper = this.outputSlot.createWrapper();
      }

      return this.wrapper;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setConsumption(int value) {
      this.consumption = value;
   }

   @Override
   public int getConsumption() {
      // Тик списывает 4096 × demand × multiplier (см. serverTick): панель
      // показывает то же, иначе укрепление даёт молчаливый голод.
      int multiplier = Math.max(1, this.upgradeSlot.strengthening * 4);
      return this.level != null && this.level.isClientSide
         ? this.consumption
         : Math.max(1, (int)Math.round(4096.0 * this.upgradeSlot.energyDemandMultiplier * multiplier));
   }

   @Override
   public boolean isStarved() {
      return this.starved;
   }

   @Override
   public void setStarved(boolean value) {
      this.starved = value;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setTimeCreation(int value) {
      this.timeCreation = value;
   }

   @Override
   public int getTimeCreation() {
      if (this.level != null && this.level.isClientSide) {
         return this.timeCreation;
      }

      return Math.max(1, this.upgradeSlot.getOperationLength(baseTicksFor(this.inputSlot.getItem(0))));
   }

   private CropData getCrop() {
      CropData data = ItemCropSeed.getCropData(this.inputSlot.getItem());
      return !data.isEmpty() && !(data.getCard() instanceof CropBaseSapling) && data.getScan() >= 4 ? data : CropData.EMPTY;
   }
}
