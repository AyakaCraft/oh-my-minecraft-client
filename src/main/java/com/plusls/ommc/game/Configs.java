package com.plusls.ommc.game;

import com.google.common.collect.ImmutableList;
import com.plusls.ommc.SharedConstants;
import com.plusls.ommc.impl.feature.englishSearch.EnglishSearchHelper;
import com.plusls.ommc.impl.feature.sortInventory.SortInventoryShulkerBoxLastType;
import com.plusls.ommc.impl.feature.sortInventory.SortInventoryHelper;
import com.plusls.ommc.impl.generic.highlightWaypoint.HighlightWaypointHandler;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.malilib.annotation.Config;
import top.hendrixshen.magiclib.api.malilib.annotation.Statistic;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigHandler;
import top.hendrixshen.magiclib.api.malilib.config.MagicConfigManager;
import top.hendrixshen.magiclib.impl.malilib.config.MagicConfigFactory;
import top.hendrixshen.magiclib.impl.malilib.config.option.*;
import top.hendrixshen.magiclib.util.minecraft.InfoUtil;

import java.util.Optional;

public class Configs {
    private static final MagicConfigManager manager = SharedConstants.getConfigManager();
    private static final MagicConfigFactory factory = manager.getConfigFactory();

    // Generic
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey clearWaypoint = factory.newConfigHotkey("clearWaypoint", "C");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean debug = factory.newConfigBoolean("debug", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBoolean dontClearChatHistory = factory.newConfigBoolean("dontClearChatHistory", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed forceParseWaypointFromChat = factory.newConfigBooleanHotkeyed("forceParseWaypointFromChat", false);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigInteger highlightBeamTime = factory.newConfigInteger("highlightBeamTime", 10, 0, Integer.MAX_VALUE);

    @Statistic(hotkey = false)
    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey openConfigGui = factory.newConfigHotkey("openConfigGui", "O,C");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed parseWaypointFromChat = factory.newConfigBooleanHotkeyed("parseWaypointFromChat", true);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey sendLookingAtBlockPos = factory.newConfigHotkey("sendLookingAtBlockPos", "O,P");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigBooleanHotkeyed sortInventorySupportEmptyShulkerBoxStack = factory.newConfigBooleanHotkeyed("sortInventorySupportEmptyShulkerBoxStack", true);

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigHotkey sortInventory = factory.newConfigHotkey("sortInventory", "R");

    @Config(category = ConfigCategory.GENERIC)
    public static MagicConfigOptionList sortInventoryShulkerBoxLast = factory.newConfigOptionList("sortInventoryShulkerBoxLast", SortInventoryShulkerBoxLastType.AUTO);

    // Feature
    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed autoSwitchElytra = factory.newConfigBooleanHotkeyed("autoSwitchElytra", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed betterSneaking = factory.newConfigBooleanHotkeyed("betterSneaking", false);

    @Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">1.15.2"))
    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableBlocklistCheck = factory.newConfigBooleanHotkeyed("disableBlocklistCheck", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableBreakBlock = factory.newConfigBooleanHotkeyed("disableBreakBlock", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableBreakScaffolding = factory.newConfigBooleanHotkeyed("disableBreakScaffolding", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disableMoveDownInScaffolding = factory.newConfigBooleanHotkeyed("disableMoveDownInScaffolding", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed disablePistonPushEntity = factory.newConfigBooleanHotkeyed("disablePistonPushEntity", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBoolean englishSearch = factory.newConfigBoolean("englishSearch", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed flatDigger = factory.newConfigBooleanHotkeyed("flatDigger", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed forceBreakingCooldown = factory.newConfigBooleanHotkeyed("forceBreakingCooldown", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed highlightLavaSource = factory.newConfigBooleanHotkeyed("highlightLavaSource", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed highlightPersistentMob = factory.newConfigBooleanHotkeyed("highlightPersistentMob", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBoolean highlightPersistentMobClientMode = factory.newConfigBoolean("highlightPersistentMobClientMode", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed preventWastageOfWater = factory.newConfigBooleanHotkeyed("preventWastageOfWater", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed preventIntentionalGameDesign = factory.newConfigBooleanHotkeyed("preventIntentionalGameDesign", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed realSneaking = factory.newConfigBooleanHotkeyed("realSneaking", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed removeBreakingCooldown = factory.newConfigBooleanHotkeyed("removeBreakingCooldown", false);

    @Config(category = ConfigCategory.FEATURE)
    public static MagicConfigBooleanHotkeyed worldEaterMineHelper = factory.newConfigBooleanHotkeyed("worldEaterMineHelper", false);

    // List
    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList blockModelNoOffsetBlacklist = factory.newConfigStringList("blockModelNoOffsetBlacklist");

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigOptionList blockModelNoOffsetListType = factory.newConfigOptionList("blockModelNoOffsetListType", UsageRestriction.ListType.NONE);

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList blockModelNoOffsetWhitelist = factory.newConfigStringList("blockModelNoOffsetWhitelist", ImmutableList.of("minecraft:wither_rose", "minecraft:poppy", "minecraft:dandelion"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList breakBlockBlackList = factory.newConfigStringList("breakBlockBlackList", ImmutableList.of("minecraft:budding_amethyst", "_bud"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList breakScaffoldingWhiteList = factory.newConfigStringList("breakScaffoldingWhiteList", ImmutableList.of("minecraft:air", "minecraft:scaffolding"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList highlightEntityBlackList = factory.newConfigStringList("highlightEntityBlackList");

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigOptionList highlightEntityListType = factory.newConfigOptionList("highlightEntityListType", UsageRestriction.ListType.WHITELIST);

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList highlightEntityWhiteList = factory.newConfigStringList("highlightEntityWhiteList", ImmutableList.of("minecraft:wandering_trader"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList moveDownInScaffoldingWhiteList = factory.newConfigStringList("moveDownInScaffoldingWhiteList", ImmutableList.of("minecraft:air", "minecraft:scaffolding"));

    @Config(category = ConfigCategory.LIST)
    public static MagicConfigStringList worldEaterMineHelperWhitelist = factory.newConfigStringList("worldEaterMineHelperWhitelist", ImmutableList.of("_ore", "minecraft:ancient_debris", "minecraft:obsidian"));

    public static void init() {
        manager.parseConfigClass(Configs.class);
        SharedConstants.getConfigHandler().setPostDeserializeCallback(Configs::onConfigLoaded);

        // Generic
        IValueChangeCallback<ConfigBoolean> reloadLevelRender = Configs::rerenderLevel;
        IValueChangeCallback<ConfigOptionList> reloadLevelRenderList = Configs::rerenderLevel;

        MagicConfigManager.setHotkeyCallback(Configs.clearWaypoint, () -> HighlightWaypointHandler.getInstance().clearHighlightPos(), true);
        MagicConfigManager.setHotkeyCallback(Configs.openConfigGui, ConfigGui::openGui, true);

        MagicConfigManager.setHotkeyCallback(Configs.sendLookingAtBlockPos, () -> {
            Minecraft client = Minecraft.getInstance();
            Entity cameraEntity = client.getCameraEntity();
            MultiPlayerGameMode clientPlayerInteractionManager = client.gameMode;

            if (cameraEntity != null && clientPlayerInteractionManager != null) {
                //#if MC>=12100
                HitResult hitResult = cameraEntity.pick(clientPlayerInteractionManager.getPlayerMode().isCreative() ? 5.0F : 4.5F, client.getFrameTimeNs(), false);
                //#elseif MC>=12005
                //$$ HitResult hitResult = cameraEntity.pick(clientPlayerInteractionManager.hasInfiniteItems() ? 5.0F : 4.5F, client.getFrameTime(), false);
                //#else
                //$$ HitResult hitResult = cameraEntity.pick(clientPlayerInteractionManager.getPickRange(), client.getFrameTime(), false);
                //#endif

                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos lookPos = ((BlockHitResult) hitResult).getBlockPos();
                    if (client.player != null) {
                        String message = String.format("[%d, %d, %d]", lookPos.getX(), lookPos.getY(), lookPos.getZ());
                        InfoUtil.sendChat(message);
                    }
                }
            }
        }, true);


        MagicConfigManager.setHotkeyCallback(Configs.sortInventory,
                () -> Optional.ofNullable(SortInventoryHelper.sort()).ifPresent(Runnable::run),
                false);

        Configs.highlightLavaSource.setValueChangeCallback(reloadLevelRender);
        Configs.worldEaterMineHelper.setValueChangeCallback(reloadLevelRender);

        Configs.englishSearch.setValueChangeCallback(option -> EnglishSearchHelper.onConfigValueChanged());

        // List
        Configs.blockModelNoOffsetListType.setValueChangeCallback(reloadLevelRenderList);
    }

    private static void onConfigLoaded(MagicConfigHandler magicConfigHandler) {
        Configs.sortInventory.getKeybind().setSettings(KeybindSettings.GUI);
    }

    private static void rerenderLevel(ConfigBase<?> option) {
        //#if MC>=260200
        //$$ Minecraft.getInstance().levelExtractor.allChanged();
        //#else
        Minecraft.getInstance().levelRenderer.allChanged();
        //#endif
    }

    public static class ConfigCategory {
        public static final String GENERIC = "generic";
        public static final String FEATURE = "feature";
        public static final String LIST = "list";
    }
}
