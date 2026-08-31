package com.plusls.ommc.mixin.feature.englishSearch;

import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;
import top.hendrixshen.magiclib.api.preprocess.DummyClass;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=1.21"))
@Mixin(DummyClass.class)
public class MixinSessionSearchTrees {

    // Implementation in main project

}
