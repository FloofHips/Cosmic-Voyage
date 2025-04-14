package com.fruityspikes.cosmic_voyage.server.blocks;

import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.ShipRoom;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

import static com.fruityspikes.cosmic_voyage.server.blocks.LampuleBlock.LIT;

public interface IShipLight {
    SoundEvent turnOnSound = SoundEvents.VAULT_ACTIVATE;
    SoundEvent turnOffSound = SoundEvents.VAULT_DEACTIVATE;
    default void turnOn(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer, SoundEvent pSound){
        pLevel.setBlock(pPos, pState.setValue(LIT, true), 2);
        playSound(pPlayer, pLevel, pPos, pState, pSound);
        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_ACTIVATE, pPos);
    }
    default void turnOff(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer, SoundEvent pSound){

        pLevel.setBlock(pPos, pState.setValue(LIT, false), 2);
        playSound(pPlayer, pLevel, pPos, pState, pSound);
        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_DEACTIVATE, pPos);
    }
    default void playSound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState, SoundEvent pSound) {
        float f = (Boolean)pState.getValue(LIT) ? 0.6F : 1.0F;
        pLevel.playSound(pPlayer, pPos, pSound, SoundSource.BLOCKS, 0.6F, f);
    }
    default void onRemove(BlockState pState, Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide) {
            SpaceshipManager manager = SpaceshipManager.get((ServerLevel) pLevel);
            Ship ship = manager.getShipByPosition(pPos);
            if (ship != null) {
                ShipRoom room = ship.getRoomByWorldPos(pPos);
                if (room != null) {
                    room.removeLight(pPos);
                }
            }
        }
    }
    default void onPlace(BlockState pState, Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide) {
            SpaceshipManager manager = SpaceshipManager.get((ServerLevel) pLevel);
            Ship ship = manager.getShipByPosition(pPos);
            if (ship != null) {
                ShipRoom room = ship.getRoomByWorldPos(pPos);
                if (room != null) {
                    room.addLight(pPos);
                }
            }
        }
    }
    default SoundEvent getTurnOnSound(){
        return turnOnSound;
    }

    default SoundEvent getTurnOffSound(){
        return turnOnSound;
    }

}
