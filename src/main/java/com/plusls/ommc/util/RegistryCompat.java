package com.plusls.ommc.util;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public final class RegistryCompat {

    public static Identifier getBlockId(Block block) {
        //#if MC >= 11903
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        //#else
        //$$ return net.minecraft.core.Registry.BLOCK.getKey(block);
        //#endif
    }
}
