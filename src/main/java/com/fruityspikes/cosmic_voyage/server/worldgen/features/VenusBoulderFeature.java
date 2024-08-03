package com.fruityspikes.cosmic_voyage.server.worldgen.features;

import com.fruityspikes.cosmic_voyage.server.registries.CVBlockRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class VenusBoulderFeature extends Feature<NoneFeatureConfiguration> implements FeatureConfiguration {
    private static final BlockState YELLOW_WOOL = CVBlockRegistry.SULFUR_DEPOSIT.get().defaultBlockState();
    private static final BlockState ORANGE_WOOL = CVBlockRegistry.DARK_SULFUR_DEPOSIT.get().defaultBlockState();
    private static final BlockState STONE = CVBlockRegistry.SEDIMENT.get().defaultBlockState();
    public VenusBoulderFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        LevelAccessor world = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        SimplexNoise noise = new SimplexNoise(random);

        if (world.getBlockState(pos.offset(0,-1,0))==Blocks.AIR.defaultBlockState()) {
            return false;
        }
            // Replace solid blocks within a spherical radius of 5 blocks with yellow wool
        int radius = 4 + random.nextInt(5);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos blockPos = pos.offset(x, y, z);
                    if (blockPos.distSqr(pos) <= radius * radius) {
                        if (world.getBlockState(blockPos).isSolid()) {
                            world.setBlock(blockPos, YELLOW_WOOL, 3);
                        }
                    }
                }
            }
        }

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos blockPos = pos.offset(x, y, z);
                    double distance = blockPos.distSqr(pos);
                    if (distance >= (radius - 0.7) * (radius - 0.7) && distance <= (radius + 0.7) * (radius + 0.7)) {
                        if (world.getBlockState(blockPos).isSolid() && world.getBlockState(blockPos)!=YELLOW_WOOL) {
                            world.setBlock(blockPos, ORANGE_WOOL, 3);
                        }
                    }
                }
            }
        }
        radius = radius-3;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos blockPos = pos.offset(x, y, z);
                    double distance = blockPos.distSqr(pos);
                    if (distance >= (radius - 0.6) * (radius - 0.6) && distance <= (radius + 0.6) * (radius + 0.6)) {
                        if (world.getBlockState(blockPos).isSolid()) {
                            world.setBlock(blockPos, ORANGE_WOOL, 3);
                        }
                    }
                }
            }
        }

        // Create a stone boulder in the middle
        createStoneBoulder(world, pos, noise);

        return true;
    }

    private void createStoneBoulder(LevelAccessor world, BlockPos center, SimplexNoise random) {
        int boulderRadius = 3;
        for (int x = -boulderRadius; x <= boulderRadius; x++) {
            for (int y = -boulderRadius; y <= boulderRadius; y++) {
                for (int z = -boulderRadius; z <= boulderRadius; z++) {
                    BlockPos blockPos = center.offset(x, y, z);
                    double noiseValue = random.getValue(blockPos.getX() * 0.1, blockPos.getY() * 0.1, blockPos.getZ() * 0.1);
                    if (x * x + y * y + z * z <= boulderRadius * boulderRadius * (0.5 + 0.5 * noiseValue)) {
                        world.setBlock(blockPos, STONE, 3);
                    }
                }
            }
        }
    }
}