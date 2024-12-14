package com.fruityspikes.cosmic_voyage.server.entities;

import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import com.fruityspikes.cosmic_voyage.server.util.TeleportUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.UUID;

public class ShipEntity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final EntityDataAccessor<String> SHIP_ID = SynchedEntityData.defineId(ShipEntity.class, EntityDataSerializers.STRING);

    public ShipEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;  // Prevent physics calculations
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {  // Changed from defineSynchedData(Builder)
        this.entityData.set(SHIP_ID, "");  // Changed to use entityData directly
    }

    public UUID getShipId() {
        String idString = this.entityData.get(SHIP_ID);
        return idString.isEmpty() ? null : UUID.fromString(idString);
    }

    public void setShipId(UUID id) {
        this.entityData.set(SHIP_ID, id.toString());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("ShipId")) {
            setShipId(compound.getUUID("ShipId"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        UUID shipId = getShipId();
        if (shipId != null) {
            compound.putUUID("ShipId", shipId);
        }
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return false;  // Prevent collisions
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            UUID shipId = getShipId();
            if (shipId == null) {
                LOGGER.error("Ship entity {} has no associated ship ID!", this);
                return InteractionResult.FAIL;
            }

            ServerLevel level = (ServerLevel) this.level();
            SpaceshipManager manager = SpaceshipManager.get(level);
            Ship ship = manager.getShip(shipId);

            if (ship == null) {
                LOGGER.error("Could not find ship with ID {} for entity {}", shipId, this);
                return InteractionResult.FAIL;
            }

            // Update ship entity location in case it moved
            ship.setEntityLocation(this.blockPosition());
            
            // Play interaction effect
            playInteractionEffect(level);

            // Teleport player to ship
            TeleportUtil.teleportToShip(serverPlayer, ship, level);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    private void playInteractionEffect(ServerLevel level) {
        Vec3 pos = this.position();
        
        // Play activation sound
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);

        // Create a ring of particles around the ship
        double radius = 1.5;
        int particles = 20;
        for (int i = 0; i < particles; i++) {
            double angle = (i / (double) particles) * Math.PI * 2;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            
            // Spawn glowing particles in a ring
            level.sendParticles(ParticleTypes.END_ROD,
                x, pos.y + 0.5, z,
                1, 0, 0.1, 0, 0.05);

            // Add some sparkle particles
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    x, pos.y + 1.0, z,
                    3, 0.1, 0.1, 0.1, 0.05);
            }
        }

        // Add some vertical beams of particles
        for (int i = 0; i < 4; i++) {
            double angle = (i / 4.0) * Math.PI * 2;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            
            for (int y = 0; y < 8; y++) {
                level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    x, pos.y + y * 0.25, z,
                    1, 0, 0, 0, 0);
            }
        }
    }
}