package com.fruityspikes.cosmic_voyage.server.ships;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.dimension.SpaceshipDimension;
import com.fruityspikes.cosmic_voyage.server.entities.ShipEntity;
import com.fruityspikes.cosmic_voyage.server.registries.CVEntityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpaceshipManager extends SavedData {
    private SpaceshipManager instance;
    private static final String SAVED_DATA_NAME = CosmicVoyage.MODID + "_spaceships";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private final Map<UUID, Ship> ships = new HashMap<>();
    private final Map<Integer, UUID> simpleIdToUuid = new HashMap<>();
    private int nextShipDimensionX = 0;
    private int nextShipDimensionZ = 0;
    private int nextSimpleId = 1;
    private static final int SHIP_SPACING = 1000; // Blocks between ships in spaceship dimension
    private static final int SHIP_SIZE = 48; // 3 chunks = 48 blocks

    public SpaceshipManager() {
        super();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag shipsList = new ListTag();
        ships.forEach((id, ship) -> {
            CompoundTag shipTag = new CompoundTag();
            ship.save(shipTag);
            shipsList.add(shipTag);
        });

        tag.put("ships", shipsList);
        tag.putInt("nextShipDimensionX", nextShipDimensionX);
        tag.putInt("nextShipDimensionZ", nextShipDimensionZ);
        tag.putInt("nextSimpleId", nextSimpleId);

        return tag;
    }

    public static SpaceshipManager get(ServerLevel level) {
        ServerLevel spaceshipDimension = level.getServer().getLevel(SpaceshipDimension.DIMENSION_KEY);
        if (spaceshipDimension == null) {
            spaceshipDimension = level.getServer().overworld();
        }
        return spaceshipDimension.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        SpaceshipManager::new,
                        (tag, provider) -> SpaceshipManager.load(tag),
                        DataFixTypes.LEVEL
                ),
                SAVED_DATA_NAME
        );
    }
    
    public Ship createShip(BlockPos entityLocation, ServerLevel level) {
        int chunkAlignedX = (nextShipDimensionX / 16) * 16;
        int chunkAlignedZ = (nextShipDimensionZ / 16) * 16;
        BlockPos shipDimensionPos = new BlockPos(chunkAlignedX, 64, chunkAlignedZ);
        ResourceLocation dimension = ResourceLocation.parse(level.dimension().location().toString());

        UUID shipId = UUID.randomUUID();
        Ship ship = new Ship(shipId, nextSimpleId, entityLocation, shipDimensionPos, dimension);
        ships.put(shipId, ship);
        simpleIdToUuid.put(nextSimpleId, shipId);

        ShipEntity shipEntity = new ShipEntity(CVEntityRegistry.SHIP.get(), level);
        shipEntity.setPos(entityLocation.getX() + 0.5, entityLocation.getY(), entityLocation.getZ() + 0.5);
        shipEntity.setShipId(shipId);
        level.addFreshEntity(shipEntity);

        ServerLevel spaceshipDimension = level.getServer().getLevel(SpaceshipDimension.DIMENSION_KEY);
        if (spaceshipDimension != null) {
            ChunkPos chunkPos = new ChunkPos(shipDimensionPos);
            spaceshipDimension.getChunkSource().addRegionTicket(TicketType.START, chunkPos, 1, Unit.INSTANCE);
            ship.initializeShip(spaceshipDimension);
        } else {
            LOGGER.error("Spaceship dimension is not available!");
        }

        updateNextShipPosition();
        nextSimpleId++;

        this.setDirty();
        
        LOGGER.info("Created new ship with ID: {} (#{}) at dimension position: {}", shipId, ship.getSimpleId(), shipDimensionPos);
        return ship;
    }

    public Ship getShip(UUID id) {
        return ships.get(id);
    }

    public Ship getShipBySimpleId(int simpleId) {
        UUID uuid = simpleIdToUuid.get(simpleId);
        return uuid != null ? ships.get(uuid) : null;
    }
    public Ship getShipByPosition(BlockPos pos) {
        final int SHIP_SIZE = 80;

        for (Ship ship : this.getShips().values()) {
            BlockPos shipPos = ship.getDimensionLocation();

            int minX = shipPos.getX();
            int minZ = shipPos.getZ();
            int maxX = minX + SHIP_SIZE - 1;
            int maxZ = minZ + SHIP_SIZE - 1;

            if (pos.getX() >= minX && pos.getX() <= maxX &&
                    pos.getZ() >= minZ && pos.getZ() <= maxZ) {
                return ship;
            }
        }

        return null;
    }
    public Map<UUID, Ship> getShips() {
        return ships;
    }

    public void removeShip(UUID id) {
        Ship ship = ships.remove(id);
        if (ship != null) {
            simpleIdToUuid.remove(ship.getSimpleId());
        }
        this.setDirty();
    }

    private void updateNextShipPosition() {
        // move to next position
        nextShipDimensionX += SHIP_SPACING;
        
        // if we go too far into X axys, reset X and increment Z
        if (nextShipDimensionX > 10000) {
            nextShipDimensionX = 0;
            nextShipDimensionZ += SHIP_SPACING;
        }
        
        this.setDirty();
    }

    private static SpaceshipManager load(CompoundTag tag) {
        SpaceshipManager manager = new SpaceshipManager();
        
        if (tag.contains("ships")) {
            ListTag shipsList = tag.getList("ships", 10);
            shipsList.forEach(shipTag -> {
                Ship ship = Ship.load((CompoundTag) shipTag);
                manager.ships.put(ship.getId(), ship);
                manager.simpleIdToUuid.put(ship.getSimpleId(), ship.getId());
            });
        }
        
        manager.nextShipDimensionX = tag.getInt("nextShipDimensionX");
        manager.nextShipDimensionZ = tag.getInt("nextShipDimensionZ");
        manager.nextSimpleId = tag.contains("nextSimpleId") ? tag.getInt("nextSimpleId") : 1;
        
        return manager;
    }
}
