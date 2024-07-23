package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.entities.ShipEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CosmicVoyage.MODID);

    //Vehicles
    public static final DeferredHolder<EntityType<?>, EntityType<ShipEntity>> SHIP = ENTITIES.register("ship", () -> EntityType.Builder.of(ShipEntity::new, MobCategory.MISC).sized(3,6).build(ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship").toString()));

    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        //event.put(SHIP.get(), ShipEntity.createAttributes().build());
    }
}
