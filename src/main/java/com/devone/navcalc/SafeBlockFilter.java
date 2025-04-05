package com.devone.navcalc;

import java.util.*;

public class SafeBlockFilter {

    public static final Set<String> UNSAFE_TYPES = Set.of(
        "AIR", "CAVE_AIR", "VOID_AIR", "WATER", "LAVA", "CACTUS", "FIRE", "MAGMA_BLOCK"
    );

    public static List<NavigablePoint> extractSafeBlocks(List<BlockData> blocks) {
        return blocks.stream()
                .filter(b -> !UNSAFE_TYPES.contains(b.type.toUpperCase()))
                .map(b -> new NavigablePoint(b.x, b.y, b.z))
                .toList();
    }

    public static List<NavigablePoint> extractUnsafeBlocks(List<BlockData> blocks) {
        return blocks.stream()
                .filter(b -> UNSAFE_TYPES.contains(b.type.toUpperCase()))
                .map(b -> new NavigablePoint(b.x, b.y, b.z))
                .toList();
    }

    public static List<NavigablePoint> extractReachableBlocksFromBot(List<BlockData> blocks, BotPosition bot) {
        Map<String, BlockData> blockMap = new HashMap<>();
        Set<String> unsafeSet = new HashSet<>();
        Set<String> visited = new HashSet<>();
        List<NavigablePoint> reachable = new ArrayList<>();

        for (BlockData b : blocks) {
            String key = b.x + "," + b.y + "," + b.z;
            blockMap.put(key, b);
            if (UNSAFE_TYPES.contains(b.type.toUpperCase())) {
                unsafeSet.add(key);
            }
        }

        Queue<NavigablePoint> queue = new LinkedList<>();
        NavigablePoint start = new NavigablePoint(bot.x, bot.y, bot.z);
        queue.add(start);
        visited.add(bot.x + "," + bot.y + "," + bot.z);

        while (!queue.isEmpty()) {
            NavigablePoint current = queue.poll();
            reachable.add(current);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;

                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = current.x + dx;
                        int ny = current.y + dy;
                        int nz = current.z + dz;

                        String key = nx + "," + ny + "," + nz;
                        String headKey = nx + "," + (ny + 1) + "," + nz;

                        if (visited.contains(key)) continue;
                        if (!blockMap.containsKey(key)) continue;
                        if (unsafeSet.contains(key)) continue;

                        // Проверка на наличие воздуха над головой
                        if (blockMap.containsKey(headKey) && !UNSAFE_TYPES.contains(blockMap.get(headKey).type.toUpperCase())) {
                            continue;
                        }

                        // Перепад высоты должен быть допустимым
                        if (Math.abs(ny - current.y) > 1) continue;

                        visited.add(key);
                        queue.add(new NavigablePoint(nx, ny, nz));
                    }
                }
            }
        }

        return reachable;
    }
}