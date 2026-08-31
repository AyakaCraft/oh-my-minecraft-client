package com.plusls.ommc.mixin.feature.englishSearch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.plusls.ommc.impl.feature.englishSearch.EnglishSearchHelper;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TranslatableContents.class)
public abstract class MixinTranslatableContents {

    @ModifyExpressionValue(
            method = "decompose",
            at = @At(
                    //#if MC>=11600
                    value = "INVOKE",
                    target = "Lnet/minecraft/locale/Language;getInstance()Lnet/minecraft/locale/Language;"
                    //#else
                    //$$ value = "FIELD",
                    //$$ target = "Lnet/minecraft/network/chat/TranslatableComponent;LANGUAGE:Lnet/minecraft/locale/Language;",
                    //$$ opcode = org.objectweb.asm.Opcodes.GETSTATIC
                    //#endif
            )
    )
    private Language overrideLanguage(Language original) {
        Language overrideLang = EnglishSearchHelper.getOverrideLangThreadLocal();
        return overrideLang != null ? overrideLang : original;
    }

}
