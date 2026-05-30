package com.plusls.ommc.mixin.feature.highlightLavaSource.vanilla;

import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.preprocess.DummyClass;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=26.1"))
@Mixin(DummyClass.class)
public class MixinFluidRenderer {

}
