package com.devone.bot.core.logic.navigation;

import com.devone.bot.core.logic.navigation.BotExplorationTargetPlanner.Strategy;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;

import java.util.List;

public class BotNavigationPlannerWrapper {

    /**
     * Выбирает цели разведки на основе достигнутых точек.
     * Если sectorCount == null, будет подобрано автоматически по площади.
     * scanRadius теперь тоже рассчитывается адаптивно.
     */
    public static List<BotBlockData> getNextExplorationTargets(BotLocation botPosition,
                                                                List<BotBlockData> reachable) {
        if (reachable == null || reachable.isEmpty()) return null;


        int sectorCount = estimateSectorCountByArea(reachable);

        int scanRadius = estimateSafeScanRadius(botPosition, reachable);

        int maxTargets = estimateAdaptiveMaxTargets(reachable, scanRadius);

        List<BotBlockData> targets = BotExplorationTargetPlanner.selectTargets(
                botPosition,
                reachable,
                Strategy.EVEN_DISTRIBUTED,
                sectorCount,
                maxTargets,
                true,
                scanRadius
        );

        return (targets == null || targets.isEmpty()) ? null : targets;
    }

    /**
     * Расчёт безопасного радиуса сканирования:
     * среднее между средней и максимальной дистанцией до reachable-точек.
     */
    private static int estimateSafeScanRadius(BotLocation bot, List<BotBlockData> reachable) {
        if (reachable.isEmpty()) return 2;

        double sum = 0;
        double max = 0;

        for (BotBlockData b : reachable) {
            double dx = b.getX() - bot.getX();
            double dy = b.getY() - bot.getY();
            double dz = b.getZ() - bot.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            sum += dist;
            if (dist > max) max = dist;
        }

        double avg = sum / reachable.size();

        return Math.max(2, (int) Math.round((avg + max) / 2));
    }

    /**
     * Оценка оптимального количества секторов на основе площади по XZ.
     */
    private static int estimateSectorCountByArea(List<BotBlockData> blocks) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BotBlockData block : blocks) {
            minX = Math.min(minX, block.getX());
            maxX = Math.max(maxX, block.getX());
            minZ = Math.min(minZ, block.getZ());
            maxZ = Math.max(maxZ, block.getZ());
        }

        int area = Math.max(1, (maxX - minX + 1) * (maxZ - minZ + 1));
        int estimated = (int) Math.sqrt(area);
        return Math.max(6, Math.min(32, estimated));
    }
    
    private static int estimateAdaptiveMaxTargets(List<BotBlockData> reachable, int scanRadius) {
        if (reachable == null || reachable.isEmpty()) return 0;
    
        int count = reachable.size();
    
        // Коэффициент плотности: сколько целей на 1 блок сканируемого радиуса
        double densityFactor = 0.8; // до 80% можно использовать в малых зонах
    
        // Радиус окружности — => площадь = π * R², но у нас не идеально круглая зона
        double approxArea = Math.PI * scanRadius * scanRadius;
    
        // Цели на 1 сектор площади
        int suggested = (int) Math.round(Math.min(count, approxArea * densityFactor));
    
        // Не меньше 1, не больше count
        return Math.max(1, Math.min(suggested, count));
    }
    
}
