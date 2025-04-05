package com.devone.navcalc;

public class BlockData {
    public int x, y, z;
    public String type;
    public boolean isBot;

    public boolean isAir() {
        return "AIR".equalsIgnoreCase(type);
    }

    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, isBot);
    }
}