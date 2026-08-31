package com.plusls.ommc.mixin.feature.englishSearch;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.plusls.ommc.impl.feature.englishSearch.EnglishSearchHelper;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import java.util.stream.Stream;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = ">=1.21"))
@Mixin(SessionSearchTrees.class)
public class MixinSessionSearchTrees {

    @WrapMethod(method = "getTooltipLines")
    private static Stream<String> getTooltipLines(Stream<ItemStack> stacks, Item.TooltipContext context, TooltipFlag type, Operation<Stream<String>> original) {
        return EnglishSearchHelper.getStrings(stacks.flatMap(stack -> stack.getTooltipLines(context, null, type).stream()));
    }

}
