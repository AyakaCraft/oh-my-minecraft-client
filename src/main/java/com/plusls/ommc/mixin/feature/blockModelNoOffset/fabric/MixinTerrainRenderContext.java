package com.plusls.ommc.mixin.feature.blockModelNoOffset.fabric;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = TerrainRenderContext.class, remap = false)
public abstract class MixinTerrainRenderContext {

    @WrapWithCondition(
            method = {
                    "bufferModel",
                    "tessellateBlock"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"
            )
    )
    private static boolean blockModelNoOffsetCondition(
            PoseStack instance, double d, double e, double f,
            @Local(argsOnly = true) BlockState blockState
    ) {
        return !BlockModelNoOffsetHelper.shouldNoOffset(blockState);
    }
}