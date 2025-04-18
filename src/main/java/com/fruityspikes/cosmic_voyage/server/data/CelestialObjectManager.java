package com.fruityspikes.cosmic_voyage.server.data;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CelestialObjectManager extends SimpleJsonResourceReloadListener {
    private final Map<ResourceLocation, CelestialObject> celestialObjects = new HashMap<>();
    private static final Gson Gson = new GsonBuilder().create();

    public CelestialObjectManager() {
        super(Gson, "space/solar_system");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        celestialObjects.clear();
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
    record CelestialObject(ResourceLocation name, Optional<ResourceLocation> parent, float eccentricity, float maxDistanceFromParent, float orbitPeriod, float lengthOfDay, float diameter, float mass, Optional<ResourceLocation> dimensionId) {
        public static final Codec<CelestialObject> CODEC = RecordCodecBuilder.create(celestialObjectInstance -> celestialObjectInstance.group(
                ResourceLocation.CODEC.fieldOf("name").forGetter(CelestialObject::name),
                ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(CelestialObject::parent),
                Codec.FLOAT.fieldOf("eccentricity").forGetter(CelestialObject::eccentricity),
                Codec.FLOAT.fieldOf("max_distance_from_parent").forGetter(CelestialObject::maxDistanceFromParent),
                Codec.FLOAT.fieldOf("orbit_period").forGetter(CelestialObject::orbitPeriod),
                Codec.FLOAT.fieldOf("length_of_day").forGetter(CelestialObject::lengthOfDay),
                Codec.FLOAT.fieldOf("diameter").forGetter(CelestialObject::diameter),
                Codec.FLOAT.fieldOf("mass").forGetter(CelestialObject::mass),
                ResourceLocation.CODEC.optionalFieldOf("dimension_id").forGetter(CelestialObject::dimensionId)
        ).apply(celestialObjectInstance, CelestialObject::new));
    }
}
