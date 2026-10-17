package com.industrialreforged.tech.item.reactor;

import com.industrialreforged.tech.api.reactor.IReactor;
import com.industrialreforged.tech.api.reactor.IReactorComponent;
import com.industrialreforged.tech.core.RadiationMobEffect;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.core.ref.IRMobEffects;
import com.industrialreforged.tech.item.armor.ItemArmorHazmat;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemReactorUranium extends AbstractDamageableReactorComponent {
   public final int numberOfCells;

   public ItemReactorUranium(Properties properties, int n) {
      this(properties, n, 20000);
   }

   protected ItemReactorUranium(Properties properties, int n, int n2) {
      super(properties, n2);
      this.numberOfCells = n;
   }

   /** В тултипе показываем, сколько нагрева и энергии стержень даёт за такт. */
   @Override
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      int basePulses = 1 + this.numberOfCells / 2;
      int minHeat = triangularNumber(basePulses) * 4;
      int maxHeat = triangularNumber(basePulses + 4) * 4;
      tooltip.add(
         Component.translatable("tooltip.industrialreforged.fuel_rod.heat", minHeat, maxHeat).withStyle(ChatFormatting.GRAY)
      );
      int minEnergy = basePulses * com.industrialreforged.tech.blockentity.reactor.NuclearReactorElectricBlockEntity.ENERGY_PER_PULSE;
      int maxEnergy = (basePulses + 4) * com.industrialreforged.tech.blockentity.reactor.NuclearReactorElectricBlockEntity.ENERGY_PER_PULSE;
      tooltip.add(
         Component.translatable("tooltip.industrialreforged.fuel_rod.energy", minEnergy, maxEnergy).withStyle(ChatFormatting.GRAY)
      );
   }

   @Override
   public void processChamber(IReactor reactor, ItemStack stack, int x, int y, boolean heatRun) {
      if (reactor.produceEnergy()) {
         int basePulses = 1 + this.numberOfCells / 2;

         for (int iteration = 0; iteration < this.numberOfCells; iteration++) {
            if (!heatRun) {
               for (int i = 0; i < basePulses; i++) {
                  this.acceptUraniumPulse(reactor, stack, stack, x, y, x, y, false);
               }

               checkPulseable(reactor, x - 1, y, stack, x, y, false);
               checkPulseable(reactor, x + 1, y, stack, x, y, false);
               checkPulseable(reactor, x, y - 1, stack, x, y, false);
               checkPulseable(reactor, x, y + 1, stack, x, y, false);
            } else {
               int pulses = basePulses + checkPulseable(reactor, x - 1, y, stack, x, y, true);
               pulses += checkPulseable(reactor, x + 1, y, stack, x, y, true);
               pulses += checkPulseable(reactor, x, y - 1, stack, x, y, true);
               pulses += checkPulseable(reactor, x, y + 1, stack, x, y, true);
               int heat = this.getFinalHeat(reactor, stack, x, y, triangularNumber(pulses) * 4);
               ArrayDeque<ItemReactorUranium.ItemStackCoord> heatAcceptors = new ArrayDeque<>();
               this.checkHeatAcceptor(reactor, x - 1, y, heatAcceptors);
               this.checkHeatAcceptor(reactor, x + 1, y, heatAcceptors);
               this.checkHeatAcceptor(reactor, x, y - 1, heatAcceptors);
               this.checkHeatAcceptor(reactor, x, y + 1, heatAcceptors);

               while (!heatAcceptors.isEmpty() && heat > 0) {
                  int dheat = heat / heatAcceptors.size();
                  heat -= dheat;
                  ItemReactorUranium.ItemStackCoord acceptor = heatAcceptors.remove();
                  IReactorComponent component = (IReactorComponent)acceptor.stack.getItem();
                  dheat = component.alterHeat(reactor, acceptor.stack, acceptor.x, acceptor.y, dheat);
                  heat += dheat;
               }

               if (heat > 0) {
                  reactor.addHeat(heat);
               }
            }
         }

         if (!heatRun) {
            stack.hurtAndBreak(1, (ServerLevel)reactor.getWorldObj(), null, item -> reactor.setItemAt(x, y, this.getDepletedStack(stack, reactor)));
         }
      }
   }

   protected int getFinalHeat(IReactor reactor, ItemStack stack, int x, int y, int heat) {
      return heat;
   }

   protected ItemStack getDepletedStack(ItemStack itemStack, IReactor iReactor) {
      return new ItemStack(switch (this.numberOfCells) {
         case 1 -> (Item)IRItems.DEPLETED_URANIUM_FUEL_ROD.get();
         case 2 -> (Item)IRItems.DEPLETED_DUAL_URANIUM_FUEL_ROD.get();
         default -> throw new RuntimeException("invalid cell count: " + this.numberOfCells);
         case 4 -> (Item)IRItems.DEPLETED_QUAD_URANIUM_FUEL_ROD.get();
      });
   }

   protected static int checkPulseable(IReactor reactor, int pulseX, int pulseY, ItemStack stack, int x, int y, boolean heatRun) {
      ItemStack itemStack2 = reactor.getItemAt(pulseX, pulseY);
      return itemStack2 != null
            && itemStack2.getItem() instanceof IReactorComponent
            && ((IReactorComponent)itemStack2.getItem()).acceptUraniumPulse(reactor, itemStack2, stack, pulseX, pulseY, x, y, heatRun)
         ? 1
         : 0;
   }

   protected static int triangularNumber(int x) {
      return (x * x + x) / 2;
   }

   protected void checkHeatAcceptor(IReactor reactor, int x, int y, Collection<ItemReactorUranium.ItemStackCoord> heatAcceptors) {
      ItemStack itemStack = reactor.getItemAt(x, y);
      if (itemStack != null
         && itemStack.getItem() instanceof IReactorComponent
         && ((IReactorComponent)itemStack.getItem()).canStoreHeat(reactor, itemStack, x, y)) {
         heatAcceptors.add(new ItemReactorUranium.ItemStackCoord(itemStack, x, y));
      }
   }

   @Override
   public boolean acceptUraniumPulse(IReactor reactor, ItemStack stack, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
      if (!heatRun) {
         reactor.addOutput(1.0F);
      }

      return true;
   }

   @Override
   public float influenceExplosion(IReactor reactor, ItemStack stack) {
      return 2 * this.numberOfCells;
   }

   public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int n, boolean bl) {
      LivingEntity livingEntity;
      if (entity instanceof LivingEntity && !ItemArmorHazmat.hasCompleteHazmat(livingEntity = (LivingEntity)entity)) {
         ((RadiationMobEffect)IRMobEffects.RADIATION.get()).applyTo(livingEntity, 200, 100);
      }
   }

   private record ItemStackCoord(ItemStack stack, int x, int y) {
   }
}
