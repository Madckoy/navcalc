package com.devone.navcalc;

public class BotPosition {
    public int x, y, z;

    public BotPosition() {}

    public BotPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("BotPosition[x=%d, y=%d, z=%d]", x, y, z);
    }
}