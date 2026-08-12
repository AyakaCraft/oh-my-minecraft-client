package com.plusls.ommc;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.i18n.I18n;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigHandler;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.GlobalConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigHandlerImpl;
import top.hendrixshen.magiclib.util.VersionUtil;

import java.util.Optional;

public class SharedConstants {
    @Getter
    private static final String modIdentifier = "ommc";
    @Getter
    private static final String modName;
    @Getter
    private static final String modVersion;

    static {
        final Optional<ModContainer> o = FabricLoader.getInstance().getModContainer(modIdentifier);
        if (o.isPresent()) {
            modName = o.get().getMetadata().getName();
            modVersion = o.get().getMetadata().getVersion().toString();
        } else {
            modName = "Oh My Minecraft Client";
            modVersion = "dev";
        }
    }

    @Getter
    private static final String modVersionType = VersionUtil.getVersionType(SharedConstants.modVersion);
    @Getter
    private static final MagicConfigManager configManager = GlobalConfigManager.getConfigManager(SharedConstants.modIdentifier);

    @Getter
    private static final MagicConfigHandler configHandler = new MagicConfigHandlerImpl(configManager, 1);
    @Getter
    private static final Logger logger = LogManager.getLogger(SharedConstants.modIdentifier);

    public static @NotNull String getTranslatedModVersionType() {
        return VersionUtil.translateVersionType(SharedConstants.modVersion);
    }

    public static @NotNull Identifier identifier(String path) {
        return ResourceLocationCompat.fromNamespaceAndPath(SharedConstants.modIdentifier, path);
    }

    public static String getTranslation(String path) {
        return I18n.tr(SharedConstants.modIdentifier + "." + path);
    }
}
