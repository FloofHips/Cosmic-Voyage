package com.fruityspikes.cosmic_voyage.server.ships;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.blocks.IShipLight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShipRoom {
    private final int index;
    ResourceLocation structureLocation_main = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_main");
    ResourceLocation structureLocation_center = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_center");
    ResourceLocation structureLocation_edge = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_edge");
    ResourceLocation structureLocation_corner = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_room_corner");
    ResourceLocation structureLocation_hallway = ResourceLocation.fromNamespaceAndPath(CosmicVoyage.MODID, "ship_hallway");
    static TagKey<Block> LIGHT_TAG = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("cosmic_voyage", "ship_lights"));
    public boolean isActive;
    public BlockPos dimensionLocation;
    private final Map<BlockPos, LightEntry> shipLights = new HashMap<>();
    public ShipRoom(int index, BlockPos dimensionLocation) {
        this.index = index;
        this.isActive = false;
        this.dimensionLocation = dimensionLocation.offset(getGridX() * 16,0,getGridZ() * 16);
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
    public CompoundTag save() {
        CosmicVoyage.LOGGER.info("Saving ShipRoom [{}] - Active: {}, Lights: {}",
            index, isActive, shipLights.size());
        CompoundTag tag = new CompoundTag();
        tag.putInt("index", index);
        tag.putBoolean("active", isActive);
        tag.putInt("dimensionX", dimensionLocation.getX());
        tag.putInt("dimensionY", dimensionLocation.getY());
        tag.putInt("dimensionZ", dimensionLocation.getZ());

        ListTag lightsList = new ListTag();
        for (BlockPos pos : shipLights.keySet()) {
            CompoundTag lightTag = new CompoundTag();
            lightTag.putLong("pos", pos.asLong());
            lightsList.add(lightTag);
        }
        tag.put("lights", lightsList);
        CosmicVoyage.LOGGER.info("Saved {} lights for room [{}]", lightsList.size(), index);
        return tag;
    }

    public static ShipRoom load(CompoundTag tag, ServerLevel level) {
        int index = tag.getInt("index");
        boolean active = tag.getBoolean("active");
        //CosmicVoyage.LOGGER.info("Loading ShipRoom [{}] - Active: {}", index, active);

        BlockPos dimensionLocation = new BlockPos(
                tag.getInt("dimensionX"),
                tag.getInt("dimensionY"),
                tag.getInt("dimensionZ")
        );
        ShipRoom room = new ShipRoom(index, dimensionLocation);
        if(active){
            room.reactivateRoom(level);
        }

        if (tag.contains("lights")) {
            ListTag lightsList = tag.getList("lights", 10);
            //CosmicVoyage.LOGGER.info("Room [{}] has {} lights in NBT", index, lightsList.size());

            int loadedLights = 0;
            for (int i = 0; i < lightsList.size(); i++) {
                CompoundTag lightTag = lightsList.getCompound(i);
                BlockPos pos = BlockPos.of(lightTag.getLong("pos"));

                if (level.isLoaded(pos)) {
                    BlockState state = level.getBlockState(pos);
                    if (state.is(LIGHT_TAG)) {
                        room.addLight(state, pos, level);
                        loadedLights++;
                    }
                } else {
                    //CosmicVoyage.LOGGER.warn("Chunk not loaded for light at {} in room [{}]", pos, index);
                }
            }
            //CosmicVoyage.LOGGER.info("Loaded {}/{} lights for room [{}]", loadedLights, lightsList.size(), index);
        } else {
            //CosmicVoyage.LOGGER.info("Room [{}] has no lights saved", index);
        }

        return room;
    }
    public void addLight(BlockState state, BlockPos pos, Level level) {
        if (!shipLights.containsKey(pos) && state.is(LIGHT_TAG)) {
            shipLights.put(pos, new LightEntry(state, pos, level));
        }
    }
    public void removeLight(BlockState blockState, BlockPos pos, Level level){
        LightEntry lightEntry = shipLights.get(pos);
        if(lightEntry!=null)
            shipLights.remove(pos);
    }
    public void turnOnLights(){
        if (shipLights.isEmpty()) return;

        List<LightEntry> lightsCopy = new ArrayList<>(shipLights.values());
        lightsCopy.sort((a, b) -> Integer.compare(b.pos.getY(), a.pos.getY()));

        System.out.println(lightsCopy);

        ServerLevel serverLevel = (ServerLevel)lightsCopy.get(0).level;
        long currentTick = serverLevel.getGameTime();

        for (int i = 0; i < lightsCopy.size(); i++) {
            LightEntry entry = lightsCopy.get(i);
            int delayTicks = i+5;

            //serverLevel.getServer().execute(() -> {
                //if (serverLevel.getGameTime() >= currentTick + delayTicks) {
                    BlockState state = entry.level.getBlockState(entry.pos);
                    if (state.is(LIGHT_TAG) && state.getBlock() instanceof IShipLight lightBlock) {
                        lightBlock.turnOn(state, entry.level, entry.pos, null, lightBlock.getTurnOnSound());
                    }
                //}
            //});
        }
    }

    public void turnOffLights() {
        if (shipLights.isEmpty()) return;

        List<LightEntry> lightsCopy = new ArrayList<>(shipLights.values());
        lightsCopy.sort((a, b) -> Integer.compare(b.pos.getY(), a.pos.getY()));

        ServerLevel serverLevel = (ServerLevel)lightsCopy.get(0).level;
        long currentTick = serverLevel.getGameTime();

        for (int i = 0; i < lightsCopy.size(); i++) {
            LightEntry entry = lightsCopy.get(i);
            int delayTicks = i+5;

            //serverLevel.getServer().execute(() -> {
                //if (serverLevel.getGameTime() >= currentTick + delayTicks) {
                    BlockState state = entry.level.getBlockState(entry.pos);
                    if (state.is(LIGHT_TAG) && state.getBlock() instanceof IShipLight lightBlock) {
                        lightBlock.turnOff(state, entry.level, entry.pos, null, lightBlock.getTurnOffSound());
                    }
                //}
            //});
        }
    }
    public void activateInitialRoom(ServerLevel serverLevel){
        setActive(true);
        StructureTemplateManager templateManager = serverLevel.getServer().getStructureManager();
        StructureTemplate template = templateManager.getOrCreate(structureLocation_main);

        BlockPos startPosition = dimensionLocation.offset(16, -34, 16);
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.CLOCKWISE_180);
        template.placeInWorld(serverLevel, startPosition, startPosition, settings, serverLevel.random, 2);
        scanForLightBlocks(serverLevel, startPosition, template.getSize());
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

        scanForLightBlocks(serverLevel, startPosition, template.getSize());
    }
    public void reactivateRoom(ServerLevel level) {
        if (!isActive) {
            isActive = true;
            shipLights.clear();
            scanForLightBlocks(level, dimensionLocation.offset(0, -14, 0), dimensionLocation.offset(16,26,16));
        }
    }
    private void scanForLightBlocks(ServerLevel level, BlockPos startPos, Vec3i size) {
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos worldPos = startPos.offset(x, y, z);
                    BlockState state = level.getBlockState(worldPos);

                    if (state.is(LIGHT_TAG)){
                        this.addLight(state, worldPos, level);
                    }
                }
            }
        }
    }
    private static class LightEntry {
        public BlockState state;
        public BlockPos pos;
        public Level level;

        public LightEntry(BlockState state, BlockPos pos, Level level) {
            this.state = state;
            this.pos = pos;
            this.level = level;
        }
    }
}
