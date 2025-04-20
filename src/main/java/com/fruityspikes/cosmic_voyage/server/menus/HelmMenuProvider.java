package com.fruityspikes.cosmic_voyage.server.menus;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;

import javax.annotation.Nullable;

public class HelmMenuProvider implements MenuProvider {
    private final BlockPos pos;
    private final int posX, posY, rotation, velocity;

    public HelmMenuProvider(BlockPos pos, int posX, int posY, int rotation, int velocity) {
        this.pos = pos;
        this.posX = posX;
        this.posY = posY;
        this.rotation = rotation;
        this.velocity = velocity;
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        SimpleContainerData data = new SimpleContainerData(4);
        data.set(0, posX);
        data.set(1, posY);
        data.set(2, rotation);
        data.set(3, velocity);

        return new HelmMenu(
                containerId,
                playerInv,
                data,
                ContainerLevelAccess.create(player.level(), pos)
        );
    }
}
