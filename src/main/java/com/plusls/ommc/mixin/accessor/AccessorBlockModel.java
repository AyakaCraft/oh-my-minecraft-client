package com.plusls.ommc.mixin.accessor;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import java.util.List;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "<1.21.5"))
@Mixin(BlockModel.class)
public interface AccessorBlockModel {
    //#if MC<12104
    @Accessor
    BlockModel getParent();
    //#endif

    @Invoker
    List<BlockElement> invokeGetElements();

    @Accessor
    //#if MC > 11903
    Boolean getHasAmbientOcclusion();
    //#else
    //$$ boolean getHasAmbientOcclusion();
    //#endif

    @Mutable
    @Accessor
    //#if MC > 11903
    void setHasAmbientOcclusion(Boolean hasAmbientOcclusion);
    //#else
    //$$ void setHasAmbientOcclusion(boolean hasAmbientOcclusion);
    //#endif
}
