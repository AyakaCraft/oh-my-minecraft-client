package com.plusls.ommc.mixin.feature.worldEaterMineHelper.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;

@Dependencies(require = @Dependency(value = "sodium", versionPredicates = "<0.4.9"))
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer", remap = false)
public class MixinBlockRendererLegacy {

    @Unique
    private final ThreadLocal<Integer> ommc$originalLuminance = ThreadLocal.withInitial(() -> -1);

    @Dynamic
    @ModifyVariable(
            method = "renderModel",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private BlockStateModel modifyBakedModel(
            BlockStateModel bakedModel,
            @Local(argsOnly = true) BlockState state,
            @Local(argsOnly = true, ordinal = 0) BlockPos pos
    ) {
        if (WorldEaterMineHelper.shouldUseCustomModel(state, pos)) {
            BlockStateModel customModel = WorldEaterMineHelper.customFullModels.get(state.getBlock());

            if (customModel != null) {
                this.ommc$originalLuminance.set(((AccessorBlockStateBase) state).getLightEmission());
                ((AccessorBlockStateBase) state).setLightEmission(15);
                return customModel;
            }
        }

        return bakedModel;
    }

    @Dynamic
    @Inject(method = "renderModel", at = @At("RETURN"))
    private void postRenderModel(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) BlockState state) {
        int originalLuminance = ommc$originalLuminance.get();

        if (originalLuminance != -1) {
            ((AccessorBlockStateBase) state).setLightEmission(originalLuminance);
            ommc$originalLuminance.set(-1);
        }
    }
}
