package com.devone.navcalc;


import java.util.List;
import java.util.stream.Collectors;

public class VerticalRangeFilter {
    public static List<BlockData> trimByYRange(List<BlockData> blocks, int botY, int range) {
        return blocks.stream()
            .filter(block -> Math.abs(block.y - botY) <= range)
            .collect(Collectors.toList());
    }
}
