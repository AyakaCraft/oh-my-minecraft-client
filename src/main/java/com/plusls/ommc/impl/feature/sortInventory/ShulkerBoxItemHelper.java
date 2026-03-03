package com.plusls.ommc.impl.feature.sortInventory;

import com.plusls.ommc.game.Configs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.api.compat.minecraft.nbt.TagCompat;

//#if MC > 12005
//$$ import net.minecraft.core.component.DataComponents;
//$$ import net.minecraft.world.item.component.ItemContainerContents;
//#endif

public class ShulkerBoxItemHelper {
    public static final int SHULKERBOX_MAX_STACK_AMOUNT = 64;

    public static boolean isEmptyShulkerBoxItem(ItemStack itemStack) {
        if (!ShulkerBoxItemHelper.isShulkerBoxBlockItem(itemStack)) {
            return false;
        }

        //#if MC < 12005
        CompoundTag nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains("BlockEntityTag", TagCompat.TAG_COMPOUND)) {
            return true;
        }
        CompoundTag tag = nbt.getCompound("BlockEntityTag");
        if (tag.contains("Items", TagCompat.TAG_LIST)) {
            ListTag tagList = tag.getList("Items", TagCompat.TAG_COMPOUND);
            return tagList.isEmpty();
        }
        return true;
        //#else
        //$$ ItemContainerContents icc = itemStack.get(DataComponents.CONTAINER);
        //$$     if (icc == null) {
        //$$         return true;
        //$$     }
        //$$     if (icc.stream().allMatch(ItemStack::isEmpty)) {
        //$$         return true;
        //$$     }
        //$$     return true;
        //#endif
    }

    public static boolean isShulkerBoxBlockItem(@NotNull ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem &&
                ((BlockItem) itemStack.getItem()).getBlock() instanceof ShulkerBoxBlock;
    }

    //#if MC < 12005
    public static int compareShulkerBox(@Nullable CompoundTag a, @Nullable CompoundTag b) {
        int aSize = 0, bSize = 0;
        ItemStack aFirst = null, bFirst = null;

        if (a != null) {
            CompoundTag tag = a.getCompound("BlockEntityTag");

            if (tag.contains("Items", TagCompat.TAG_LIST)) {
                ListTag tagList = tag.getList("Items", TagCompat.TAG_COMPOUND);
                aSize = tagList.size();
                aFirst = ItemStack.of(tagList.getCompound(0));
            }
        }

        if (b != null) {
            CompoundTag tag = b.getCompound("BlockEntityTag");

            if (tag.contains("Items", TagCompat.TAG_LIST)) {
                ListTag tagList = tag.getList("Items", TagCompat.TAG_COMPOUND);
                bSize = tagList.size();
                bFirst = ItemStack.of(tagList.getCompound(0));
            }
        }

        int ret = aSize - bSize;
        if (ret == 0 && aFirst != null && bFirst != null) {
            return SortInventoryHelper.ItemStackComparator.INSTANCE.compare(aFirst, bFirst);
        }
        return ret;
    }
    //#else
    //$$ public static int compareShulkerBox(@Nullable ItemContainerContents a, @Nullable ItemContainerContents b) {
    //$$     int aSize = 0, bSize = 0;
    //$$     ItemStack aFirst = null, bFirst = null;
    //$$     if (a != null) {
    //$$         aSize = a.stream().toList().size();
    //$$         aFirst = a.stream().filter(i -> !i.isEmpty()).findFirst().orElse(null);
    //$$     }
    //$$     if (b != null) {
    //$$         bSize = b.stream().toList().size();;
    //$$         bFirst = b.stream().filter(i -> !i.isEmpty()).findFirst().orElse(null);
    //$$     }
    //$$     int ret = aSize - bSize;
    //$$     if (ret == 0 && aFirst != null && bFirst != null) {
    //$$         return SortInventoryHelper.ItemStackComparator.INSTANCE.compare(aFirst, bFirst);
    //$$     }
    //$$     return ret;
    //$$ }
    //#endif

    public static int getMaxCount(ItemStack itemStack) {
        if (Configs.sortInventorySupportEmptyShulkerBoxStack.getBooleanValue() &&
                ShulkerBoxItemHelper.isEmptyShulkerBoxItem(itemStack)) {
            return ShulkerBoxItemHelper.SHULKERBOX_MAX_STACK_AMOUNT;
        } else {
            return itemStack.getMaxStackSize();
        }
    }

    public static boolean isStackable(ItemStack itemStack) {
        return getMaxCount(itemStack) > 1 && (!itemStack.isDamageableItem() || !itemStack.isDamaged());
    }
}