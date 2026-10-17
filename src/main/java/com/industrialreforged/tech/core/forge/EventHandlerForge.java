package com.industrialreforged.tech.core.forge;

import com.industrialreforged.tech.api.block.BreakableBlock;
import com.industrialreforged.tech.api.item.IArmorTick;
import com.industrialreforged.tech.container.InventoryDynamic;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.tech.core.TickHandler;
import com.industrialreforged.tech.core.WorldData;
import com.industrialreforged.tech.core.ref.IRItems;
import com.industrialreforged.tech.item.armor.ItemArmorElectric;
import com.industrialreforged.tech.item.armor.ItemArmorQuantumSuit;
import com.industrialreforged.tech.item.armor.ItemArmorSingularSuit;
import com.industrialreforged.tech.item.armor.jetpack.IJetpack;
import com.industrialreforged.item.energy.BasicEnergyArmorItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Pre;

public final class EventHandlerForge {
   public static List<ItemStack> itemCollection = null;
   private final ResourceLocation boostSpeedModifierId = IR.location("leggings_boost_speed");
   private static final ResourceLocation SET_STRENGTH_ID = IR.location("armor_set_strength");
   private static final ResourceLocation SET_ABSORPTION_ID = IR.location("armor_set_absorption");

   @SubscribeEvent
   public void serverStart(ServerStartingEvent event) {
      IR.sideProxy.onServerAvailable(event.getServer());
   }

   @SubscribeEvent
   public void onPlayerLogout(PlayerLoggedOutEvent event) {
      if (IR.sideProxy.isSimulating()) {
         IR.sideProxy.getKeyboard().removePlayerReferences(event.getEntity());
      }
   }

   @SubscribeEvent
   public void onWorldUnload(Unload unload) {
      WorldData.onWorldUnload((Level)unload.getLevel());
   }

   @SubscribeEvent
   public void onBlockStartBreak(LeftClickBlock event) {
      BlockState blockState = event.getLevel().getBlockState(event.getPos());
      if (blockState.getBlock() instanceof BreakableBlock breakableBlock) {
         InteractionResult result = breakableBlock.startBreak(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), blockState, event.getFace());
         if (result == InteractionResult.FAIL) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public void onWorldTick(Pre e) {
      TickHandler.onWorldTickStart(e.getLevel());
   }

   @SubscribeEvent
   public void onGetBurnTime(FurnaceFuelBurnTimeEvent event) {
      ItemStack itemStack = event.getItemStack();
      if (!itemStack.isEmpty()) {
         Item item = itemStack.getItem();
         if (item == IRItems.RUBBER_SAPLING.get()) {
            event.setBurnTime(80);
         }

         if (item == Items.SUGAR_CANE) {
            event.setBurnTime(50);
         }

         if (item == Items.CACTUS) {
            event.setBurnTime(50);
         }
      }
   }

   @SubscribeEvent
   public void entityJoinWorld(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof ItemEntity && itemCollection != null && !event.isCanceled()) {
         itemCollection.add(((ItemEntity)event.getEntity()).getItem());
         event.setCanceled(true);
      }
   }

   public static List<ItemStack> breakAndCollectWithPlayer(Level world, BlockPos pos, Player player) {
      List<ItemStack> stacks = new ArrayList<>();
      if (!(world instanceof ServerLevel)) {
         return stacks;
      }

      BlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      itemCollection = new ArrayList<>();
      BlockEntity tile = world.getBlockEntity(pos);
      if (state.onDestroyedByPlayer(world, pos, player, true, world.getFluidState(pos))) {
         block.destroy(world, pos, state);
         block.playerDestroy(world, player, pos, state, tile, player.getMainHandItem());
      }

      stacks.addAll(itemCollection);
      itemCollection = null;
      return stacks;
   }

   public static void breakAndCollectWithPlayer(Level world, BlockPos pos, InventoryDynamic inventoryDynamic, Player player, int xp) {
      for (ItemStack stack : breakAndCollectWithPlayer(world, pos, player)) {
         if (stack != null && !stack.isEmpty()) {
            inventoryDynamic.addItem(stack);
         }
      }

      inventoryDynamic.xp += xp;
   }

   @SubscribeEvent
   public void jumpBoost(LivingJumpEvent event) {
      if (event.getEntity() instanceof Player player) {
         ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
         if (stack.getItem() instanceof ItemArmorElectric armor
            && (armor instanceof ItemArmorQuantumSuit || armor instanceof ItemArmorSingularSuit)
            && IR.keyboard.isBoostKeyDown(player)
            && armor.use(stack, 4000, true)) {
            if (!player.level().isClientSide) {
               armor.use(stack, 4000, false);
            }

            Vec3 vec3 = player.getDeltaMovement();
            player.setDeltaMovement(vec3.x * 3.5, vec3.y + 0.3F, vec3.z * 3.5);
         }
      }
   }

   @SubscribeEvent
   public void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre event) {
      Player player = event.getEntity();

      for (ItemStack stack : player.getInventory().armor) {
         if (stack.getItem() instanceof IArmorTick armorTick) {
            armorTick.onPreArmorTick(stack, player.level(), player);
         }
      }

      if (!player.level().isClientSide) {
         tickArmorSetBonus(player);
      }
   }

   /**
    * Бонусы полного комплекта энергоброни — без зелий (нет иконок и частиц, молоко не снимает):
    * сила +3, поглощение +4 HP и регенерация с темпом эффекта I/II/III для нано/кванта/сингулярной;
    * сопротивление (−20 % за уровень) считается в onLivingDamage. Ступень — по худшему куску. Заряд не нужен: при заряде урон и так гасит энергия,
    * а бонусы — страховка на случай, когда батареи сели.
    */
   private static void tickArmorSetBonus(Player player) {
      int tier = ItemArmorElectric.getWornSetTier(player);
      setBonusModifier(player, Attributes.ATTACK_DAMAGE, SET_STRENGTH_ID, 3.0 * tier);
      setBonusModifier(player, Attributes.MAX_ABSORPTION, SET_ABSORPTION_ID, 4.0 * tier);
      if (tier > 0 && player.tickCount % (50 >> (tier - 1)) == 0) {
         if (player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F);
         }

         if (player.getAbsorptionAmount() < player.getMaxAbsorption()) {
            player.setAbsorptionAmount(player.getAbsorptionAmount() + 1.0F);
         }
      }
   }

   /** Трогает модификатор только при смене значения: иначе атрибут слался бы клиенту каждый тик. */
   private static void setBonusModifier(Player player, Holder<Attribute> attribute, ResourceLocation id, double amount) {
      AttributeInstance instance = player.getAttribute(attribute);
      if (instance == null) {
         return;
      }

      AttributeModifier current = instance.getModifier(id);
      if (amount <= 0.0) {
         if (current != null) {
            instance.removeModifier(id);
         }
      } else if (current == null || current.amount() != amount) {
         instance.addOrUpdateTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
      }
   }

   /**
    * Энергоброня даёт ванильную защиту только при заряде: у разряженного куска
    * срезаем броню/прочность/отброс, материал при этом хранит полные значения.
    */
   @SubscribeEvent
   public void onItemAttributes(ItemAttributeModifierEvent event) {
      ItemStack stack = event.getItemStack();
      if (stack.getItem() instanceof BasicEnergyArmorItem armor && armor.getEnergyStored(stack) <= 0) {
         event.removeAllModifiersFor(Attributes.ARMOR);
         event.removeAllModifiersFor(Attributes.ARMOR_TOUGHNESS);
         event.removeAllModifiersFor(Attributes.KNOCKBACK_RESISTANCE);
      }
   }

   /**
    * Энергоброня в духе IC2: каждый заряженный кусок забирает свою долю урона
    * (шлем и ботинки 15 %, нагрудник 40 %, поножи 30 %, полный комплект — 100 %)
    * и платит за неё энергией. Не хватило энергии — остаток проходит.
    * Pre идёт после неуязвимости и ванильной брони, один раз на реальный удар,
    * поэтому лава и огонь не списывают энергию каждый тик. /kill и пустоту не держит.
    * Перед этим — сопротивление полного комплекта (см. tickArmorSetBonus).
    */
   @SubscribeEvent
   public void onLivingDamage(LivingDamageEvent.Pre event) {
      // Только игроки: как и прочие бонусы комплекта; мобы в фермах выходят здесь сразу.
      float damage = event.getNewDamage();
      if (!(event.getEntity() instanceof Player player)
         || damage <= 0.0F
         || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return;
      }

      // Сопротивление полного комплекта: −20 % за уровень, как у эффекта, и с теми же
      // исключениями. Считается до энергии — она платит уже за остаток.
      int setTier = ItemArmorElectric.getWornSetTier(player);
      if (setTier > 0
         && !event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS)
         && !event.getSource().is(DamageTypeTags.BYPASSES_RESISTANCE)) {
         damage *= 1.0F - 0.2F * setTier;
      }

      float totalShare = 0.0F;
      for (ItemStack stack : player.getArmorSlots()) {
         if (stack.getItem() instanceof ItemArmorElectric armor && armor.getEnergyPerDamage() > 0 && armor.getEnergyStored(stack) > 0) {
            totalShare += armor.getBaseAbsorptionRatio() * armor.getDamageAbsorptionRatio();
         }
      }

      // Квантовый нагрудник (1.1) выводит комплект за 100 % — режем до целого.
      float scale = Math.max(1.0F, totalShare);
      float absorbed = 0.0F;
      for (ItemStack stack : player.getArmorSlots()) {
         if (stack.getItem() instanceof ItemArmorElectric armor && armor.getEnergyPerDamage() > 0) {
            int stored = armor.getEnergyStored(stack);
            if (stored <= 0) {
               continue;
            }

            float part = damage * armor.getBaseAbsorptionRatio() * armor.getDamageAbsorptionRatio() / scale;
            double cost = Math.ceil((double)part * armor.getEnergyPerDamage());
            if (cost <= stored) {
               armor.use(stack, (int)cost, false);
               absorbed += part;
            } else {
               armor.use(stack, stored, false);
               absorbed += (float)stored / armor.getEnergyPerDamage();
            }
         }
      }

      // Сумма долей в float может недобрать ulp — крохи не считаем уроном.
      float left = damage - absorbed;
      event.setNewDamage(left < 1.0E-3F ? 0.0F : left);
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public void swap(RightClickItem event) {
      Player player = event.getEntity();
      ItemStack itemstack = player.getItemInHand(event.getHand());
      EquipmentSlot equipmentslot = itemstack.getEquipmentSlot();
      if (equipmentslot == null) {
         Equipable equipable = Equipable.get(itemstack);
         equipmentslot = equipable != null ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
      }

      ItemStack itemstack1 = player.getItemBySlot(equipmentslot);
      if (itemstack1.getItem() instanceof IJetpack || itemstack1.getItem() instanceof ElytraItem) {
         event.setCanceled(true);
      }
   }
}
