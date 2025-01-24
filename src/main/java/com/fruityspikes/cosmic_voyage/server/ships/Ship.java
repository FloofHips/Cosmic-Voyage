package com.fruityspikes.cosmic_voyage.server.ships;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.minecraft.core.BlockPos;
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
    ResourceLocation structureLocation = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "main_ship_room_base");
    private final UUID id;
    private final int simpleId;
    private BlockPos entityLocation;
    private final BlockPos dimensionLocation;
    private ResourceLocation dimension;

    public Ship(UUID id, int simpleId, BlockPos entityLocation, BlockPos dimensionLocation, ResourceLocation dimension) {
        this.id = id;
        this.simpleId = simpleId;
        this.entityLocation = entityLocation;
        this.dimensionLocation = dimensionLocation;
        this.dimension = dimension;
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

    // Get the spawn position inside the ship (center of the 3x3 chunk area)
    public BlockPos getSpawnPosition() {
        return dimensionLocation.offset(24, 1, 24); // Center of the 3x3 chunk area, 1 block above floor
    }

    public void initializeStructure(ServerLevel level) {
         StructureTemplateManager templateManager = level.getServer().getStructureManager();
         StructureTemplate template = templateManager.getOrCreate(structureLocation);

         BlockPos startPosition = dimensionLocation.offset(-1, -34, -1);
         StructurePlaceSettings settings = new StructurePlaceSettings() // Adjust settings as needed
                 .setRotation(Rotation.NONE);
         template.placeInWorld(level, startPosition, startPosition, settings, level.random, 2);

        for (int y = 0; y < 4; y++) {
            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 12; x++) {

                    level.setBlock(startPosition.offset(x+17,y+7,z+7), Blocks.AIR.defaultBlockState(), 20);
                }
            }
        }
    }
}
