package com.fruityspikes.cosmic_voyage.server.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class OutletBlock extends DirectionalBlock {
    public static final MapCodec<OutletBlock> CODEC = simpleCodec(OutletBlock::new);
    public OutletBlock(Properties p_52591_) {
        super(p_52591_);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP));
    }
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getClickedFace());
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}
