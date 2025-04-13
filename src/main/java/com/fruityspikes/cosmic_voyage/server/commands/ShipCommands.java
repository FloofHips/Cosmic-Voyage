package com.fruityspikes.cosmic_voyage.server.commands;

import com.fruityspikes.cosmic_voyage.server.dimension.SpaceshipDimension;
import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.ShipRoom;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import com.fruityspikes.cosmic_voyage.server.util.TeleportUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.awt.*;
import java.util.Arrays;

public class ShipCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ship")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("create")
                .executes(context -> createShip(context.getSource())))
            .then(Commands.literal("find")
                    .executes(context -> findShip(context.getSource())))
            .then(Commands.literal("list")
                .executes(context -> listShips(context.getSource())))
            .then(Commands.literal("tp")
                .then(Commands.argument("shipId", IntegerArgumentType.integer(1))
                    .executes(context -> teleportToShip(
                        context.getSource(),
                        IntegerArgumentType.getInteger(context, "shipId")))))
            .then(Commands.literal("delete")
                .then(Commands.argument("shipId", IntegerArgumentType.integer(1))
                    .executes(context -> deleteShip(
                        context.getSource(),
                        IntegerArgumentType.getInteger(context, "shipId")))))
            .then(Commands.literal("exit")
                .executes(context -> exitShip(context.getSource()))));
    }

    private static int createShip(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        SpaceshipManager manager = SpaceshipManager.get(level);
        Ship ship = manager.createShip(player.blockPosition(), level);

        source.sendSuccess(() -> 
            Component.literal("Created new ship #" + ship.getSimpleId()), true);
        return 1;
    }

    private static int listShips(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        SpaceshipManager manager = SpaceshipManager.get(level);
        
        if (manager.getShips().isEmpty()) {
            source.sendSuccess(() -> Component.literal("No ships exist"), false);
            return 0;
        }

        manager.getShips().values().forEach(ship -> {
            source.sendSuccess(() -> Component.literal(String.format(
                "Ship #%d at (%d, %d, %d)",
                ship.getSimpleId(),
                ship.getEntityLocation().getX(),
                ship.getEntityLocation().getY(),
                ship.getEntityLocation().getZ()
            )), false);
        });
        
        return 1;
    }

    private static int teleportToShip(CommandSourceStack source, int simpleId) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        SpaceshipManager manager = SpaceshipManager.get(level);
        Ship ship = manager.getShipBySimpleId(simpleId);

        if (ship == null) {
            source.sendFailure(Component.literal("Ship #" + simpleId + " does not exist"));
            return 0;
        }

        TeleportUtil.teleportToShip(player, ship, level);
        source.sendSuccess(() -> 
            Component.literal("Teleported to ship #" + simpleId), true);
        return 1;
    }

    private static int deleteShip(CommandSourceStack source, int simpleId) {
        ServerLevel level = source.getLevel();
        SpaceshipManager manager = SpaceshipManager.get(level);
        Ship ship = manager.getShipBySimpleId(simpleId);

        if (ship == null) {
            source.sendFailure(Component.literal("Ship #" + simpleId + " does not exist"));
            return 0;
        }

        manager.removeShip(ship.getId());
        source.sendSuccess(() -> 
            Component.literal("Deleted ship #" + simpleId), true);
        return 1;
    }

    private static int exitShip(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        ServerLevel level = source.getLevel();

        if (!level.dimension().equals(SpaceshipDimension.DIMENSION_KEY)) {
            source.sendFailure(Component.literal("You must be in a ship to use this command"));
            return 0;
        }

        SpaceshipManager manager = SpaceshipManager.get(level);
        Ship ship = manager.getShipByPosition(player.blockPosition());

        if (ship == null) {
            source.sendFailure(Component.literal("Could not determine which ship you are in"));
            return 0;
        }

        TeleportUtil.teleportFromShip(player, ship, level);
        source.sendSuccess(() -> Component.literal("Exited ship #" + ship.getSimpleId()), true);
        return 1;
    }

    private static int findShip(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        SpaceshipManager manager = SpaceshipManager.get(level);

        BlockPos pos = BlockPos.containing(source.getPosition());
        Ship ship = manager.getShipByPosition(pos);

        if (ship == null) {
            source.sendFailure(Component.literal("Could not find ship at position" + pos));
            return 0;
        }

        ShipRoom room = ship.getRoomByWorldPos(pos);

        source.sendSuccess(() ->
                Component.literal("Ship #" + ship.getSimpleId() + " in room #" + room.getIndex()), true);
        return 1;
    }
}
