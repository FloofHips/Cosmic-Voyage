package com.fruityspikes.cosmic_voyage.server.ships;

import com.fruityspikes.cosmic_voyage.CosmicVoyage;
import com.fruityspikes.cosmic_voyage.server.dimension.SpaceshipDimension;
import com.fruityspikes.cosmic_voyage.server.entities.ShipEntity;
import com.fruityspikes.cosmic_voyage.server.registries.CVEntityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
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
    private static SpaceshipManager instance;
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
        if (instance == null) {
            MinecraftServer server = level.getServer();
            ServerLevel overworld = server.overworld();
            instance = overworld.getDataStorage().computeIfAbsent(
                    new SavedData.Factory<>(
                            SpaceshipManager::new,
                            (tag, provider) -> SpaceshipManager.load(tag),
                            DataFixTypes.LEVEL
                    ),
                    SAVED_DATA_NAME
            );
        }
        return instance;
    }

    public static SpaceshipManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SpaceshipManager has not been initialized!");
        }
        return instance;
    }
    
    public Ship createShip(BlockPos entityLocation, ServerLevel level) {
        // Calculate next available position in spaceship dimension
        BlockPos shipDimensionPos = new BlockPos(nextShipDimensionX, 64, nextShipDimensionZ);
        
        // Create new ship with unique ID and simple ID
        UUID shipId = UUID.randomUUID();
        Ship ship = new Ship(shipId, nextSimpleId, entityLocation, shipDimensionPos);
        ships.put(shipId, ship);
        simpleIdToUuid.put(nextSimpleId, shipId);

        // Create ship entity
        ShipEntity shipEntity = new ShipEntity(CVEntityRegistry.SHIP.get(), level);
        shipEntity.setPos(entityLocation.getX() + 0.5, entityLocation.getY(), entityLocation.getZ() + 0.5);
        shipEntity.setShipId(shipId);
        level.addFreshEntity(shipEntity);

        ServerLevel spaceshipDimension = level.getServer().getLevel(SpaceshipDimension.DIMENSION_KEY);
        if (spaceshipDimension != null) {
            ChunkPos chunkPos = new ChunkPos(shipDimensionPos);  // Convert BlockPos to ChunkPos
            spaceshipDimension.getChunkSource().addRegionTicket(TicketType.START, chunkPos, 1, Unit.INSTANCE);  // Use Unit.INSTANCE as the value
            ship.initializeStructure(spaceshipDimension);
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
        // Move to next position, ensuring ships are spaced apart
        nextShipDimensionX += SHIP_SPACING;
        
        // If we've gone too far in X direction, reset X and increment Z
        if (nextShipDimensionX > 10000) {
            nextShipDimensionX = 0;
            nextShipDimensionZ += SHIP_SPACING;
        }
        
        this.setDirty();
    }

    private static SpaceshipManager load(CompoundTag tag) {
        SpaceshipManager manager = new SpaceshipManager();
        
        if (tag.contains("ships")) {
            ListTag shipsList = tag.getList("ships", 10); // 10 is the NBT ID for CompoundTag
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
