package com.plusls.ommc.util.preprocess;

import net.minecraft.world.entity.Entity;

public final class EntityPatterns {

    @Pattern
    private static boolean isOnGround(Entity entity) {
        //#if MC > 11502
        return entity.onGround();
        //#else
        //$$ return entity.onGround;
        //#endif
    }
}
