package com.fruityspikes.cosmic_voyage.server.worldgen.placement_modifiers;

import com.fruityspikes.cosmic_voyage.server.registries.CVPlacementModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
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
                Codec.INT.fieldOf("offset").forGetter((placement) -> placement.offset)
        ).apply(instance, PeriodicPlacement::new);
    });

    private final int x;
    private final int y;
    private final int offset;
    public PeriodicPlacement(int x, int y, int offset) {
        this.x = x;
        this.y = y;
        this.offset = offset;
    }
    @Override
    public Stream<BlockPos> getPositions(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos) {
        int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());

        if (!placementContext.getLevel().hasChunk(chunkX, chunkZ)) {
            return Stream.empty();
        }

        if (chunkX % offset == 0 && chunkZ % offset == 0) {
            int worldX = SectionPos.sectionToBlockCoord(chunkX, 0) + x;
            int worldY = y;
            int worldZ = SectionPos.sectionToBlockCoord(chunkZ, 0);

            return Stream.of(new BlockPos(worldX, worldY, worldZ));
        }

        return Stream.empty();
    }


    @Override
    public PlacementModifierType<?> type() {
        return CVPlacementModifierTypes.PERIODIC_PLACEMENT.get();
    }
}
