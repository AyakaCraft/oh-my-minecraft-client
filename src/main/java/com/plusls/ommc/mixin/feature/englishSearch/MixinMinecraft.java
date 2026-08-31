package com.plusls.ommc.mixin.feature.englishSearch;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "<1.21"))
@Mixin(Minecraft.class)
public class MixinMinecraft {

    // Implementation in 1.20.6

}
