package com.fruityspikes.cosmic_voyage.server.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChunkDelimiterItem extends Item {

    public ChunkDelimiterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, net.minecraft.world.level.ClipContext.Fluid.NONE);

        if (hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            BlockPos hitPos = hitResult.getBlockPos();
            BlockPos subChunkCorner = new BlockPos(
                    (hitPos.getX() >> 4) << 4,
                    (hitPos.getY() >> 4) << 4,
                    (hitPos.getZ() >> 4) << 4
            );

            placeCornerBlocks(level, subChunkCorner);
            level.playSound(player, subChunkCorner, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (!player.isCreative()) {
                itemstack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }

        return InteractionResultHolder.pass(itemstack);
    }

    private void placeCornerBlocks(Level level, BlockPos subChunkCorner) {
        BlockState blockState = Blocks.STONE.defaultBlockState();

        level.setBlock(subChunkCorner, blockState, 3);
        level.setBlock(subChunkCorner.offset(15, 0, 0), blockState, 3);
        level.setBlock(subChunkCorner.offset(0, 15, 0), blockState, 3);
        level.setBlock(subChunkCorner.offset(0, 0, 15), blockState, 3);
        level.setBlock(subChunkCorner.offset(15, 15, 0), blockState, 3);
        level.setBlock(subChunkCorner.offset(0, 15, 15), blockState, 3);
        level.setBlock(subChunkCorner.offset(15, 0, 15), blockState, 3);
        level.setBlock(subChunkCorner.offset(15, 15, 15), blockState, 3);
    }
}
