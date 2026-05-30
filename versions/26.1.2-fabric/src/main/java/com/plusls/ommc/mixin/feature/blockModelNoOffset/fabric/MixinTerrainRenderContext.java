package com.plusls.ommc.mixin.feature.blockModelNoOffset.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AltModelBlockRendererImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = AltModelBlockRendererImpl.class)
public abstract class MixinTerrainRenderContext {

    @WrapOperation(
            method = "tesselateBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getOffset(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private static Vec3 blockModelNoOffsetCondition(BlockState instance, BlockPos blockPos, Operation<Vec3> original, @Local(argsOnly = true) BlockState blockState) {
        return BlockModelNoOffsetHelper.shouldNoOffset(blockState) ? Vec3.ZERO : original.call(instance, blockPos);
    }
}