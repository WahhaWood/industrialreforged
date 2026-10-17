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
import com.industrialreforged.tech.menu.generator.kinetic.WindKineticGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WindKineticGeneratorBlockEntity extends AbstractKineticSourceBlockEntity implements IRotorProvider {
   public final InvSlotConsumableClass rotorSlot;
   private double windStrength;
   private float rotationSpeed;
   private float angle = 0.0F;
   private long lastcheck;
   private int updateTicker;

   public WindKineticGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(IRBlockEntities.WIND_KINETIC_GENERATOR.get(), blockPos, blockState);
      this.rotorSlot = new InvSlotConsumableKineticRotor(this, "rotor", 1, IKineticRotor.GearboxType.WIND);
      this.updateTicker = IR.random.nextInt(this.getTickRate());
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

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new WindKineticGeneratorMenu(containerId, playerInventory, this);
   }

   @Override
   public boolean facingMatchesDirection(Direction direction) {
      return direction == this.getFacing();
   }

   @Override
   public int getKineticEnergy(Direction direction) {
      return this.facingMatchesDirection(direction.getOpposite()) ? this.getPower() : 0;
   }

   @Override
   public int extractKineticEnergy(Direction direction, int amount, boolean simulation) {
      return this.facingMatchesDirection(direction.getOpposite()) ? Math.min(amount, this.getPower()) : 0;
   }

   public int checkSpace(int n, boolean bl) {
      int n2 = this.getRotorDiameter() / 2;
      int n3 = 0;
      if (bl) {
 n = 1;
         n3 = n + 1;
      }

      if (!bl) {
         n2 *= 2;
      }

      Direction direction = this.getFacing();
      Direction direction2 = direction.getClockWise(Axis.Y);
      int n4 = Math.abs(n * direction.getStepX() + n2 * direction2.getStepX());
      int n5 = Math.abs(n * direction.getStepZ() + n2 * direction2.getStepZ());
      PathNavigationRegion pathNavigationRegion = new PathNavigationRegion(
         this.getLevel(), this.worldPosition.offset(-n4, -n2, -n5), this.worldPosition.offset(n4, n2, n5)
      );
      int n6 = 0;
      int n7 = this.worldPosition.getX();
      int n8 = this.worldPosition.getY();
      int n9 = this.worldPosition.getZ();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = -n2; i <= n2; i++) {
         int n10 = n8 + i;

         for (int j = -n2; j <= n2; j++) {
            boolean bl2 = false;

            for (int k = n3 - n; k <= n; k++) {
               int n11 = n7 + k * direction.getStepX() + j * direction2.getStepX();
               int n12 = n9 + k * direction.getStepZ() + j * direction2.getStepZ();
               mutableBlockPos.set(n11, n10, n12);
               assert Math.abs(n11 - n7) <= n4;
               assert Math.abs(n12 - n9) <= n5;
               BlockState blockState = pathNavigationRegion.getBlockState(mutableBlockPos);
               if (!blockState.isAir()) {
                  bl2 = true;
                  if ((i != 0 || j != 0 || k != 0) && pathNavigationRegion.getBlockEntity(mutableBlockPos) instanceof WindKineticGeneratorBlockEntity && !bl) {
                     return -1;
                  }
               }
            }

            if (bl2) {
               n6++;
            }
         }
      }

      return n6;
   }

   /** Раз в 1.6 секунды пересчитывает силу ветра на высоте. */
   @Override
   public void serverTick() {
      if (this.level == null || this.level.isClientSide) {
         return;
      }

      if (--this.updateTicker > 0) {
         return;
      }

      this.updateTicker = this.getTickRate();
      double strengthBefore = this.windStrength;
      float speedBefore = this.rotationSpeed;
      boolean activeBefore = com.industrialreforged.block.ActiveBlock.isActive(this);
      if (!this.hasRotor() || this.getRotorDiameter() <= 0 || !this.rotorHasSpace()) {
         this.windStrength = 0.0;
         this.setRotationSpeed(0.0F);
         com.industrialreforged.block.ActiveBlock.setActive(this, false);
         // Уже стоял — останавливать нечего, чанк не пачкаем.
         if (this.windStrength != strengthBefore || this.rotationSpeed != speedBefore
            || com.industrialreforged.block.ActiveBlock.isActive(this) != activeBefore) {
            this.markForSave();
         }
         return;
      }

      // базовый бриз + высота: ветер есть уже у земли и заметно растёт с высотой
      double strength = 3.0 + Math.max(0.0, this.worldPosition.getY() - 60) / 3.5;
      if (this.level.isThundering()) {
         strength *= 1.5;
      } else if (this.level.isRaining()) {
         strength *= 1.2;
      }

      this.windStrength = Math.min(strength, 100.0);
      boolean running = this.isWindStrongEnough() && !this.isRotorOverloaded();
      this.setRotationSpeed(running ? Math.max(0.05F, (float)this.windStrength / 100.0F) : 0.0F);
      com.industrialreforged.block.ActiveBlock.setActive(this, running);
      boolean damaged = false;
      if (running) {
         damaged = this.damageRotor();
      }

      // Износ ротора — прямая мутация стака мимо listener'а, её ловим флагом.
      // Остальное — сравнением: погода стоит, значения те же — не пачкаем.
      if (damaged || this.windStrength != strengthBefore || this.rotationSpeed != speedBefore) {
         this.markForSave();
      }
   }

   /** Ротор изнашивается, пока крутится; сломанный — вылетает из слота. Возвращает true, если износ был. */
   private boolean damageRotor() {
      ItemStack rotor = this.rotorSlot.getItem();
      if (rotor.getMaxDamage() <= 0) {
         return false; // вечный ротор (иридий/титан/вольфрам)
      }

      int damage = rotor.getDamageValue() + 1;
      if (damage >= rotor.getMaxDamage()) {
         this.rotorSlot.setItem(ItemStack.EMPTY);
         this.windStrength = 0.0;
         this.setRotationSpeed(0.0F);
         com.industrialreforged.block.ActiveBlock.setActive(this, false);
         return true;
      } else {
         rotor.setDamageValue(damage);
         return true;
      }
   }

   public boolean hasRotor() {
      return !this.rotorSlot.isEmpty();
   }

   public boolean rotorHasSpace() {
      return this.checkSpace(1, true) == 0;
   }

   private void setRotationSpeed(float f) {
      if (this.rotationSpeed != f) {
         this.rotationSpeed = f;
         this.markForUpdate();
      }
   }

   public int getTickRate() {
      return 32;
   }

   @Override
   public float getAngle() {
      if (this.rotationSpeed != 0.0F) {
         this.angle = this.angle + (float)(System.currentTimeMillis() - this.lastcheck) * this.rotationSpeed;
         this.angle %= 360.0F;
      }

      this.lastcheck = System.currentTimeMillis();
      return this.angle;
   }

   public float getEfficiency() {
      ItemStack stack = this.rotorSlot.getItem();
      return stack.getItem() instanceof IKineticRotor rotor ? rotor.getEfficiency(stack) : 0.0F;
   }

   public int getMinWindStrength() {
      ItemStack stack = this.rotorSlot.getItem();
      return stack.getItem() instanceof IKineticRotor rotor ? rotor.getMinWindStrength(stack) : 0;
   }

   public int getMaxWindStrength() {
      ItemStack stack = this.rotorSlot.getItem();
      return stack.getItem() instanceof IKineticRotor rotor ? rotor.getMaxWindStrength(stack) : 0;
   }

   @Override
   public int getRotorDiameter() {
      ItemStack stack = this.rotorSlot.getItem();
      return stack.getItem() instanceof IKineticRotor rotor ? rotor.getDiameter(stack) : 0;
   }

   @Override
   public Direction getFacing() {
      return RotateBlock.getHorizontalFacing(this);
   }

   @Override
   public ResourceLocation getRotorRenderTexture() {
      ItemStack stack = this.rotorSlot.getItem();
      return stack.getItem() instanceof IKineticRotor rotor ? rotor.getRotorRenderTexture(stack) : IR.texture("items/rotor/wood_rotor_model");
   }

   public boolean isRotorOverloaded() {
      return this.hasRotor() && this.rotorHasSpace() && this.isWindStrongEnough() && this.windStrength > this.getMaxWindStrength();
   }

   public boolean isWindStrongEnough() {
      return this.windStrength >= this.getMinWindStrength();
   }

   @Override
   public int getPower() {
      return this.windStrength >= this.getMinWindStrength() && ActiveBlock.isActive(this) ? (int)(this.windStrength * 10.0 * this.getEfficiency()) : 0;
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

   public int getWindStrengthData() {
      return (int)(this.windStrength * 10000.0);
   }

   public void setWindStrengthData(int value) {
      this.windStrength = value / 10000.0;
   }
}
