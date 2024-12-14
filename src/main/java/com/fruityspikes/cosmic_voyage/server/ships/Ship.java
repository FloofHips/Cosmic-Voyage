package com.fruityspikes.cosmic_voyage.server.ships;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class Ship {
    private final UUID id;
    private final int simpleId;
    private BlockPos entityLocation;
    private final BlockPos dimensionLocation;

    public Ship(UUID id, int simpleId, BlockPos entityLocation, BlockPos dimensionLocation) {
        this.id = id;
        this.simpleId = simpleId;
        this.entityLocation = entityLocation;
        this.dimensionLocation = dimensionLocation;
    }

    public UUID getId() {
        return id;
    }

    public int getSimpleId() {
        return simpleId;
    }

    public BlockPos getEntityLocation() {
        return entityLocation;
    }

    public void setEntityLocation(BlockPos entityLocation) {
        this.entityLocation = entityLocation;
    }

    public BlockPos getDimensionLocation() {
        return dimensionLocation;
    }

    public void save(CompoundTag tag) {
        tag.putUUID("id", id);
        tag.putInt("simpleId", simpleId);
        tag.putInt("entityX", entityLocation.getX());
        tag.putInt("entityY", entityLocation.getY());
        tag.putInt("entityZ", entityLocation.getZ());
        tag.putInt("dimensionX", dimensionLocation.getX());
        tag.putInt("dimensionY", dimensionLocation.getY());
        tag.putInt("dimensionZ", dimensionLocation.getZ());
    }

    public static Ship load(CompoundTag tag) {
        UUID id = tag.getUUID("id");
        int simpleId = tag.getInt("simpleId");
        BlockPos entityLocation = new BlockPos(
            tag.getInt("entityX"),
            tag.getInt("entityY"),
            tag.getInt("entityZ")
        );
        BlockPos dimensionLocation = new BlockPos(
            tag.getInt("dimensionX"),
            tag.getInt("dimensionY"),
            tag.getInt("dimensionZ")
        );
        return new Ship(id, simpleId, entityLocation, dimensionLocation);
    }

    // Get the spawn position inside the ship (center of the 3x3 chunk area)
    public BlockPos getSpawnPosition() {
        return dimensionLocation.offset(24, 1, 24); // Center of the 3x3 chunk area, 1 block above floor
    }

    public void initializeStructure(ServerLevel level) {
        // Create a simple platform with light blue wool
        BlockState floorBlock = Blocks.LIGHT_BLUE_WOOL.defaultBlockState();
        int size = 5;
        
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                level.setBlock(dimensionLocation.offset(x, 0, z), floorBlock, 3);
            }
        }
    }
}
