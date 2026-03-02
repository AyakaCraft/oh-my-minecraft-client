package com.plusls.ommc.mixin.accessor;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPipelines.class)
public interface AccessorRenderPipelines {
    @Accessor(value = "GUI_TEXTURED_SNIPPET")
    static RenderPipeline.Snippet getGuiTexturedSnipped() {
        throw new AssertionError();
    }
}
