package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.plusls.ommc.SharedConstants;
import com.plusls.ommc.impl.feature.worldEaterMineHelper.WorldEaterMineHelper;
import com.plusls.ommc.mixin.accessor.AccessorBlockModel;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.hendrixshen.magiclib.api.compat.minecraft.resources.ResourceLocationCompat;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import java.util.List;
import java.util.Map;

import static net.minecraft.client.resources.model.UnbakedModel.getTopAmbientOcclusion;
import static net.minecraft.client.resources.model.UnbakedModel.getTopGuiLight;
import static net.minecraft.client.resources.model.UnbakedModel.getTopTextureSlots;
import static net.minecraft.client.resources.model.UnbakedModel.getTopTransforms;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "1.21.4"))
@Mixin(targets = "net.minecraft.client.resources.model.ModelBakery$ModelBakerImpl")
public class MixinModelBakerImpl {

    @Dynamic
    @WrapOperation(
            method = "bake",
            at = @At(
                    value = "INVOKE",
                    target = "bakeWithTopModelValues(Lnet/minecraft/client/resources/model/UnbakedModel;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;"
            )
    )
    private BakedModel bakeCustom(
            UnbakedModel unbakedModel,
            ModelBaker baker,
            ModelState modelSettings,
            Operation<BakedModel> original,
            @Local(argsOnly = true) ResourceLocation resourceLocation
    ) {
        BakedModel ret = original.call(unbakedModel, baker, modelSettings);

        if (resourceLocation == null) {
            return ret;
        }
        String[]         splitResult = resourceLocation.getPath().split("/");
        ResourceLocation blockId = ResourceLocationCompat.parse(splitResult[splitResult.length - 1]);
        Block            block   = BuiltInRegistries.BLOCK.getValue(blockId);

        if (block == Blocks.AIR) {
            return ret;
        }

        SharedConstants.getLogger().debug("Baking custom model for model {} block {}", resourceLocation, blockId);

        UnbakedModel unbaked = unbakedModel;
        while (unbaked instanceof BlockModel && ((AccessorBlockModel) unbaked).invokeGetElements().isEmpty() && unbaked.getParent() != null) {
            unbaked = unbaked.getParent();
        }
        if (!(unbaked instanceof BlockModel)) return ret;

        List<BlockElement> originalModelElements = new ImmutableList.Builder<BlockElement>().addAll(((AccessorBlockModel) unbaked).invokeGetElements()).build();
        List<BlockElement> modelElements         = Lists.newArrayListWithExpectedSize(originalModelElements.size());

        for (BlockElement modelElement : originalModelElements) {
            Vector3f origin = new Vector3f(0F, 67F, 150F).mul(0.0625F);
            BlockElementRotation             newModelRotation = new BlockElementRotation(origin, Direction.Axis.X, 45, false);
            Map<Direction, BlockElementFace> faces            = Maps.newHashMap();

            for (Map.Entry<Direction, BlockElementFace> entry : modelElement.faces.entrySet()) {
                BlockElementFace originalModelElementFace = entry.getValue();
                BlockElementFace modelElementFace = new BlockElementFace(null, originalModelElementFace.tintIndex(), originalModelElementFace.texture(), originalModelElementFace.uv());
                faces.put(entry.getKey(), modelElementFace);
            }

            modelElements.add(new BlockElement(modelElement.from, modelElement.to, faces, newModelRotation, modelElement.shade, modelElement.lightEmission));
        }

        TextureSlots textureSlots = getTopTextureSlots(unbakedModel, baker.rootName());
        boolean bl2 = getTopGuiLight(unbakedModel).lightLikeBlock();
        ItemTransforms itemTransforms = getTopTransforms(unbakedModel);

        // OMMC part only model bake
        BakedModel customBakedModel = SimpleBakedModel.bakeElements(modelElements, textureSlots, baker.sprites(), modelSettings, false, bl2, true, itemTransforms);
        WorldEaterMineHelper.customModels.put(block, customBakedModel);

        // Full model bake
        modelElements.addAll(originalModelElements);
        BakedModel customFullBakedModel = SimpleBakedModel.bakeElements(modelElements, textureSlots, baker.sprites(), modelSettings, false, bl2, true, itemTransforms);
        WorldEaterMineHelper.customFullModels.put(block, customFullBakedModel);

        return ret;
    }
}
