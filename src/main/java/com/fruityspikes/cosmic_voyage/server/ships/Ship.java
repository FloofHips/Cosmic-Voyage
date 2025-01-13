package com.fruityspikes.cosmic_voyage.server.ships;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
         StructureTemplateManager templateManager = level.getServer().getStructureManager();
         StructureTemplate template = templateManager.getOrCreate(structureLocation);

         BlockPos startPosition = dimensionLocation.offset(0, -3, -1);
         StructurePlaceSettings settings = new StructurePlaceSettings() // Adjust settings as needed
                 .setRotation(Rotation.NONE) // Optionally rotate
                 .setMirror(Mirror.FRONT_BACK);;

         if (template != null) {
             template.placeInWorld(level, startPosition, startPosition, settings, level.random, 2);
             System.out.println("Ship room created at: " + startPosition);
         } else {
             System.out.println("Failed to load structure: " + structureLocation);
         }

        System.out.println("Wool floor created at: " + startPosition);
    }
}
