package com.fruityspikes.cosmic_voyage.server.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ShipLightSwitchBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<ShipLightSwitchBlock> CODEC = simpleCodec(ShipLightSwitchBlock::new);
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public ShipLightSwitchBlock(Properties p_54633_) {
        super(p_54633_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(UP, false)));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }

    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.pull(pState.cycle(UP), pLevel, pPos, (Player)null);
            return InteractionResult.CONSUME;
        }
    }

    public void pull(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
        BlockState blockstate = (BlockState)pState.cycle(UP);
        pLevel.setBlock(pPos, pState, 3);
        //Ship
        playSound(pPlayer, pLevel, pPos, pState);
        pLevel.gameEvent(pPlayer, GameEvent.BLOCK_ACTIVATE, pPos);
    }

    protected static void playSound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        float f = (Boolean)pState.getValue(UP) ? 1.0F : 1.5F;
        pLevel.playSound(pPlayer, pPos, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.3F, f);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{FACING, UP});
    }
}
