package com.devone.bot.utils;

import java.util.Set;

public class BlockMaterialUtils {

    public static final Set<String> AIR_TYPES = Set.of("AIR", "CAVE_AIR", "VOID_AIR");

    public static final Set<String> UNSAFE_TYPES = Set.of(
        "AIR", "CAVE_AIR", "VOID_AIR", "WATER", "LAVA", "CACTUS", "FIRE", "MAGMA_BLOCK"
    );

    public static boolean isAir(String type) {
        return type.equalsIgnoreCase("AIR")
            || type.equalsIgnoreCase("CAVE_AIR")
            || type.equalsIgnoreCase("VOID_AIR");
    }

    public static String getColorCodeForType(String type) {
        if (type == null) return "#cccccc";
        String t = type.toUpperCase();
        if (t.contains("AIR")) return "#e0e0e0";
        if (t.contains("STONE")) return "#808080";
        if (t.contains("DIRT")) return "#a0522d";
        if (t.contains("GRASS")) return "#228b22";
        if (t.contains("WATER")) return "#1e90ff";
        if (t.contains("LAVA")) return "#ff4500";
        return "#aaaaaa";
    }
}