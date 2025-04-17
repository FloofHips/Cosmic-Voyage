package com.fruityspikes.cosmic_voyage.server.util;

import com.fruityspikes.cosmic_voyage.server.dimension.SpaceshipDimension;
import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class TeleportUtil {
    private static final Random RANDOM = new Random();
    private static final String RETURN_POS_TAG = "ReturnPosition";

    public static void teleportToShip(ServerPlayer player, Ship ship, ServerLevel sourceLevel) {
        ServerLevel shipDimension = sourceLevel.getServer().getLevel(SpaceshipDimension.DIMENSION_KEY);
        if (shipDimension == null) {
            return;
        }
        storeReturnPosition(player, ship);
        playTeleportEffects(sourceLevel, player.position(), true);
        Vec3 targetPos = Vec3.atBottomCenterOf(ship.getDimensionLocation());
        player.teleportTo(shipDimension, 
            targetPos.x+25+16, targetPos.y-26, targetPos.z+8.5+16,
            90, 0);

        playTeleportEffects(shipDimension, targetPos, false);
    }

    public static void teleportFromShip(ServerPlayer player, Ship ship, ServerLevel shipDimension) {
        ServerLevel overworld = shipDimension.getServer().overworld();
        Vec3 returnPos = getReturnPosition(player, ship);
        if (returnPos == null) {
            returnPos = Vec3.atBottomCenterOf(overworld.getSharedSpawnPos());
        }

        player.teleportTo(overworld,
            returnPos.x, returnPos.y, returnPos.z,
            player.getYRot(), player.getXRot());

        clearReturnPosition(player, ship);
    }

    public static void exitShip(ServerPlayer player, Ship ship, ServerLevel shipDimension) {
        MinecraftServer server = shipDimension.getServer();
        BlockPos returnPos = ship.getEntityLocation();
        ResourceLocation dimensionRL = ship.getDimension();

        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, dimensionRL);
        ServerLevel targetDim = server.getLevel(dimensionKey);

        if(targetDim!=null)
            player.teleportTo(targetDim,
                returnPos.getX(), returnPos.getY(), returnPos.getZ(),
                player.getYRot(), player.getXRot());
    }

    private static void playTeleportEffects(ServerLevel level, Vec3 pos, boolean isDeparture) {
        // Play sound effect
        level.playSound(null, pos.x, pos.y, pos.z,
            isDeparture ? SoundEvents.PORTAL_TRAVEL : SoundEvents.PORTAL_TRIGGER,
            SoundSource.PLAYERS, 1.0F, isDeparture ? 0.8F : 1.2F);

        // Spawn particles in a spiral pattern
        double radius = 1.0;
        double height = 2.5;
        int particleCount = 50;

        for (int i = 0; i < particleCount; i++) {
            double progress = i / (double) particleCount;
            double angle = progress * Math.PI * 4; // Two full rotations
            double spiralRadius = radius * (1 - progress); // Radius decreases as it goes up
            double x = pos.x + Math.cos(angle) * spiralRadius;
            double y = pos.y + progress * height;
            double z = pos.z + Math.sin(angle) * spiralRadius;

            // Add some randomness to particle positions
            x += (RANDOM.nextDouble() - 0.5) * 0.2;
            y += (RANDOM.nextDouble() - 0.5) * 0.2;
            z += (RANDOM.nextDouble() - 0.5) * 0.2;

            // Spawn both portal and reverse_portal particles for a cool effect
            level.sendParticles(isDeparture ? ParticleTypes.PORTAL : ParticleTypes.REVERSE_PORTAL,
                x, y, z, 1, 0, 0, 0, 0);
            
            // Add some end rod particles for extra flair
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.END_ROD,
                    x, y, z, 1, 0, 0, 0, 0.05);
            }
        }
    }

    private static void storeReturnPosition(ServerPlayer player, Ship ship) {
        CompoundTag playerData = player.getPersistentData();
        CompoundTag returnPosData = new CompoundTag();
        Vec3 pos = player.position();
        returnPosData.putDouble("x", pos.x);
        returnPosData.putDouble("y", pos.y);
        returnPosData.putDouble("z", pos.z);
        playerData.put(RETURN_POS_TAG + "_" + ship.getId(), returnPosData);
    }

    private static Vec3 getReturnPosition(ServerPlayer player, Ship ship) {
        CompoundTag playerData = player.getPersistentData();
        String tag = RETURN_POS_TAG + "_" + ship.getId();
        if (playerData.contains(tag)) {
            CompoundTag returnPosData = playerData.getCompound(tag);
            return new Vec3(
                returnPosData.getDouble("x"),
                returnPosData.getDouble("y"),
                returnPosData.getDouble("z")
            );
        }
        return null;
    }

    private static void clearReturnPosition(ServerPlayer player, Ship ship) {
        CompoundTag playerData = player.getPersistentData();
        playerData.remove(RETURN_POS_TAG + "_" + ship.getId());
    }
}
