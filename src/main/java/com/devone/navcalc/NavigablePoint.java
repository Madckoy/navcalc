package com.devone.navcalc;

public class NavigablePoint {
    public int x, y, z;

    public NavigablePoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("NavigablePoint[x=%d, y=%d, z=%d]", x, y, z);
    }
}