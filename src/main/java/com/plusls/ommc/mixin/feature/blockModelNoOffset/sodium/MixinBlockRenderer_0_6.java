package com.plusls.ommc.mixin.feature.blockModelNoOffset.sodium;

import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import top.hendrixshen.magiclib.libs.com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = ">=0.6-"))
@Pseudo
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class MixinBlockRenderer_0_6 {
    @Dynamic
    @WrapOperation(
            method = "renderModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;hasOffsetFunction()Z",
                    ordinal = 0,
                    remap = true
            )
    )
    private boolean blockModelNoOffset(BlockState blockState, Operation<Boolean> original) {
        if (BlockModelNoOffsetHelper.shouldNoOffset(blockState)) {
            return false;
        }

        return original.call(blockState);
    }
}
