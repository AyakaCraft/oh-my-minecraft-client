package net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline;

import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRenderer {
    public native void renderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin);
}
