package com.plusls.ommc.mixin.feature.blockModelNoOffset.fabric;

import com.llamalad7.mixinextras.sugar.Local;
import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//#if MC > 11903
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
//#else
//$$ import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$
//#if MC > 11701
//$$ import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
//#else
//$$ import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainBlockRenderInfo;
//#endif
//#endif

//#if MC > 11802
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

//#if MC>=11500
import com.mojang.blaze3d.vertex.PoseStack;
//#endif

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = TerrainRenderContext.class, remap = false)
public abstract class MixinTerrainRenderContext
        //#if MC > 11903
        extends AbstractBlockRenderContext
        //#else
        //$$ implements RenderContext
        //#endif
{

    //#if MC < 11904
    //$$ @Shadow
    //$$ @Final
    //#if MC > 11701
    //$$ private BlockRenderInfo blockInfo;
    //#else
    //$$ private TerrainBlockRenderInfo blockInfo;
    //#endif
    //#endif

    @Inject(
            method = {
                    "tessellateBlock", // Fabric Indigo 0.5.0+
                    "tesselateBlock"
            },
            at = @At("HEAD")
    )
    private void blockModelNoOffset(
            //#if MC > 11802
            CallbackInfo ci,
            //#else
            //$$ CallbackInfoReturnable<Boolean> cir,
            //#endif
            @Local(argsOnly = true) BlockState blockState,
            @Local(argsOnly = true) BlockPos blockPos
            //#if MC>=11500
            , @Local(argsOnly = true) PoseStack poseStack
            //#endif
    ) {
        if (BlockModelNoOffsetHelper.shouldNoOffset(blockState)) {
            Vec3 offsetPos = blockState.getOffset(this.blockInfo.blockView, blockPos);

            //#if MC>=11500
            poseStack.translate(-offsetPos.x, -offsetPos.y, -offsetPos.z);
            //#else
            //$$ // will cause crash, i don't know why
            //$$ // com.mojang.blaze3d.platform.GlStateManager.translated(-offsetPos.x, -offsetPos.y, -offsetPos.z);
            //#endif
        }
    }
}
