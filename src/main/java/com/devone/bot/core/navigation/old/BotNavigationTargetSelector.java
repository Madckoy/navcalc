
package com.devone.bot.core.navigation.old;

import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

import java.util.*;

public class BotNavigationTargetSelector {

    public enum SelectionStrategy {
        RANDOM_FARTHEST,
        CENTER_PROXIMITY,
        LOWEST_POINT,
        HIGHEST_POINT
    }

    /**
     * Возвращает список возможных целевых точек согласно стратегии и минимальной дистанции.
     * Если подходящих точек нет — возвращает null.
     */
    public static List<BotBlockData> selectNavigationTargets(BotCoordinate3D botPosition,
                                                            List<BotBlockData> reachable,
                                                            double minDistance,
                                                            SelectionStrategy strategy) {
        if (reachable == null || reachable.isEmpty()) return null;

        List<BotBlockData> filtered = new ArrayList<>();
        for (BotBlockData p : reachable) {
            if (squaredDistance(botPosition, p) >= minDistance * minDistance) {
                filtered.add(p);
            }
        }

        if (filtered.isEmpty()) return null;

        switch (strategy) {
            case RANDOM_FARTHEST:
                return findFarthestPoints(botPosition, filtered);
            case CENTER_PROXIMITY:
                return List.of(findClosestToCenter(filtered));
            case LOWEST_POINT:
                return List.of(filtered.stream().min(Comparator.comparingInt(c -> c.y)).orElse(null));
            case HIGHEST_POINT:
                return List.of(filtered.stream().max(Comparator.comparingInt(c -> c.y)).orElse(null));
            default:
                return null;
        }
    }

    private static List<BotBlockData> findFarthestPoints(BotCoordinate3D from, List<BotBlockData> candidates) {
        double maxDistance = -1;
        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData point : candidates) {
            double distance = squaredDistance(from, point);
            if (distance > maxDistance) {
                maxDistance = distance;
                result.clear();
                result.add(point);
            } else if (distance == maxDistance) {
                result.add(point);
            }
        }
        return result;
    }

    private static BotBlockData findClosestToCenter(List<BotBlockData> candidates) {
        int minX = candidates.stream().mapToInt(p -> p.x).min().orElse(0);
        int maxX = candidates.stream().mapToInt(p -> p.x).max().orElse(0);
        int minY = candidates.stream().mapToInt(p -> p.y).min().orElse(0);
        int maxY = candidates.stream().mapToInt(p -> p.y).max().orElse(0);
        int minZ = candidates.stream().mapToInt(p -> p.z).min().orElse(0);
        int maxZ = candidates.stream().mapToInt(p -> p.z).max().orElse(0);

        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;
        double centerZ = (minZ + maxZ) / 2.0;

        return candidates.stream()
                .min(Comparator.comparingDouble(p -> squaredDistance(p, centerX, centerY, centerZ)))
                .orElse(null);
    }

    private static double squaredDistance(BotCoordinate3D a, BotCoordinate3D b) {
        return squaredDistance(a, b.x, b.y, b.z);
    }

    private static double squaredDistance(BotCoordinate3D a, double x, double y, double z) {
        double dx = a.x - x;
        double dy = a.y - y;
        double dz = a.z - z;
        return dx * dx + dy * dy + dz * dz;
    }
}
