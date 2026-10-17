package com.industrialreforged.tech.multiblock.tank;

import com.industrialreforged.block.ActiveBlock;
import com.industrialreforged.block.BaseEntityBlock;
import com.industrialreforged.tech.api.tile.IWrenchModeSwitch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Клапан резервуара-котла. Режим переключается ключом по кругу:
 * вход пара → вход воды → выход пара → выход воды.
 */
public class TankValveBlock extends BaseEntityBlock implements IWrenchModeSwitch {
   public static final EnumProperty<TankValveMode> MODE = EnumProperty.create("mode", TankValveMode.class);
   /**
    * Куда смотрит рукав клапана. Задаётся игроком по грани клика и никак не
    * участвует в валидации мультиблока: он нужен только для модели, чтобы
    * портовая грань была одна, а не на всех шести.
    */
   public static final DirectionProperty FACING = BlockStateProperties.FACING;

   public TankValveBlock() {
      super(BlockBehaviour.Properties.of()
         .strength(3.0F, 15.0F)
         .requiresCorrectToolForDrops()
         .sound(SoundType.METAL));
      this.registerDefaultState(this.defaultBlockState()
         .setValue(ActiveBlock.ACTIVE, false)
         .setValue(FACING, Direction.NORTH)
         .setValue(MODE, TankValveMode.WATER_IN));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{ActiveBlock.ACTIVE, MODE, FACING});
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      // Порт смотрит на игрока, а не на грань, по которой кликнули: тот, кто
      // ставит клапан, сразу видит, куда он смотрит. Горизонталь — потому что
      // рукав клапана вертикальным быть не может.
      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Override
   public BlockState rotate(BlockState state, Rotation rotation) {
      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation(state.getValue(FACING)));
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TankValveBlockEntity(pos, state);
   }

   @Override
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof TankValveBlockEntity valve && valve.isFormed()) {
         // Пробуем наполнить/слить жидкость ведром или капсулой (по правилам режима клапана)
         boolean fluidInteracted = FluidUtil.interactWithFluidHandler(player, hand, valve.getFluidWrapper(hitResult.getDirection()));
         if (fluidInteracted) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
         }
      }
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (level.getBlockEntity(pos) instanceof TankValveBlockEntity valve) {
         if (!level.isClientSide) {
            if (valve.isFormed() && !player.isShiftKeyDown()) {
               // Перепроверка: стенку могли сломать без уведомления (камень/стекло)
               if (!valve.validateStructure(null)) {
                  valve.validateStructure(player);
                  ActiveBlock.setActive(valve, false);
               } else {
                  player.sendSystemMessage(valve.getStatusMessage());
               }
            } else {
               // Валидация структуры
               boolean formed = valve.validateStructure(player);
               ActiveBlock.setActive(valve, formed);
            }
         }
         return InteractionResult.sidedSuccess(level.isClientSide);
      }
      return InteractionResult.PASS;
   }

   /** Ключ переключает режим клапана по кругу. */
   @Override
   public boolean switchMode(Level level, BlockPos pos, BlockState state, Player player) {
      if (level.isClientSide) {
         return true;
      }

      TankValveMode[] modes = TankValveMode.values();
      TankValveMode next = modes[(state.getValue(MODE).ordinal() + 1) % modes.length];
      level.setBlock(pos, state.setValue(MODE, next), 3);
      level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0.6F, 1.0F + next.ordinal() * 0.15F);
      if (player != null) {
         player.displayClientMessage(Component.translatable(next.getTranslationKey()), true);
      }

      return true;
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && !level.isClientSide) {
         if (level.getBlockEntity(pos) instanceof TankValveBlockEntity valve) {
            valve.disbandStructure();
         }
         TankWallBlock.notifyNearbyValves(level, pos);
      }
      super.onRemove(state, level, pos, newState, isMoving);
   }

   /**
    * Четыре режима клапана. Порядок объявления — это порядок обхода ключом:
    * вход воды → выход воды → вход пара → выход пара. Сначала среда, потом
    * направление, чтобы первые два щелчка выбирали, что клапан гонит, а
    * следующие два — куда.
    */
   public enum TankValveMode implements StringRepresentable {
      WATER_IN("water_in"),
      WATER_OUT("water_out"),
      STEAM_IN("steam_in"),
      STEAM_OUT("steam_out");

      private final String name;

      TankValveMode(String name) {
         this.name = name;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public String getTranslationKey() {
         return "text.industrialreforged.multiblock.tank.mode." + this.name;
      }

      public boolean isInput() {
         return this == STEAM_IN || this == WATER_IN;
      }

      public boolean isSteam() {
         return this == STEAM_IN || this == STEAM_OUT;
      }
   }
}
