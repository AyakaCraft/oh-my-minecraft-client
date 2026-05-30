package com.plusls.ommc.mixin.feature.highlightLavaSource.vanilla;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.plusls.ommc.game.Configs;
import com.plusls.ommc.impl.feature.highlightLavaSource.LavaSourceResourceLoader;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import java.util.Objects;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=26.1"))
@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {

    @ModifyExpressionValue(
            method = "tesselate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/sprite/Material$Baked;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
                    ordinal = 0
            )
    )
    public TextureAtlasSprite modifyStill(TextureAtlasSprite original, @Local(argsOnly = true) FluidState fluidState) {
        if (Configs.highlightLavaSource.getBooleanValue() && fluidState.getType() instanceof LavaFluid && fluidState.getAmount() >= 8) return Objects.requireNonNullElse(LavaSourceResourceLoader.lavaSourceStillSprite, original);
        return original;
    }

    @ModifyExpressionValue(
            method = "tesselate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/sprite/Material$Baked;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
                    ordinal = 1
            )
    )
    public TextureAtlasSprite modifyFlowing(TextureAtlasSprite original, @Local(argsOnly = true) FluidState fluidState) {
        if (Configs.highlightLavaSource.getBooleanValue() && fluidState.getType() instanceof LavaFluid && fluidState.getAmount() >= 8) return Objects.requireNonNullElse(LavaSourceResourceLoader.lavaSourceFlowSprite, original);
        return original;
    }

}
