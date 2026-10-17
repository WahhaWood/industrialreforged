package com.industrialreforged.tech.api.upgrade;

import com.industrialreforged.tech.api.tile.EnergyHandler;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public interface IUpgradableBlock {
   @Nullable
   default EnergyHandler getEnergy() {
      return null;
   }

   default int getEnergyStored() {
      return this.getEnergy() == null ? 0 : this.getEnergy().getEnergyStored();
   }

   default boolean useEnergy(int amount) {
      return this.getEnergy() == null || this.getEnergy().hasExtractEnergy(amount, false);
   }

   Set<UpgradableProperty> getUpgradableProperties();

   default void setConsumption(int value) {
   }

   default int getConsumption() {
      return -1;
   }

   /**
    * Честный расход за тик для панели: то, что машина реально списывает
    * (с пакетом укрепления и его наценкой). По умолчанию равен базовому
    * getConsumption; машины с пакетной ценой переопределяют.
    */
   default int getDisplayConsumption() {
      return this.getConsumption();
   }

   default void setDisplayConsumption(int value) {
   }

   /**
    * Голод: есть начатая работа, но энергии не хватает на полный тик.
    * Считает сервер каждый тик, клиент получает через синк меню.
    */
   default boolean isStarved() {
      return false;
   }

   default void setStarved(boolean value) {
   }

   default void setTimeCreation(int value) {
   }

   default int getTimeCreation() {
      return -1;
   }
}
