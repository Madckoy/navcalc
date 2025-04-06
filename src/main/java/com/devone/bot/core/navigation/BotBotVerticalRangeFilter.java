package com.devone.bot.core.navigation;


import java.util.List;
import java.util.stream.Collectors;

import com.devone.bot.util.BotBlockData;

public class BotBotVerticalRangeFilter {
    public static List<BotBlockData> trimByYRange(List<BotBlockData> blocks, int botY, int range) {
        return blocks.stream()
            .filter(block -> Math.abs(block.y - botY) <= range)
            .collect(Collectors.toList());
    }
}
