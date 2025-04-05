
package com.devone.navcalc;

import java.util.*;

public class ExplorationPlanner {

    public static List<List<NavigablePoint>> findPathsToDistantTargets(
            NavigablePoint current,
            List<NavigablePoint> reachable) {

        int scanRadius = GeoUtils.estimateHorizontalRadius(GeoDataLoader.blocks);

        List<NavigablePoint> targets = AdaptiveTargetSelector.selectAdaptiveSectorTargets(
            reachable,
            new BotPosition(current.x, current.y, current.z)
        );
        
        /* 
        List<NavigablePoint> targets = EvenlyDistributedTargetSelector.findEvenlyDistributedTargets(
            reachable,
            new BotPosition(current.x, current.y, current.z),
            64, maxPaths,
            false, // prefer nearer spread
            scanRadius
        );
        */

        List<List<NavigablePoint>> result = new ArrayList<>();
        Set<String> reachableSet = new HashSet<>();
        for (NavigablePoint p : reachable) {
            reachableSet.add(p.x + "," + p.y + "," + p.z);
        }

        Set<String> visitedTargets = new HashSet<>();

        for (NavigablePoint target : targets) {
            if (target.equals(current)) continue;
            if (visitedTargets.contains(target.x + "," + target.y + "," + target.z)) continue;

            List<NavigablePoint> path = Pathfinder.findPath(current, target, reachableSet);
            if (path != null && !path.isEmpty()) {
                result.add(path);
                visitedTargets.add(target.x + "," + target.y + "," + target.z);
            }
        }

        return result;
    }

    private static int manhattanDistance(NavigablePoint a, NavigablePoint b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z);
    }
}
