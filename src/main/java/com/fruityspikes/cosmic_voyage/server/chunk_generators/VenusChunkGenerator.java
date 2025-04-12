package com.fruityspikes.cosmic_voyage.server.chunk_generators;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.registries.CVBlockRegistry;
import com.fruityspikes.cosmic_voyage.server.registries.CVFluidRegistry;
import com.fruityspikes.cosmic_voyage.server.util.VoronoiNoise;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class VenusChunkGenerator extends NoiseBasedChunkGenerator {
    private final Supplier<VoronoiNoise> voronoiNoise = Suppliers.memoize(() ->
            new VoronoiNoise(12345L, (short) 0)
    );
    private final List<DeferredHolder<Fluid, Fluid>> acidVariants;

    public static final MapCodec<VenusChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((p_255585_) -> {
        return p_255585_.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((p_255584_) -> {
            return p_255584_.biomeSource;
        }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((p_224278_) -> {
            return p_224278_.settings;
        })).apply(p_255585_, p_255585_.stable(VenusChunkGenerator::new));
    });
    private final Holder<NoiseGeneratorSettings> settings;
    private static final int QUARRY_DEPTH = 120;
    private static final int STRATA_COUNT = 8;
    private final SimplexNoise strataNoise;
    public VenusChunkGenerator(BiomeSource p_256415_, Holder<NoiseGeneratorSettings> p_256182_) {
        super(p_256415_, p_256182_);
        this.settings = p_256182_;
        this.acidVariants = CVFluidRegistry.ACID_FLUIDS_STILL.values().stream().toList();
        this.strataNoise = new SimplexNoise(RandomSource.create(1546786));
    }
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunk) {
        return super.fillFromNoise(blender, randomState, structureManager, chunk).thenApply((chunkAccess) -> {
            ChunkPos chunkPos = chunkAccess.getPos();

            for (int y = chunkAccess.getMinBuildHeight(); y < chunkAccess.getMaxBuildHeight(); y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = new BlockPos(
                                chunkPos.getMinBlockX() + x,
                                y,
                                chunkPos.getMinBlockZ() + z
                        );

                        if (shouldReplaceWithAcid(chunkAccess, pos)) {
                            Fluid selectedAcid = selectAcidVariant(pos);
                            chunkAccess.setBlockState(
                                    pos,
                                    selectedAcid.defaultFluidState().createLegacyBlock(),
                                    false
                            );
                        }
                    }
                }
            }
            return chunkAccess;
        });
    }

    private boolean shouldReplaceWithAcid(ChunkAccess chunkAccess, BlockPos pos) {
        BlockState current = chunkAccess.getBlockState(pos);
        return (current.is(Blocks.WATER));
    }

    private Fluid selectAcidVariant(BlockPos pos) {
        double cellSize = 5.0;
        VoronoiNoise.CellResult cell = voronoiNoise.get().getCell(
                pos.getX(),
                pos.getZ(),
                1.0 / cellSize
        );

        int variantIndex = (int)(Math.abs(cell.cellId()) % acidVariants.size());
        return acidVariants.get(variantIndex).get();
    }
}