package com.fruityspikes.cosmic_voyage.server.worldgen.placement_modifiers;

import com.fruityspikes.cosmic_voyage.server.registries.CVPlacementModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class PeriodicPlacement extends PlacementModifier {
    public static final MapCodec<PeriodicPlacement> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(
                Codec.INT.fieldOf("x").forGetter((placement) -> placement.x),
                Codec.INT.fieldOf("y").forGetter((placement) -> placement.y),
                Codec.INT.fieldOf("spacing").forGetter((placement) -> placement.spacing)
        ).apply(instance, PeriodicPlacement::new);
    });

    private final int x;
    private final int y;
    private final int spacing;

    public PeriodicPlacement(int x, int y, int spacing) {
        this.x = x;
        this.y = y;
        this.spacing = spacing;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int currentChunkX = SectionPos.blockToSectionCoord(pos.getX());
        int currentChunkZ = SectionPos.blockToSectionCoord(pos.getZ());

        if (currentChunkX == x && currentChunkZ % spacing == 0) {
            int centerX = SectionPos.sectionToBlockCoord(x, 8);
            int centerZ = SectionPos.sectionToBlockCoord(currentChunkZ, 8);

            WorldGenRegion region = (WorldGenRegion)context.getLevel();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (!region.hasChunk(x + dx, currentChunkZ + dz)) {
                        return Stream.empty();
                    }
                }
            }

            return Stream.of(new BlockPos(centerX, y, centerZ));
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return CVPlacementModifierTypes.PERIODIC_PLACEMENT.get();
    }
}