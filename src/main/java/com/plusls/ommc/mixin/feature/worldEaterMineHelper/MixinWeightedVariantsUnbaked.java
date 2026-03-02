package com.plusls.ommc.mixin.feature.worldEaterMineHelper;

import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.preprocess.DummyClass;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=1.21.5"))
@Mixin(DummyClass.class)
public class MixinWeightedVariantsUnbaked {
}
