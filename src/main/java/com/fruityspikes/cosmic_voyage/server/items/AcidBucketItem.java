package com.fruityspikes.cosmic_voyage.server.items;

import com.fruityspikes.cosmic_voyage.server.registries.CVFluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AcidBucketItem extends BucketItem {
    public AcidBucketItem(Fluid pContent, Properties pProperties) {
        super(pContent, pProperties);
    }

    public Fluid chooseLiquid(Level pLevel, BlockPos pPos){
        for (Direction direction : Direction.values()) {
            Fluid fluid = pLevel.getBlockState(pPos.relative(direction)).getFluidState().getType();
            if(fluid instanceof EmptyFluid)
                continue;

            for (DeferredHolder<Fluid, Fluid> holder : CVFluidRegistry.ACID_FLUIDS_STILL.values()) {
                if (fluid.isSame(holder.get())) {
                    return holder.get();
                }
            }
        }
        List<DeferredHolder<Fluid, Fluid>> fluids =
                new ArrayList<>(CVFluidRegistry.ACID_FLUIDS_STILL.values());
            return fluids.get(pLevel.random.nextInt(fluids.size())).get();
    }
    public boolean emptyContents(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, @Nullable BlockHitResult pResult, @Nullable ItemStack container) {
        Fluid var7 = this.content;
        if (!(var7 instanceof FlowingFluid flowingfluid)) {
            return false;
        } else {
            boolean $$8;
            BlockState blockstate;
            boolean flag2;
            Block $$7;
            label78: {
                label77: {
                    blockstate = pLevel.getBlockState(pPos);
                    $$7 = blockstate.getBlock();
                    $$8 = blockstate.canBeReplaced(this.content);
                    if (!blockstate.isAir() && !$$8) {
                        if (!($$7 instanceof LiquidBlockContainer)) {
                            break label77;
                        }

                        LiquidBlockContainer liquidblockcontainer = (LiquidBlockContainer)$$7;
                        if (!liquidblockcontainer.canPlaceLiquid(pPlayer, pLevel, pPos, blockstate, this.content)) {
                            break label77;
                        }
                    }

                    flag2 = true;
                    break label78;
                }

                flag2 = false;
            }

            Optional<FluidStack> containedFluidStack = Optional.ofNullable(container).flatMap(FluidUtil::getFluidContained);
            if (!flag2) {
                return pResult != null && this.emptyContents(pPlayer, pLevel, pResult.getBlockPos().relative(pResult.getDirection()), (BlockHitResult)null, container);
            } else if (containedFluidStack.isPresent() && this.content.getFluidType().isVaporizedOnPlacement(pLevel, pPos, (FluidStack)containedFluidStack.get())) {
                this.content.getFluidType().onVaporize(pPlayer, pLevel, pPos, (FluidStack)containedFluidStack.get());
                return true;
            } else if (pLevel.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
                int l = pPos.getX();
                int i = pPos.getY();
                int j = pPos.getZ();
                pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.8F);

                for(int k = 0; k < 8; ++k) {
                    pLevel.addParticle(ParticleTypes.LARGE_SMOKE, (double)l + Math.random(), (double)i + Math.random(), (double)j + Math.random(), 0.0, 0.0, 0.0);
                }

                return true;
            } else {
                if ($$7 instanceof LiquidBlockContainer) {
                    LiquidBlockContainer liquidblockcontainer1 = (LiquidBlockContainer)$$7;
                    if (liquidblockcontainer1.canPlaceLiquid(pPlayer, pLevel, pPos, blockstate, this.content)) {
                        liquidblockcontainer1.placeLiquid(pLevel, pPos, blockstate, flowingfluid.getSource(false));
                        this.playEmptySound(pPlayer, pLevel, pPos);
                        return true;
                    }
                }

                if (!pLevel.isClientSide && $$8 && !blockstate.liquid()) {
                    pLevel.destroyBlock(pPos, true);
                }

                if (!pLevel.setBlock(pPos, chooseLiquid(pLevel, pPos).defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(pPlayer, pLevel, pPos);
                    return true;
                }
            }
        }
    }
}
