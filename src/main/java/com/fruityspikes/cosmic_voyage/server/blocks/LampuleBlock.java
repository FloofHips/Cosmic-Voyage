package com.fruityspikes.cosmic_voyage.server.blocks;

import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.ShipRoom;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class LampuleBlock extends RodBlock implements IShipLight {
    public static final MapCodec<LampuleBlock> CODEC = simpleCodec(LampuleBlock::new);
    public static final BooleanProperty LIT;
    public LampuleBlock(Properties p_154339_) {
        super(p_154339_);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(FACING, Direction.UP));
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
    }
    @Override
    protected MapCodec<? extends RodBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        if (!pLevel.isClientSide) {
            SpaceshipManager manager = SpaceshipManager.get((ServerLevel) pLevel);
            Ship ship = manager.getShipByPosition(pPos);
            if (ship != null) {
                ShipRoom room = ship.getRoomByWorldPos(pPos);
                if (room != null) {
                    room.addLight(pState, pPos, pLevel);
                }
            }
        }
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        if (!pLevel.isClientSide) {
            SpaceshipManager manager = SpaceshipManager.get((ServerLevel) pLevel);
            Ship ship = manager.getShipByPosition(pPos);
            if (ship != null) {
                ShipRoom room = ship.getRoomByWorldPos(pPos);
                if (room != null) {
                    room.removeLight(pState, pPos, pLevel);
                }
            }
        }
    }

    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide) {
            BlockState blockstate = (BlockState)pState.cycle(LIT);
            return InteractionResult.SUCCESS;
        } else {
            this.toggle(pState, pLevel, pPos, (Player)null);
            return InteractionResult.CONSUME;
        }
    }
    public void toggle(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
        if(pState.getValue(LIT))
            turnOff(pState, pLevel, pPos, pPlayer);
        else
            turnOn(pState, pLevel, pPos, pPlayer);
    }
    protected static void playSound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        float f = (Boolean)pState.getValue(LIT) ? 0.6F : 1.0F;
        pLevel.playSound(pPlayer, pPos, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 0.6F, f);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{FACING, LIT});
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return (state.getValue(LIT) ? 15 : 0);
    }

    static {
        LIT = BlockStateProperties.LIT;
    }

    @Override
    public void turnOn(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
        pLevel.setBlock(pPos, pState.setValue(LIT, true), 2);
        playSound(pPlayer, pLevel, pPos, pState);
        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_ACTIVATE, pPos);
    }

    @Override
    public void turnOff(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
        pLevel.setBlock(pPos, pState.setValue(LIT, false), 2);
        playSound(pPlayer, pLevel, pPos, pState);
        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_DEACTIVATE, pPos);
    }
}
