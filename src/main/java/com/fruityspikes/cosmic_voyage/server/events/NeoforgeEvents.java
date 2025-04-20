package com.fruityspikes.cosmic_voyage.server.events;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class NeoforgeEvents {
    @SubscribeEvent
    public void register(AddReloadListenerEvent event) {
        event.addListener(CosmicVoyage.getCelestialObjectManager());
    }
}
