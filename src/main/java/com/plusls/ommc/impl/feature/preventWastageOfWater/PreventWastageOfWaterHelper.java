package com.plusls.ommc.impl.feature.preventWastageOfWater;

import com.plusls.ommc.game.Configs;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

//#if MC>=12103
import net.minecraft.world.InteractionResult;
//#elseif MC <= 11404
//$$ import net.minecraft.world.InteractionResult;
//#endif

public class PreventWastageOfWaterHelper implements UseItemCallback {
    public static void init() {
        PreventWastageOfWaterHelper handler = new PreventWastageOfWaterHelper();
        UseItemCallback.EVENT.register(handler);
    }

    //#if MC>=12103
    @Override
    public InteractionResult interact(Player player, Level world, InteractionHand hand) {
        return (Configs.preventWastageOfWater.getBooleanValue()
                //#if MC>=12109
                && world.isClientSide()
                //#else
                //$$ && world.isClientSide
                //#endif
                && player.getItemInHand(hand).getItem() == Items.WATER_BUCKET
                //#if MC>=12111
                && world.environmentAttributes().getDimensionValue(net.minecraft.world.attribute.EnvironmentAttributes.WATER_EVAPORATES))
                //#else
                //$$ && world.dimensionType().ultraWarm())
                //#endif
                ? InteractionResult.FAIL
                : InteractionResult.PASS;
    }
    //#elseif MC > 11404
    //$$ @Override
    //$$ public net.minecraft.world.InteractionResultHolder<ItemStack> interact(Player player, Level world, InteractionHand hand) {
    //$$     return (Configs.preventWastageOfWater.getBooleanValue()
    //$$             && world.isClientSide
    //$$             && player.getItemInHand(hand).getItem() == Items.WATER_BUCKET
                //#if MC > 11502
                //$$ && world.dimensionType().ultraWarm())
                //#else
                //$$ && world.getDimension().isUltraWarm())
                //#endif
    //$$             ? net.minecraft.world.InteractionResultHolder.fail(ItemStack.EMPTY)
    //$$             : net.minecraft.world.InteractionResultHolder.pass(ItemStack.EMPTY);
    //$$ }
    //#else
    //$$ @Override
    //$$ public InteractionResult interact(Player player, Level world, InteractionHand hand) {
    //$$     return (Configs.preventWastageOfWater.getBooleanValue()
    //$$             && world.isClientSide
    //$$             && player.getItemInHand(hand).getItem() == Items.WATER_BUCKET
    //$$             && world.getDimension().isUltraWarm())
    //$$             ? InteractionResult.FAIL
    //$$             : InteractionResult.PASS;
    //$$ }
    //#endif
}
