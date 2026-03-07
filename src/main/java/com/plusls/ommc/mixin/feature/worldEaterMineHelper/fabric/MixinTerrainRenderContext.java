package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;

import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

//#if MC>=12104
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.core.Direction;
import java.util.function.Predicate;
//#else
//$$ import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//#endif

//#if MC>11903
import net.minecraft.client.renderer.block.model.BlockStateModel;
//#else
//$$ import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
//#endif

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = TerrainRenderContext.class, remap = false)
public class MixinTerrainRenderContext {

    @WrapOperation(
            method = {
                    "bufferModel", // 1.21.5+
                    "tessellateBlock", // For fabric-renderer-indigo 0.5.0 and above
                    "tesselateBlock" // For fabric-renderer-indigo 0.5.0 below
            },
            at = @At(
                    value = "INVOKE",
                    //#if MC>=12105
                    target = "Lnet/minecraft/client/renderer/block/model/BlockStateModel;emitQuads(Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;Ljava/util/function/Predicate;)V",
                    //#elseif MC>=12104
                    //$$ target = "Lnet/minecraft/client/resources/model/BakedModel;emitBlockQuads(Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadEmitter;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Ljava/util/function/Predicate;)V",
                    //#elseif MC>11903
                    //$$ target = "Lnet/minecraft/client/resources/model/BakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
                    //#else
                    //$$ target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBakedModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
                    //#endif
                    remap = true
            )
    )
    private void emitCustomBlockQuads(
            //#if MC>11903
            BlockStateModel instance,
            //#else
            //$$ FabricBakedModel instance,
            //#endif
            //#if MC>=12104
            QuadEmitter emitter,
            //#endif
            BlockAndTintGetter blockAndTintGetter,
            //#if MC>=12105
            BlockPos blockPos, BlockState blockState, net.minecraft.util.RandomSource supplier,
            //#else
            //$$ BlockState blockState, BlockPos blockPos, Supplier<?> supplier,
            //#endif
            //#if MC>=12104
            Predicate<Direction> renderContext,
            //#else
            //$$ RenderContext renderContext,
            //#endif
            Operation<Void> original) {
        original.call(instance,
                //#if MC>=12104
                emitter,
                //#endif
                blockAndTintGetter,
                //#if MC>=12105
                blockPos, blockState,
                //#else
                //$$ blockState, blockPos,
                //#endif
                supplier, renderContext);
        WorldEaterMineHelper.emitCustomBlockQuads(
                //#if MC>=12104
                emitter,
                //#endif
                blockAndTintGetter, blockState, blockPos, supplier, renderContext);
    }
}