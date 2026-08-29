package com.plusls.ommc;

import com.plusls.ommc.impl.feature.highlightLavaSource.LavaSourceResourceLoader;
import com.plusls.ommc.impl.feature.preventWastageOfWater.PreventWastageOfWaterHelper;
import com.plusls.ommc.impl.feature.realSneaking.RealSneakingEventHelper;
import com.plusls.ommc.game.Configs;
import com.plusls.ommc.impl.generic.highlightWaypoint.HighlightWaypointHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;

import java.util.Locale;

public class OhMyMinecraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(() -> {
            ConfigManager.getInstance().registerConfigHandler(SharedConstants.getModIdentifier(), SharedConstants.getConfigHandler());

            //#if MC >= 12101
            fi.dy.masa.malilib.registry.Registry.CONFIG_SCREEN.registerConfigScreenFactory(new fi.dy.masa.malilib.util.data.ModInfo(SharedConstants.getModIdentifier().toUpperCase(Locale.ROOT), SharedConstants.getModName(), com.plusls.ommc.game.ConfigGui::new));
            //#endif
        });
        Configs.init();
        LavaSourceResourceLoader.init();
        HighlightWaypointHandler.init();
        RealSneakingEventHelper.init();
        PreventWastageOfWaterHelper.init();
    }
}

