package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.resources.model.*;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "<1.21.4"))
@Mixin(value = BlockModel.class)
public abstract class MixinJsonUnbakedModel implements UnbakedModel {
}
