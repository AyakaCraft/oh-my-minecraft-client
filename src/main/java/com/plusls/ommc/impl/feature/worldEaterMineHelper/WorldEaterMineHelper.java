package com.plusls.ommc.impl.feature.worldEaterMineHelper;

import com.plusls.ommc.game.Configs;
import com.plusls.ommc.mixin.accessor.AccessorBlockStateBase;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import top.hendrixshen.magiclib.util.MiscUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

//#if MC>=12104
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.core.Direction;
import java.util.function.Predicate;
//#else
//$$ import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
//#endif

//#if MC >= 11903
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

public class WorldEaterMineHelper {
    public static final Map<Block, BlockStateModel> customModels = new HashMap<>();
    public static final Map<Block, BlockStateModel> customFullModels = new HashMap<>();
    //#if MC>=12105
    public static final ThreadLocal<Boolean> bakingWeighted = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> weightedBaked = ThreadLocal.withInitial(() -> false);
    //#endif

    public static boolean blockInWorldEaterMineHelperWhitelist(Block block) {
        String blockName = block.getName().getString();
        //#if MC >= 11903
        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
        //#else
        //$$ String blockId = Registry.BLOCK.getKey(block).toString();
        //#endif
        return Configs.worldEaterMineHelperWhitelist
                .getStrings()
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(s -> blockId.contains(s) || blockName.contains(s));
    }

    public static boolean shouldUseCustomModel(BlockState blockState, BlockPos pos) {
        Block block = blockState.getBlock();

        if (!Configs.worldEaterMineHelper.getBooleanValue() ||
                !WorldEaterMineHelper.blockInWorldEaterMineHelperWhitelist(block)) {
            return false;
        }

        ClientLevel world = Minecraft.getInstance().level;

        if (world == null) {
            return false;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int yMax = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        if (y < yMax) {
            int j = 0;

            for (int i = y + 1; i <= yMax; ++i) {
                if (world.getBlockState(new BlockPos(x, i, z))
                        //#if MC > 11904
                        .isSolid()
                        //#else
                        //$$ .getMaterial().isSolidBlocking()
                        //#endif
                        && j < 20
                ) {
                    return false;
                }

                j++;
            }
        }

        return true;
    }

    static public void emitCustomFullBlockQuads(
            //#if MC>=12104
            QuadEmitter emitter,
            //#endif
            FabricBlockStateModel model, BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<?> randomSupplier,
            //#if MC>=12104
            Predicate<Direction> context
            //#else
            //$$ RenderContext context
            //#endif
    ) {
        Block block = state.getBlock();

        if (WorldEaterMineHelper.shouldUseCustomModel(state, pos)) {
            FabricBlockStateModel customModel = (FabricBlockStateModel) WorldEaterMineHelper.customFullModels.get(block);
            if (customModel != null) {
                int luminance = ((AccessorBlockStateBase) state).getLightEmission();
                ((AccessorBlockStateBase) state).setLightEmission(15);
                //#if MC>=12105
                customModel.emitQuads(emitter, blockView, pos, state, MiscUtil.cast(randomSupplier), context);
                //#else
                //$$ customModel.emitBlockQuads(
                        //#if MC>=12104
                        //$$ emitter,
                        //#endif
                //$$         blockView, state, pos, MiscUtil.cast(randomSupplier), context);
                //#endif
                ((AccessorBlockStateBase) state).setLightEmission(luminance);
                return;
            }
        }

        //#if MC>=12105
        model.emitQuads(emitter, blockView, pos, state, MiscUtil.cast(randomSupplier), context);
        //#else
        //$$ model.emitBlockQuads(
                //#if MC>=12104
                //$$ emitter,
                //#endif
        //$$         blockView, state, pos, MiscUtil.cast(randomSupplier), context);
        //#endif
    }

    public static void emitCustomBlockQuads(
            //#if MC>=12104
            QuadEmitter emitter,
            //#endif
            BlockAndTintGetter blockView, BlockState state, BlockPos pos,
            //#if MC>=12105
            net.minecraft.util.RandomSource randomSupplier,
            //#else
            //$$ Supplier<?> randomSupplier,
            //#endif
            //#if MC>=12104
            Predicate<Direction> context
            //#else
            //$$ RenderContext context
            //#endif
    ) {
        Block block = state.getBlock();

        if (WorldEaterMineHelper.shouldUseCustomModel(state, pos)) {
            FabricBlockStateModel customModel = (FabricBlockStateModel) WorldEaterMineHelper.customModels.get(block);

            if (customModel != null) {
                int luminance = ((AccessorBlockStateBase) state).getLightEmission();
                ((AccessorBlockStateBase) state).setLightEmission(15);
                //#if MC>=12105
                customModel.emitQuads(emitter, blockView, pos, state, MiscUtil.cast(randomSupplier), context);
                //#else
                //$$ customModel.emitBlockQuads(
                        //#if MC>=12104
                        //$$ emitter,
                        //#endif
                //$$         blockView, state, pos, MiscUtil.cast(randomSupplier), context);
                //#endif
                ((AccessorBlockStateBase) state).setLightEmission(luminance);
            }
        }
    }
}
