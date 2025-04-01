package com.fruityspikes.cosmic_voyage.server.items;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FillerItem extends DevBuildingItem {
    private static final String POS1_KEY = "Pos1";
    private static final String POS2_KEY = "Pos2";

    public FillerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown()) {
            clearPosition(stack, POS1_KEY);
            clearPosition(stack, POS2_KEY);
            playErrorSound(level, pos);
            return InteractionResult.CONSUME;
        }

        BlockPos pos1 = getStoredPosition(stack, POS1_KEY);
        BlockPos pos2 = getStoredPosition(stack, POS2_KEY);

        if (pos1 == null) {
            storePosition(stack, POS1_KEY, pos);
            spawnParticles(level, pos);
            level.playSound(null, pos, SoundEvents.VAULT_INSERT_ITEM, SoundSource.BLOCKS, 1.0F, 2.0F);
            return InteractionResult.CONSUME;
        } else if (pos2 == null) {
            storePosition(stack, POS2_KEY, pos);
            spawnParticles(level, pos);
            level.playSound(null, pos, SoundEvents.VAULT_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, 1.5F);
            return InteractionResult.CONSUME;
        } else {
            BlockState placementBlock = getPlacementBlock(player);

            int minX = Math.min(pos1.getX(), pos2.getX());
            int minY = Math.min(pos1.getY(), pos2.getY());
            int minZ = Math.min(pos1.getZ(), pos2.getZ());
            int maxX = Math.max(pos1.getX(), pos2.getX());
            int maxY = Math.max(pos1.getY(), pos2.getY());
            int maxZ = Math.max(pos1.getZ(), pos2.getZ());

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        BlockPos fillPos = new BlockPos(x, y, z);
                        level.setBlock(fillPos, placementBlock, 3);
                        playPlaceSound(level, fillPos);
                    }
                }
            }

            clearPosition(stack, POS1_KEY);
            clearPosition(stack, POS2_KEY);
            spawnParticles(level, pos);
            return InteractionResult.CONSUME;
        }
    }
}