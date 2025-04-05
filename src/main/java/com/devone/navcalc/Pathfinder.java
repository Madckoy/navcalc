package com.devone.navcalc;

import java.util.*;

public class Pathfinder {

    public static List<NavigablePoint> findPath(NavigablePoint start, NavigablePoint goal, Set<String> reachableSet) {
        Map<String, NavigablePoint> parentMap = new HashMap<>();
        Queue<NavigablePoint> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        String startKey = key(start);
        String goalKey = key(goal);

        queue.add(start);
        visited.add(startKey);

        while (!queue.isEmpty()) {
            NavigablePoint current = queue.poll();

            if (key(current).equals(goalKey)) {
                return reconstructPath(current, parentMap);
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;

                    for (int dy = -1; dy <= 1; dy++) {
                        int nx = current.x + dx;
                        int ny = current.y + dy;
                        int nz = current.z + dz;

                        String neighborKey = nx + "," + ny + "," + nz;

                        if (visited.contains(neighborKey)) continue;
                        if (!reachableSet.contains(neighborKey)) continue;

                        NavigablePoint neighbor = new NavigablePoint(nx, ny, nz);
                        queue.add(neighbor);
                        visited.add(neighborKey);
                        parentMap.put(neighborKey, current);
                    }
                }
            }
        }

        return Collections.emptyList(); // путь не найден
    }

    private static List<NavigablePoint> reconstructPath(NavigablePoint end, Map<String, NavigablePoint> parentMap) {
        List<NavigablePoint> path = new LinkedList<>();
        String currentKey = key(end);
        NavigablePoint current = end;

        while (current != null) {
            path.add(0, current);
            current = parentMap.get(key(current));
        }

        return path;
    }

    private static String key(NavigablePoint p) {
        return p.x + "," + p.y + "," + p.z;
    }
}