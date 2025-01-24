package com.fruityspikes.cosmic_voyage.server.registries;


import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.blocks.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;
public class CVBlockRegistry {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CosmicVoyage.MODID);
    //public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredBlock<Block> COMPUTER = registerBlock("computer", ComputerBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
            .noOcclusion()
    );
    public static final DeferredBlock<Block> OUTLET = registerBlock("outlet", OutletBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> EPOXY = registerBlock("epoxy", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> EPOXY_PILLAR = registerBlock("epoxy_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOL)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.COPPER_BULB)
            );
    public static final DeferredBlock<Block> EPOXY_SLAB = registerBlock("epoxy_slab", SlabBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> EPOXY_STAIRS = registerSpecial("epoxy_stairs", () -> new StairBlock(
            EPOXY.get().defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(EPOXY.get()).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> REINFORCED_EPOXY_PILLAR = registerBlock("reinforced_epoxy_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> DURALUMIN_PILLAR = registerBlock("duralumin_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_GRATE)
    );
    public static final DeferredBlock<Block> HULL_BLOCK = registerBlock("hull_block", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    public static final DeferredBlock<Block> LAMPULE = registerBlock("lampule", LampuleBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    public static final DeferredBlock<Block> SHIP_ROOM_GATE = registerBlock("ship_room_gate", ShipRoomGateBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    public static final DeferredBlock<Block> SHIP_EXIT = registerBlock("ship_exit", ShipExitBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    public static final DeferredBlock<Block> SHIP_LIGHT_SWITCH = registerBlock("ship_light_switch", ShipLightSwitchBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    public static final DeferredBlock<Block> HELM = registerBlock("helm", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    //Venus
    public static final DeferredBlock<Block> SEDIMENT = registerBlock("sediment", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERRACK)
    );
    public static final DeferredBlock<Block> SULFUR_DEPOSIT = registerBlock("sulfur_deposit", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.FUNGUS)
    );
    public static final DeferredBlock<Block> DARK_SULFUR_DEPOSIT = registerBlock("dark_sulfur_deposit", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.FUNGUS)
    );
    public static final DeferredBlock<Block> CENTIPEDE_INNER_WALL = registerBlock("centipede_inner_wall", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    public static final DeferredBlock<Block> TIN = registerBlock("tin", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    public static final DeferredBlock<Block> TIN_DOOR = registerSpecial("tin_door", () -> new DoorBlock(
            CVBlockSetTypes.TIN,
            BlockBehaviour.Properties.ofFullCopy(TIN.get()).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> TIN_STAIRS = registerSpecial("tin_stairs", () -> new StairBlock(
            TIN.get().defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(EPOXY.get()).sound(SoundType.COPPER_GRATE)));
    public static final DeferredBlock<Block> CONCRETE = registerBlock("concrete", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.BASALT)
    );
    public static final DeferredBlock<Block> UNSHAKABLE_METAL = registerBlock("unshakable_metal", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.NETHERITE_BLOCK)
    );
    private static <T extends Block> DeferredBlock<Block> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> func , BlockBehaviour.Properties props) {
        DeferredBlock<Block> toReturn = BLOCKS.registerBlock(name, func, props);
        CVItemRegistry.ITEMS.registerSimpleBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<Block> registerSpecial(String name, Supplier<? extends T> supp) {
        DeferredBlock<Block> toReturn = BLOCKS.register(name, supp);
        CVItemRegistry.ITEMS.registerSimpleBlockItem(name, toReturn);
        return toReturn;
    }
}