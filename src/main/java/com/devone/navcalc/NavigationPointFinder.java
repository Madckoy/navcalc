package com.devone.navcalc;

import java.util.*;

public class NavigationPointFinder {

    public static List<NavigablePoint> findTopmostNavigablePoints(List<BlockData> blocks) {
        Map<String, List<BlockData>> columnMap = new HashMap<>();
        List<NavigablePoint> result = new ArrayList<>();

        for (BlockData block : blocks) {
            String key = block.x + "," + block.z;
            columnMap.computeIfAbsent(key, k -> new ArrayList<>()).add(block);
        }

        for (Map.Entry<String, List<BlockData>> entry : columnMap.entrySet()) {
            List<BlockData> column = entry.getValue();
            boolean foundSafe = false;
            int topY = Integer.MIN_VALUE;
            int safeX = 0, safeZ = 0;

            for (BlockData b : column) {
                if (b.isAir()) {
                    foundSafe = true;
                    if (b.y > topY) {
                        topY = b.y;
                        safeX = b.x;
                        safeZ = b.z;
                    }
                }
            }

            if (foundSafe) {
                result.add(new NavigablePoint(safeX, topY, safeZ));
            }
        }

        return result;
    }

    public static List<NavigablePoint> getAllNavigableAirBlocks(List<BlockData> blocks) {
        List<NavigablePoint> result = new ArrayList<>();
        for (BlockData block : blocks) {
            if (block.isAir()) {
                result.add(new NavigablePoint(block.x, block.y, block.z));
            }
        }
        return result;
    }
}