package com.fruityspikes.cosmic_voyage.server.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class CelestialObject {
    // Constants
    private static final float AU_TO_KM = 149.6e6f;
    private static final float DAYS_TO_TICKS = 24000f;

    // Fields
    private final ResourceLocation name;
    private final Optional<ResourceLocation> parent;
    private final float eccentricity;
    private final float averageDistance;
    private final float orbitPeriod;
    private final float closestApproachAngle;
    private final float lengthOfDay;
    private final float diameter;
    private final float mass;
    private final float axialTilt;
    private final Optional<ResourceLocation> dimensionId;

    // CODEC for serialization
    public static final Codec<CelestialObject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("name").forGetter(CelestialObject::getName),
            ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(CelestialObject::getParent),
            Codec.FLOAT.fieldOf("orbit_eccentricity").forGetter(CelestialObject::getEccentricity),
            Codec.FLOAT.fieldOf("average_distance_au").forGetter(CelestialObject::getAverageDistance),
            Codec.FLOAT.fieldOf("orbit_period_days").forGetter(CelestialObject::getOrbitPeriod),
            Codec.FLOAT.fieldOf("closest_approach_angle").forGetter(CelestialObject::getClosestApproachAngle),
            Codec.FLOAT.fieldOf("diameter_km").forGetter(CelestialObject::getDiameter),
            Codec.FLOAT.fieldOf("mass_kg").forGetter(CelestialObject::getMass),
            Codec.FLOAT.fieldOf("day_length_hours").forGetter(CelestialObject::getLengthOfDay),
            Codec.FLOAT.fieldOf("axial_tilt_degrees").forGetter(CelestialObject::getAxialTilt),
            ResourceLocation.CODEC.optionalFieldOf("dimension_id").forGetter(CelestialObject::getDimensionId)
    ).apply(instance, CelestialObject::new));

    // Constructor
    public CelestialObject(ResourceLocation name,
                           Optional<ResourceLocation> parent,
                           float eccentricity,
                           float averageDistance,
                           float orbitPeriod,
                           float closestApproachAngle,
                           float lengthOfDay,
                           float diameter,
                           float mass,
                           float axialTilt,
                           Optional<ResourceLocation> dimensionId) {
        this.name = name;
        this.parent = parent;
        this.eccentricity = eccentricity;
        this.averageDistance = averageDistance;
        this.orbitPeriod = orbitPeriod;
        this.closestApproachAngle = closestApproachAngle;
        this.lengthOfDay = lengthOfDay;
        this.diameter = diameter;
        this.mass = mass;
        this.axialTilt = axialTilt;
        this.dimensionId = dimensionId;
    }

    // Getters
    public ResourceLocation getName() {
        return name;
    }

    public Optional<ResourceLocation> getParent() {
        return parent;
    }

    public float getEccentricity() {
        return eccentricity;
    }

    public float getAverageDistance() {
        return averageDistance;
    }

    public float getOrbitPeriod() {
        return orbitPeriod;
    }

    public float getClosestApproachAngle() {
        return closestApproachAngle;
    }

    public float getLengthOfDay() {
        return lengthOfDay;
    }

    public float getDiameter() {
        return diameter;
    }

    public float getMass() {
        return mass;
    }

    public float getAxialTilt() {
        return axialTilt;
    }

    public Optional<ResourceLocation> getDimensionId() {
        return dimensionId;
    }

    // Calculation methods
    public Vec2 calculatePosition(double timeDays, Map<ResourceLocation, CelestialObject> allObjects) {
        if (parent.isEmpty()) {
            return Vec2.ZERO;
        }

        CelestialObject parentObj = allObjects.get(parent.get());
        if (parentObj == null) {
            return Vec2.ZERO;
        }

        int testeccentricity = 0;

        Vec2 parentPosition = parentObj.calculatePosition(timeDays, allObjects);

        double progress = (timeDays % orbitPeriod) / orbitPeriod;
        double meanAnomaly = progress * 2 * Math.PI;

        double eccentricAnomaly = solveKeplersEquation(meanAnomaly, testeccentricity);

        double theta = 2 * Math.atan2(
                Math.sqrt(1 + testeccentricity) * Math.sin(eccentricAnomaly / 2),
                Math.sqrt(1 - testeccentricity) * Math.cos(eccentricAnomaly / 2)
        );

        theta += Math.toRadians(closestApproachAngle);

        double distance = averageDistance * (1 - testeccentricity * testeccentricity)
                / (1 + testeccentricity * Math.cos(theta));

        double x = parentPosition.x + (distance * Math.cos(theta));
        double y = parentPosition.y + (distance * Math.sin(theta));

        return new Vec2((float)x, (float)y);
    }

    private double solveKeplersEquation(double M, double e) {
        double E = M;
        for (int i = 0; i < 5; i++) {
            double delta = (E - e * Math.sin(E) - M) / (1 - e * Math.cos(E));
            E -= delta;
            if (Math.abs(delta) < 1e-6) break;
        }
        return E;
    }

    @Override
    public String toString() {
        return "CelestialObject{" +
                "name=" + name +
                ", parent=" + parent +
                ", eccentricity=" + eccentricity +
                ", averageDistance=" + averageDistance +
                ", orbitPeriod=" + orbitPeriod +
                ", closestApproachAngle=" + closestApproachAngle +
                ", lengthOfDay=" + lengthOfDay +
                ", diameter=" + diameter +
                ", mass=" + mass +
                ", axialTilt=" + axialTilt +
                ", dimensionId=" + dimensionId +
                '}';
    }
}
