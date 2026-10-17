package com.industrialreforged.tech.blockentity.generator.kinetic;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.block.RotateBlock;
import com.industrialreforged.tech.api.item.IKineticRotor;
import com.industrialreforged.tech.api.tile.IRotorProvider;
import com.industrialreforged.tech.blockentity.base.AbstractKineticSourceBlockEntity;
import com.industrialreforged.tech.container.InvSlotConsumableClass;
import com.industrialreforged.tech.container.InvSlotConsumableKineticRotor;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.ref.IRBlockEntities;
import com.industrialreforged.tech.menu.generator.kinetic.WaterKineticGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WaterKineticGeneratorBlockEntity extends AbstractKineticSourceBlockEntity implements IRotorProvider {
   public InvSlotConsumableClass rotorSlot;
   public WaterKineticGeneratorBlockEntity.BiomeState type = WaterKineticGeneratorBlockEntity.BiomeState.UNKNOWN;
   private boolean rightFacing;
   private int distanceToNormalBiome;
   private int crossSection;
   private int obstructedCrossSection;
   private int waterFlow;
   private long lastcheck;
   private float angle = 0.0F;
   private float rotationSpeed;
   protected int updateTicker;

   public WaterKineticGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.WATER_KINETIC_GENERATOR.get(), blockPos, blockState);
      this.rotorSlot = new InvSlotConsumableKineticRotor(this, "rotor", 1, IKineticRotor.GearboxType.WATER);
      this.updateTicker = IR.random.nextInt(this.getTickRate());
   }

   /** Мощность считается из потока, поэтому поле power не трогаем. */
   @Override
   public int extractKineticEnergy(net.minecraft.core.Direction direction, int amount, boolean simulation) {
      int available = this.getKineticEnergy(direction);
      return available > 0 ? Math.min(amount, available) : 0;
   }

   /** Раз в секунду пересчитывает поток воды вокруг ротора и выдаёт кинетику. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (--this.updateTicker > 0) {
         return;
      }

      this.updateTicker = this.getTickRate();
      float speedBefore = this.rotationSpeed;
      boolean activeBefore = ActiveBlock.isActive(this);
      if (this.rotorSlot.isEmpty() || this.getRotorDiameter() <= 0) {
         this.stopSpinning();
         ActiveBlock.setActive(this, false);
         // Уже стоял — останавливать нечего, чанк не пачкаем.
         if (this.rotationSpeed != speedBefore || ActiveBlock.isActive(this) != activeBefore) {
            this.markForSave();
         }
         return;
      }

      BiomeState typeBefore = this.type;
      int crossBefore = this.crossSection;
      int obstructedBefore = this.obstructedCrossSection;
      int flowBefore = this.waterFlow;
      Holder<Biome> biome = this.level.getBiome(this.worldPosition);
      this.type = biome.is(BiomeTags.IS_OCEAN) ? BiomeState.OCEAN : (biome.is(BiomeTags.IS_RIVER) ? BiomeState.RIVER : BiomeState.UNKNOWN);
      int radius = Math.max(1, this.getRotorDiameter() / 2);
      Direction side = this.getFacing().getClockWise(Axis.Y);
      MutableBlockPos pos = new MutableBlockPos();
      int water = 0;
      int total = 0;

      for (int dy = -radius; dy <= radius; dy++) {
         for (int ds = -radius; ds <= radius; ds++) {
            pos.set(
               this.worldPosition.getX() + side.getStepX() * ds,
               this.worldPosition.getY() + dy,
               this.worldPosition.getZ() + side.getStepZ() * ds
            );
            total++;
            // не форсируем загрузку чанков: во время выгрузки это дедлок
            if (this.level.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4) == null) {
               continue;
            }

            if (this.level.getBlockState(pos).getBlock() == Blocks.WATER) {
               water++;
            }
         }
      }

      this.crossSection = total;
      this.obstructedCrossSection = total - water;
      float efficiency = this.getEfficiency();
      this.waterFlow = (int)(water * efficiency * 100.0F * (this.type == BiomeState.OCEAN ? 1.0F : 0.75F));
      this.rotationSpeed = this.waterFlow > 0 ? Math.max(0.05F, this.waterFlow / 100.0F) : 0.0F;
      ActiveBlock.setActive(this, this.waterFlow > 0);
      // Вода стоит, значения те же — чанк не пачкаем (пересчёт и так раз в 20 тиков).
      if (this.type != typeBefore || this.crossSection != crossBefore
         || this.obstructedCrossSection != obstructedBefore || this.waterFlow != flowBefore
         || this.rotationSpeed != speedBefore) {
         this.markForSave();
      }
   }

   protected int getTickRate() {
      return 20;
   }

   @Override
   public void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.rotorSlot.load(tag, registries);
   }

   @Override
   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      this.rotorSlot.save(tag, registries);
   }

   public void onLoad() {
      super.onLoad();
      this.updateSeaInfo();
   }

   protected void stopSpinning() {
      boolean bl = this.rotationSpeed != 0.0F;
      this.rotationSpeed = 0.0F;
      this.waterFlow = 0;
      if (bl) {
         this.markForUpdate();
      }
   }

   public void setBlockState(BlockState blockState) {
      super.setBlockState(blockState);
      this.updateSeaInfo();
   }

   @Override
   public int getRotorDiameter() {
      ItemStack itemStack = this.rotorSlot.getItem();
      if (itemStack.isEmpty() || !(itemStack.getItem() instanceof IKineticRotor)) {
         return 0;
      } else {
         return this.type == WaterKineticGeneratorBlockEntity.BiomeState.OCEAN
            ? ((IKineticRotor)itemStack.getItem()).getDiameter(itemStack)
            : (((IKineticRotor)itemStack.getItem()).getDiameter(itemStack) + 1) * 2 / 3;
      }
   }

   @Override
   public Direction getFacing() {
      return RotateBlock.getHorizontalFacing(this);
   }

   public int checkSpace(int n, boolean bl) {
      int n2 = this.getRotorDiameter() / 2;
      int n3 = 0;
      if (bl) {
 n = 1;
         n3 = n + 1;
      } else {
         n2 *= 2;
      }

      Direction direction = this.getFacing();
      Direction direction2 = direction.getClockWise(Axis.Y);
      int n4 = 0;
      int n5 = this.worldPosition.getX();
      int n6 = this.worldPosition.getY();
      int n7 = this.worldPosition.getZ();
      Level level = this.getLevel();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = -n2; i <= n2; i++) {
         int n8 = n6 + i;

         for (int j = -n2; j <= n2; j++) {
            boolean bl2 = false;

            for (int k = n3 - n; k <= n; k++) {
               int n9 = n5 + k * direction.getStepX() + j * direction2.getStepX();
               int n10 = n7 + k * direction.getStepZ() + j * direction2.getStepZ();
               mutableBlockPos.set(n9, n8, n10);
               if (level.getBlockState(mutableBlockPos).getBlock() != Blocks.WATER) {
                  bl2 = true;
                  if ((i != 0 || j != 0 || k != 0) && level.getBlockEntity(mutableBlockPos) instanceof WaterKineticGeneratorBlockEntity && !bl) {
                     return -1;
                  }
               }
            }

            if (bl2) {
               n4++;
            }
         }
      }

      return n4;
   }

   public void updateSeaInfo() {
      Level level = this.getLevel();
      Direction direction = this.getFacing();

      for (int i = 1; i < 200; i++) {
         Holder<Biome> holder = level.getBiome(this.worldPosition.relative(direction, i));
         if (!this.isValidBiome(holder)) {
            this.distanceToNormalBiome = i;
            this.rightFacing = true;
            return;
         }

         holder = level.getBiome(this.worldPosition.relative(direction, -i));
         if (!this.isValidBiome(holder)) {
            this.distanceToNormalBiome = i;
            this.rightFacing = false;
            return;
         }
      }

      this.distanceToNormalBiome = 200;
      this.rightFacing = true;
   }

   public boolean isValidBiome(Holder<Biome> holder) {
      return holder.is(BiomeTags.IS_OCEAN) || holder.is(BiomeTags.IS_RIVER);
   }

   @Override
   public int getPower() {
      return ActiveBlock.isActive(this) ? (int)Math.abs(this.waterFlow * 0.2F) : 0;
   }

   @Override
   protected boolean facingMatchesDirection(Direction direction) {
      return super.facingMatchesDirection(direction.getOpposite());
   }

   public float getEfficiency() {
      ItemStack itemStack = this.rotorSlot.getItem();
      return !itemStack.isEmpty() && itemStack.getItem() instanceof IKineticRotor ? ((IKineticRotor)itemStack.getItem()).getEfficiency(itemStack) : 0.0F;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new WaterKineticGeneratorMenu(containerId, playerInventory, this);
   }

   @Override
   public ResourceLocation getRotorRenderTexture() {
      ItemStack itemStack = this.rotorSlot.getItem();
      return !itemStack.isEmpty() && itemStack.getItem() instanceof IKineticRotor
         ? ((IKineticRotor)itemStack.getItem()).getRotorRenderTexture(itemStack)
         : IR.texture("items/rotor/wood_rotor_model");
   }

   @Override
   public float getAngle() {
      if (this.rotationSpeed != 0.0F) {
         this.angle = this.angle + (float)(System.currentTimeMillis() - this.lastcheck) * this.rotationSpeed * 0.1F;
         this.angle %= 360.0F;
      }

      this.lastcheck = System.currentTimeMillis();
      return this.angle;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag compound = super.getUpdateTag(registries);
      compound.putFloat("rotationSpeed", this.rotationSpeed);
      if (!this.rotorSlot.isEmpty()) {
         compound.put("rotorSlot", this.rotorSlot.getItem().save(registries));
      }

      return compound;
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.rotationSpeed = tag.getFloat("rotationSpeed");
      this.rotorSlot.setItem(ItemStack.parseOptional(lookupProvider, tag.getCompound("rotorSlot")));
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
   }

   public int getBiomeStateData() {
      return this.type.ordinal();
   }

   public void setBiomeStateData(int value) {
      this.type = WaterKineticGeneratorBlockEntity.BiomeState.values()[value];
   }

   public enum BiomeState {
      UNKNOWN,
      OCEAN,
      RIVER,
      INVALID;
   }
}
