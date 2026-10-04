package com.plusls.ommc.util.preprocess;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public final class ChatPatterns {

    @Pattern
    private static ClickEvent runCommand(String command) {
        //#if MC>=12105
        return new ClickEvent.RunCommand(command);
        //#else
        //$$ return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        //#endif
    }

    @Pattern
    private static HoverEvent showText(Component component) {
        //#if MC>=12105
        return new HoverEvent.ShowText(component);
        //#else
        //$$ return new HoverEvent(HoverEvent.Action.SHOW_TEXT, component);
        //#endif
    }
}
