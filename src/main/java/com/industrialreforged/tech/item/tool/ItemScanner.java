package com.industrialreforged.tech.item.tool;

import com.industrialreforged.item.energy.BasicEnergyItem;
import com.industrialreforged.tech.core.ref.IRSounds;
import com.industrialreforged.tech.core.util.ItemComparableItemStack;
import com.industrialreforged.tech.core.util.SoundUtil;
import com.industrialreforged.tech.core.util.StackUtil;
import com.industrialreforged.tech.menu.item.ToolScannerMenu;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags.Blocks;
import org.apache.commons.lang3.tuple.Pair;

public class ItemScanner extends BasicEnergyItem implements MenuProvider {
   public ItemScanner(int capacity, int energyTick, Properties properties) {
      super(properties.stacksTo(1).component(DataComponents.CUSTOM_DATA, CustomData.EMPTY), capacity, energyTick);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("text.industrialreforged.tooltip.scanner.range", new Object[]{this.getScanRange()}));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      int receiveTick = this.getEnergyReceiveTick(itemStack);
      if ((receiveTick != 512 || this.use(itemStack, 200, false)) && (receiveTick != 2048 || this.use(itemStack, 1000, false))) {
         if (!level.isClientSide) {
            List<Pair<ItemStack, Integer>> map = this.scan(player, this.getScanRange());
            player.openMenu(this, buf -> buf.writeCollection(map, (buffer, value) -> {
               ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, (ItemStack)value.getKey());
               buf.writeVarInt((Integer)value.getValue());
            }));
         } else {
            SoundUtil.play(IRSounds.SOUND_TOOL_SCANNER, 1.0F, 1.0F);
         }

         return new InteractionResultHolder(InteractionResult.SUCCESS, itemStack);
      } else {
         return new InteractionResultHolder(InteractionResult.FAIL, itemStack);
      }
   }

   public boolean onDroppedByPlayer(ItemStack itemStack, Player player) {
      if (!player.getCommandSenderWorld().isClientSide
         && !itemStack.isEmpty()
         && player.containerMenu instanceof ToolScannerMenu menu
         && menu.container.stillValid(player)) {
         player.closeContainer();
      }

      return true;
   }

   public Component getDisplayName() {
      return Component.empty();
   }

   public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
      return new ToolScannerMenu(containerId, playerInventory);
   }

   public int startLayerScan(ItemStack itemStack) {
      return this.use(itemStack, 50, false) ? this.getScanRange() / 2 : 0;
   }

   public int getScanRange() {
      return 6;
   }

   private List<Pair<ItemStack, Integer>> scan(Player player, int radius) {
      BlockPos blockPos = player.blockPosition();
      HashMap<ItemComparableItemStack, Integer> hashMap = new HashMap<>();
      PathNavigationRegion pathNavigationRegion = new PathNavigationRegion(
         player.level(), blockPos.offset(-radius, -radius, -radius), blockPos.offset(radius, radius, radius)
      );
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = blockPos.getY() - radius; i <= blockPos.getY() + radius; i++) {
         for (int j = blockPos.getZ() - radius; j <= blockPos.getZ() + radius; j++) {
            for (int k = blockPos.getX() - radius; k <= blockPos.getX() + radius; k++) {
               mutableBlockPos.set(k, i, j);
               BlockState blockState = pathNavigationRegion.getBlockState(mutableBlockPos);
               if (!blockState.isAir() && blockState.is(Blocks.ORES)) {
                  ItemStack itemStack2 = StackUtil.getPickStack(player.level(), mutableBlockPos, blockState, player);
                  ItemComparableItemStack itemComparableItemStack = new ItemComparableItemStack(itemStack2, true);
                  Integer n2 = hashMap.get(itemComparableItemStack);
                  if (n2 == null) {
                     n2 = 0;
                  }

                  n2 = n2 + itemStack2.getCount();
                  hashMap.put(itemComparableItemStack, n2);
               }
            }
         }
      }

      return hashMap.entrySet()
         .stream()
         .map(entry -> Pair.of(entry.getKey().toStack(), entry.getValue()))
         .sorted((t2, t22) -> (Integer)t22.getValue() - (Integer)t2.getValue())
         .toList();
   }
}
