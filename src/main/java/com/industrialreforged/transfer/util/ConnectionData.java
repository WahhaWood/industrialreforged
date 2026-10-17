package com.industrialreforged.transfer.util;

import com.industrialreforged.transfer.block.WireBlockEntity;
import com.industrialreforged.transfer.system.EnergyReceive;

public class ConnectionData {
   public ConnectionType type = ConnectionType.NONE;
   public EnergyReceive receive;
   public WireBlockEntity wire;
   public ConnectionData.Resistor resistor;
   public boolean transformer;

   public long getSend(long value) {
      return this.resistor == null || this.resistor.value == -1L
         ? value
         : (this.resistor.mode == 0 ? Math.min(this.resistor.value, value) : value * this.resistor.value / 100L);
   }

   public static class Resistor {
      public int mode;
      public long value;

      public Resistor(int mode, long value) {
         this.mode = mode;
         this.value = value;
      }
   }
}
