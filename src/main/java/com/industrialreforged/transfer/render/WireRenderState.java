package com.industrialreforged.transfer.render;

import com.industrialreforged.transfer.util.ConnectionType;
import java.util.EnumMap;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class WireRenderState {
   public static final ModelProperty<WireRenderState> PROPERTY = new ModelProperty();
   private EnumMap<Direction, ConnectionType> connectionTypes = new EnumMap<>(Direction.class);

   public EnumMap<Direction, ConnectionType> getConnectionTypes() {
      return this.connectionTypes;
   }

   public void setConnectionTypes(EnumMap<Direction, ConnectionType> connectionTypes) {
      this.connectionTypes = connectionTypes;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + (this.connectionTypes == null ? 0 : this.connectionTypes.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      WireRenderState other = (WireRenderState)obj;
      return Objects.equals(this.connectionTypes, other.connectionTypes);
   }
}
