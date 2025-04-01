package com.fruityspikes.cosmic_voyage.server.items;

import net.minecraft.core.BlockPos;

import java.util.Objects;

public class PosDevItem extends DevBuildingItem{
    public PosDevItem(Properties properties) {
        super(properties);
    }
    protected BlockPos value1;
    protected BlockPos value2;

    @Override
    public int hashCode() {
        return Objects.hash(this.value1, this.value2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof PosDevItem ex
                    && this.value1 == ex.value1
                    && this.value2 == ex.value2;
        }
    }
}
