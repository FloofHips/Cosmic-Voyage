package com.fruityspikes.cosmic_voyage.server.blocks;

import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.ShipRoom;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ShipLightBlock extends Block implements IShipLight {
    public static final MapCodec<LampuleBlock> CODEC = simpleCodec(LampuleBlock::new);
    public static final BooleanProperty LIT;
    public ShipLightBlock(BlockBehaviour.Properties p_154339_) {
        super(p_154339_);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState();
    }
    @Override
    protected MapCodec<? extends RodBlock> codec() {
        return CODEC;
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
    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        onRemove(pState, pLevel, pPos);
    }
    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        onPlace(pState, pLevel, pPos);
    }
    public void toggle(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
        if(pState.getValue(LIT))
            turnOff(pState, pLevel, pPos, pPlayer, turnOnSound);
        else
            turnOn(pState, pLevel, pPos, pPlayer, turnOffSound);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{LIT});
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return (state.getValue(LIT) ? 15 : 0);
    }

    static {
        LIT = BlockStateProperties.LIT;
    }

//    @Override
//    public void turnOn(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
//        pLevel.setBlock(pPos, pState.setValue(LIT, true), 2);
//        playSound(pPlayer, pLevel, pPos, pState);
//        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_ACTIVATE, pPos);
//    }

//    @Override
//    public void turnOff(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
//        pLevel.setBlock(pPos, pState.setValue(LIT, false), 2);
//        playSound(pPlayer, pLevel, pPos, pState);
//        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_DEACTIVATE, pPos);
//    }
}
