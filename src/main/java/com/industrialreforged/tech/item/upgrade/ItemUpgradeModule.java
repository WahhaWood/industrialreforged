package com.industrialreforged.tech.item.upgrade;

import com.industrialreforged.utils.MsgUtils;
import com.industrialreforged.tech.api.upgrade.IUpgradeItem;
import com.industrialreforged.tech.api.upgrade.UpgradableProperty;
import com.industrialreforged.tech.api.upgrade.UpgradeRegistry;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemUpgradeModule extends Item implements IUpgradeItem {
   private static final DecimalFormat decimalformat = new DecimalFormat("0.##");
   public final ItemUpgradeModule.UpgradeType type;

   public ItemUpgradeModule(Properties properties, ItemUpgradeModule.UpgradeType upgradeType) {
      // «Укрепление» — по одному на механизм (и в инвентаре не стакается)
      super(upgradeType == UpgradeType.STRENGTHENING ? properties.stacksTo(1) : properties);
      this.type = upgradeType;
      UpgradeRegistry.register(this);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, list, tooltipFlag);
      switch (this.type) {
         case OVERCLOCKER_1_LEVEL:
            list.add(
               Component.translatable("industrialreforged.tooltip.upgrade.overclocker.time", new Object[]{decimalformat.format(100.0 * Math.pow(0.7, stack.getCount()))})
            );
            list.add(
               Component.translatable("industrialreforged.tooltip.upgrade.overclocker.power", new Object[]{decimalformat.format(100.0 * Math.pow(2.0, stack.getCount()))})
            );
            break;
         case OVERCLOCKER_2_LEVEL:
            list.add(
               Component.translatable("industrialreforged.tooltip.upgrade.overclocker.time", new Object[]{decimalformat.format(100.0 * Math.pow(0.6, stack.getCount()))})
            );
            list.add(
               Component.translatable("industrialreforged.tooltip.upgrade.overclocker.power", new Object[]{decimalformat.format(100.0 * Math.pow(2.0, stack.getCount()))})
            );
            break;
         case OVERCLOCKER_3_LEVEL:
            list.add(
               Component.translatable("industrialreforged.tooltip.upgrade.overclocker.time", new Object[]{decimalformat.format(100.0 * Math.pow(0.5, stack.getCount()))})
            );
            list.add(
               Component.translatable("industrialreforged.tooltip.upgrade.overclocker.power", new Object[]{decimalformat.format(100.0 * Math.pow(2.0, stack.getCount()))})
            );
            break;
         case TRANSFORMER:
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.transformer"));
            break;
         case ENERGY_STORAGE:
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.storage"));
            break;
         case REDSTONE_INVERTER:
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.redstone"));
            break;
         case EJECTOR:
         case FLUID_EJECTOR:
            String stringx = getSideName(stack);
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.ejector", new Object[]{Component.translatable(stringx)}));
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.ejector.usage"));
            if (this.type == UpgradeType.EJECTOR) {
               list.add(Component.translatable("industrialreforged.tooltip.upgrade.ejector.level", new Object[]{stack.getCount()}));
            }
            if (stringx.endsWith("direction.all")) {
               list.add(Component.translatable("industrialreforged.tooltip.upgrade.ejector.warn"));
            }
            break;
         case PULLING:
         case FLUID_PULLING:
            String string = getSideName(stack);
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.pulling", new Object[]{Component.translatable(string)}));
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.pulling.usage"));
            if (this.type == UpgradeType.PULLING) {
               list.add(Component.translatable("industrialreforged.tooltip.upgrade.pulling.level", new Object[]{stack.getCount()}));
            }
            if (string.endsWith("direction.all")) {
               list.add(Component.translatable("industrialreforged.tooltip.upgrade.pulling.warn"));
            }
            break;
         case STRENGTHENING:
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.strengthening"));
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.strengthening.one"));
            break;
         case PARTICLE_ACCELERATOR:
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.particle_accelerator"));
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.particle_accelerator_1"));
            break;
         case SOUNDPROOFING:
            list.add(Component.translatable("industrialreforged.tooltip.upgrade.soundproofing"));
      }
   }

   private static String getSideName(ItemStack itemStack) {
      Direction direction = getDirection(itemStack);
      return "industrialreforged.tooltip.upgrade.direction." + (direction == null ? "all" : direction.getName());
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      ItemStack itemStack = useOnContext.getItemInHand();
      if (this.type.directional) {
         Direction clickedFace = useOnContext.getClickedFace();
         if (itemStack.get(IRDataComponents.DIRECTION) == clickedFace) {
            itemStack.set(IRDataComponents.DIRECTION, null);
         } else {
            itemStack.set(IRDataComponents.DIRECTION, clickedFace);
         }

         if (useOnContext.getLevel().isClientSide() && useOnContext.getPlayer() != null) {
            switch (this.type) {
               case EJECTOR:
               case FLUID_EJECTOR:
                  MsgUtils.send(
                     useOnContext.getPlayer(),
                     Component.translatable("industrialreforged.tooltip.upgrade.ejector", new Object[]{Component.translatable(getSideName(itemStack))})
                  );
                  break;
               case PULLING:
               case FLUID_PULLING:
                  MsgUtils.send(
                     useOnContext.getPlayer(),
                     Component.translatable("industrialreforged.tooltip.upgrade.pulling", new Object[]{Component.translatable(getSideName(itemStack))})
                  );
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      return new InteractionResultHolder(InteractionResult.PASS, player.getItemInHand(interactionHand));
   }

   @Override
   public boolean isSuitableFor(ItemStack stack, Set<UpgradableProperty> properties) {
      return switch (this.type) {
         case OVERCLOCKER_1_LEVEL, OVERCLOCKER_2_LEVEL, OVERCLOCKER_3_LEVEL -> properties.contains(UpgradableProperty.PROCESSING)
            || properties.contains(UpgradableProperty.AUGMENTABLE);
         case TRANSFORMER -> false; // больше не вставляется в машины: только компонент для крафта
         case ENERGY_STORAGE -> properties.contains(UpgradableProperty.ENERGY_STORAGE);
         case REDSTONE_INVERTER -> properties.contains(UpgradableProperty.REDSTONE_SENSITIVE);
         case EJECTOR -> properties.contains(UpgradableProperty.ITEM_PRODUCING);
         case PULLING -> properties.contains(UpgradableProperty.ITEM_CONSUMING);
         case FLUID_EJECTOR -> properties.contains(UpgradableProperty.FLUID_PRODUCING);
         case FLUID_PULLING -> properties.contains(UpgradableProperty.FLUID_CONSUMING);
         case STRENGTHENING -> properties.contains(UpgradableProperty.STRENGTHENING);
         case PARTICLE_ACCELERATOR -> properties.contains(UpgradableProperty.PARTICLE_ACCELERATOR);
         case SOUNDPROOFING -> properties.contains(UpgradableProperty.SOUNDPROOFING);
      };
   }

   @Nullable
   public static Direction getDirection(ItemStack itemStack) {
      return (Direction)itemStack.get(IRDataComponents.DIRECTION);
   }

   public enum UpgradeType {
      OVERCLOCKER_1_LEVEL(false),
      OVERCLOCKER_2_LEVEL(false),
      OVERCLOCKER_3_LEVEL(false),
      TRANSFORMER(false),
      ENERGY_STORAGE(false),
      REDSTONE_INVERTER(false),
      EJECTOR(true),
      PULLING(true),
      FLUID_EJECTOR(true),
      FLUID_PULLING(true),
      STRENGTHENING(false),
      PARTICLE_ACCELERATOR(false),
      SOUNDPROOFING(false);

      public final boolean directional;

      UpgradeType(boolean bl) {
         this.directional = bl;
      }
   }
}
