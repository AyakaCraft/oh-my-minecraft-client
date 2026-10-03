package com.plusls.ommc.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//#if MC<12103
//$$ import net.minecraft.world.entity.EquipmentSlot;
//$$ import net.minecraft.world.item.ItemStack;
//$$ import org.jetbrains.annotations.NotNull;
//#endif

//#if MC>=12103
//#elseif MC > 12006
//$$ import net.minecraft.world.item.Equipable;
//#elseif MC > 11605
//$$ import net.minecraft.world.entity.LivingEntity;
//#else
//$$ import net.minecraft.world.entity.Mob;
//#endif

public final class ItemUtil {

    public static String getItemNameTranslated(Item item) {
        //#if MC>=12103
        return net.minecraft.network.chat.Component.translatable(item.getDescriptionId()).getString();
        //#else
        //$$ return item.getDescription().getString();
        //#endif
    }

    //#if MC>=12103
    public static boolean allowsEquipmentSlot(ItemStack itemStack, EquipmentSlot slot) {
        var slotGroup = fi.dy.masa.malilib.util.EquipmentUtils.getEquipmentSlot(itemStack);
        return slotGroup != null && slotGroup.test(slot);
    }
    //#else
    //$$ public static @NotNull EquipmentSlot getEquipmentSlot(ItemStack itemStack) {
    //#if MC > 12006
    //$$ Equipable equipable = Equipable.get(itemStack);
    //$$ return equipable != null ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
    //#elseif MC > 11605
    //$$ return LivingEntity.getEquipmentSlotForItem(itemStack);
    //#else
    //$$ return Mob.getEquipmentSlotForItem(itemStack);
    //#endif
    //$$ }
    //#endif

}
