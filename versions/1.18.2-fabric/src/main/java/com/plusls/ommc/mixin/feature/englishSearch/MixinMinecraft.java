package com.plusls.ommc.mixin.feature.englishSearch;

import com.llamalad7.mixinextras.sugar.Local;
import com.plusls.ommc.impl.feature.englishSearch.EnglishSearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.ReloadableSearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.api.dependency.annotation.Dependency;

import java.util.stream.Stream;

@Dependencies(require = @Dependency(value = "minecraft", versionPredicates = "<1.21"))
@Mixin(Minecraft.class)
public class MixinMinecraft {

    @ModifyArg(
            method = "createSearchTrees",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/searchtree/SearchRegistry;register(Lnet/minecraft/client/searchtree/SearchRegistry$Key;Lnet/minecraft/client/searchtree/MutableSearchTree;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private MutableSearchTree<ItemStack> modifyCreativeNames(MutableSearchTree<ItemStack> par2, @Local NonNullList<ItemStack> nonNullList) {
        ReloadableSearchTree<ItemStack> tree = new ReloadableSearchTree<>(
                (itemStack) -> EnglishSearchHelper.getStrings(itemStack.getTooltipLines(null, TooltipFlag.Default.NORMAL).stream()),
                (itemStack) -> Stream.of(Registry.ITEM.getKey(itemStack.getItem()))
        );
        nonNullList.forEach(tree::add);
        return tree;
    }

    @ModifyArg(
            method = "createSearchTrees",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/searchtree/SearchRegistry;register(Lnet/minecraft/client/searchtree/SearchRegistry$Key;Lnet/minecraft/client/searchtree/MutableSearchTree;)V",
                    ordinal = 2
            ),
            index = 1
    )
    private MutableSearchTree<RecipeCollection> modifyRecipeCollections(MutableSearchTree<RecipeCollection> par2) {
        return new ReloadableSearchTree<>(
                (recipeCollection) -> recipeCollection.getRecipes().stream().flatMap((recipe) -> EnglishSearchHelper.getStrings(recipe.getResultItem().getTooltipLines(null, TooltipFlag.Default.NORMAL).stream())),
                (recipeCollection) -> recipeCollection.getRecipes().stream().map((recipe) -> Registry.ITEM.getKey(recipe.getResultItem().getItem()))
        );
    }
}
