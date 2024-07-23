package com.fruityspikes.cosmic_voyage.client.client_registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class CVModelLayers {
    public static final ModelLayerLocation SHIP = register("ship");

    private static ModelLayerLocation register(String name) {
        return register(name, "main");
    }

    private static ModelLayerLocation register(String name, String layer_name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, name), layer_name);
    }
}
