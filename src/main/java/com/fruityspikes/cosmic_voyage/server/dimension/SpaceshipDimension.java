package com.fruityspikes.cosmic_voyage.server.dimension;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public class SpaceshipDimension {
    public static final ResourceKey<Level> DIMENSION_KEY = ResourceKey.create(
        Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "spaceship_dimension")
    );

    public static final ResourceKey<DimensionType> DIMENSION_TYPE_KEY = ResourceKey.create(
        Registries.DIMENSION_TYPE,
            ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "spaceship_dimension_type")
    );

    public static final ResourceKey<LevelStem> DIMENSION_LEVEL_STEM_KEY = ResourceKey.create(
        Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "spaceship_dimension")
    );
}
