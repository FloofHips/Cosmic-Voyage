package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.items.ChunkDelimiterItem;
import com.fruityspikes.cosmic_voyage.server.items.PlatformPlacerItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CosmicVoyage.MODID);
    public static final DeferredItem<Item> COSMIC_TEST = ITEMS.registerSimpleItem("cosmic_test", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<ChunkDelimiterItem> CHUNK_DELIMITER = ITEMS.register("chunk_delimiter", () -> new ChunkDelimiterItem(new Item.Properties()));
    public static final DeferredItem<PlatformPlacerItem> PLATFORM_PLACER = ITEMS.register("platform_placer", () -> new PlatformPlacerItem(new Item.Properties()));
    public static final DeferredItem<Item> ROOM_UPGRADE = ITEMS.registerSimpleItem("room_upgrade", new Item.Properties());

    public static void creativeTabBuild(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        ITEMS.getEntries().forEach((i) -> {
                output.accept(i.get().asItem());
            }
        );
    }
}