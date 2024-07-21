package com.fruityspikes.cosmic_voyage.server.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PlatformPlacerItem extends Item {

    public PlatformPlacerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        ItemStack offHandItem = player.getOffhandItem();
        BlockState blockState;

        if (offHandItem.getItem() instanceof BlockItem) {
            blockState = ((BlockItem) offHandItem.getItem()).getBlock().defaultBlockState();
        } else {
            blockState = Blocks.STONE.defaultBlockState();
        }

        BlockPos subChunkCorner = new BlockPos(
                (pos.getX() >> 4) << 4,
                (pos.getY() >> 4) << 4,
                (pos.getZ() >> 4) << 4
        );

        placePlatform(level, subChunkCorner, face, blockState);

        level.playSound(player, subChunkCorner, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!player.isCreative()) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void placePlatform(Level level, BlockPos subChunkCorner, Direction face, BlockState blockState) {
        switch (face) {
            case UP:
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        level.setBlock(subChunkCorner.offset(x, 15, z), blockState, 3);
                    }
                }
                break;
            case DOWN:
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        level.setBlock(subChunkCorner.offset(x, 0, z), blockState, 3);
                    }
                }
                break;
            case NORTH:
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        level.setBlock(subChunkCorner.offset(x, y, 0), blockState, 3);
                    }
                }
                break;
            case SOUTH:
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        level.setBlock(subChunkCorner.offset(x, y, 15), blockState, 3);
                    }
                }
                break;
            case EAST:
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        level.setBlock(subChunkCorner.offset(15, y, z), blockState, 3);
                    }
                }
                break;
            case WEST:
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        level.setBlock(subChunkCorner.offset(0, y, z), blockState, 3);
                    }
                }
                break;
        }
    }
}
