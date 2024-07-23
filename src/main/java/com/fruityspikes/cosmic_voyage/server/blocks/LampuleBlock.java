package com.fruityspikes.cosmic_voyage.server.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class LampuleBlock extends RodBlock {
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
        pLevel.setBlock(pPos, (BlockState)pState.cycle(LIT), 2);
        playSound(pPlayer, pLevel, pPos, pState);
        pLevel.gameEvent(pPlayer, (Boolean)pState.getValue(LIT) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pPos);
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
}
