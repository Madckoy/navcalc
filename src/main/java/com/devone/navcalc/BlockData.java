package com.devone.navcalc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockData {
    public int x, y, z;
    public String type;
    public boolean bot;  // из JSON

    public boolean isAir() {
        return "AIR".equalsIgnoreCase(type);
    }

    public boolean isBot() {
        return bot;
    }

    @Override
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, bot);
    }
}