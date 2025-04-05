package com.devone.navcalc;

import java.util.*;

public class ExplorationPlanner {

    public static List<List<NavigablePoint>> findPathsToDistantTargets(
            NavigablePoint current,
            List<NavigablePoint> reachable) {

        List<NavigablePoint> targets = AdaptiveTargetSelector.selectAdaptiveSectorTargets(
            reachable,
            new BotPosition(current.x, current.y, current.z)
        );
        

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
}
