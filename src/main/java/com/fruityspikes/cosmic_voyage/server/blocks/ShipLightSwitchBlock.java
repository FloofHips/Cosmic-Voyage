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
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(UP, true)));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return null;
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            this.click(pState.cycle(UP), pLevel, pPos, (Player)null);
            return InteractionResult.CONSUME;
        }
    }

    public void click(BlockState pState, Level pLevel, BlockPos pPos, @Nullable Player pPlayer) {
        SpaceshipManager manager = SpaceshipManager.get((ServerLevel) pLevel);
        Ship ship = manager.getShipByPosition(pPos);
        pLevel.setBlock(pPos, pState, 3);
        if (ship != null) {
            ShipRoom room = ship.getRoomByWorldPos(pPos);
            if (room != null) {
                if (pState.getValue(UP)) {
                    room.turnOnLights();
                } else {
                    room.turnOffLights();
                }
            }
        }

        playSound(pPlayer, pLevel, pPos, pState);
        pLevel.gameEvent(pPlayer, pState.getValue(UP) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pPos);
    }

    protected static void playSound(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        float f = (Boolean)pState.getValue(UP) ? 1.0F : 1.5F;
        pLevel.playSound(pPlayer, pPos, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.3F, f);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{FACING, UP});
    }
}
