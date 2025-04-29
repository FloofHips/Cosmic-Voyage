package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.entities.ShipEntity;
import com.fruityspikes.cosmic_voyage.server.entities.venus.ExplorerEntity;
import com.fruityspikes.cosmic_voyage.server.entities.venus.TowerEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = CosmicVoyage.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CVEntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CosmicVoyage.MODID);

    //Vehicles
    public static final DeferredHolder<EntityType<?>, EntityType<ShipEntity>> SHIP =
            ENTITIES.register("ship", () -> EntityType.Builder
                    .of(ShipEntity::new, MobCategory.MISC)
                    .sized(3,6)
                    .build(ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship")
                            .toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<ExplorerEntity>> EXPLORER =
            ENTITIES.register("explorer", () -> EntityType.Builder
                    .of(ExplorerEntity::new, MobCategory.MISC)
                    .sized(1,2)
                    .build(ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "explorer")
                            .toString()));
    public static final DeferredHolder<EntityType<?>, EntityType<TowerEntity>> TOWER =
            ENTITIES.register("tower", () -> EntityType.Builder
                    .of(TowerEntity::new, MobCategory.MISC)
                    .sized(3,6)
                    .build(ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "tower")
                            .toString()));
    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(EXPLORER.get(), ExplorerEntity.createAttributes().build());
        event.put(TOWER.get(), TowerEntity.createAttributes().build());
    }
}
