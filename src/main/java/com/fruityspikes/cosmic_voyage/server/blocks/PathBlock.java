package com.fruityspikes.cosmic_voyage.server.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathBlock extends Block {
    public static final MapCodec<PathBlock> CODEC = simpleCodec(PathBlock::new);
    protected static final VoxelShape SHAPE;

    public MapCodec<PathBlock> codec() {
        return CODEC;
    }

    public PathBlock(BlockBehaviour.Properties p_153129_) {
        super(p_153129_);
    }
    protected VoxelShape getShape(BlockState p_153143_, BlockGetter p_153144_, BlockPos p_153145_, CollisionContext p_153146_) {
        return SHAPE;
    }

    protected boolean isPathfindable(BlockState p_153138_, PathComputationType p_153141_) {
        return false;
    }

    static {
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    }
}
