package com.plusls.ommc.impl.generic.highlightWaypoint;

import com.plusls.ommc.SharedConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

//#if MC>=12109
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
//#else
//$$ import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//$$ import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
//#endif

//#if MC < 11903
//$$ import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
//$$ import net.minecraft.client.renderer.texture.TextureAtlas;
//#endif

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HighlightWaypointResourceLoader implements
        //#if MC>=12109
        ResourceManagerReloadListener
        //#else
        //$$ SimpleSynchronousResourceReloadListener
        //#endif
{
    private static final HighlightWaypointResourceLoader instance = new HighlightWaypointResourceLoader();
    private static final Identifier listenerId = SharedConstants.identifier("target_reload_listener");

    //#if MC>=12109
    public static final Identifier targetId = SharedConstants.identifier("target");
    //#else
    //$$ public static final ResourceLocation targetId = SharedConstants.identifier("block/target");
    //#endif

    public static TextureAtlasSprite targetIdSprite;

    protected static void init() {
        //#if MC < 11903
        //$$ ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(
        //$$         (atlasTexture, registry) -> registry.register(HighlightWaypointResourceLoader.targetId)
        //$$ );
        //#endif
        //#if MC>=260100
        //$$ ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(listenerId, HighlightWaypointResourceLoader.instance);
        //#elseif MC>=12109
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(listenerId, HighlightWaypointResourceLoader.instance);
        //#else
        //$$ ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(HighlightWaypointResourceLoader.instance);
        //#endif
    }

    //#if MC<12109
    //$$ @Override
    //$$ public ResourceLocation getFabricId() {
    //$$     return HighlightWaypointResourceLoader.listenerId;
    //$$ }
    //#endif

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        //#if MC>=12109
        targetIdSprite = Minecraft.getInstance().getAtlasManager().get(net.minecraft.client.renderer.Sheets.BLOCKS_MAPPER.apply(targetId));
        //#elseif MC > 11404
        //$$ targetIdSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(targetId);
        //#else
        //$$ targetIdSprite = Minecraft.getInstance().getTextureAtlas().getSprite(targetId);
        //#endif
    }
}
