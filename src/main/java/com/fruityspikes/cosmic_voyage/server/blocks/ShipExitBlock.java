package com.fruityspikes.cosmic_voyage.server.blocks;

import com.fruityspikes.cosmic_voyage.server.commands.ShipCommands;
import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import com.fruityspikes.cosmic_voyage.server.util.TeleportUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;

public class ShipExitBlock extends Block implements Portal {
    public static final MapCodec<ShipExitBlock> CODEC = simpleCodec(ShipExitBlock::new);
    public ShipExitBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public MapCodec<ShipExitBlock> codec() {
        return CODEC;
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);
        SpaceshipManager manager;
        pPos = pPos.offset(0,0,16);
        if(pLevel instanceof ServerLevel serverLevel && pEntity instanceof ServerPlayer player){

            manager = SpaceshipManager.get(serverLevel);
            Ship ship = manager.getShipByPosition(pPos);
            if(ship!=null)
                TeleportUtil.exitShip(player, ship, serverLevel);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel pLevel, Entity pEntity) {
        return Portal.super.getPortalTransitionTime(pLevel, pEntity);
    }

    @Override
    public @Nullable DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
        return null;
    }

    @Override
    public Transition getLocalTransition() {
        return Portal.super.getLocalTransition();
    }
}
