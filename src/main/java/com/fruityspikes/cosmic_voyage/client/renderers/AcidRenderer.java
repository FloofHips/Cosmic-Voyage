package com.fruityspikes.cosmic_voyage.client.renderers;

import com.fruityspikes.cosmic_voyage.server.blocks.AcidBlock;
import com.fruityspikes.cosmic_voyage.server.registries.CVFluidRegistry;
import com.fruityspikes.cosmic_voyage.server.util.CVConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;

import java.util.Iterator;

public class AcidRenderer {
    static TagKey<Fluid> acidTag = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("cosmic_voyage", "acids"));

    private static final float MAX_FLUID_HEIGHT = 0.8888889F;

    public AcidRenderer() {
    }

    private static boolean isNeighborSameFluid(FluidState pSecondState) {
        return pSecondState.is(acidTag);
    }

    private static boolean isFaceOccludedByState(BlockGetter pLevel, Direction pFace, float pHeight, BlockPos pPos, BlockState pState) {
        if (pState.canOcclude()) {
            VoxelShape voxelshape = Shapes.box(0.0, 0.0, 0.0, 1.0, (double)pHeight, 1.0);
            VoxelShape voxelshape1 = pState.getOcclusionShape(pLevel, pPos);
            return Shapes.blockOccudes(voxelshape, voxelshape1, pFace);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter pLevel, BlockPos pPos, Direction pSide, float pHeight, BlockState pBlockState) {
        return isFaceOccludedByState(pLevel, pSide, pHeight, pPos.relative(pSide), pBlockState);
    }

    private static boolean isFaceOccludedBySelf(BlockGetter pLevel, BlockPos pPos, BlockState pState, Direction pFace) {
        return isFaceOccludedByState(pLevel, pFace.getOpposite(), 1.0F, pPos, pState);
    }

    public static boolean shouldRenderFace(BlockAndTintGetter pLevel, BlockPos pPos, FluidState pFluidState, BlockState pBlockState, Direction pSide, FluidState pNeighborFluid) {
        return !isFaceOccludedBySelf(pLevel, pPos, pBlockState, pSide) && !isNeighborSameFluid(pNeighborFluid);
    }

    public void tesselate(BlockAndTintGetter pLevel, BlockPos pPos, VertexConsumer pBuffer, BlockState pBlockState, FluidState pFluidState) {
        boolean flag = pFluidState.is(FluidTags.LAVA);
        TextureAtlasSprite[] atextureatlassprite = FluidSpriteCache.getFluidSprites(pLevel, pPos, pFluidState);
        int i = IClientFluidTypeExtensions.of(pFluidState).getTintColor(pFluidState, pLevel, pPos);
        float alpha = (float)(i >> 24 & 255) / 255.0F;
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;
        BlockState blockstate = pLevel.getBlockState(pPos.relative(Direction.DOWN));
        FluidState fluidstate = blockstate.getFluidState();
        BlockState blockstate1 = pLevel.getBlockState(pPos.relative(Direction.UP));
        FluidState fluidstate1 = blockstate1.getFluidState();
        BlockState blockstate2 = pLevel.getBlockState(pPos.relative(Direction.NORTH));
        FluidState fluidstate2 = blockstate2.getFluidState();
        BlockState blockstate3 = pLevel.getBlockState(pPos.relative(Direction.SOUTH));
        FluidState fluidstate3 = blockstate3.getFluidState();
        BlockState blockstate4 = pLevel.getBlockState(pPos.relative(Direction.WEST));
        FluidState fluidstate4 = blockstate4.getFluidState();
        BlockState blockstate5 = pLevel.getBlockState(pPos.relative(Direction.EAST));
        FluidState fluidstate5 = blockstate5.getFluidState();
        boolean flag1 = !isNeighborSameFluid(fluidstate1);
        boolean flag2 = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.DOWN, fluidstate) && !isFaceOccludedByNeighbor(pLevel, pPos, Direction.DOWN, 0.8888889F, blockstate);
        boolean flag3 = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.NORTH, fluidstate2);
        boolean flag4 = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.SOUTH, fluidstate3);
        boolean flag5 = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.WEST, fluidstate4);
        boolean flag6 = shouldRenderFace(pLevel, pPos, pFluidState, pBlockState, Direction.EAST, fluidstate5);
        if (flag1 || flag2 || flag6 || flag5 || flag3 || flag4) {
            float f3 = pLevel.getShade(Direction.DOWN, true);
            float f4 = pLevel.getShade(Direction.UP, true);
            float f5 = pLevel.getShade(Direction.NORTH, true);
            float f6 = pLevel.getShade(Direction.WEST, true);
            Fluid fluid = pFluidState.getType();
            float f11 = this.getHeight(pLevel, fluid, pPos, pBlockState, pFluidState);
            float f7;
            float f8;
            float f9;
            float f10;
            float f36;
            float f37;
            float f38;
            float f39;
            if (f11 >= 1.0F) {
                f7 = 1.0F;
                f8 = 1.0F;
                f9 = 1.0F;
                f10 = 1.0F;
            } else {
                f36 = this.getHeight(pLevel, fluid, pPos.north(), blockstate2, fluidstate2);
                f37 = this.getHeight(pLevel, fluid, pPos.south(), blockstate3, fluidstate3);
                f38 = this.getHeight(pLevel, fluid, pPos.east(), blockstate5, fluidstate5);
                f39 = this.getHeight(pLevel, fluid, pPos.west(), blockstate4, fluidstate4);
                f7 = this.calculateAverageHeight(pLevel, fluid, f11, f36, f38, pPos.relative(Direction.NORTH).relative(Direction.EAST));
                f8 = this.calculateAverageHeight(pLevel, fluid, f11, f36, f39, pPos.relative(Direction.NORTH).relative(Direction.WEST));
                f9 = this.calculateAverageHeight(pLevel, fluid, f11, f37, f38, pPos.relative(Direction.SOUTH).relative(Direction.EAST));
                f10 = this.calculateAverageHeight(pLevel, fluid, f11, f37, f39, pPos.relative(Direction.SOUTH).relative(Direction.WEST));
            }

            f36 = (float)(pPos.getX() & 15);
            f37 = (float)(pPos.getY() & 15);
            f38 = (float)(pPos.getZ() & 15);
            f39 = 0.001F;
            float f16 = flag2 ? 0.001F : 0.0F;
            float f17;
            float f18;
            float f44;
            float f45;
            float f47;
            float f49;
            float f51;
            float f52;
            float f56;
            float f58;
            float f59;
            float f60;
            if (flag1 && !isFaceOccludedByNeighbor(pLevel, pPos, Direction.UP, Math.min(Math.min(f8, f10), Math.min(f9, f7)), blockstate1)) {
                f8 -= 0.001F;
                f10 -= 0.001F;
                f9 -= 0.001F;
                f7 -= 0.001F;
                Vec3 vec3 = pFluidState.getFlow(pLevel, pPos);
                TextureAtlasSprite textureatlassprite;
                float f54;
                float f55;
                if (vec3.x == 0.0 && vec3.z == 0.0) {
                    textureatlassprite = atextureatlassprite[0];
                    f17 = textureatlassprite.getU(0.0F);
                    f47 = textureatlassprite.getV(0.0F);
                    f18 = f17;
                    f49 = textureatlassprite.getV(1.0F);
                    f44 = textureatlassprite.getU(1.0F);
                    f51 = f49;
                    f45 = f44;
                    f52 = f47;
                } else {
                    textureatlassprite = atextureatlassprite[1];
                    f54 = (float) Mth.atan2(vec3.z, vec3.x) - 1.5707964F;
                    f55 = Mth.sin(f54) * 0.25F;
                    f56 = Mth.cos(f54) * 0.25F;
                    f58 = 0.5F;
                    f17 = textureatlassprite.getU(0.5F + (-f56 - f55));
                    f47 = textureatlassprite.getV(0.5F + -f56 + f55);
                    f18 = textureatlassprite.getU(0.5F + -f56 + f55);
                    f49 = textureatlassprite.getV(0.5F + f56 + f55);
                    f44 = textureatlassprite.getU(0.5F + f56 + f55);
                    f51 = textureatlassprite.getV(0.5F + (f56 - f55));
                    f45 = textureatlassprite.getU(0.5F + (f56 - f55));
                    f52 = textureatlassprite.getV(0.5F + (-f56 - f55));
                }

                float f53 = (f17 + f18 + f44 + f45) / 4.0F;
                f54 = (f47 + f49 + f51 + f52) / 4.0F;
                f55 = atextureatlassprite[0].uvShrinkRatio();
                f17 = Mth.lerp(f55, f17, f53);
                f18 = Mth.lerp(f55, f18, f53);
                f44 = Mth.lerp(f55, f44, f53);
                f45 = Mth.lerp(f55, f45, f53);
                f47 = Mth.lerp(f55, f47, f54);
                f49 = Mth.lerp(f55, f49, f54);
                f51 = Mth.lerp(f55, f51, f54);
                f52 = Mth.lerp(f55, f52, f54);
                int l = this.getLightColor(pLevel, pPos);
                f58 = f4 * f;
                f59 = f4 * f1;
                f60 = f4 * f2;
                this.vertex(pBuffer, f36 + 0.0F, f37 + f8, f38 + 0.0F, f58, f59, f60, alpha, f17, f47, l);
                this.vertex(pBuffer, f36 + 0.0F, f37 + f10, f38 + 1.0F, f58, f59, f60, alpha, f18, f49, l);
                this.vertex(pBuffer, f36 + 1.0F, f37 + f9, f38 + 1.0F, f58, f59, f60, alpha, f44, f51, l);
                this.vertex(pBuffer, f36 + 1.0F, f37 + f7, f38 + 0.0F, f58, f59, f60, alpha, f45, f52, l);
                if (pFluidState.shouldRenderBackwardUpFace(pLevel, pPos.above())) {
                    this.vertex(pBuffer, f36 + 0.0F, f37 + f8, f38 + 0.0F, f58, f59, f60, alpha, f17, f47, l);
                    this.vertex(pBuffer, f36 + 1.0F, f37 + f7, f38 + 0.0F, f58, f59, f60, alpha, f45, f52, l);
                    this.vertex(pBuffer, f36 + 1.0F, f37 + f9, f38 + 1.0F, f58, f59, f60, alpha, f44, f51, l);
                    this.vertex(pBuffer, f36 + 0.0F, f37 + f10, f38 + 1.0F, f58, f59, f60, alpha, f18, f49, l);
                }
            }

            if (flag2) {
                float f40 = atextureatlassprite[0].getU0();
                f17 = atextureatlassprite[0].getU1();
                f18 = atextureatlassprite[0].getV0();
                f44 = atextureatlassprite[0].getV1();
                int k = this.getLightColor(pLevel, pPos.below());
                f47 = f3 * f;
                f49 = f3 * f1;
                f51 = f3 * f2;
                this.vertex(pBuffer, f36, f37 + f16, f38 + 1.0F, f47, f49, f51, alpha, f40, f44, k);
                this.vertex(pBuffer, f36, f37 + f16, f38, f47, f49, f51, alpha, f40, f18, k);
                this.vertex(pBuffer, f36 + 1.0F, f37 + f16, f38, f47, f49, f51, alpha, f17, f18, k);
                this.vertex(pBuffer, f36 + 1.0F, f37 + f16, f38 + 1.0F, f47, f49, f51, alpha, f17, f44, k);
            }

            int j = this.getLightColor(pLevel, pPos);
            Iterator var69 = Direction.Plane.HORIZONTAL.iterator();

            while(var69.hasNext()) {
                Direction direction = (Direction)var69.next();
                boolean flag7;
                switch (direction) {
                    case NORTH:
                        f44 = f8;
                        f45 = f7;
                        f47 = f36;
                        f51 = f36 + 1.0F;
                        f49 = f38 + 0.001F;
                        f52 = f38 + 0.001F;
                        flag7 = flag3;
                        break;
                    case SOUTH:
                        f44 = f9;
                        f45 = f10;
                        f47 = f36 + 1.0F;
                        f51 = f36;
                        f49 = f38 + 1.0F - 0.001F;
                        f52 = f38 + 1.0F - 0.001F;
                        flag7 = flag4;
                        break;
                    case WEST:
                        f44 = f10;
                        f45 = f8;
                        f47 = f36 + 0.001F;
                        f51 = f36 + 0.001F;
                        f49 = f38 + 1.0F;
                        f52 = f38;
                        flag7 = flag5;
                        break;
                    default:
                        f44 = f7;
                        f45 = f9;
                        f47 = f36 + 1.0F - 0.001F;
                        f51 = f36 + 1.0F - 0.001F;
                        f49 = f38;
                        f52 = f38 + 1.0F;
                        flag7 = flag6;
                }

                if (flag7 && !isFaceOccludedByNeighbor(pLevel, pPos, direction, Math.max(f44, f45), pLevel.getBlockState(pPos.relative(direction)))) {
                    BlockPos blockpos = pPos.relative(direction);
                    TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
                    if (atextureatlassprite[2] != null && pLevel.getBlockState(blockpos).shouldDisplayFluidOverlay(pLevel, blockpos, pFluidState)) {
                        textureatlassprite2 = atextureatlassprite[2];
                    }

                    f56 = textureatlassprite2.getU(0.0F);
                    f58 = textureatlassprite2.getU(0.5F);
                    f59 = textureatlassprite2.getV((1.0F - f44) * 0.5F);
                    f60 = textureatlassprite2.getV((1.0F - f45) * 0.5F);
                    float f31 = textureatlassprite2.getV(0.5F);
                    float f32 = direction.getAxis() == Direction.Axis.Z ? f5 : f6;
                    float f33 = f4 * f32 * f;
                    float f34 = f4 * f32 * f1;
                    float f35 = f4 * f32 * f2;
                    this.vertex(pBuffer, f47, f37 + f44, f49, f33, f34, f35, alpha, f56, f59, j);
                    this.vertex(pBuffer, f51, f37 + f45, f52, f33, f34, f35, alpha, f58, f60, j);
                    this.vertex(pBuffer, f51, f37 + f16, f52, f33, f34, f35, alpha, f58, f31, j);
                    this.vertex(pBuffer, f47, f37 + f16, f49, f33, f34, f35, alpha, f56, f31, j);
                    if (textureatlassprite2 != atextureatlassprite[2]) {
                        this.vertex(pBuffer, f47, f37 + f16, f49, f33, f34, f35, alpha, f56, f31, j);
                        this.vertex(pBuffer, f51, f37 + f16, f52, f33, f34, f35, alpha, f58, f31, j);
                        this.vertex(pBuffer, f51, f37 + f45, f52, f33, f34, f35, alpha, f58, f60, j);
                        this.vertex(pBuffer, f47, f37 + f44, f49, f33, f34, f35, alpha, f56, f59, j);
                    }
                }
            }
        }

    }

    private float calculateAverageHeight(BlockAndTintGetter pLevel, Fluid pFluid, float pCurrentHeight, float pHeight1, float pHeight2, BlockPos pPos) {
        if (!(pHeight2 >= 1.0F) && !(pHeight1 >= 1.0F)) {
            float[] afloat = new float[2];
            if (pHeight2 > 0.0F || pHeight1 > 0.0F) {
                float f = this.getHeight(pLevel, pFluid, pPos);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                this.addWeightedHeight(afloat, f);
            }

            this.addWeightedHeight(afloat, pCurrentHeight);
            this.addWeightedHeight(afloat, pHeight2);
            this.addWeightedHeight(afloat, pHeight1);
            return afloat[0] / afloat[1];
        } else {
            return 1.0F;
        }
    }

    private void addWeightedHeight(float[] pOutput, float pHeight) {
        if (pHeight >= 0.8F) {
            pOutput[0] += pHeight * 10.0F;
            pOutput[1] += 10.0F;
        } else if (pHeight >= 0.0F) {
            pOutput[0] += pHeight;
            int var10002 = (int) pOutput[1]++;
        }

    }

    private float getHeight(BlockAndTintGetter pLevel, Fluid pFluid, BlockPos pPos) {
        BlockState blockstate = pLevel.getBlockState(pPos);
        return this.getHeight(pLevel, pFluid, pPos, blockstate, blockstate.getFluidState());
    }

    private void vertex(VertexConsumer p_110985_, float p_110989_, float p_110990_, float p_110991_, float p_110992_, float p_110993_, float p_350595_, float alpha, float p_350459_, float p_350437_, int p_110994_) {
        p_110985_.addVertex(p_110989_, p_110990_, p_110991_).setColor(p_110992_, p_110993_, p_350595_, alpha).setUv(p_350459_, p_350437_).setLight(p_110994_).setNormal(0.0F, 1.0F, 0.0F);
    }

    private boolean isAcid(Fluid pFluid){
        return pFluid.is(acidTag);
    }
    private float getHeight(BlockAndTintGetter pLevel, Fluid pFluid, BlockPos pPos, BlockState pBlockState, FluidState pFluidState) {
        if (isAcid(pFluidState.getType())) {
            BlockState blockstate = pLevel.getBlockState(pPos.above());
            return pFluid.isSame(blockstate.getFluidState().getType()) ? 1.0F : pFluidState.getOwnHeight();
        } else {
            return !pBlockState.isSolid() ? 0.0F : -1.0F;
        }
    }

    private void vertex(VertexConsumer pBuffer, float pX, float pY, float pZ, float pRed, float pGreen, float pBlue, float pU, float pV, int pPackedLight) {
        pBuffer.addVertex(pX, pY, pZ).setColor(pRed, pGreen, pBlue, 1.0F).setUv(pU, pV).setLight(pPackedLight).setNormal(0.0F, 1.0F, 0.0F);
    }

    private int getLightColor(BlockAndTintGetter pLevel, BlockPos pPos) {
        int i = LevelRenderer.getLightColor(pLevel, pPos);
        int j = LevelRenderer.getLightColor(pLevel, pPos.above());
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }
}