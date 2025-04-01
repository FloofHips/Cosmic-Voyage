package com.fruityspikes.cosmic_voyage.server.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlatformPlacerItem extends DevBuildingItem {
    public PlatformPlacerItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        Direction facing = getFacingDirection(player);
        BlockState placementBlock = getPlacementBlock(player);

        Vec3 lookVec = player.getLookAngle();
        BlockPos centerPos = player.blockPosition().offset(
                (int)Math.round(lookVec.x * 5),
                (int)Math.round(lookVec.y * 5),
                (int)Math.round(lookVec.z * 5)
        );

        BlockPos subChunkCorner = new BlockPos(
                (centerPos.getX() >> 4) << 4,
                (centerPos.getY() >> 4) << 4,
                (centerPos.getZ() >> 4) << 4
        );

        placePlatform(level, subChunkCorner, facing, placementBlock);

        BlockPos soundPos = subChunkCorner.offset(8, 8, 8);
        playPlaceSound(level, soundPos);

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        return InteractionResultHolder.success(stack);
    }

    private Direction getFacingDirection(Player player) {
        Vec3 lookVec = player.getLookAngle();
        return Direction.getNearest(lookVec.x, lookVec.y, lookVec.z);
    }

    private void placePlatform(Level level, BlockPos subChunkCorner, Direction face, BlockState blockState) {
        switch (face) {
            case UP:
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = subChunkCorner.offset(x, 15, z);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, blockState, 3);
                        }
                    }
                }
                break;

            case DOWN:
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        BlockPos pos = subChunkCorner.offset(x, 0, z);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, blockState, 3);
                        }
                    }
                }
                break;

            case NORTH:
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        BlockPos pos = subChunkCorner.offset(x, y, 0);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, blockState, 3);
                        }
                    }
                }
                break;

            case SOUTH:
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        BlockPos pos = subChunkCorner.offset(x, y, 15);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, blockState, 3);
                        }
                    }
                }
                break;

            case EAST:
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        BlockPos pos = subChunkCorner.offset(15, y, z);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, blockState, 3);
                        }
                    }
                }
                break;

            case WEST:
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        BlockPos pos = subChunkCorner.offset(0, y, z);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlock(pos, blockState, 3);
                        }
                    }
                }
                break;
        }
    }
}