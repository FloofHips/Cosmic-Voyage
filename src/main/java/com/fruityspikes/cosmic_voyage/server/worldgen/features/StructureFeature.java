package com.fruityspikes.cosmic_voyage.server.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

public class StructureFeature extends Feature<StructureFeatureConfiguration> {
    public StructureFeature(Codec<StructureFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<StructureFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();

        StructureTemplateManager templateManager = world.getLevel().getServer().getStructureManager();
        StructureTemplate template = templateManager.getOrCreate(
                context.config().getStructures().get(context.random().nextInt(context.config().getStructures().size()))
        );

        if (template == null) {
            return false;
        }

        Vec3i size = template.getSize();
        BlockPos structureStart = origin.subtract(new BlockPos(size.getX()/2, 0, size.getZ()/2));

        int minChunkX = SectionPos.blockToSectionCoord(structureStart.getX());
        int minChunkZ = SectionPos.blockToSectionCoord(structureStart.getZ());
        int maxChunkX = SectionPos.blockToSectionCoord(structureStart.getX() + size.getX());
        int maxChunkZ = SectionPos.blockToSectionCoord(structureStart.getZ() + size.getZ());

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (!world.hasChunk(chunkX, chunkZ)) {
                    return false;
                }
            }
        }

        StructurePlaceSettings settings = new StructurePlaceSettings();
        template.placeInWorld(world, structureStart, structureStart, settings, context.random(), 4);

        //TODO TRIGGER STRUCTURE BLOCKS

        return true;
    }
}