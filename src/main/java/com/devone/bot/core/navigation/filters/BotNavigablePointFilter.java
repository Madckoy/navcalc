
package com.devone.bot.core.navigation.filters;

import com.devone.bot.utils.BotBlockData;

import java.util.*;

public class BotNavigablePointFilter {

    /**
     * Оставляет только те точки, к которым можно перейти хотя бы с одной соседней.
     */
    public static List<BotBlockData> filterNavigablePoints(List<BotBlockData> walkableBlocks) {
        Map<String, BotBlockData> map = new HashMap<>();
        for (BotBlockData block : walkableBlocks) {
            map.put(key(block.x, block.y, block.z), block);
        }

        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData block : walkableBlocks) {
            if (hasNavigableNeighbor(block, map)) {
                result.add(block);
            }
        }

        return result;
    }

    private static boolean hasNavigableNeighbor(BotBlockData block, Map<String, BotBlockData> map) {
        int x = block.x;
        int y = block.y;
        int z = block.z;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue; // Пропускаем саму точку

                    if (Math.abs(dy) > 1) continue; // Прыжки на 2 и более блоков запрещены

                    String neighborKey = key(x + dx, y + dy, z + dz);
                    if (map.containsKey(neighborKey)) {
                        return true; // Есть хотя бы один сосед
                    }
                }
            }
        }

        return false;
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }
}
