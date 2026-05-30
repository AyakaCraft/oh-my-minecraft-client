package com.plusls.ommc.mixin.feature.worldEaterMineHelper.fabric;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainLikeRenderContext;
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
@Mixin(value = TerrainLikeRenderContext.class, remap = false)
public abstract class MixinBlockRenderContext {

    @WrapOperation(
            //#if MC>=260100
            //$$ method = "tesselateBlock",
            //#elseif MC>=12105
            method = "bufferModel",
            //#elseif MC > 11404
            //$$ method = "render",
            //#else
            //$$ method = "tesselate",
            //#endif
            at = @At(value = "INVOKE",
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
            BlockAndTintGetter blockView,
            //#if MC>=12105
            BlockPos pos, BlockState state, net.minecraft.util.RandomSource supplier,
            //#else
            //$$ BlockState state, BlockPos pos, Supplier<?> supplier,
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
                blockView,
                //#if MC>=12105
                pos, state,
                //#else
                //$$ state, pos,
                //#endif
                supplier, renderContext);
        WorldEaterMineHelper.emitCustomBlockQuads(
                //#if MC>=12104
                emitter,
                //#endif
                blockView, state, pos, supplier, renderContext);
    }
}
