package com.plusls.ommc.impl.feature.highlightLavaSource;

import com.plusls.ommc.SharedConstants;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class LavaSourceResourceLoader implements ResourceManagerReloadListener {
    private static final Identifier listenerId = SharedConstants.identifier("lava_reload_listener");
    private static final Identifier flowingSpriteId = SharedConstants.identifier("lava_flow");
    private static final Identifier stillSpriteId = SharedConstants.identifier("lava_still");
    public static final TextureAtlasSprite[] lavaSourceSpites = new TextureAtlasSprite[2];
    public static final TextureAtlasSprite[] defaultLavaSourceSpites = new TextureAtlasSprite[2];
    public static TextureAtlasSprite lavaSourceFlowSprite;
    public static TextureAtlasSprite lavaSourceStillSprite;
    public static TextureAtlasSprite defaultLavaSourceFlowSprite;
    public static TextureAtlasSprite defaultLavaSourceStillSprite;

    public static void init() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(listenerId, new LavaSourceResourceLoader());
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        var atlasManager = Minecraft.getInstance().getAtlasManager();
        lavaSourceStillSprite = atlasManager.get(Sheets.BLOCKS_MAPPER.apply(stillSpriteId));
        lavaSourceFlowSprite = atlasManager.get(Sheets.BLOCKS_MAPPER.apply(flowingSpriteId));
        defaultLavaSourceStillSprite = atlasManager.get(Sheets.BLOCKS_MAPPER.defaultNamespaceApply("lava_still"));
        defaultLavaSourceStillSprite = atlasManager.get(Sheets.BLOCKS_MAPPER.defaultNamespaceApply("lava_flow"));
        lavaSourceSpites[0] = lavaSourceStillSprite;
        lavaSourceSpites[1] = lavaSourceFlowSprite;
        defaultLavaSourceSpites[0] = defaultLavaSourceStillSprite;
        defaultLavaSourceSpites[1] = defaultLavaSourceFlowSprite;
    }
}
