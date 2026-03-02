package com.plusls.ommc.util;

import net.minecraft.world.item.Item;

public final class ItemUtil {

    public static String getItemNameTranslated(Item item) {
        //#if MC>=12103
        //$$ return net.minecraft.network.chat.Component.translatable(item.getDescriptionId()).getString();
        //#else
        return item.getDescription().getString();
        //#endif
    }

}
