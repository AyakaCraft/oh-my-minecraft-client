package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import java.util.List;
import java.util.Map;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=1.21.5"))
@Mixin(SingleVariant.Unbaked.class)
public class MixinSingleVariantUnbaked {

    @Shadow
    @Final
    private Variant variant;

    @WrapMethod(method = "bake")
    private BlockStateModel bakeCustom(ModelBaker modelBaker, Operation<BlockStateModel> original) {
        BlockStateModel ret = original.call(modelBaker);

        if (WorldEaterMineHelper.bakingWeighted.get() && WorldEaterMineHelper.weightedBaked.get()) return ret;

        Identifier id = variant.modelLocation();
        String[] splitResult = id.getPath().split("/");
        Identifier blockId = ResourceLocationCompat.parse(splitResult[splitResult.length - 1]);
        Block block = BuiltInRegistries.BLOCK.getValue(blockId);

        if (block == Blocks.AIR) return ret;

        ModelState modelState = variant.modelState().asModelState();

        ResolvedModel resolvedModel = modelBaker.getModel(id);
        UnbakedModel unbakedModel = resolvedModel.wrapped();
        if (!(unbakedModel instanceof BlockModel)) return ret;

        TextureSlots textureSlots = resolvedModel.getTopTextureSlots();

        UnbakedGeometry geometry = resolvedModel.getTopGeometry();
        List<BlockElement> originalModelElements;
        {
            ImmutableList.Builder<BlockElement> builder = new ImmutableList.Builder<>();
            if (geometry instanceof SimpleUnbakedGeometry) {
                builder.addAll(((SimpleUnbakedGeometry) geometry).elements());
            }
            originalModelElements = builder.build();
        }
        List<BlockElement> modelElements = Lists.newArrayListWithExpectedSize(originalModelElements.size());

        for (BlockElement modelElement : originalModelElements) {
            Vector3f origin = new Vector3f(0F, 67F, 150F);
            origin.mul(0.0625F);
            BlockElementRotation newModelRotation = new BlockElementRotation(origin,
                    //#if MC>=12111
                    new BlockElementRotation.SingleAxisRotation(Direction.Axis.X, 45),
                    //#else
                    //$$ Direction.Axis.X, 45,
                    //#endif
                    false);
            Map<Direction, BlockElementFace> faces = Maps.newHashMap();

            for (Map.Entry<Direction, BlockElementFace> entry : modelElement.faces().entrySet()) {
                BlockElementFace originalModelElementFace = entry.getValue();
                BlockElementFace modelElementFace = new BlockElementFace(null, originalModelElementFace.tintIndex(), originalModelElementFace.texture(), originalModelElementFace.uvs(), originalModelElementFace.rotation());
                faces.put(entry.getKey(), modelElementFace);
            }

            modelElements.add(new BlockElement(modelElement.from(), modelElement.to(), faces, newModelRotation, modelElement.shade(), modelElement.lightEmission()));
        }

        // OMMC part only model bake
        //#if MC>=260100
        //$$ var textureAtlasSprite = resolvedModel.resolveParticleMaterial(textureSlots, modelBaker);
        //#else
        var textureAtlasSprite = resolvedModel.resolveParticleSprite(textureSlots, modelBaker);
        //#endif
        {
            QuadCollection quadCollection = SimpleUnbakedGeometry.bake(modelElements, textureSlots,
                    //#if MC>=12111
                    modelBaker,
                    //#else
                    //$$ modelBaker.sprites(),
                    //#endif
                    modelState, resolvedModel);
            WorldEaterMineHelper.customModels.putIfAbsent(block, new SingleVariant(new SimpleModelWrapper(quadCollection, false, textureAtlasSprite)));
        }

        // Full model bake
        modelElements.addAll(originalModelElements);
        {
            QuadCollection quadCollection = SimpleUnbakedGeometry.bake(modelElements, textureSlots,
                    //#if MC>=12111
                    modelBaker,
                    //#else
                    //$$ modelBaker.sprites(),
                    //#endif
                    modelState, resolvedModel);
            WorldEaterMineHelper.customFullModels.putIfAbsent(block, new SingleVariant(new SimpleModelWrapper(quadCollection, false, textureAtlasSprite)));
        }

        if (WorldEaterMineHelper.bakingWeighted.get()) {
            WorldEaterMineHelper.weightedBaked.set(true);
        }

        return ret;
    }

}
