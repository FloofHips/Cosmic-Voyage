package com.fruityspikes.cosmic_voyage;

import com.fruityspikes.cosmic_voyage.client.client_registries.CVModelLayers;
import com.fruityspikes.cosmic_voyage.client.gui.HelmGui;
import com.fruityspikes.cosmic_voyage.client.models.ShipModel;
import com.fruityspikes.cosmic_voyage.client.renderers.AcidRenderer;
import com.fruityspikes.cosmic_voyage.client.renderers.ShipRenderer;
import com.fruityspikes.cosmic_voyage.data.CVBlockstateGen;
import com.fruityspikes.cosmic_voyage.data.CVItemModelGen;
import com.fruityspikes.cosmic_voyage.data.CVLangGen;
import com.fruityspikes.cosmic_voyage.server.commands.ShipCommands;
import com.fruityspikes.cosmic_voyage.server.data.CelestialObjectManager;
import com.fruityspikes.cosmic_voyage.server.events.NeoforgeEvents;
import com.fruityspikes.cosmic_voyage.server.registries.*;
import com.fruityspikes.cosmic_voyage.server.util.CVConstants;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.joml.Vector3f;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.stream.Stream;

import static com.fruityspikes.cosmic_voyage.server.registries.CVEntityRegistry.SHIP;
import static com.fruityspikes.cosmic_voyage.server.registries.CVItemRegistry.ITEMS;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CosmicVoyage.MODID)
public class CosmicVoyage
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cosmic_voyage";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    private static CelestialObjectManager celestialObjectManager;

    public static CelestialObjectManager getCelestialObjectManager() {
        if(celestialObjectManager==null)
            return new CelestialObjectManager();
        return celestialObjectManager;
    }
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    //public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    //public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    //public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
    //        .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CVItemRegistry.COSMIC_TEST.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                ITEMS.getEntries().forEach((i) -> {
                            output.accept(i.get().asItem());
                        }
                ); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CosmicVoyage(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        CVBlockRegistry.BLOCKS.register(modEventBus);
        CVFluidRegistry.FLUIDS.register(modEventBus);
        CVFluidRegistry.FLUID_TYPES.register(modEventBus);

        // Register the Deferred Register to the mod event bus so block entities get registered
        CVBlockEntityRegistry.BLOCK_ENTITIES.register(modEventBus);
        CVChunkGeneratorRegistry.CHUNK_GENS.register(modEventBus);
        CVEntityRegistry.ENTITIES.register(modEventBus);
        CVFeatureRegistry.FEATURES.register(modEventBus);
        CVPlacementModifierTypes.PLACEMENT_MODIFIERS.register(modEventBus);
        CVItemRegistry.ITEMS.register(modEventBus);
        CVMenus.MENUS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        CVBlockRegistry.registerFluids();
        CVFluidRegistry.registerAcidTypes();
        CVFluidRegistry.registerAcids();

        modEventBus.addListener(this::gatherData);
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.register(new NeoforgeEvents());
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(CVBlockRegistry.HULL_BLOCK);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Register ship commands
        ShipCommands.register(event.getServer().getCommands().getDispatcher());
            //CVBlockRegistry.registerFluids();
            //CVFluidRegistry.registerAcidTypes();
            //CVFluidRegistry.registerAcids();
            //CVItemRegistry.registerBuckets();
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(CVMenus.HELM.get(), HelmGui::new);
        }
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
//            Stream.of(CVFluidRegistry.ACID, CVFluidRegistry.ACID_FLOWING).map(DeferredHolder::get)
//                    .forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent()));

            CVFluidRegistry.ACID_FLUIDS_STILL.values().forEach(acids -> {
                ItemBlockRenderTypes.setRenderLayer(acids.get(), RenderType.translucent());
            });
            CVFluidRegistry.ACID_FLUIDS_FLOWING.values().forEach(acids -> {
                ItemBlockRenderTypes.setRenderLayer(acids.get(), RenderType.translucent());
            });
        }
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(CVModelLayers.SHIP, ShipModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SHIP.get(), ShipRenderer::new);
        }
        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            CVConstants.AcidColors.forEach((name, color) -> {
                event.registerFluidType(new IClientFluidTypeExtensions() {
                    private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID,"block/acid_still"),
                            FLOW = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID,"block/acid_flowing"),
                            OVERLAY = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID,"block/acid_still"),
                            VIEW_OVERLAY = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID,"textures/block/acid_still.png");

                    @Override
                    public ResourceLocation getStillTexture() {
                        return STILL;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return FLOW;
                    }

                    @Override
                    public ResourceLocation getOverlayTexture() {
                        return OVERLAY;
                    }

                    @Override
                    public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                        return VIEW_OVERLAY;
                    }

                    @Override
                    public int getTintColor() {
                        return (int) Long.parseLong(color.substring(2), 16) | 0xFF000000;
                    }

                    @Override
                    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                        int color = this.getTintColor();
                        return new Vector3f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F);
                    }

                    @Override
                    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                        nearDistance = -8F;
                        farDistance = 24F;

                        if (farDistance > renderDistance) {
                            farDistance = renderDistance;
                            shape = FogShape.SPHERE;
                        }

                        RenderSystem.setShaderFogStart(nearDistance);
                        RenderSystem.setShaderFogEnd(farDistance);
                        RenderSystem.setShaderFogShape(shape);
                    }

                    @Override
                    public boolean renderFluid(FluidState fluidState, BlockAndTintGetter getter, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState) {
                        new AcidRenderer().tesselate(getter, pos, vertexConsumer, blockState, fluidState);
                        return true;
                    }
                }, CVFluidRegistry.ACID_FLUID_TYPES.get(name).get());
            });
        }
    }
    public void gatherData(GatherDataEvent event)
    {
        try {
            DataGenerator generator = event.getGenerator();
            PackOutput output = generator.getPackOutput();
            ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

            generator.addProvider(event.includeClient(), new CVLangGen(output, "en_us"));
            generator.addProvider(event.includeClient(), new CVItemModelGen(output, existingFileHelper));
            generator.addProvider(event.includeClient(), new CVBlockstateGen(output, existingFileHelper));
        } catch (RuntimeException e) {
            System.out.println("Failed to get data");
        }
    }
}
