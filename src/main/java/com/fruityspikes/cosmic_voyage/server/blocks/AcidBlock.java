package com.fruityspikes.cosmic_voyage.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AcidBlock extends Block {
    public AcidBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        // Check if we should convert to flowing water
        checkForConversion(level, currentPos);
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }


    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        System.out.println("lala");
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        checkForConversion(pLevel, pPos);
    }

    private void checkForConversion(LevelAccessor level, BlockPos pos) {
        // Check the block below
        if (level.getBlockState(pos.below()).isAir()) {
            level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
            return;
        }

        // Check all horizontal directions
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos adjacentPos = pos.relative(direction);
            if (level.getBlockState(adjacentPos).isAir()) {
                level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                return;
            }
        }
    }
}