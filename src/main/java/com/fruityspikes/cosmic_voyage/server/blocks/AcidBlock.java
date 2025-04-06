package com.fruityspikes.cosmic_voyage.server.blocks;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class AcidBlock extends LiquidBlock {
    public AcidBlock(FlowingFluid p_54694_, Properties p_54695_) {
        super(p_54694_, p_54695_);
    }
    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        if(pState.getValue(LEVEL)==0)
            return RenderShape.MODEL;
        return RenderShape.INVISIBLE;
    }
}
