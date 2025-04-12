package com.fruityspikes.cosmic_voyage.server.worldgen.features;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class PillarFeature extends Feature<NoneFeatureConfiguration> {
    // Proportionality constants
    private static final float HEIGHT_TO_WIDTH_RATIO = 3.5f;
    private static final int MIN_DIAMETER = 3;
    private static final int MAX_DIAMETER = 9;
    private static final float FLARE_RATIO = 1.8f; // Top diameter multiplier

    public PillarFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

//        BlockPos surfacePos = findAcidSurface(level, origin);
//        if (surfacePos == null) return false;

        int diameter = 2 + random.nextInt(3);
        int height = (int)(diameter * HEIGHT_TO_WIDTH_RATIO) + random.nextInt(10);
        int flareDiameter = (int)(diameter * FLARE_RATIO*2);
        int flareHeight = height / 3;

        // Generate pillar
        generatePillar(level, origin, diameter, flareDiameter, height, flareHeight, random);
        return true;
    }

    private void generatePillar(WorldGenLevel level, BlockPos surfacePos, int baseDiameter, int topDiameter, int height, int flareHeight, RandomSource random) {
        generateSection(level, surfacePos.offset(0,height,0), baseDiameter, topDiameter, flareHeight, true, random);

        //if (height > flareHeight) {
          generateSection(level, surfacePos, baseDiameter, baseDiameter, height, true, random);
        //}

        generateUndergroundColumn(level, surfacePos.below(), baseDiameter);
    }

    private void generateSection(WorldGenLevel level, BlockPos basePos, int baseDiam, int topDiam, int height, boolean upward, RandomSource random) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int y = 0; y < height; y++) {
            float progress = ((float)y / height)*1.5F;
            int currentDiam = (int) Mth.lerp(progress, baseDiam, topDiam);
            int radius = currentDiam / 2;

            int yPos = upward ? basePos.getY() + y : basePos.getY() - y;

            // Generate circular layer
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + z*z <= radius*radius) { // Slightly organic shape
                        mutablePos.set(
                                basePos.getX() + x,
                                yPos,
                                basePos.getZ() + z
                        );

                        if (canReplace(level.getBlockState(mutablePos))) {
                            level.setBlock(mutablePos, getPillarMaterial(mutablePos, random), 2);
                        }
                    }
                }
            }
        }
    }

    private void generateUndergroundColumn(WorldGenLevel level, BlockPos topPos, int diameter) {
        int radius = diameter / 2;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int depth = 0;

        while (depth < 128) { // Max depth
            boolean placedAny = false;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x*x + z*z <= radius*radius) {
                        mutablePos.set(
                                topPos.getX() + x,
                                topPos.getY() - depth,
                                topPos.getZ() + z
                        );

                        BlockState current = level.getBlockState(mutablePos);
                        if (canReplace(current)) {
                            // Get more stone-like as we go deeper
                            BlockState material = Blocks.DEEPSLATE.defaultBlockState();
                            level.setBlock(mutablePos, material, 2);
                            placedAny = true;
                        }
                    }
                }
            }

            if (!placedAny) break;
            depth++;
        }
    }

    private BlockState getPillarMaterial(BlockPos pos, RandomSource random) {
        // 80% chance main material, 20% chance variant
        return Blocks.CALCITE.defaultBlockState();
    }

//    private BlockPos findAcidSurface(WorldGenLevel level, BlockPos startPos) {
//        for (int y = startPos.getY(); y < level.getMaxBuildHeight(); y++) {
//            BlockPos pos = new BlockPos(startPos.getX(), y, startPos.getZ());
//            if (isAcidSurface(level, pos)) {
//                return pos;
//            }
//        }
//        return null;
//    }

//    private boolean isAcidSurface(WorldGenLevel level, BlockPos pos) {
//        return level.getBlockState(pos).isAir() &&
//                level.getBlockState(pos.below()).getFluidState().is(CVFluidRegistry.ACID_STILL.get());
//    }

    private boolean canReplace(BlockState state) {
//        return state.isAir() ||
//                state.getFluidState().is(CVFluidRegistry.ACID_STILL.get()) ||
//                state.getFluidState().is(CVFluidRegistry.ACID_FLOWING.get()) ||
//                state.is(Blocks.STONE) || // Can replace some stone underground
//                state.is(Blocks.DEEPSLATE);
        return true;
    }
}