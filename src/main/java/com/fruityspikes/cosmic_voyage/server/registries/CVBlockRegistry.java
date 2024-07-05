package com.fruityspikes.cosmic_voyage.server.registries;


import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
public class CVBlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CosmicVoyage.MODID);
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredBlock<Block> OFFICE_LIGHT = registerBlock("office_light", BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOL)
                            .requiresCorrectToolForDrops()
                            .strength(1.5F, 6.0F)
                            .lightLevel(d->8)
                            .sound(SoundType.CHAIN)
            );

    private static <T extends Block> DeferredBlock<Block> registerBlock(String name, BlockBehaviour.Properties props) {
        DeferredBlock<Block> toReturn = BLOCKS.registerSimpleBlock(name, props);
        CVItemRegistry.ITEMS.registerSimpleBlockItem(name, toReturn);
        return toReturn;
    }
}