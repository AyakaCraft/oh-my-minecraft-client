package com.plusls.ommc.mixin.accessor;

import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "<1.21.5"))
@Mixin(BlockModel.class)
public abstract class AccessorBlockModel {
}
