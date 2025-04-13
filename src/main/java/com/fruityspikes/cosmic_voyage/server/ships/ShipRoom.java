package com.fruityspikes.cosmic_voyage.server.ships;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ShipRoom {
    private final int index;
    ResourceLocation structureLocation_main = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_main");
    ResourceLocation structureLocation_center = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_center");
    ResourceLocation structureLocation_edge = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_edge");
    ResourceLocation structureLocation_corner = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_corner");
    ResourceLocation structureLocation_hallway = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_hallway");
    public boolean isActive;
    public BlockPos dimensionLocation;
    public ShipRoom(int index, BlockPos dimensionLocation) {
        this.index = index;
        this.isActive = false;
        //this.structureLocation = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID,"ship_room_" + (index));
        this.dimensionLocation = getOffsetPosition(dimensionLocation);
    }
    public int getIndex() {
        return index;
    }
    public int getGridX() {
        return index % 5;
    }

    public int getGridZ() {
        return index / 5;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
    public BlockPos getDimensionLocation() {
        return dimensionLocation;
    }
    public BlockPos getOffsetPosition(BlockPos pos) {
        return pos.offset(getRelativePosition());
    }
    public BlockPos getRelativePosition() {
        return new BlockPos(
                getGridX() * 16,
                0,
                getGridZ() * 16
        );
    }
    public void activateInitialRoom(ServerLevel serverLevel){
        setActive(true);
        StructureTemplateManager templateManager = serverLevel.getServer().getStructureManager();
        StructureTemplate template = templateManager.getOrCreate(structureLocation_main);

        BlockPos startPosition = dimensionLocation.offset(16, -34, 16);
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.CLOCKWISE_180);
        template.placeInWorld(serverLevel, startPosition, startPosition, settings, serverLevel.random, 2);
    }
    public void activateRoom(ServerLevel serverLevel){
        setActive(true);
        StructureTemplateManager templateManager = serverLevel.getServer().getStructureManager();
        templateManager.getOrCreate(structureLocation_corner);

        StructureTemplate template = templateManager.getOrCreate(structureLocation_hallway);
        Mirror mirror = Mirror.NONE;
        Rotation rotation = Rotation.NONE;
        BlockPos startPosition = dimensionLocation.offset(-1, -34, -1);
        template = switch (index) {
            case 0, 4, 20, 24 -> templateManager.getOrCreate(structureLocation_corner);
            case 10, 14, 22 -> templateManager.getOrCreate(structureLocation_edge);
            case 12 -> templateManager.getOrCreate(structureLocation_center);
            default -> template;
        };
        switch (index){
            case 0, 10:
                rotation = Rotation.COUNTERCLOCKWISE_90;
                startPosition = dimensionLocation.offset(-1, -34, 16);
                break;
            case 4:
                mirror = Mirror.LEFT_RIGHT;
                rotation = Rotation.COUNTERCLOCKWISE_90;
                startPosition = dimensionLocation.offset(16, -34, 16);
                break;
            case 14, 24:
                rotation = Rotation.CLOCKWISE_90;
                startPosition = dimensionLocation.offset(16, -34, -1);
                break;
            case 12, 22, 20:
                rotation = Rotation.CLOCKWISE_180;
                startPosition = dimensionLocation.offset(16, -34, 16);
                break;
            case 7, 17, 9, 19, 15, 5:
                startPosition = dimensionLocation.offset(6, -28, 0);
                break;
            case 3, 13, 23, 21, 11, 1:
                rotation = Rotation.CLOCKWISE_90;
                startPosition = dimensionLocation.offset(15, -28, 6);
                break;
        }
        StructurePlaceSettings settings = new StructurePlaceSettings().setMirror(mirror).setRotation(rotation);
        template.placeInWorld(serverLevel, startPosition, startPosition, settings, serverLevel.random, 2);
    }
}
