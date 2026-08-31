package com.plusls.ommc.mixin.accessor;

import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=1.16"))
@Mixin(Language.class)
public interface AccessorLanguage {

    //#if MC>=11600
    @org.spongepowered.asm.mixin.gen.Invoker("loadDefault")
    static Language loadDefault$ommc() {
        throw new AssertionError();
    }
    //#endif

    //#if MC<11600
    //$$ @org.spongepowered.asm.mixin.gen.Accessor
    //$$ void setLastUpdateTime(long lastUpdateTime);
    //#endif

}
