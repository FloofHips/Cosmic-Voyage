package com.fruityspikes.cosmic_voyage.server.registries;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CVBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CosmicVoyage.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FurnaceBlockEntity>> COMPUTER = BLOCK_ENTITIES.register("peculiar_room", () -> BlockEntityType.Builder.<FurnaceBlockEntity>of(FurnaceBlockEntity::new, CVBlockRegistry.COMPUTER.get()).build(null));
}