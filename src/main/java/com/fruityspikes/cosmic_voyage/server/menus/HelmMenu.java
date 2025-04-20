package com.fruityspikes.cosmic_voyage.server.menus;

import com.fruityspikes.cosmic_voyage.server.data.CelestialObject;
import com.fruityspikes.cosmic_voyage.server.data.CelestialObjectManager;
import com.fruityspikes.cosmic_voyage.server.registries.CVMenus;
import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HelmMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private Ship boundShip;
    private static final int POS_X_INDEX = 0;
    private static final int POS_Y_INDEX = 1;
    private static final int ROTATION_INDEX = 2;
    private static final int VELOCITY_INDEX = 3;
    private static final int DATA_COUNT = 4;
    private Map<CelestialObject, Vec2> currentCelestialPositions;

    public HelmMenu(int containerId, Inventory playerInventory, ContainerData data, ContainerLevelAccess access) {
        super(CVMenus.HELM.get(), containerId);
        this.access = access;
        this.data = data;
        checkContainerDataCount(data, DATA_COUNT);
        addDataSlots(data);
        access.evaluate((level, pos) -> {
            this.boundShip = SpaceshipManager.get((ServerLevel) level).getShipByPosition(pos);
            //this.currentCelestialPositions = boundShip.getCurrentCelestialPositions();
            return true;
        });
    }

    public HelmMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    public Map<CelestialObject, Vec2> getCurrentCelestialPositions() {
        return currentCelestialPositions;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    public int getPosX() {
        return data.get(POS_X_INDEX);
    }
    public int getPosY() {
        return data.get(POS_Y_INDEX);
    }
    public int getVelocity() {
        return data.get(VELOCITY_INDEX);
    }
    public int getRotation() {
        return data.get(ROTATION_INDEX);
    }
}
