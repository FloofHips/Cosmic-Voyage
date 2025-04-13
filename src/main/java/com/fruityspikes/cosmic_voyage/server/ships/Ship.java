package com.fruityspikes.cosmic_voyage.server.ships;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.registries.CVBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class Ship {
    private final UUID id;
    private final int simpleId;
    private BlockPos entityLocation;
    private final BlockPos dimensionLocation;
    private ResourceLocation dimension;
    public final ShipRoom[] rooms = new ShipRoom[25];


    public Ship(UUID id, int simpleId, BlockPos entityLocation, BlockPos dimensionLocation, ResourceLocation dimension) {
        this.id = id;
        this.simpleId = simpleId;
        this.entityLocation = entityLocation;
        this.dimensionLocation = dimensionLocation;
        this.dimension = dimension;
        for (int i = 0; i < 25; i++) {
            rooms[i] = new ShipRoom(i, dimensionLocation);
        }
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

    public ResourceLocation getDimension() {
        return dimension;
    }

    public void setDimension(ResourceLocation dimension) {
        this.dimension = dimension;
    }
    public ShipRoom getRoom(int index) {
        if (index < 0 || index >= 25) {
            return null;
        }
        return rooms[index];
    }
    public ShipRoom getRoomByRelativePos(int relX, int relZ) {
        if (relX < 0 || relX >= 80 || relZ < 0 || relZ >= 80) {
            return null;
        }

        int roomX = relX / 16;
        int roomZ = relZ / 16;
        int roomIndex = roomZ * 5 + roomX;

        return getRoom(roomIndex);
    }

    public ShipRoom getRoomByWorldPos(BlockPos worldPos) {
        BlockPos shipPos = getDimensionLocation();
        int relX = worldPos.getX() - shipPos.getX();
        int relZ = worldPos.getZ() - shipPos.getZ();
        return getRoomByRelativePos(relX, relZ);
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
        tag.putString("dimension", dimension.toString());
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
        ResourceLocation dimension = ResourceLocation.tryParse(tag.getString("dimension"));
        return new Ship(id, simpleId, entityLocation, dimensionLocation, dimension);
    }

    public BlockPos getSpawnPosition() {
        return dimensionLocation.offset(24, 1, 24);
    }

    public void initializeShip(ServerLevel level) {
         this.generateDoorWays(level);
         this.rooms[2].activateInitialRoom(level);
    }
    public void generateDoorWays(ServerLevel level) {
        BlockState doorMaterial = CVBlockRegistry.SHIP_ROOM_GATE.get().defaultBlockState();
        BlockPos pos;
        for (ShipRoom room: rooms) {
            BlockPos roomOrigin = room.getDimensionLocation().offset(8, -28, 8);
            pos = roomOrigin;
            for (Direction direction : Direction.Plane.HORIZONTAL){
                if(direction==Direction.EAST || direction==Direction.SOUTH)
                    pos = pos.relative(direction, 7);
                else
                    pos = pos.relative(direction, 8);

                for (int height = 1; height < 4; height++) {
                    for (int width = -1; width < 1; width++) {
                        if (direction.getAxis() == Direction.Axis.X) {
                            level.setBlock(pos.offset(0, height, width), doorMaterial, 3);
                        } else {
                            level.setBlock(pos.offset(width, height, 0), doorMaterial, 3);
                        }
                    }
                }
                pos = roomOrigin;
            }
        }
    }
}
