package com.fruityspikes.cosmic_voyage.server.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShipEntity extends Entity {

    public ShipEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}
//    public void animateHurt(float pYaw) {
//        this.setHurtDir(-this.getHurtDir());
//        this.setHurtTime(10);
//        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
//    }

    public boolean canCollideWith(Entity pEntity) {
        return true;
    }
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    public InteractionResult interact(Player player, InteractionHand hand) {
      if (!this.level().isClientSide && player instanceof ServerPlayer) {
//            ServerPlayer serverPlayer = (ServerPlayer) player;
//            ServerLevel nether = serverPlayer.getServer().getLevel(Level.NETHER);
//            if (nether != null) {
//                serverPlayer.changeDimension(nether);
//                serverPlayer.teleportTo(0, 100, 0); // Teleport to specific coordinates in the Nether
//                serverPlayer.playSound(SoundEvents.PORTAL_TRAVEL, 1.0f, 1.0f);
//            }
//
//            serverPlayer.setAsInsidePortal(this, this.blockPosition());
//
          System.out.println("waddup");
          this.playSound(SoundEvents.VAULT_INSERT_ITEM, 1.0f, 1.0f);
          return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}