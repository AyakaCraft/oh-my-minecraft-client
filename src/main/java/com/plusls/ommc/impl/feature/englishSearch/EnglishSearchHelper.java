package com.plusls.ommc.impl.feature.englishSearch;

import com.plusls.ommc.game.Configs;
import com.plusls.ommc.mixin.accessor.AccessorLanguage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class EnglishSearchHelper {

    //#if MC>=11600
    private static final Language EN_US = AccessorLanguage.loadDefault$ommc();
    //#else
    //$$ private static final Language EN_US = new Language();
    //$$
    //$$ static {
    //$$     ((AccessorLanguage) EN_US).setLastUpdateTime(-1L);
    //$$ }
    //#endif

    private static final ThreadLocal<Language> OVERRIDE_LANG = new ThreadLocal<>();

    public static @Nullable Language getOverrideLangThreadLocal() {
        return OVERRIDE_LANG.get();
    }

    public static Stream<String> getStrings(Stream<Component> components){
        if (Configs.englishSearch.getBooleanValue())
            return components.flatMap(text -> Stream.of(getStringWithLang(text, EN_US), text.getString()))
                .map(str -> ChatFormatting.stripFormatting(str).trim())
                .filter(string -> !string.isEmpty())
                .distinct();
        return components.map(text -> ChatFormatting.stripFormatting(text.getString()).trim())
                .filter(string -> !string.isEmpty())
                .distinct();
    }

    public static @NotNull String getStringWithLang(
            //#if MC>=11600
            net.minecraft.network.chat.FormattedText formattedText,
            //#else
            //$$ Component formattedText,
            //#endif
            Language language
    ) {
        OVERRIDE_LANG.set(language);
        try {
            return formattedText.getString();
        } finally {
            OVERRIDE_LANG.remove();
        }
    }

    public static void onConfigValueChanged() {
        //#if MC>=12100
        net.minecraft.client.multiplayer.ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        if (clientPacketListener != null) {
            clientPacketListener.updateSearchTrees();
        }
        //#else
        //$$ ((com.plusls.ommc.mixin.accessor.AccessorMinecraft) Minecraft.getInstance()).getSearchRegistry().onResourceManagerReload(null);
        //#endif
    }
}