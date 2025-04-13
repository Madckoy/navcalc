package com.devone.bot.utils.blocks;

import java.util.Objects;

public class BotCoordinate3D {
    public int x, y, z;

    public BotCoordinate3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public BotCoordinate3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BotCoordinate3D(BotCoordinate3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BotCoordinate3D)) return false;
        BotCoordinate3D that = (BotCoordinate3D) o;
        return x == that.x && y == that.y && z == that.z;
    }

    public double distanceTo(BotCoordinate3D other) {
        if (other == null) return Double.MAX_VALUE;
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        int dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
 
    @Override
    public String toString() {
        return String.format("(%d,%d,%d)", x, y, z);
    }
}
