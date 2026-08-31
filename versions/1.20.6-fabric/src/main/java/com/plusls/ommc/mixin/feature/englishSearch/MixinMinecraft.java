package com.plusls.ommc.mixin.feature.englishSearch;

import com.plusls.ommc.impl.feature.englishSearch.EnglishSearchHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
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
                    target = "Lnet/minecraft/client/searchtree/SearchRegistry;register(Lnet/minecraft/client/searchtree/SearchRegistry$Key;Lnet/minecraft/client/searchtree/SearchRegistry$TreeBuilderSupplier;)V",
                    ordinal = 0
            ),
            index = 1
    )
    private SearchRegistry.TreeBuilderSupplier<ItemStack> modifyCreativeNames(SearchRegistry.TreeBuilderSupplier<ItemStack> treeBuilderSupplier) {
        return (list) -> new FullTextSearchTree<>(
                (itemStack) -> EnglishSearchHelper.getStrings(itemStack.getTooltipLines(
                        //#if MC >= 12006
                        Item.TooltipContext.EMPTY,
                        //#endif
                        null,
                        TooltipFlag.Default.NORMAL.asCreative()).stream()),
                (itemStack) -> Stream.of(BuiltInRegistries.ITEM.getKey(itemStack.getItem())),
                list
        );
    }

    @ModifyArg(
            method = "createSearchTrees",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/searchtree/SearchRegistry;register(Lnet/minecraft/client/searchtree/SearchRegistry$Key;Lnet/minecraft/client/searchtree/SearchRegistry$TreeBuilderSupplier;)V",
                    ordinal = 2
            ),
            index = 1
    )
    private SearchRegistry.TreeBuilderSupplier<RecipeCollection> modifyRecipeCollections(SearchRegistry.TreeBuilderSupplier<RecipeCollection> treeBuilderSupplier) {
        return (list) -> new FullTextSearchTree<>(
                (recipeCollection) -> recipeCollection.getRecipes().stream().flatMap((recipe) -> EnglishSearchHelper.getStrings(recipe
                        //#if MC >= 12002
                        .value()
                        //#endif
                        .getResultItem(recipeCollection.registryAccess())
                        .getTooltipLines(
                                //#if MC >= 12006
                                Item.TooltipContext.EMPTY,
                                //#endif
                                null,
                                TooltipFlag.Default.NORMAL
                        ).stream())),
                (recipeCollection) -> recipeCollection.getRecipes().stream().map((recipe) -> BuiltInRegistries.ITEM.getKey(recipe
                        //#if MC >= 12002
                        .value()
                        //#endif
                        .getResultItem(recipeCollection.registryAccess())
                        .getItem())),
                list
        );
    }
}
