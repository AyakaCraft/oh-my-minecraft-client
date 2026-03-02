package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.WeightedVariants;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=1.21.5"))
@Mixin(WeightedVariants.Unbaked.class)
public class MixinWeightedVariantsUnbaked {

    @WrapMethod(method = "bake")
    private BlockStateModel wrapBake(ModelBaker modelBaker, Operation<BlockStateModel> original) {
        WorldEaterMineHelper.bakingWeighted.set(true);
        BlockStateModel ret = original.call(modelBaker);
        WorldEaterMineHelper.bakingWeighted.set(false);
        WorldEaterMineHelper.weightedBaked.set(false);
        return ret;
    }

}
