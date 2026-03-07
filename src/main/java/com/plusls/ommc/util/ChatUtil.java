package com.plusls.ommc.util;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public final class ChatUtil {

    public static ClickEvent runCommand(String command) {
        //#if MC>=12105
        return new ClickEvent.RunCommand(command);
        //#else
        //$$ return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        //#endif
    }

    public static HoverEvent showText(Component component) {
        //#if MC>=12105
        return new HoverEvent.ShowText(component);
        //#else
        //$$ return new HoverEvent(HoverEvent.Action.SHOW_TEXT, component);
        //#endif
    }

}
