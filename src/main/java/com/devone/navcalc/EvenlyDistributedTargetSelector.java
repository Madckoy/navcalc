
package com.devone.navcalc;

import java.util.*;
import java.util.stream.Collectors;

public class EvenlyDistributedTargetSelector {

    public static List<NavigablePoint> findEvenlyDistributedTargets(List<NavigablePoint> reachable, BotPosition bot, int sectors, int maxTargets) {
        Map<Integer, NavigablePoint> sectorMap = new HashMap<>();

        for (NavigablePoint point : reachable) {
            if (point.x == bot.x && point.z == bot.z) continue;

            double angle = Math.atan2(point.z - bot.z, point.x - bot.x);
            int sector = (int) ((angle + Math.PI) / (2 * Math.PI) * sectors) % sectors;

            double distanceSq = Math.pow(point.x - bot.x, 2) + Math.pow(point.y - bot.y, 2) + Math.pow(point.z - bot.z, 2);
            NavigablePoint current = sectorMap.get(sector);

            if (current == null || distanceSq > squaredDistance(current, bot)) {
                sectorMap.put(sector, point);
            }
        }

        return sectorMap.values().stream()
                .sorted(Comparator.comparingDouble(p -> -squaredDistance(p, bot))) // дальние первыми
                .limit(maxTargets)
                .collect(Collectors.toList());
    }

    private static double squaredDistance(NavigablePoint point, BotPosition bot) {
        return Math.pow(point.x - bot.x, 2) + Math.pow(point.y - bot.y, 2) + Math.pow(point.z - bot.z, 2);
    }
}
