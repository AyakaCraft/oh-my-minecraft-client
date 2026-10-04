package com.plusls.ommc.util.preprocess;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class GuiPatterns {

    @Pattern
    private static Screen getScreen(Minecraft client) {
        //#if MC>=260200
        //$$ return client.gui.screen();
        //#else
        return client.screen;
        //#endif
    }

    @Pattern
    private static Window getWindow(Minecraft client) {
        //#if MC > 11404
        return client.getWindow();
        //#else
        //$$ return client.window;
        //#endif
    }
}
