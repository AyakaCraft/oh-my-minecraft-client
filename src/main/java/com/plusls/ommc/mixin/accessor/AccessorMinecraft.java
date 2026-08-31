package com.plusls.ommc.mixin.accessor;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public interface AccessorMinecraft {

    //#if MC<12100
    //$$ @org.spongepowered.asm.mixin.gen.Accessor("searchRegistry")
    //$$ net.minecraft.client.searchtree.SearchRegistry getSearchRegistry();
    //#endif

}
