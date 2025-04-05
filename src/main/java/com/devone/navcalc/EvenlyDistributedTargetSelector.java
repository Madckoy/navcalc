
package com.devone.navcalc;

import java.util.*;
import java.util.stream.Collectors;

public class EvenlyDistributedTargetSelector {

    public static List<NavigablePoint> findEvenlyDistributedTargets(
            List<NavigablePoint> reachable,
            BotPosition bot,
            int sectors,
            int maxTargets,
            boolean preferDistant,
            int scanRadius) {

        double minRadius = Math.max(2.0, scanRadius * 1); // <-- привязка к радиусу

        Map<Integer, NavigablePoint> sectorMap = new HashMap<>();

        for (NavigablePoint point : reachable) {
            if (point.x == bot.x && point.z == bot.z) continue;

            double dx = point.x - bot.x;
            double dy = point.y - bot.y;
            double dz = point.z - bot.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            double dist = Math.sqrt(distSq);
            if (dist < minRadius) continue;

            double angle = Math.atan2(dz, dx);
            int sector = (int) ((angle + Math.PI) / (2 * Math.PI) * sectors) % sectors;

            NavigablePoint current = sectorMap.get(sector);

            if (current == null ||
                (preferDistant && distSq > squaredDistance(current, bot)) ||
                (!preferDistant && distSq < squaredDistance(current, bot))) {
                sectorMap.put(sector, point);
            }
        }

        return sectorMap.values().stream()
                .sorted(Comparator.comparingDouble(p -> -squaredDistance(p, bot)))
                .limit(maxTargets)
                .collect(Collectors.toList());
    }

    private static double squaredDistance(NavigablePoint point, BotPosition bot) {
        return Math.pow(point.x - bot.x, 2) + Math.pow(point.y - bot.y, 2) + Math.pow(point.z - bot.z, 2);
    }
}
