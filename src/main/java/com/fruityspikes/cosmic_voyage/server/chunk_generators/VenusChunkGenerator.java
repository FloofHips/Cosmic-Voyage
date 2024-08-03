package com.fruityspikes.cosmic_voyage.server.chunk_generators;

import com.fruityspikes.cosmic_voyage.server.registries.CVBlockRegistry;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class VenusChunkGenerator extends NoiseBasedChunkGenerator {
    private final PerlinSimplexNoise noiseGenerator;
    public static final MapCodec<VenusChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((p_255585_) -> {
        return p_255585_.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((p_255584_) -> {
            return p_255584_.biomeSource;
        }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((p_224278_) -> {
            return p_224278_.settings;
        })).apply(p_255585_, p_255585_.stable(VenusChunkGenerator::new));
    });
    private final Holder<NoiseGeneratorSettings> settings;
    public VenusChunkGenerator(BiomeSource p_256415_, Holder<NoiseGeneratorSettings> p_256182_) {
        super(p_256415_, p_256182_);
        this.settings = p_256182_;
        this.noiseGenerator = new PerlinSimplexNoise(RandomSource.create(), Collections.singletonList(3));
    }
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return super.fillFromNoise(blender, randomState, structureManager, chunk).thenApply((chunkAccess) -> {
            ChunkPos chunkPos = chunkAccess.getPos();
            int startX = chunkPos.getMinBlockX();
            int startZ = chunkPos.getMinBlockZ();

            for (int x = 0; x < 16; x++) {
                int globalX = startX + x;
                if (globalX >= 128 && globalX < 176) {
                    double noiseValue = noiseGenerator.getValue((startZ) * 0.001, (startZ) * 0.001, false);
                    for (int y = 128; y < 176; y++) {
                        for (int z = 0; z < 16; z++) {
                            BlockPos blockPos = new BlockPos(globalX, y, startZ + z);
                            BlockState blockState = getBlockStateFromNoise(noiseValue);
                            chunkAccess.setBlockState(blockPos, blockState, false);
                        }
                    }
                }
            }
            return chunkAccess;
        });
    }

    private BlockState getBlockStateFromNoise(double noiseValue) {
        // Map noise value to different wool colors
//        if (noiseValue < -0.5) {
//            return Blocks.RED_WOOL.defaultBlockState();
//        } else if (noiseValue < 0) {
//            return Blocks.ORANGE_WOOL.defaultBlockState();
//        } else if (noiseValue < 0.5) {
//            return Blocks.YELLOW_WOOL.defaultBlockState();
//        } else {
//            return Blocks.LIME_WOOL.defaultBlockState();
//        }
        return CVBlockRegistry.CENTIPEDE_INNER_WALL.get().defaultBlockState();
    }
}