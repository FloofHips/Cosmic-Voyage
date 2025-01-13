package com.fruityspikes.cosmic_voyage.server.entities;

import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import com.fruityspikes.cosmic_voyage.server.util.TeleportUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.util.Objects;
import java.util.UUID;

public class ShipEntity extends Entity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ShipEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    public UUID getShipId() {
        return this.getUUID();
    }

    public void setShipId(UUID id) {
        this.setUUID(id);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    protected Entity.MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean isPickable() {
        return true;
    }

    public ItemStack getPickResult() {
        return  new ItemStack(Items.FIREWORK_ROCKET);
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult interactionresult = super.interact(player, hand);
        ServerLevel level = (ServerLevel) this.level();
        SpaceshipManager manager = SpaceshipManager.get(level);
        Ship ship = manager.getShip(getShipId());

        if (interactionresult != InteractionResult.PASS) {
            System.out.println("A!");
            return interactionresult;
        } else if (player.isSecondaryUseActive()) {
            System.out.println("B!");
            return InteractionResult.PASS;
        } else  {
            if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                System.out.println("C!");
                ship.setEntityLocation(this.blockPosition());
                TeleportUtil.teleportToShip(serverPlayer, ship, level);
                return InteractionResult.CONSUME;
            } else {
                System.out.println("D!");
                return InteractionResult.SUCCESS;
            }
        }
    }

    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
        InteractionResult interactionresult = super.interact(pPlayer, pHand);
        ServerLevel level = null;
        SpaceshipManager manager;
        Ship ship = null;

        if(!this.level().isClientSide){
            level = Objects.requireNonNull(this.level().getServer()).overworld();
            manager = SpaceshipManager.get(level);
            ship = manager.getShip(getShipId());
        }
        if (interactionresult != InteractionResult.PASS) {
            return interactionresult;
        } else if (pPlayer.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else  {
            if (!this.level().isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
                assert ship != null;
                ship.setEntityLocation(this.blockPosition());
                TeleportUtil.teleportToShip(serverPlayer, ship, level);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
    }
}