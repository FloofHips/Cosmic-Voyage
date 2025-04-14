package com.fruityspikes.cosmic_voyage.server.blocks;

import com.fruityspikes.cosmic_voyage.server.registries.CVItemRegistry;
import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.ShipRoom;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
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
                            serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                    particlePos.getX() + 0.5,
                                    particlePos.getY() + 0.5,
                                    particlePos.getZ() + 0.5,
                                    5, 0.5, 0.5, 0.5, 0.2);
                        }
                    }
                }

                serverLevel.playSound(null, pPos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0f, 1.0f);

                serverLevel.scheduleTick(pPos, this, 20);
            }
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
        }
        else {
            return pHand == InteractionHand.MAIN_HAND &&
                    isRoomUpgrade(pPlayer.getItemInHand(InteractionHand.OFF_HAND)) ?
                    ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION :
                    ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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

        level.playSound(null, pos, SoundEvents.VAULT_OPEN_SHUTTER, SoundSource.BLOCKS, 1.0f, 1.0f);

        SpaceshipManager manager = SpaceshipManager.get(level);
        Ship ship = manager.getShipByPosition(pos);

        if(ship != null){
            ShipRoom currentRoom = ship.getRoomByWorldPos(pos);
            int currentIndex = currentRoom.getIndex();

            int newIndex = getAdjacentRoomIndex(currentIndex, lastHitFace);

            if(newIndex != -1) {
                ship.getRoom(newIndex).activateRoom(level);
                activateConnectingHallways(level, ship, newIndex);
            }
        }
    }
    private int getAdjacentRoomIndex(int currentIndex, Direction direction) {
        int currentX = currentIndex % 5;
        int currentZ = currentIndex / 5;

        int newX = currentX + direction.getStepX();
        int newZ = currentZ + direction.getStepZ();

        if(newX >= 0 && newX < 5 && newZ >= 0 && newZ < 5) {
            return newZ * 5 + newX;
        }
        return -1;
    }

    private void activateConnectingHallways(ServerLevel level, Ship ship, int roomB) {
        int roomBX = roomB % 5;
        int roomBZ = roomB / 5;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            int adjX = roomBX + dir.getStepX();
            int adjZ = roomBZ + dir.getStepZ();
            int adjRoomX = adjX + dir.getStepX();
            int adjRoomZ = adjZ + dir.getStepZ();

            if (adjX < 0 || adjX >= 5 || adjZ < 0 || adjZ >= 5 || adjRoomX < 0 || adjRoomX >= 5 || adjRoomZ < 0 || adjRoomZ >= 5) {
                continue;
            }

            int adjacentIndex = adjZ * 5 + adjX;
            int adjacentRoomIndex = adjRoomZ * 5 + adjRoomX;
            if(ship.rooms[adjacentRoomIndex].isActive())
                ship.rooms[adjacentIndex].activateRoom(level);
        }
    }
}