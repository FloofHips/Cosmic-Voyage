package com.fruityspikes.cosmic_voyage.server.blocks;

import com.fruityspikes.cosmic_voyage.server.registries.CVItemRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;
import java.util.Random;

public class ShipRoomGateBlock extends Block {
    public static final MapCodec<ShipRoomGateBlock> CODEC = simpleCodec(ShipRoomGateBlock::new);
    public static final IntegerProperty PIECE = IntegerProperty.create("piece", 0, 5);
    private Direction lastHitFace;

    public ShipRoomGateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(PIECE, 0));
    }

    @Override
    public MapCodec<ShipRoomGateBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIECE);
    }

    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (isRoomUpgrade(pStack)) {
            lastHitFace = pHitResult.getDirection().getOpposite();

            if (!pLevel.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) pLevel;

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && y == 0 && z == 0) continue;
                            BlockPos particlePos = pPos.offset(x, y, z);
                            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, particlePos.getX() + 0.5, particlePos.getY() + 0.5, particlePos.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);
                        }
                    }
                }

                serverLevel.playSound(null, pPos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0f, 1.0f);

                // Schedule a tick update for 1 second later (20 ticks)
                serverLevel.scheduleTick(pPos, this, 20);
            }
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        else {
            return pHand == InteractionHand.MAIN_HAND && isRoomUpgrade(pPlayer.getItemInHand(InteractionHand.OFF_HAND)) ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    private boolean isRoomUpgrade(ItemStack pStack) {
        return pStack.is(CVItemRegistry.ROOM_UPGRADE);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                BlockPos targetPos = pos.offset(x, y, 0);
                if (level.getBlockState(targetPos).getBlock() instanceof ShipRoomGateBlock) {
                    level.removeBlock(targetPos, false);
                }
            }
        }
        level.playSound(null, pos, SoundEvents.VAULT_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

        BlockPos adjacentChunkPos = getAdjacentChunkPos(pos, lastHitFace);
        placeStructure(level, adjacentChunkPos);
    }
    private BlockPos getAdjacentChunkPos(BlockPos pos, Direction face) {
        int chunkX = pos.getX() >> 4;
        int chunkY = pos.getY() >> 4;
        int chunkZ = pos.getZ() >> 4;

        switch (face) {
            case UP:
                return new BlockPos(chunkX << 4, (chunkY + 1) << 4, chunkZ << 4);
            case DOWN:
                return new BlockPos(chunkX << 4, (chunkY - 1) << 4, chunkZ << 4);
            case NORTH:
                return new BlockPos(chunkX << 4, chunkY << 4, (chunkZ - 1) << 4);
            case SOUTH:
                return new BlockPos(chunkX << 4, chunkY << 4, (chunkZ + 1) << 4);
            case WEST:
                return new BlockPos((chunkX - 1) << 4, chunkY << 4, chunkZ << 4);
            case EAST:
                return new BlockPos((chunkX + 1) << 4, chunkY << 4, chunkZ << 4);
            default:
                return pos;
        }
    }
    private void placeStructure(ServerLevel level, BlockPos pos) {
        Random random = new Random();
        BlockState[] woolColors = {
                Blocks.WHITE_WOOL.defaultBlockState(),
                Blocks.ORANGE_WOOL.defaultBlockState(),
                Blocks.MAGENTA_WOOL.defaultBlockState(),
                Blocks.LIGHT_BLUE_WOOL.defaultBlockState(),
                Blocks.YELLOW_WOOL.defaultBlockState(),
                Blocks.LIME_WOOL.defaultBlockState(),
                Blocks.PINK_WOOL.defaultBlockState(),
                Blocks.GRAY_WOOL.defaultBlockState(),
                Blocks.LIGHT_GRAY_WOOL.defaultBlockState(),
                Blocks.CYAN_WOOL.defaultBlockState(),
                Blocks.PURPLE_WOOL.defaultBlockState(),
                Blocks.BLUE_WOOL.defaultBlockState(),
                Blocks.BROWN_WOOL.defaultBlockState(),
                Blocks.GREEN_WOOL.defaultBlockState(),
                Blocks.RED_WOOL.defaultBlockState(),
                Blocks.BLACK_WOOL.defaultBlockState()
        };

        BlockState randomWool = woolColors[random.nextInt(woolColors.length)];

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos targetPos = pos.offset(x, y, z);
                    level.setBlock(targetPos, randomWool, 3);
                }
            }
        }
    }
}
