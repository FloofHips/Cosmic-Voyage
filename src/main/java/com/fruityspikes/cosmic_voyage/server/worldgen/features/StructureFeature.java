package com.fruityspikes.cosmic_voyage.server.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public class StructureFeature extends Feature<StructureFeatureConfiguration> {
    public StructureFeature(Codec<StructureFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<StructureFeatureConfiguration> context) {
        RandomSource random = context.random();
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        StructureFeatureConfiguration config = context.config();

        int index = random.nextInt(config.getStructures().size());
        ResourceLocation structureLocation = config.getStructures().get(index);

        StructureTemplateManager templateManager = world.getLevel().getServer().getStructureManager();
        StructureTemplate template = templateManager.getOrCreate(structureLocation);

        if (template == null) {
            return false;
        }

        StructurePlaceSettings placeSettings = new StructurePlaceSettings();
        template.placeInWorld(world, origin, origin, placeSettings, random, 4);

        return true;
    }
}
