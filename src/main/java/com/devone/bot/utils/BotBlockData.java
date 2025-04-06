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
        String t = type.toUpperCase();
        return !(t.contains("AIR") || t.contains("WATER") || t.contains("LAVA"));
    }

    public boolean isBot() {
        return bot;
    }

    @Override
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, bot);
    }
}