package com.industrialreforged.tech.item.tool;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.item.energy.BasicEnergyItem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.industrialreforged.tech.api.tile.RetexturableBlock;
import com.industrialreforged.tech.blockentity.comp.Obscuration;
import com.industrialreforged.tech.core.SideProxyClient;
import com.industrialreforged.tech.core.ref.IRDataComponents;
import com.industrialreforged.tech.core.util.Util;
import com.industrialreforged.tech.network.ObscurationPacket;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ItemObscurator extends BasicEnergyItem {
   private final int SCAN_COST = 20000;
   private final int PRINT_COST = 5000;
   private static final int[] noTint = new int[]{-1};
   private static final int[] zeroTint = new int[]{0};
   private static final int[] defaultColorMultiplier = new int[]{16777215};
   private static final int[] colorMultiplierOpaqueWhite = new int[]{-1};

   public ItemObscurator(Properties properties) {
      super(properties.stacksTo(1).component(IRDataComponents.OBSCURATION, Obscuration.ObscurationData.EMPTY), 100000, 512);
   }

   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();
      Direction direction = context.getClickedFace();
      Player player = context.getPlayer();
      if (!player.isShiftKeyDown() && this.use(stack, 5000, true)) {
         if (level.isClientSide) {
            return InteractionResult.SUCCESS;
         }

         Obscuration.ObscurationData obscurationData = (Obscuration.ObscurationData)stack.get(IRDataComponents.OBSCURATION);
         if (obscurationData != null && !obscurationData.isEmpty()) {
            BlockState blockState2 = level.getBlockState(blockPos);
            if (blockState2.getBlock() instanceof RetexturableBlock retexturableBlock
               && retexturableBlock.retexture(blockState2, level, blockPos, direction, player, obscurationData)) {
               this.use(stack, 5000, false);
               return InteractionResult.CONSUME;
            } else {
               return InteractionResult.PASS;
            }
         } else {
            return InteractionResult.PASS;
         }
      } else if (!player.isShiftKeyDown() || !this.use(stack, 20000, true)) {
         return InteractionResult.PASS;
      } else if (!level.isClientSide) {
         return InteractionResult.CONSUME;
      } else {
         return this.scanBlock(stack, player, level, blockPos, direction) ? InteractionResult.SUCCESS : InteractionResult.PASS;
      }
   }

   private boolean scanBlock(ItemStack stack, Player player, Level level, BlockPos blockPos, Direction direction) {
      assert level.isClientSide;
      BlockState blockState = level.getBlockState(blockPos);
      if (blockState.isAir()) {
         return false;
      }

      ItemObscurator.ObscuredRenderInfo renderInfo = getRenderInfo(blockState, direction);
      if (renderInfo == null) {
         return false;
      }

      int[] colors = new int[renderInfo.tints.length];

      for (byte b = 0; b < renderInfo.tints.length; b++) {
         colors[b] = SideProxyClient.mc.getBlockColors().getColor(blockState, level, blockPos, renderInfo.tints[b]);
      }

      Obscuration.ObscurationData obscurationData = (Obscuration.ObscurationData)stack.get(IRDataComponents.OBSCURATION);
      Obscuration.ObscurationData obscurationData1 = new Obscuration.ObscurationData(blockState, direction, colors);
      if (obscurationData != null && !obscurationData.isEmpty() && obscurationData.equals(obscurationData1)) {
         return false;
      }

      IndustrialReforged.sendToServer(new ObscurationPacket(player.getInventory().selected, obscurationData1), new CustomPacketPayload[0]);
      return true;
   }

   public void writeObscuration(ItemStack itemStack, Obscuration.ObscurationData obscurationData) {
      if (this.use(itemStack, 20000, false)) {
         itemStack.set(IRDataComponents.OBSCURATION, obscurationData);
      }
   }

   public static ItemObscurator.ObscuredRenderInfo getRenderInfo(BlockState paramBlockState, Direction paramDirection) {
      if (ItemBlockRenderTypes.getChunkRenderType(paramBlockState) == RenderType.translucent()) {
         return null;
      }

      BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(paramBlockState);
      if (bakedModel == null) {
         return null;
      }

      List<BakedQuad> list = bakedModel.getQuads(paramBlockState, paramDirection, RandomSource.create(42L));
      if (list.isEmpty()) {
         return null;
      }

      float[] arrayOfFloat = new float[list.size() * 4];
      byte b = 0;
      int[] arrayOfInt = new int[list.size()];
      TextureAtlasSprite[] arrayOfTextureAtlasSprite = new TextureAtlasSprite[list.size()];

      for (BakedQuad bakedQuad : list) {
         ItemObscurator.ExtractingVertexConsumer consumer = new ItemObscurator.ExtractingVertexConsumer();
         consumer.setTexture(bakedQuad.getSprite());
         consumer.setQuadOrientation(bakedQuad.getDirection());
         if (bakedQuad.isTinted()) {
            consumer.setQuadTint(bakedQuad.getTintIndex());
         }

         consumer.setApplyDiffuseLighting(bakedQuad.isShade());
         double[] arrayOfFloat1 = consumer.positions();
         int i = paramDirection.getStepX();
         int j = paramDirection.getStepY();
         int k = paramDirection.getStepZ();
         int m = (i + 1) / 2;
         int n = (j + 1) / 2;
         int i1 = (k + 1) / 2;
         byte b1 = -1;

         for (byte b2 = 0; b2 < 4; b2++) {
            int i2 = b2 * 3;
            if (Util.isSimilar(arrayOfFloat1[i2], m) && Util.isSimilar(arrayOfFloat1[i2 + 1], n) && Util.isSimilar(arrayOfFloat1[i2 + 2], i1)) {
               b1 = b2;
               break;
            }
         }

         if (b1 != -1) {
            Vec3 vector31 = new Vec3(arrayOfFloat1[3] - arrayOfFloat1[0], arrayOfFloat1[4] - arrayOfFloat1[1], arrayOfFloat1[5] - arrayOfFloat1[2]);
            if (Util.isSimilar(vector31.lengthSqr(), 1.0)) {
               Vec3 vector32 = new Vec3(arrayOfFloat1[9] - arrayOfFloat1[0], arrayOfFloat1[10] - arrayOfFloat1[1], arrayOfFloat1[11] - arrayOfFloat1[2]);
               if (Util.isSimilar(vector32.lengthSqr(), 1.0)) {
                  Vec3 vector33 = new Vec3(arrayOfFloat1[9] - arrayOfFloat1[6], arrayOfFloat1[10] - arrayOfFloat1[7], arrayOfFloat1[11] - arrayOfFloat1[8]);
                  if (Util.isSimilar(vector33.add(vector31).lengthSqr(), 0.0)) {
                     Vec3 vector34 = vector31.cross(vector32);
                     if (Util.isSimilar(vector34.subtract(i, j, k).lengthSqr(), 0.0)) {
                        arrayOfInt[b / 4] = consumer.tint();
                        arrayOfTextureAtlasSprite[b / 4] = consumer.sprite();
                        float[] arrayOfFloat2 = consumer.uvs();
                        arrayOfFloat[b++] = arrayOfFloat2[b1 * 2];
                        arrayOfFloat[b++] = arrayOfFloat2[b1 * 2 + 1];
                        arrayOfFloat[b++] = arrayOfFloat2[(b1 + 2) % 4 * 2];
                        arrayOfFloat[b++] = arrayOfFloat2[(b1 + 2) % 4 * 2 + 1];
                     }
                  }
               }
            }
         }
      }

      if (b == 0) {
         return null;
      }

      if (b < arrayOfFloat.length) {
         arrayOfFloat = Arrays.copyOf(arrayOfFloat, b);
         arrayOfInt = Arrays.copyOf(arrayOfInt, b / 4);
      }

      arrayOfInt = internTints(arrayOfInt);
      return new ItemObscurator.ObscuredRenderInfo(arrayOfFloat, arrayOfInt, arrayOfTextureAtlasSprite);
   }

   public static int[] internTints(int[] paramArrayOfint) {
      if (paramArrayOfint.length == 1) {
         if (paramArrayOfint[0] == noTint[0]) {
            return noTint;
         }

         if (paramArrayOfint[0] == zeroTint[0]) {
            return zeroTint;
         }
      }

      return paramArrayOfint;
   }

   public static int[] internColorMultipliers(int[] paramArrayOfint) {
      if (paramArrayOfint.length == 1) {
         if (paramArrayOfint[0] == defaultColorMultiplier[0]) {
            return defaultColorMultiplier;
         }

         if (paramArrayOfint[0] == colorMultiplierOpaqueWhite[0]) {
            return colorMultiplierOpaqueWhite;
         }
      }

      return paramArrayOfint;
   }

   private static class ExtractingVertexConsumer implements VertexConsumer {
      private final double[] positions = new double[12];
      private int posIdx;
      private final float[] uvs = new float[8];
      private int uvIdx;
      private int tint = -1;
      private TextureAtlasSprite sprite;

      public double[] positions() {
         return this.positions;
      }

      public float[] uvs() {
         return this.uvs;
      }

      public int tint() {
         return this.tint;
      }

      public TextureAtlasSprite sprite() {
         return this.sprite;
      }

      public VertexFormat getVertexFormat() {
         return DefaultVertexFormat.POSITION_TEX;
      }

      public void setQuadTint(int n) {
         this.tint = n;
      }

      public void setQuadOrientation(Direction direction) {
      }

      public void setApplyDiffuseLighting(boolean bl) {
      }

      public void setTexture(TextureAtlasSprite textureAtlasSprite) {
         this.sprite = textureAtlasSprite;
      }

      public VertexConsumer addVertex(float p_85945_, float p_85946_, float p_85947_) {
         this.positions[this.posIdx++] = p_85945_;
         this.positions[this.posIdx++] = p_85946_;
         this.positions[this.posIdx++] = p_85947_;
         return this;
      }

      public VertexConsumer setColor(int p_85973_, int p_85974_, int p_85975_, int p_85976_) {
         return this;
      }

      public VertexConsumer setUv(float p_85948_, float p_85949_) {
         this.uvs[this.uvIdx++] = p_85948_;
         this.uvs[this.uvIdx++] = p_85949_;
         return this;
      }

      public VertexConsumer setUv1(int u, int v) {
         return this;
      }

      public VertexConsumer setUv2(int u, int v) {
         return this;
      }

      public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
         return this;
      }
   }

   public static class ObscuredRenderInfo {
      public final float[] uvs;
      public final int[] tints;
      public final TextureAtlasSprite[] sprites;

      private ObscuredRenderInfo(float[] param1ArrayOffloat, int[] param1ArrayOfint, TextureAtlasSprite[] param1ArrayOfTextureAtlasSprite) {
         this.uvs = param1ArrayOffloat;
         this.tints = param1ArrayOfint;
         this.sprites = param1ArrayOfTextureAtlasSprite;
      }
   }
}
