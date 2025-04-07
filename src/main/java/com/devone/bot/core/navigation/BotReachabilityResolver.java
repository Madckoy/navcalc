
package com.devone.bot.core.navigation;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

import java.util.*;

public class BotReachabilityResolver {

    private static final int[][] DELTAS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    public static List<BotBlockData> findReachablePoints(BotCoordinate3D botPos, List<BotBlockData> navigableBlocks) {
        Map<String, BotBlockData> map = new HashMap<>();
        for (BotBlockData b : navigableBlocks) {
            map.put(b.x + "," + b.y + "," + b.z, b);
        }

        Set<String> visited = new HashSet<>();
        Queue<BotCoordinate3D> queue = new LinkedList<>();
        queue.add(botPos);

        List<BotBlockData> reachable = new ArrayList<>();

        while (!queue.isEmpty()) {
            BotCoordinate3D current = queue.poll();
            String key = current.x + "," + current.y + "," + current.z;

            if (visited.contains(key)) continue;
            visited.add(key);

            BotBlockData data = map.get(key);
            if (data != null) {
                reachable.add(data);

                for (int[] d : DELTAS) {
                    int dx = d[0];
                    int dz = d[1];

                    for (int dy = -1; dy <= 1; dy++) { // проверка по высоте
                        int nx = current.x + dx;
                        int ny = current.y + dy;
                        int nz = current.z + dz;

                        String nKey = nx + "," + ny + "," + nz;
                        if (!visited.contains(nKey) && map.containsKey(nKey)) {
                            queue.add(new BotCoordinate3D(nx, ny, nz));
                        }
                    }
                }
            }
        }

        return reachable;
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }
}
