package com.fruityspikes.cosmic_voyage.server.registries;


import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.blocks.ComputerBlock;
import com.fruityspikes.cosmic_voyage.server.blocks.LampuleBlock;
import com.fruityspikes.cosmic_voyage.server.blocks.OutletBlock;
import com.fruityspikes.cosmic_voyage.server.blocks.ShipRoomGateBlock;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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
    );
    public static final DeferredBlock<Block> OUTLET = registerBlock("outlet", OutletBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> PLASTIC_BLOCK = registerBlock("plastic_block", Block::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> PLASTIC_PILLAR = registerBlock("plastic_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOL)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 6.0F)
                            .sound(SoundType.COPPER_BULB)
            );
    public static final DeferredBlock<Block> PLASTIC_SLAB = BLOCKS.registerBlock("plastic_slab", SlabBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> PLASTIC_STAIRS = registerStair("plastic_stairs", () -> new StairBlock(
            PLASTIC_BLOCK.get().defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(PLASTIC_BLOCK.get()).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> REINFORCED_PLASTIC_PILLAR = registerBlock("reinforced_plastic_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .requiresCorrectToolForDrops()
            .strength(1.5F, 6.0F)
            .sound(SoundType.COPPER_BULB)
    );
    public static final DeferredBlock<Block> PLATE_PILLAR = registerBlock("plating_pillar", RotatedPillarBlock::new, BlockBehaviour.Properties.of()
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
    private static <T extends Block> DeferredBlock<Block> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> func , BlockBehaviour.Properties props) {
        DeferredBlock<Block> toReturn = BLOCKS.registerBlock(name, func, props);
        CVItemRegistry.ITEMS.registerSimpleBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<Block> registerStair(String name, Supplier<? extends T> supp) {
        DeferredBlock<Block> toReturn = BLOCKS.register(name, supp);
        CVItemRegistry.ITEMS.registerSimpleBlockItem(name, toReturn);
        return toReturn;
    }
}