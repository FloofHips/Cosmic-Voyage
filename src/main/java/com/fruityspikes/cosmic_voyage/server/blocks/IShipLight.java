package com.fruityspikes.cosmic_voyage.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface IShipLight {
    public void turnOn(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer);
    public void turnOff(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer);
}
