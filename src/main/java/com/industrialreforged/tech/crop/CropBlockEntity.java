package com.industrialreforged.tech.crop;

import com.industrialreforged.blockentity.BaseBlockEntity;
import com.industrialreforged.blockentity.api.IServerTick;
import com.industrialreforged.tech.api.crops.BaseSeed;
import com.industrialreforged.tech.api.crops.CropCard;
import com.industrialreforged.tech.api.crops.CropSoilType;
import com.industrialreforged.tech.api.crops.ICropTile;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlocks;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.crop.data.CropData;
import com.industrialreforged.tech.crop.data.GrowthData;
import com.industrialreforged.tech.crop.data.StorageData;
import com.industrialreforged.tech.crop.data.TerrainData;
import com.industrialreforged.tech.item.ItemCropSeed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropBlockEntity extends BaseBlockEntity implements ICropTile, IServerTick {
   private final CropData crop;
   private final StorageData storage;
   private final TerrainData terrain;
   private final GrowthData growth;
   public boolean dirty = true;

   public CropBlockEntity(Supplier<BlockEntityType<CropBlockEntity>> supplier, BlockPos blockPos, BlockState blockState) {
      super(supplier.get(), blockPos, blockState);
      this.crop = new CropData(this.getBlockState().getBlock());
      this.storage = new StorageData();
      this.terrain = new TerrainData(this);
      this.growth = new GrowthData();
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag compound = super.getUpdateTag(provider);
      this.crop.save(compound);
      this.storage.save(compound);
      this.terrain.save(compound);
      this.growth.save(compound);
      return compound;
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.crop.load(tag);
      this.storage.load(tag);
      this.terrain.load(tag);
      this.growth.load(tag);
   }

   public void loadAdditional(@NotNull CompoundTag compound, @NotNull Provider registries) {
      super.loadAdditional(compound, registries);
      this.crop.load(compound);
      this.storage.load(compound);
      this.terrain.load(compound);
      this.growth.load(compound);
   }

   public void saveAdditional(@NotNull CompoundTag compound, @NotNull Provider registries) {
      super.saveAdditional(compound, registries);
      this.crop.save(compound);
      this.storage.save(compound);
      this.terrain.save(compound);
      this.growth.save(compound);
   }

   /**
    * Тик посева.  В IR код тика называется performTick() и вызывался центральным
    * планировщиком, а в порту тикает BaseEntityBlock -> BaseBlockEntity.Ticker,
    * который дергает именно serverTick().  Метод не был переопределён, поэтому
    * работал пустой default из IServerTick и вся система посевов была мёртвой:
    * ничего не росло, не скрещивалось, терраун не обновлялся.
    */
   @Override
   public void serverTick() {
      this.performTick();
   }

   public void performTick() {
      if (!this.level.isClientSide) {
         long l = this.level.getGameTime();
         if (l % 1024L == 0L) {
            this.terrain.updateHumidity();
         }

         if ((l + 256L) % 1024L == 0L) {
            this.terrain.updateNutrients();
         }

         if ((l + 512L) % 1024L == 0L) {
            this.terrain.updateAirQuality();
         }

         CropCard card = this.crop.getCard();
         if (card == null && (!this.isCrossingBase() || !this.attemptCrossing()) && (!this.isCrossingBase() || !this.attemptSpreading())) {
            int weedEx = this.storage.getWeedEx();
            if (IR.random.nextInt(100) != 0 || weedEx > 0) {
               if (weedEx > 0 && IR.random.nextInt(10) == 0) {
                  this.storage.setWeedEX((short)(weedEx - 1));
               }

               return;
            }

            this.transformCropBlock(IRCrops.weed, 0).storage.copy(this.storage);
         }

         if (card != null) {
            card.tick(this);
            if (card.canGrow(this)) {
               this.performGrowthTick();
               card = this.crop.getCard();
               if (card == null) {
                  return;
               }

               if (this.growth.getPoints() >= card.getGrowthDuration(this)) {
                  this.growth.setPoints((short)0);
                  this.setCurrentAge(this.getCurrentAge() + 1);
                  this.dirty = true;
               }
            }

            int storage = this.storage.getNutrients();
            if (storage > 0) {
               this.storage.setNutrients((short)(storage - 1));
            }

            int var6 = this.storage.getWater();
            if (var6 > 0) {
               this.storage.setWater((short)(var6 - 1));
            }

            if (card.isWeed(this) && IR.random.nextInt(50) - this.crop.getGrowth() <= 2) {
               this.performWeedWork();
            }
         }
      }
   }

   public void performGrowthTick() {
      CropCard card = this.crop.getCard();
      if (card != null) {
         int baseGrow = 3 + IR.random.nextInt(7) + this.crop.getGrowth();
         int strong = Math.max((card.getProperties().tier() - 1) * 4 + this.crop.getGrowth() + this.crop.getGain() + this.crop.getResistance(), 0);
         int shield = card.getWeightInfluences(this, this.terrain.getHumidity(), this.terrain.getNutrients(), this.terrain.getAirQuality()) * 5;
         int grow;
         if (shield >= strong) {
            grow = baseGrow * (100 + shield - strong) / 100;
         } else {
            int attack = (strong - shield) * 4;
            if (attack > 100 && IR.random.nextInt(32) > this.crop.getResistance()) {
               this.reset();
               grow = 0;
            } else {
               grow = Math.max(baseGrow * (100 - attack) / 100, 0);
            }
         }

         this.growth.setPoints((short)(this.growth.getPoints() + grow));
      }
   }

   public void performWeedWork() {
      Level level = this.getLevel();
      if (level != null) {
         BlockPos blockPos = this.worldPosition.relative(Util.HORIZONTAL_DIRS[IR.random.nextInt(4)]);
         if (level.getBlockEntity(blockPos) instanceof CropBlockEntity entity) {
            CropCard cropCard = entity.crop.getCard();
            if (cropCard == null || !cropCard.isWeed(entity) && IR.random.nextInt(32) >= entity.crop.getResistance() && !entity.hasWeedEX()) {
               int i = Math.max(this.crop.getGrowth(), entity.crop.getGrowth());
               if (i < 31 && IR.random.nextBoolean()) {
                  i++;
               }

               StorageData dataOld = entity.getStorage();
               CropBlockEntity var10 = entity.transformCropBlock(IRCrops.weed, 0);
               var10.crop.setGrowth((byte)i);
               var10.storage.copy(dataOld);
            }
         } else if (level.isEmptyBlock(blockPos)) {
            BlockPos blockPos1 = blockPos.below();
            Block block = level.getBlockState(blockPos1).getBlock();
            if (block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND) {
               boolean tallGrass = IR.random.nextBoolean();
               BlockPos grassPos = blockPos1.above();
               if (level.getBlockState(grassPos).isAir()) {
                  if (tallGrass) {
                     BlockPos tallGrassTopPos = grassPos.above(1);
                     if (level.getBlockState(tallGrassTopPos).isAir()) {
                        level.setBlock(grassPos, (BlockState)Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 7);
                        level.setBlock(
                           tallGrassTopPos, (BlockState)Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 7
                        );
                     }
                  } else {
                     level.setBlock(grassPos, Blocks.GRASS_BLOCK.defaultBlockState(), 7);
                  }
               }
            }
         }
      }
   }

   public boolean hasWeedEX() {
      int storage = this.storage.getWeedEx();
      if (storage > 0) {
         this.storage.setWeedEX((short)(storage - 5));
         return true;
      } else {
         return false;
      }
   }

   public InteractionResult onActivated(Player paramPlayer, InteractionHand paramInteractionHand, Direction paramDirection, Vec3 paramVec3) {
      if (this.level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         return this.rightClick(paramPlayer, paramInteractionHand) ? InteractionResult.CONSUME : InteractionResult.PASS;
      }
   }

   public InteractionResult onClicked(Player paramPlayer, Direction direction) {
      if (paramPlayer.getAbilities().instabuild) {
         return InteractionResult.PASS;
      } else {
         CropCard card = this.crop.getCard();
         if (card != null) {
            return card.onLeftClick(this, paramPlayer) ? InteractionResult.SUCCESS : InteractionResult.PASS;
         } else if (this.isCrossingBase() && !this.level.isClientSide) {
            this.setCrossingBase(false);
            StackUtil.dropAsEntity(this.getLevel(), this.worldPosition, new ItemStack((ItemLike)IRItems.CROP_STICK.get()));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public void onBlockBreak() {
      if (!this.level.isClientSide) {
         if (this.isCrossingBase()) {
            StackUtil.dropAsEntity(this.level, this.worldPosition, new ItemStack((ItemLike)IRItems.CROP_STICK.get(), 1));
         }

         this.pick();
      }
   }

   @NotNull
   public ItemStack adjustDrop(@NotNull ItemStack stack, @NotNull ItemStack wrench) {
      return stack;
   }

   protected List<AABB> getAabbs(boolean paramBoolean) {
      ArrayList<AABB> arrayList = new ArrayList<>();
      if (paramBoolean) {
         arrayList.add(new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
      } else {
         arrayList.add(new AABB(0.2F, 0.0, 0.2F, 0.8F, 0.85F, 0.8F));
      }

      return arrayList;
   }

   public boolean rightClick(Player player, InteractionHand hand) {
      ItemStack itemStack = StackUtil.get(player, hand);
      CropCard card = this.crop.getCard();
      if (!itemStack.isEmpty()) {
         if (card == null && !this.isCrossingBase()) {
            if (itemStack.getItem() == IRItems.CROP_STICK.get()) {
               StackUtil.consumeOrError(player, hand, 1);
               this.setCrossingBase(true);
               return true;
            }

            if (IRCrops.INSTANCE.getBaseSeed(itemStack) != null) {
               BaseSeed baseSeed = IRCrops.INSTANCE.getBaseSeed(itemStack);
               StackUtil.consumeOrError(player, hand, 1);
               CropBlockEntity entity = this.transformCropBlock(baseSeed.crop, baseSeed.size);
               entity.crop.setGain((byte)baseSeed.statGain);
               entity.crop.setGrowth((byte)baseSeed.statGrowth);
               entity.crop.setResistance((byte)baseSeed.statResistance);
               entity.storage.copy(this.storage);
               return true;
            }
         }

         if (StackUtil.checkItemEquality(itemStack, (ItemLike)IRItems.FERTILIZER.get())) {
            if (this.applyFertilizer(true)) {
               this.dirty = true;
            }

            StackUtil.consumeOrError(player, hand, 1);
            return true;
         }

         if (itemStack.getCount() == 1) {
            IFluidHandlerItem handler = (IFluidHandlerItem)FluidUtil.getFluidHandler(itemStack).orElse(null);
            if (handler != null) {
               FluidStack fluidStack = new FluidStack(Fluids.WATER, Integer.MAX_VALUE);
               int i = handler.drain(fluidStack, FluidAction.SIMULATE).getAmount();
               if (i > 0) {
                  i = this.applyHydration(i, true);
                  if (i > 0) {
                     fluidStack.setAmount(i);
                     i = handler.drain(fluidStack, FluidAction.EXECUTE).getAmount();
                     this.applyHydration(i, false);
                     StackUtil.set(player, hand, handler.getContainer());
                     this.dirty = true;
                  }

                  return true;
               }
            }
         }
      }

      return card != null && card.onRightClick(this, player);
   }

   @Override
   public CropData getCrop() {
      return this.crop;
   }

   @Override
   public StorageData getStorage() {
      return this.storage;
   }

   @Override
   public TerrainData getTerrain() {
      return this.terrain;
   }

   @Override
   public GrowthData getGrowth() {
      return this.growth;
   }

   @Override
   public void setCrop(CropCard card) {
      CropBlockEntity crop = this.transformCropBlock(card.getCropBlock(), this.growth.getAge());
      crop.terrain.updateHumidity();
      crop.terrain.updateNutrients();
      crop.terrain.updateAirQuality();
   }

   @Override
   public int getCurrentAge() {
      return this.crop.getCard() == null ? 0 : (Integer)this.getBlockState().getValue(this.getAgeProperty());
   }

   @Override
   public void setCurrentAge(int value) {
      this.growth.setAge((byte)value);
      if (this.level != null) {
         this.level.setBlockAndUpdate(this.worldPosition, (BlockState)this.getBlockState().setValue(this.getAgeProperty(), value));
      }
   }

   private IntegerProperty getAgeProperty() {
      return ((IRCropBlock)this.getBlockState().getBlock()).getType().getProperty();
   }

   @Override
   public boolean isCrossingBase() {
      if (this.crop.getCard() != null) {
         return false;
      } else {
         return this.level == null ? false : (Boolean)this.level.getBlockState(this.worldPosition).getValue(IRCropBlock.CROSSING_BASE);
      }
   }

   @Override
   public void setCrossingBase(boolean paramBoolean) {
      if (this.crop.getCard() == null) {
         if (this.level != null) {
            this.level
               .setBlockAndUpdate(
                  this.worldPosition,
                  (BlockState)((IRCropBlock)IRBlocks.CROP_STICK.get()).defaultBlockState().setValue(IRCropBlock.CROSSING_BASE, paramBoolean)
               );
            this.dirty = true;
         }
      }
   }

   @Override
   public BlockPos getPosition() {
      return this.worldPosition;
   }

   @Override
   public Level getWorldObj() {
      return this.getLevel();
   }

   @Override
   public int getLightLevel() {
      return this.getLevel().getMaxLocalRawBrightness(this.worldPosition);
   }

   public int getLightValue() {
      CropCard card = this.crop.getCard();
      return card == null ? 0 : card.getEmittedLight(this);
   }

   @Override
   public boolean pick() {
      CropCard card = this.crop.getCard();
      if (card == null) {
         return false;
      }

      Level level = this.getLevel();
      if (level == null) {
         return false;
      }

      boolean canBeHarvested = card.canBeHarvested(this);
      float seedChance = card.dropSeedChance(this);
      seedChance = (float)(seedChance * Math.pow(1.1, this.crop.getResistance()));
      int seedCount = 0;
      if (canBeHarvested) {
         if (level.random.nextFloat() <= (seedChance + 1.0F) * 0.8F) {
            seedCount++;
         }

         float extraSeedChance = card.dropSeedChance(this) + this.crop.getGrowth() / 100.0F;

         for (byte gain = 23; gain < this.crop.getGain(); gain++) {
            extraSeedChance *= 0.95F;
         }

         if (level.random.nextFloat() <= extraSeedChance) {
            seedCount++;
         }
      } else if (level.random.nextFloat() <= seedChance * 1.5F) {
         seedCount++;
      }

      ItemStack[] seeds = new ItemStack[seedCount];

      for (int i = 0; i < seeds.length; i++) {
         seeds[i] = card.getSeeds(this);
      }

      this.reset();
      if (!level.isClientSide) {
         for (ItemStack seed : seeds) {
            StackUtil.dropAsEntity(level, this.worldPosition, seed);
         }
      }

      return true;
   }

   @Override
   public boolean performManualHarvest() {
      List<ItemStack> list = this.performHarvest();
      if (list != null && !list.isEmpty()) {
         list.forEach(paramItemStack -> StackUtil.dropAsEntity(this.getLevel(), this.worldPosition, paramItemStack));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public List<ItemStack> performHarvest() {
      CropCard card = this.crop.getCard();
      if (card != null && card.canBeHarvested(this)) {
         double d = card.dropGainChance();
 d *= Math.pow(1.03, this.crop.getGain());
         int i = (int)Math.max(0L, Math.round(IR.random.nextDouble() * d * 0.6827 + d));
         List<ItemStack> list = IntStream.range(0, i).mapToObj(paramInt -> Arrays.stream(card.getGains(this)).peek(itemStack -> {
            if (!itemStack.isEmpty() && IR.random.nextInt(100) <= this.crop.getGain()) {
               itemStack.grow(1);
            }
         })).flatMap(Function.identity()).toList();
         this.setCurrentAge(card.getAgeAfterHarvest(this));
         this.dirty = true;
         return list;
      } else {
         return null;
      }
   }

   @Override
   public void reset() {
      if (this.level != null) {
         this.level
            .setBlockAndUpdate(
               this.worldPosition, (BlockState)((IRCropBlock)IRBlocks.CROP_STICK.get()).defaultBlockState().setValue(IRCropBlock.CROSSING_BASE, false)
            );
      }
   }

   @Override
   public void updateState() {
      BlockState blockState = this.getBlockState();
      Objects.requireNonNull(this.level).sendBlockUpdated(this.worldPosition, blockState, blockState, 2);
   }

   @Override
   public boolean isBlockBelow(Block block) {
      return this.isBlockBelow(state -> state.is(block));
   }

   @Override
   public boolean isBlockBelow(TagKey<Block> tagKey) {
      return this.isBlockBelow(state -> state.is(tagKey));
   }

   public boolean isBlockBelow(Predicate<BlockState> predicate) {
      CropCard card = this.crop.getCard();
      if (card == null) {
         return false;
      }

      for (byte b = 1; b < card.getRootsLength(this); b++) {
         BlockPos blockPos = this.worldPosition.below(b);
         BlockState blockState = this.level.getBlockState(blockPos);
         if (blockState.isAir()) {
            return false;
         }

         if (predicate.test(blockState)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public ItemStack generateSeeds(CropCard card, int growth, int gain, int resistance, int scan) {
      return ItemCropSeed.generateItemStackFromValues(card, growth, gain, resistance, scan);
   }

   protected int getLightOpacity() {
      return 0;
   }

   public CropBlockEntity transformCropBlock(CropCard card, int age) {
      return this.transformCropBlock(card.getCropBlock(), age);
   }

   public CropBlockEntity transformCropBlock(Block b, int age) {
      if (this.level != null && b instanceof IRCropBlock block) {
         BlockState blockState = block.defaultBlockState();
         if (!blockState.is((Block)IRBlocks.CROP_STICK.get())) {
            blockState = (BlockState)blockState.setValue(block.getType().getProperty(), age);
         }

         this.level.setBlockAndUpdate(this.worldPosition, blockState);
         return (CropBlockEntity)this.level.getBlockEntity(this.worldPosition);
      } else {
         return null;
      }
   }

   public boolean wrenchCanRemove(Player paramPlayer) {
      return false;
   }

   protected ItemStack getPickBlock(Player paramPlayer, BlockHitResult paramBlockHitResult) {
      CropCard card = this.crop.getCard();
      return card == null
         ? new ItemStack((ItemLike)IRBlocks.CROP_STICK.get())
         : this.generateSeeds(card, this.crop.getGrowth(), this.crop.getGain(), this.crop.getResistance(), this.crop.getScan());
   }

   private boolean attemptCrossing() {
      if (IR.random.nextInt(3) != 0) {
         return false;
      }

      ArrayList<CropBlockEntity> arrayList = new ArrayList<>(4);
      this.checkCrossingAvailability(this.worldPosition.north(), arrayList);
      this.checkCrossingAvailability(this.worldPosition.south(), arrayList);
      this.checkCrossingAvailability(this.worldPosition.east(), arrayList);
      this.checkCrossingAvailability(this.worldPosition.west(), arrayList);
      if (arrayList.size() < 2) {
         return false;
      }

      CropCard[] arrayOfCropCard = IRCrops.INSTANCE.getCrops().toArray(CropCard[]::new);
      if (arrayOfCropCard.length == 0) {
         return false;
      }

      int[] arrayOfInt = new int[arrayOfCropCard.length];
      int i = 0;

      for (int j = 0; j < arrayOfInt.length; j++) {
         CropCard cropCard = arrayOfCropCard[j];
         if (cropCard.canGrow(this)) {
            for (CropBlockEntity crop : arrayList) {
               i += this.calculateRatioFor(cropCard, crop.crop.getCard());
            }
         }

         arrayOfInt[j] = i;
      }

      int var10 = IR.random.nextInt(i);
      int k = 0;
      int m = arrayOfInt.length - 1;

      while (k < m) {
         int i1 = (k + m) / 2;
         int i2 = arrayOfInt[i1];
         if (var10 < i2) {
 m = i1;
         } else {
 k = i1 + 1;
         }
      }

      if ((k != m || arrayOfInt[k] <= var10 || k != 0) && arrayOfInt[k - 1] > var10) {
         return false;
      }

      this.crop.setGain(this.calculate(arrayList, CropData::getGain));
      this.crop.setGrowth(this.calculate(arrayList, CropData::getGrowth));
      this.crop.setResistance(this.calculate(arrayList, CropData::getResistance));
      CropBlockEntity entityCrop = this.transformCropBlock(arrayOfCropCard[k], 0);
      entityCrop.setCurrentAge(0);
      entityCrop.crop.copy(this.crop);
      entityCrop.storage.copy(this.storage);
      this.dirty = true;
      return true;
   }

   private byte calculate(ArrayList<CropBlockEntity> list, ToIntFunction<CropData> function) {
      int size = list.size();
      int total = list.stream().mapToInt(value -> function.applyAsInt(value.crop)).sum();
      return (byte)Mth.clamp(total / size + IR.random.nextInt(1 + 2 * size) - size, 0, 31);
   }

   private boolean attemptSpreading() {
      CropBlockEntity entity = null;

      for (Direction direction : Util.HORIZONTAL_DIRS) {
         if (this.level.getBlockEntity(this.worldPosition.relative(direction)) instanceof CropBlockEntity e) {
            if (entity != null) {
               return false;
            }

            entity = e;
         }
      }

      if (entity != null && this.checkEntityCrop(entity)) {
         CropBlockEntity entityCrop = this.transformCropBlock(entity.crop.getCard(), 0);
         entityCrop.crop.copy(entity.crop);
         entityCrop.storage.copy(entity.storage);
         this.dirty = true;
         return true;
      } else {
         return false;
      }
   }

   private int calculateRatioFor(CropCard paramCropCard1, CropCard paramCropCard2) {
      if (paramCropCard1 == paramCropCard2) {
         return 500;
      }

      int n3 = 0;
      int[] arrayOfInt1 = paramCropCard2.getProperties().getAllProperties();
      int[] arrayOfInt2 = paramCropCard1.getProperties().getAllProperties();
      if (arrayOfInt1.length != arrayOfInt2.length) {
         return 0;
      }

      for (int i = 0; i < 5; i++) {
         int k = Math.abs(arrayOfInt1[i] - arrayOfInt2[i]);
         n3 += -k + 2;
      }

      for (String str : paramCropCard1.getAttributes()) {
         for (String str1 : paramCropCard2.getAttributes()) {
            if (str.equalsIgnoreCase(str1)) {
               n3 += 5;
            }
         }
      }

      int j = paramCropCard1.getProperties().tier() - paramCropCard2.getProperties().tier();
      if (j > 1) {
         n3 -= 2 * j;
      }

      if (j < -3) {
         n3 -= -j;
      }

      return Math.max(n3, 0);
   }

   private void checkCrossingAvailability(BlockPos paramBlockPos, List<CropBlockEntity> paramList) {
      if (this.level.getBlockEntity(paramBlockPos) instanceof CropBlockEntity entity && this.checkEntityCrop(entity)) {
         paramList.add(entity);
      }
   }

   private boolean checkEntityCrop(CropBlockEntity entity) {
      CropCard card = entity.crop.getCard();
      if (card == null) {
         return false;
      }

      if (card.canGrow(this) && card.canCross(entity)) {
         int i = 4;
         if (entity.crop.getGrowth() >= 16) {
            i++;
         }

         if (entity.crop.getGrowth() >= 30) {
            i++;
         }

         if (entity.crop.getResistance() >= 28) {
            i += 27 - entity.crop.getResistance();
         }

         return i >= IR.random.nextInt(16);
      } else {
         return false;
      }
   }

   public void onNeighborChange(BlockState state, BlockPos neighbor) {
      Level level = this.getLevel();
      if (!CropSoilType.contains(level.getBlockState(this.worldPosition.below()).getBlock())) {
         this.pick();
         level.removeBlock(this.worldPosition, false);
      }
   }

   public int applyHydration(int value, boolean simulation) {
      int storage = this.storage.getWater();
      int space = 200 - storage;
      if (space <= 0) {
         return 0;
      }

      value = Math.min(value, space);
      if (!simulation) {
         this.storage.setWater((short)(storage + value));
      }

      return value;
   }

   public int applyWeedEx(int value, boolean extract, boolean simulation) {
      int storage = this.storage.getWeedEx();
      int space = 100 - storage;
      if (extract) {
         if (space <= value) {
            return 0;
         }
      } else {
         if (space <= 0) {
            return 0;
         }

         value = Math.min(value, space);
      }

      if (!simulation) {
         this.storage.setWeedEX((short)(storage + value));
      }

      return value;
   }

   public boolean applyFertilizer(boolean manual) {
      int storage = this.storage.getNutrients();
      if (storage >= 100) {
         return false;
      }

      this.storage.setNutrients((short)(storage + (manual ? 100 : 90)));
      return true;
   }
}
