
package com.devone.navcalc;

import java.util.List;

public class GeoUtils {
    public static int estimateHorizontalRadius(List<BlockData> blocks) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockData block : blocks) {
            if (block.x < minX) minX = block.x;
            if (block.x > maxX) maxX = block.x;
            if (block.z < minZ) minZ = block.z;
            if (block.z > maxZ) maxZ = block.z;
        }

        return Math.max((maxX - minX) / 2, (maxZ - minZ) / 2);
    }
}
