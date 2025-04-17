package com.fruityspikes.cosmic_voyage.server.menus;

import com.fruityspikes.cosmic_voyage.server.registries.CVMenus;
import com.fruityspikes.cosmic_voyage.server.ships.Ship;
import com.fruityspikes.cosmic_voyage.server.ships.SpaceshipManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HelmMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Player player;
    private Ship ship;
    public HelmMenu(int pContainerId, Inventory pPlayerInventory){
        this(pContainerId, pPlayerInventory, null);
    }

    public HelmMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(CVMenus.HELM.get(), pContainerId);

        this.access = pAccess;
        this.player = pPlayerInventory.player;
        //ship = SpaceshipManager.get((ServerLevel) pPlayerInventory.player.level()).getShipByPosition(BlockPos.containing(pPlayerInventory.player.position()));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
