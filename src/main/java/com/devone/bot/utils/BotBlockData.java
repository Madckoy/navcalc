package com.devone.bot.utils;

import java.util.Set;


public class BotBlockData extends BotCoordinate3D {

    private static final Set<String> AIR_TYPES = Set.of("AIR", "CAVE_AIR", "VOID_AIR");

    public int x, y, z;
    public String type;
    public boolean bot;  // из JSON

    public boolean isAir() {
        return type != null && AIR_TYPES.contains(type.toUpperCase());
    } 
    
    public boolean isSolid() {
        // сюда можно добавлять исключения по мере надобности
        if (type == null) return false;
        String t = type.toUpperCase();
        return !(
            t.contains("AIR") ||
            t.contains("WATER") ||
            t.contains("LAVA") ||

            t.equals("FIRE") ||
            t.equals("CACTUS")
        );
    }

    public boolean isBot() {
        return bot;
    }

    public boolean isPassableAndSafe() {
        if (type == null) return false;
        String t = type.toUpperCase();
        return AIR_TYPES.contains(t) || t.equals("TALL_GRASS") || t.equals("SNOW") || t.equals("FLOWER");
    }

    @Override
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, bot);
    }
}