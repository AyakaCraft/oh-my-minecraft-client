package com.plusls.ommc.mixin.feature.worldEaterMineHelper.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.util.MiscUtil;

import java.util.function.Supplier;

//#if MC<12104
//$$ import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//#endif

//#if MC > 11802
import net.minecraft.util.RandomSource;
//#else
//$$ import java.util.Random;
//#endif

// TODO
@Dependencies(require = @Dependency(value = "sodium", versionPredicates = "<0.0.0"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.render.renderer.TerrainRenderContext", remap = false)
public class MixinTerrainRenderContext {
    @Dynamic
    @WrapOperation(
            method = "renderBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/api/renderer/v1/model/FabricBlockStateModel;emitBlockQuads(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Ljava/util/function/Supplier;Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;)V",
                    ordinal = 0,
                    remap = true
            )
    )
    private void emitCustomBlockQuads(
            FabricBlockStateModel model,
            BlockAndTintGetter blockView,
            BlockState state,
            BlockPos pos,
            //#if MC > 11802
            Supplier<RandomSource> randomSupplier,
            //#else
            //$$ Supplier<Random> randomSupplier,
            //#endif
            //#if MC<12104
            //$$ RenderContext context,
            //#endif
            Operation<Void> original
    ) {
        //#if MC<12104
        //$$ original.call(model, blockView, state, pos, randomSupplier, context);
        //$$ WorldEaterMineHelper.emitCustomBlockQuads(blockView, state, pos, MiscUtil.cast(randomSupplier), context);
        //#endif
    }
}
