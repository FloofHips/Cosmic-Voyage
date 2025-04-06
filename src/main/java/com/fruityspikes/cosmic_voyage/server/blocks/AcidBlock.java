package com.fruityspikes.cosmic_voyage.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

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

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if (pRandom.nextInt(10) == 0)
            if(pLevel.getBlockState(pPos.relative(Direction.UP)).isAir())
                addParticle(ParticleTypes.TRIAL_OMEN, Vec3.atLowerCornerOf(pPos), pLevel);

    }

    private static void addParticle(SimpleParticleType pParticleType, Vec3 pPos, Level pLevel) {
        pLevel.addParticle(pParticleType, pPos.x()+0.5F, pPos.y()+1F, pPos.z()+0.5F, 0.0, 0.0, 0.0);
    }
}
