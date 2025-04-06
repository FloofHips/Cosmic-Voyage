package com.fruityspikes.cosmic_voyage.server.blocks;

import com.fruityspikes.cosmic_voyage.server.registries.CVFluidRegistry;
import com.fruityspikes.cosmic_voyage.server.registries.CVItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class AcidFluid extends FlowingFluid {
    @Override
    public Item getBucket() {
        return CVItemRegistry.ACID_BUCKETS.get("yellow").get().asItem();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return 0;
    }
//    @Override
//    public boolean isSame(Fluid pFluid) {
//        System.out.println("hi");
//        if(pFluid.isSame(CVFluidRegistry.ACID_FLUIDS_STILL.get("yellow").get()))
//            return true;
//        if(pFluid.isSame(CVFluidRegistry.ACID_FLUIDS_STILL.get("olive").get()))
//            return true;
//        if(pFluid.isSame(CVFluidRegistry.ACID_FLUIDS_STILL.get("green").get()))
//            return true;
//        if(pFluid.isSame(CVFluidRegistry.ACID_FLUIDS_STILL.get("turquoise").get()))
//            return true;
//        return false;
//    }
    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        return null;
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return false;
    }

    @Override
    public Fluid getFlowing() {
        return null;
    }

    @Override
    public Fluid getSource() {
        return null;
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return 0;
    }

    @Override
    protected int getDropOff(LevelReader levelReader) {
        return 0;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return 0;
    }
}
