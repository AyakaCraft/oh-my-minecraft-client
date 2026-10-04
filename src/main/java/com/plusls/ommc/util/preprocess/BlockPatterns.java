package com.plusls.ommc.util.preprocess;

import net.minecraft.world.level.block.state.BlockState;

public final class BlockPatterns {

    @Pattern
    private static boolean isSolid(BlockState blockState) {
        //#if MC > 11904
        return blockState.isSolid();
        //#else
        //$$ return blockState.getMaterial().isSolidBlocking();
        //#endif
    }
}
