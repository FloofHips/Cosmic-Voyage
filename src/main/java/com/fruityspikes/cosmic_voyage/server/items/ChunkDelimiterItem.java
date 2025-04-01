package com.fruityspikes.cosmic_voyage.server.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ChunkDelimiterItem extends DevBuildingItem {
    public ChunkDelimiterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        BlockPos placementPos;
        HitResult hitResult = player.pick(5.0, 0.0F, false);

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            placementPos = ((BlockHitResult)hitResult).getBlockPos();
        } else {
            Vec3 lookVec = player.getLookAngle();
            placementPos = player.blockPosition().offset(
                    (int)Math.round(lookVec.x * 5),
                    (int)Math.round(lookVec.y * 5),
                    (int)Math.round(lookVec.z * 5)
            );
        }

        BlockState placementBlock = getPlacementBlock(player);

        int subChunkX = placementPos.getX() >> 4;
        int subChunkY = placementPos.getY() >> 4;
        int subChunkZ = placementPos.getZ() >> 4;

        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    BlockPos cornerPos = new BlockPos(
                            (subChunkX << 4) + (x * 15),
                            (subChunkY << 4) + (y * 15),
                            (subChunkZ << 4) + (z * 15)
                    );

                    if (level.isEmptyBlock(cornerPos)) {
                        level.setBlock(cornerPos, placementBlock, 3);
                        playPlaceSound(level, cornerPos);
                        spawnParticles(level, cornerPos);
                    }
                }
            }
        }

        return InteractionResultHolder.success(stack);
    }
}