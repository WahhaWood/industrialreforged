package com.industrialreforged.tech.item;

import com.industrialreforged.tech.block.BrewBarrelBlock;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.ref.IRItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ItemBooze extends Item {
   public String[] solidRatio = new String[]{"Watery ", "Clear ", "Lite ", "", "Strong ", "Thick ", "Stodge ", "X"};
   public String[] hopsRatio = new String[]{"Soup ", "Alcfree ", "White ", "", "Dark ", "Full ", "Black ", "X"};
   public String[] timeRatioNames = new String[]{"Brew", "Youngster", "Beer", "Ale", "Dragonblood", "Black Stuff"};
   public int[] baseDuration = new int[]{300, 600, 900, 1200, 1600, 2000, 2400};
   public float[] baseIntensity = new float[]{0.4F, 0.75F, 1.0F, 1.5F, 2.0F};
   public static float rumStackability = 2.0F;
   public static int rumDuration = 600;

   public ItemBooze(Properties properties) {
      super(properties.component(IRDataComponents.BOOZE, new BrewBarrelBlock.BrewBoozeData(0, 0, 0, false, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0)));
   }

   public ItemStack getItemStack(byte type, byte solid, byte hops, byte time, int booze, byte rumProgress) {
      ItemStack stack = new ItemStack(this);
      stack.set(IRDataComponents.BOOZE, new BrewBarrelBlock.BrewBoozeData(type, booze, rumProgress, false, (byte)0, (byte)0, solid, hops, time));
      return stack;
   }

   public byte getType(ItemStack stack) {
      return (byte)this.getData(stack).type;
   }

   public byte getSolid(ItemStack stack) {
      return this.getData(stack).solid;
   }

   public byte getHops(ItemStack stack) {
      return this.getData(stack).hops;
   }

   public byte getTime(ItemStack stack) {
      return this.getData(stack).time;
   }

   public byte getRumProgress(ItemStack stack) {
      return (byte)this.getData(stack).age;
   }

   private BrewBarrelBlock.BrewBoozeData getData(ItemStack stack) {
      return (BrewBarrelBlock.BrewBoozeData)stack.get(IRDataComponents.BOOZE);
   }

   public Component getName(ItemStack stack) {
      int type = this.getType(stack);
      if (type == 1) {
         int timeRatio = Math.min(this.getTime(stack), this.timeRatioNames.length - 1);
         return timeRatio == this.timeRatioNames.length - 1
            ? Component.literal(this.timeRatioNames[timeRatio])
            : Component.literal(this.solidRatio[this.getSolid(stack)] + this.hopsRatio[this.getHops(stack)] + this.timeRatioNames[timeRatio]);
      } else {
         return type == 2 ? Component.literal("Rum") : Component.literal("Zero");
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      int type = this.getType(stack);
      if (type == 0) {
         return new ItemStack((ItemLike)IRItems.EMPTY_MUG.get());
      }

      if (type == 1) {
         byte time = this.getTime(stack);
         if (this.getTime(stack) == 5) {
            return this.drinkBlackStuff(entity);
         }

         byte solidRatio = this.getSolid(stack);
         byte alc = this.getHops(stack);
         int duration = this.baseDuration[solidRatio];
         float intensity = this.baseIntensity[time];
         if (entity instanceof Player) {
            ((Player)entity).getFoodData().addExhaustion(solidRatio * 0.15F);
         }

         int max = (int)(intensity * (alc * 0.5F));
         MobEffectInstance slow = entity.getEffect(MobEffects.DIG_SLOWDOWN);
         int amplifier = -1;
         if (slow != null) {
            amplifier = slow.getAmplifier();
         }

         this.amplifyEffect(entity, MobEffects.DIG_SLOWDOWN, max, intensity, duration);
         if (amplifier > -1) {
            this.amplifyEffect(entity, MobEffects.DAMAGE_BOOST, max, intensity, duration);
            if (amplifier > 0) {
               this.amplifyEffect(entity, MobEffects.MOVEMENT_SLOWDOWN, max / 2, intensity, duration);
               if (amplifier > 1) {
                  this.amplifyEffect(entity, MobEffects.DAMAGE_RESISTANCE, max - 1, intensity, duration);
                  if (amplifier > 2) {
                     this.amplifyEffect(entity, MobEffects.CONFUSION, 0, intensity, duration);
                     if (amplifier > 3) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1, entity.level().random.nextInt(3)));
                     }
                  }
               }
            }
         }
      }

      if (type == 2) {
         if (this.getRumProgress(stack) < 100) {
            this.drinkBlackStuff(entity);
         } else {
            this.amplifyEffect(entity, MobEffects.FIRE_RESISTANCE, 0, rumStackability, rumDuration);
            MobEffectInstance def = entity.getEffect(MobEffects.DAMAGE_RESISTANCE);
            int level2 = -1;
            if (def != null) {
               level2 = def.getAmplifier();
            }

            this.amplifyEffect(entity, MobEffects.DAMAGE_RESISTANCE, 2, rumStackability, rumDuration);
            if (level2 >= 0) {
               this.amplifyEffect(entity, MobEffects.BLINDNESS, 0, rumStackability, rumDuration);
            }

            if (level2 >= 1) {
               this.amplifyEffect(entity, MobEffects.CONFUSION, 0, rumStackability, rumDuration);
            }
         }
      }

      return new ItemStack((ItemLike)IRItems.EMPTY_MUG.get());
   }

   public void amplifyEffect(LivingEntity living, Holder<MobEffect> potion, int max, float intensity, int duration) {
      MobEffectInstance eff = living.getEffect(potion);
      if (eff == null) {
         living.addEffect(new MobEffectInstance(potion, duration, 0));
      } else {
         int currentDuration = eff.getDuration();
         int maxnewdur = (int)(duration * (1.0F + intensity * 2.0F) - currentDuration) / 2;
         if (maxnewdur < 0) {
            maxnewdur = 0;
         }

         if (maxnewdur < duration) {
            duration = maxnewdur;
         }

         currentDuration += duration;
         int newamp = eff.getAmplifier();
         if (newamp < max) {
            newamp++;
         }

         living.addEffect(new MobEffectInstance(potion, currentDuration, newamp));
      }
   }

   public ItemStack drinkBlackStuff(LivingEntity living) {
      switch (living.level().random.nextInt(6)) {
         case 1:
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 1200, 0));
            break;
         case 2:
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 2400, 0));
            break;
         case 3:
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 2400, 0));
            break;
         case 4:
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
            break;
         case 5:
            living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1, living.level().random.nextInt(4)));
      }

      return new ItemStack((ItemLike)IRItems.EMPTY_MUG.get());
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      player.startUsingItem(hand);
      return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
   }

   private enum BoozeMugType {
      beer_brew,
      beer_youngster,
      beer_beer,
      beer_ale,
      beer_dragon_blood,
      beer_black_stuff,
      rum;
   }
}
