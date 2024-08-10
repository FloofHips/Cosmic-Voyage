package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.worldgen.placement_modifiers.PeriodicPlacement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVPlacementModifierTypes {

    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, CosmicVoyage.MODID);
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<PeriodicPlacement>> PERIODIC_PLACEMENT =
            PLACEMENT_MODIFIERS.register("periodic_placement", () -> () -> PeriodicPlacement.CODEC);

}