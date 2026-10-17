package com.industrialreforged.tech.client.render;

import com.industrialreforged.tech.core.ref.IRFluids;
import com.industrialreforged.tech.multiblock.tank.TankValveBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Полупрозрачная вода и пар внутри резервуара. Рисует объём по синхронизированному уровню
 * (промилле ёмкости): вода снизу, пар над ней. Виден сквозь стеклянную оболочку.
 *
 * <p>Жидкость рисуется настоящей текстурой (спрайт блочного атласа, тин жидкости, биомный тин воды),
 * поэтому анимация из {@code .mcmeta} приезжает сама — свой код анимации не нужен.
 */
public class TankFluidRenderer implements BlockEntityRenderer<TankValveBlockEntity> {
   /** Ниже этого толщина слоя в блоках: рисуть слой толще одного шума рендера бессмысленно. */
   private static final double MIN_VISIBLE = 0.02;

   /**
    * Зазор между жидкостью и внутренней гранью стекла. У {@code debugFilledBox} был
    * шейдерный трюк {@code VIEW_OFFSET_Z_LAYERING}, который подтягивал бокс к камере; у обычного
    * {@link Sheets#translucentCullBlockSheet()} его нет, и копланарные грани дают z-fighting.
    * Ровно так же отступает {@code LiquidBlockRenderer}.
    */
   private static final double INSET = 0.001;

   /** UV по X и Z — горизонтальные грани (верх/низ). */
   private static final int UV_XZ = 0;
   /** UV по X и Y — грани, смотрящие в ±Z. */
   private static final int UV_XY = 1;
   /** UV по Z и Y — грани, смотрящие в ±X. */
   private static final int UV_ZY = 2;

   public TankFluidRenderer(Context context) {
   }

   @Override
   public void render(@NotNull TankValveBlockEntity tank, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
      // Жидкость рисует только мастер-клапан: у него есть границы и актуальные уровни
      if (!tank.isFormed() || !tank.isMaster()) {
         return;
      }
      BlockPos min = tank.getMinPos();
      BlockPos max = tank.getMaxPos();
      BlockAndTintGetter level = tank.getLevel();
      if (min == null || max == null || level == null) {
         return;
      }

      float water = clamp01(tank.getDisplayWaterFill() / 1000.0f);
      float steam = clamp01(tank.getDisplaySteamFill() / 1000.0f);
      if (water <= 0.0f && steam <= 0.0f) {
         return;
      }

      BlockPos origin = tank.getBlockPos();
      double x0 = min.getX() - origin.getX() + 1.0;
      double x1 = max.getX() - origin.getX();
      double y0 = min.getY() - origin.getY() + 1.0;
      double y1 = max.getY() - origin.getY();
      double z0 = min.getZ() - origin.getZ() + 1.0;
      double z1 = max.getZ() - origin.getZ();
      if (x1 <= x0 || y1 <= y0 || z1 <= z0) {
         return;
      }

      // Верх воды клэмпится в y1: при 100% воды и 100% пара вода иначе вылезла бы за полость
      double waterTop = Math.min(y1, y0 + (y1 - y0) * water);
      double steamTop = Math.min(y1, waterTop + (y1 - y0) * steam);

      VertexConsumer buffer = buffers.getBuffer(Sheets.translucentCullBlockSheet());
      if (waterTop - y0 > MIN_VISIBLE) {
         FluidVisual visual = visual(level, fluidOf(tank.waterTank.getFluid(), Fluids.WATER),
            inside(min, max, origin, y0, waterTop));
         if (visual != null) {
            box(poseStack, buffer, level, visual, x0, y0, z0, x1, waterTop, z1);
         }
      }
      if (steamTop - waterTop > MIN_VISIBLE) {
         FluidVisual visual = visual(level, fluidOf(tank.steamTank.getFluid(), IRFluids.STEAM.getFluidStill()),
            inside(min, max, origin, waterTop, steamTop));
         if (visual != null) {
            box(poseStack, buffer, level, visual, x0, waterTop, z0, x1, steamTop, z1);
         }
      }
   }

   /**
    * Блок ВНУТРИ слоя жидкости: по нему берутся биомный тин и свет. Переданный в
    * {@code render} {@code packedLight} не годится — он посчитан в позиции клапана, то есть на стенке,
    * и в глубине бака давал бы неверную освещённость.
    */
   private static BlockPos inside(BlockPos min, BlockPos max, BlockPos origin, double bottomLocal, double topLocal) {
      int x = (min.getX() + max.getX()) / 2 + 1;
      int z = (min.getZ() + max.getZ()) / 2 + 1;
      // Середина слоя: у слоя в один блок это единственный его блок, у высокого — внутри объёма,
      // а не у верхней стенки. Клэмп на случай, если полость всего один блок в высоту
      double middle = (bottomLocal + topLocal) * 0.5 + origin.getY();
      int y = Mth.clamp((int)Math.floor(middle), min.getY(), max.getY());
      return new BlockPos(x, y, z);
   }

   /** Конкретная жидкость резервуара; если стак на клиенте ещё не пришёл — дефолт по слою. */
   private static Fluid fluidOf(FluidStack stack, Fluid fallback) {
      if (stack.isEmpty()) {
         return fallback;
      }
      return stack.getFluid();
   }

   /**
    * Собирает всё, что нужно для отрисовки одного слоя: спрайт текстуры, тин и свет.
    * @return {@code null}, если атлас ещё не собран и спрайта нет — рисовать нечего, но падать нельзя.
    */
   @Nullable
   private static FluidVisual visual(BlockAndTintGetter level, Fluid fluid, BlockPos inside) {
      FluidState fluidState = fluid.defaultFluidState();
      IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidState);

      // Только getTintColor(FluidState, ...): перегрузка со FluidStack вернула бы плоский цвет
      // без биома, и вода в океане была бы не синей
      int tint = extensions.getTintColor(fluidState, level, inside);
      ResourceLocation still = extensions.getStillTexture(fluidState, level, inside);
      if (still == null) {
         return null;
      }
      // FluidSpriteCache отдаёт still-спрайт напрямую. Через getFluidSprites нельзя: там ещё ищется
      // flowing-текстура, а у пара она null, а Map.getOrDefault(null, ...) кидает NPE
      TextureAtlasSprite sprite = FluidSpriteCache.getSprite(still);
      if (sprite == null) {
         return null;
      }
      return new FluidVisual(sprite, tint, light(level, inside));
   }

   /** Свет изнутри объёма: max(низ, верх), как в {@code LiquidBlockRenderer}. */
   private static int light(BlockAndTintGetter level, BlockPos pos) {
      int low = LevelRenderer.getLightColor(level, pos);
      int high = LevelRenderer.getLightColor(level, pos.above());
      int lowBlock = low & 0xFF;
      int highBlock = high & 0xFF;
      int lowSky = low >> 16 & 0xFF;
      int highSky = high >> 16 & 0xFF;
      return Math.max(lowBlock, highBlock) | Math.max(lowSky, highSky) << 16;
   }

   /**
    * Куб из 6 граней, по 4 вершины на грань. Именно QUADS: только они сортируются в
    * {@code MultiBufferSource.endBatch} на прозрачной фазе, а TRIANGLE_STRIP отсортировать нельзя.
    */
   private static void box(PoseStack pose, VertexConsumer buffer, BlockAndTintGetter level, FluidVisual visual,
                          double x0, double y0, double z0, double x1, double y1, double z1) {
      double ax = x0 + INSET;
      double ay = y0 + INSET;
      double az = z0 + INSET;
      double bx = x1 - INSET;
      double by = y1 - INSET;
      double bz = z1 - INSET;
      if (bx <= ax || by <= ay || bz <= az) {
         return;
      }

      face(pose, buffer, level, visual, UV_XZ, Direction.UP,
         ax, by, bz, bx, by, bz, bx, by, az, ax, by, az);
      face(pose, buffer, level, visual, UV_XZ, Direction.DOWN,
         ax, ay, az, bx, ay, az, bx, ay, bz, ax, ay, bz);
      face(pose, buffer, level, visual, UV_XY, Direction.SOUTH,
         ax, ay, bz, bx, ay, bz, bx, by, bz, ax, by, bz);
      face(pose, buffer, level, visual, UV_XY, Direction.NORTH,
         bx, ay, az, ax, ay, az, ax, by, az, bx, by, az);
      face(pose, buffer, level, visual, UV_ZY, Direction.EAST,
         bx, ay, bz, bx, ay, az, bx, by, az, bx, by, bz);
      face(pose, buffer, level, visual, UV_ZY, Direction.WEST,
         ax, ay, az, ax, ay, bz, ax, by, bz, ax, by, az);
   }

   /**
    * Одна грань: 4 вершины по часовой стрелке снаружи (порядок важен для back-face culling).
    * На каждой вершине обязателен полный набор атрибутов — без {@code setLight} шейдер
    * {@code rendertype_entity_translucent_cull.vsh} читает {@code texelFetch(Sampler2, UV2 / 16, 0)}
    * из неинициализированного UV2 и красит геометрию мусором.
    */
   private static void face(PoseStack pose, VertexConsumer buffer, BlockAndTintGetter level, FluidVisual visual, int uvMode,
                            Direction normal,
                            double px, double py, double pz, double qx, double qy, double qz,
                            double rx, double ry, double rz, double sx, double sy, double sz) {
      double[][] corners = {{px, py, pz}, {qx, qy, qz}, {rx, ry, rz}, {sx, sy, sz}};
      PoseStack.Pose pose0 = pose.last();
      float shade = level.getShade(normal, true);
      float red = visual.red() * shade;
      float green = visual.green() * shade;
      float blue = visual.blue() * shade;
      float nx = normal.getStepX();
      float ny = normal.getStepY();
      float nz = normal.getStepZ();
      for (double[] corner : corners) {
         double x = corner[0];
         double y = corner[1];
         double z = corner[2];
         float u;
         float v;
         if (uvMode == UV_XZ) {
            u = visual.u(x);
            v = visual.v(z);
         } else if (uvMode == UV_XY) {
            u = visual.u(x);
            v = visual.v(y);
         } else {
            u = visual.u(z);
            v = visual.v(y);
         }
         buffer.addVertex(pose0, (float)x, (float)y, (float)z)
            .setColor(red, green, blue, visual.alpha())
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(visual.light())
            .setNormal(pose0, nx, ny, nz);
      }
   }

   @Override
   public boolean shouldRenderOffScreen(@Nullable TankValveBlockEntity tank) {
      // Границы резервуара всегда за пределами блока мастера
      return true;
   }

   /**
    * По умолчанию рендер отсекается по AABB одного блока, а бак шириной до 8 блоков — это дыры
    * на краях объёма. Отдаём настоящий объём резервуара.
    */
   @Override
   public AABB getRenderBoundingBox(TankValveBlockEntity tank) {
      BlockPos min = tank.getMinPos();
      BlockPos max = tank.getMaxPos();
      if (min == null || max == null) {
         return new AABB(tank.getBlockPos());
      }
      // min/max включительные, поэтому верхняя грань бака — это max + 1
      return new AABB(min.getX(), min.getY(), min.getZ(), max.getX() + 1.0, max.getY() + 1.0, max.getZ() + 1.0);
   }

   private static float clamp01(float value) {
      if (Float.isNaN(value)) {
         return 0.0f;
      }
      return Math.max(0.0f, Math.min(1.0f, value));
   }

   /**
    * Всё, что постоянно для слоя на кадр: окно спрайта в атласе, распакованный тин ARGB и упакованный свет.
    * Окно спрайта не меняется от кадра к кадру — {@code TextureAtlas.cycleAnimationFrames} перезаливает
    * очередной кадр в ту же область атласа, поэтому анимация приезжает сама.
    */
   private record FluidVisual(TextureAtlasSprite sprite, float red, float green, float blue, float alpha, int light) {
      private FluidVisual(TextureAtlasSprite sprite, int tint, int light) {
         this(
            sprite,
            (tint >> 16 & 0xFF) / 255.0F,
            (tint >> 8 & 0xFF) / 255.0F,
            (tint & 0xFF) / 255.0F,
            (tint >> 24 & 0xFF) / 255.0F,
            light
         );
      }

      /** Координата в окне спрайта, повторяющаяся каждый блок, чтобы текстура не растягивалась на весь бак. */
      private float u(double blocks) {
         return this.sprite.getU(frac(blocks));
      }

      private float v(double blocks) {
         return this.sprite.getV(frac(blocks));
      }

      private static float frac(double blocks) {
         double f = blocks - Math.floor(blocks);
         return (float)f;
      }
   }
}