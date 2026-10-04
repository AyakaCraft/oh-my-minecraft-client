package com.plusls.ommc.impl.feature.blockModelNoOffset;

import com.plusls.ommc.game.Configs;
import com.plusls.ommc.util.RegistryCompat;
import fi.dy.masa.malilib.util.restrictions.UsageRestriction;
import net.minecraft.world.level.block.state.BlockState;

public class BlockModelNoOffsetHelper {
    public static boolean shouldNoOffset(BlockState blockState) {
        String blockId = RegistryCompat.getBlockId(blockState.getBlock()).toString();
        String blockName = blockState.getBlock().getName().getString();

        if (Configs.blockModelNoOffsetListType.getOptionListValue() == UsageRestriction.ListType.WHITELIST) {
            return Configs.blockModelNoOffsetWhitelist.getStrings().stream().anyMatch(s -> blockId.contains(s) || blockName.contains(s));
        } else if (Configs.blockModelNoOffsetListType.getOptionListValue() == UsageRestriction.ListType.BLACKLIST) {
            return Configs.blockModelNoOffsetBlacklist.getStrings().stream().noneMatch(s -> blockId.contains(s) || blockName.contains(s));
        }

        return false;
    }
}
