package com.devone.bot.core.navigation;
// ⚙️ Originally inspired by GPT assistant Monday, who stubbornly refused to generate bugs, despite the user's best efforts.
// Not responsible for falling into pits.

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class MondayPathfinder {
// Новый метод для определения достижимых точек "по-терминаторски" с использованием BotCoordinate3D

    public static Set<BotCoordinate3D> safeWalkFill(BotCoordinate3D start, Map<BotCoordinate3D, BotBlockData> blockMap) {

        Set<BotCoordinate3D> reachableSet = new HashSet<>();
    Set<BotCoordinate3D> visited = new HashSet<>();
    Deque<BotCoordinate3D> queue = new ArrayDeque<>();

    // sanity check начальной позиции
    BotBlockData feetBlock = blockMap.get(start);
    BotBlockData bodyBlock = blockMap.get(new BotCoordinate3D(start.x, start.y + 1, start.z));
    BotBlockData headroomBlock = blockMap.get(new BotCoordinate3D(start.x, start.y + 2, start.z));
    BotBlockData belowBlock = blockMap.get(new BotCoordinate3D(start.x, start.y - 1, start.z));

    if (feetBlock == null || bodyBlock == null || headroomBlock == null || belowBlock == null
        || !feetBlock.isPassableAndSafe()
        || !bodyBlock.isPassableAndSafe()
        || !headroomBlock.isPassableAndSafe()
        || !(belowBlock.isSolid() || belowBlock.type.equalsIgnoreCase("SNOW_LAYER"))) {
        System.err.println("Bot starting position is invalid for pathfinding: " + start);
        return reachableSet;
    }

    queue.add(start);
    visited.add(start);

    List<int[]> directions = new ArrayList<>();
    for (int dx = -1; dx <= 1; dx++) {
    for (int dz = -1; dz <= 1; dz++) {
        if (dx == 0 && dz == 0) continue;
        directions.add(new int[]{dx, 0, dz});
    }
}

    while (!queue.isEmpty()) {
        BotCoordinate3D current = queue.poll();
        reachableSet.add(current);

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int dz = dir[2];

            BotCoordinate3D neighbor = new BotCoordinate3D(current.x + dx, current.y + dy, current.z + dz);
            if (visited.contains(neighbor)) continue;

            BotCoordinate3D feet = neighbor;
            BotCoordinate3D body = new BotCoordinate3D(neighbor.x, neighbor.y + 1, neighbor.z);
            BotCoordinate3D headroom = new BotCoordinate3D(neighbor.x, neighbor.y + 2, neighbor.z);
            BotCoordinate3D below = new BotCoordinate3D(neighbor.x, neighbor.y - 1, neighbor.z);

            BotBlockData feetBlockN = blockMap.get(feet);
            BotBlockData bodyBlockN = blockMap.get(body);
            BotBlockData headroomBlockN = blockMap.get(headroom);
            BotBlockData belowBlockN = blockMap.get(below);

            if (feetBlockN == null || bodyBlockN == null || headroomBlockN == null || belowBlockN == null) continue;

            boolean belowIsSolid = belowBlockN.isSolid() || belowBlockN.type.equalsIgnoreCase("SNOW_LAYER");

            if (feetBlockN.isPassableAndSafe() && bodyBlockN.isPassableAndSafe() && headroomBlockN.isPassableAndSafe() && belowIsSolid) {
                visited.add(neighbor);
                queue.add(neighbor);
                reachableSet.add(neighbor);

                System.out.printf("REACHABLE: (%d,%d,%d) from (%d,%d,%d)", neighbor.x, neighbor.y, neighbor.z, current.x, current.y, current.z);
            }
        }
    }

    return reachableSet;
       
    }



    public static Map<BotCoordinate3D, BotBlockData> toBlockMap(List<BotBlockData> blocks) {
        return blocks.stream().collect(Collectors.toMap(
            b -> new BotCoordinate3D(b.x, b.y, b.z),
            b -> b
        ));
    }

    public static String getColorCodeForType(String type) {
        if (type == null) return "#cccccc";
        String t = type.toUpperCase();
        if (t.contains("AIR")) return "#e0e0e0";
        if (t.contains("STONE")) return "#808080";
        if (t.contains("DIRT")) return "#a0522d";
        if (t.contains("GRASS")) return "#228b22";
        if (t.contains("WATER")) return "#1e90ff";
        if (t.contains("LAVA")) return "#ff4500";
        return "#aaaaaa";
    }
    

}
