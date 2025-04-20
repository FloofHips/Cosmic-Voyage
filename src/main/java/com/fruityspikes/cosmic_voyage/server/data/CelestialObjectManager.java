package com.fruityspikes.cosmic_voyage.server.data;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CelestialObjectManager extends SimpleJsonResourceReloadListener {
    private Map<ResourceLocation, CelestialObject> celestialObjects = new HashMap<>();
    private static final Gson Gson = new GsonBuilder().create();

    public CelestialObjectManager() {
        super(Gson, "space/solar_system");
    }
    @Override
    public void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        //System.out.println(celestialObjects.values());
        celestialObjects.clear();
        //System.out.println(celestialObjects.values());
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceLocationJsonElementMap.entrySet() ) {
            try {
                CelestialObject def = CelestialObject.CODEC.parse(JsonOps.INSTANCE, entry.getValue()).result().orElseThrow(() -> new JsonParseException("Invalid Celestial Object definition"));
                celestialObjects.put(entry.getKey(), def);
            } catch (Exception exception){
                CosmicVoyage.LOGGER.error("Failed to load Celestial Object '{}': '{}'", entry.getKey(), exception.getMessage());
            }
        }
        CosmicVoyage.LOGGER.info("Successfully loaded {} Celestial Objects", celestialObjects.size());
    }

    public CelestialObject getCelestialObject(ResourceLocation resourceLocation) {
        return celestialObjects.get(resourceLocation);
    }
    public Collection<ResourceLocation> getAllCelestialObjectIds() {
        return celestialObjects.keySet();
    }

    public Collection<CelestialObject> getAll() {
        return celestialObjects.values();
    }

    public Map<ResourceLocation, CelestialObject> getObjectMap() {
        return Collections.unmodifiableMap(celestialObjects);
    }
}
