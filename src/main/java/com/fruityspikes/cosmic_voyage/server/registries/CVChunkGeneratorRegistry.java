package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.chunk_generators.VenusChunkGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVChunkGeneratorRegistry {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENS = DeferredRegister.create(
            BuiltInRegistries.CHUNK_GENERATOR,
            CosmicVoyage.MODID
    );
    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>,
            MapCodec<VenusChunkGenerator>> VENUS_CHUNK_GENERATOR = CHUNK_GENS.register("venus", () -> VenusChunkGenerator.CODEC
    );
}
