package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.blocks.AcidBlock;
import com.fruityspikes.cosmic_voyage.server.items.*;
import com.fruityspikes.cosmic_voyage.server.util.CVConstants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

public class CVItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CosmicVoyage.MODID);
    public static final DeferredItem<Item> COSMIC_TEST = ITEMS.registerSimpleItem("cosmic_test", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<ChunkDelimiterItem> CHUNK_DELIMITER = ITEMS.register("chunk_delimiter", () -> new ChunkDelimiterItem(new Item.Properties()));
    public static final DeferredItem<PlatformPlacerItem> PLATFORM_PLACER = ITEMS.register("platform_placer", () -> new PlatformPlacerItem(new Item.Properties()));
    public static final DeferredItem<FillerItem> FILLER = ITEMS.register("filler", () -> new FillerItem(new Item.Properties()));
    public static final DeferredItem<HollowFillerItem> HOLLOW_FILLER = ITEMS.register("hollow_filler", () -> new HollowFillerItem(new Item.Properties()));
    public static final DeferredItem<LinerItem> LINER = ITEMS.register("liner", () -> new LinerItem(new Item.Properties()));
    public static final DeferredItem<Item> ROOM_UPGRADE = ITEMS.registerSimpleItem("room_upgrade", new Item.Properties());
    public static final DeferredItem<Item> ACID_BUCKET = ITEMS.registerItem("acid_bucket", props -> new AcidBucketItem(CVFluidRegistry.ACID_FLUIDS_STILL.get("green").get(), props.craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final Map<String, DeferredHolder<Item, BucketItem>> ACID_BUCKETS = new HashMap<>();

    public static void creativeTabBuild(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        ITEMS.getEntries().forEach((i) -> {
                output.accept(i.get().asItem());
            }
        );
    }
}