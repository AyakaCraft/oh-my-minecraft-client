package com.plusls.ommc.mixin.feature.blockModelNoOffset.fabric;

import com.plusls.ommc.impl.feature.blockModelNoOffset.BlockModelNoOffsetHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainBlockRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = TerrainRenderContext.class, remap = false)
public class MixinTerrainRenderContext {

    @Shadow
    @Final
    private TerrainBlockRenderInfo blockInfo;

    @Inject(method = "tesselateBlock", at = @At("HEAD"))
    private void blockModelNoOffset(BlockState blockState, BlockPos blockPos, BakedModel model, CallbackInfoReturnable<Boolean> cir) {
        if (BlockModelNoOffsetHelper.shouldNoOffset(blockState)) {
            // will cause crash, i don't know why
            // Vec3 offsetPos = blockState.getOffset(this.blockInfo.blockView, blockPos);
            // GlStateManager.translated(-offsetPos.x, -offsetPos.y, -offsetPos.z);
        }
    }
}
