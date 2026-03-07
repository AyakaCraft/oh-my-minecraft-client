package me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline;

import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class BlockRenderContext {
    private final WorldSlice world = null;

    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private final Vector3f origin = new Vector3f();

    private BlockState state;
    private BlockStateModel model;

    private long seed;

    public BlockRenderContext(WorldSlice world) {
    }

    public native void update(BlockPos pos, BlockPos origin, BlockState state, BlockStateModel model, long seed);

    public native BlockPos pos();

    public native WorldSlice world();

    public native BlockState state();

    public native BlockStateModel model();

    public native Vector3fc origin();

    public native long seed();
}
