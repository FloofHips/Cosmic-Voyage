package com.fruityspikes.cosmic_voyage.server.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.List;

public class StructureFeatureConfiguration implements FeatureConfiguration {
    public static final Codec<StructureFeatureConfiguration> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    ResourceLocation.CODEC.listOf().fieldOf("structures").forGetter(StructureFeatureConfiguration::getStructures),
                    StructureProcessorType.LIST_CODEC.optionalFieldOf("processors", Holder.direct(new StructureProcessorList(List.of()))).forGetter(StructureFeatureConfiguration::getProcessors)
            ).apply(instance, StructureFeatureConfiguration::new)
    );

    private final List<ResourceLocation> structures;
    private final Holder<StructureProcessorList> processors;

    public StructureFeatureConfiguration(List<ResourceLocation> structures, Holder<StructureProcessorList> processors) {
        this.structures = structures;
        this.processors = processors;
    }

    public List<ResourceLocation> getStructures() {
        return structures;
    }

    public Holder<StructureProcessorList> getProcessors() {
        return processors;
    }
}
