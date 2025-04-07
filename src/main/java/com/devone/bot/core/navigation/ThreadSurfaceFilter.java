
package com.devone.bot.core.navigation;

import com.devone.bot.utils.BlockMaterialUtils;
import com.devone.bot.utils.BotBlockData;

import java.util.*;

public class ThreadSurfaceFilter {

    /**
     * Мультипроход по слоям Y: оставляет только безопасные блоки, над которыми есть два уровня воздуха
     * или ничего (что трактуется как пустота).
     */
    public static List<BotBlockData> filterWalkableSurfaceBlocks(List<BotBlockData> blocks) {
        Map<String, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData block : blocks) {
            String key = block.x + "," + block.y + "," + block.z;
            blockMap.put(key, block);
        }

        List<BotBlockData> result = new ArrayList<>();

        for (BotBlockData block : blocks) {
            int x = block.x;
            int y = block.y;
            int z = block.z;

            String key1 = x + "," + (y + 1) + "," + z;
            String key2 = x + "," + (y + 2) + "," + z;

            boolean airAbove1 = !blockMap.containsKey(key1) || BlockMaterialUtils.isAir(blockMap.get(key1).type);
            boolean airAbove2 = !blockMap.containsKey(key2) || BlockMaterialUtils.isAir(blockMap.get(key2).type);

            if (airAbove1 && airAbove2) {
                result.add(block);
            }
        }

        return result;
    }

/**
     * Комплексная фильтрация безопасных блоков, пригодных для движения:
     * - работает снизу вверх
     * - отбрасывает блоки, над которыми нет 2-х уровней воздуха (или отсутствуют данные)
     * - не учитывает блоки, перекрытые плотной массой (если воздух скрыт)
     */
    public static List<BotBlockData> filterWalkableSurfaceBlocksComplex(List<BotBlockData> blocks) {
        Map<String, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData block : blocks) {
            String key = block.x + "," + block.y + "," + block.z;
            blockMap.put(key, block);
        }

        // Собираем уникальные уровни Y
        Set<Integer> allY = new TreeSet<>();
        for (BotBlockData block : blocks) {
            allY.add(block.y);
        }

        // Группируем по XZ для плотностного анализа
        Map<String, List<BotBlockData>> columns = new HashMap<>();
        for (BotBlockData block : blocks) {
            String colKey = block.x + "," + block.z;
            columns.computeIfAbsent(colKey, k -> new ArrayList<>()).add(block);
        }

        List<BotBlockData> result = new ArrayList<>();

        for (Map.Entry<String, List<BotBlockData>> entry : columns.entrySet()) {
            List<BotBlockData> column = entry.getValue();
            column.sort(Comparator.comparingInt(b -> b.y)); // Снизу вверх

            for (BotBlockData block : column) {
                int x = block.x;
                int y = block.y;
                int z = block.z;

                String key1 = x + "," + (y + 1) + "," + z;
                String key2 = x + "," + (y + 2) + "," + z;

                boolean airAbove1 = !blockMap.containsKey(key1) || BlockMaterialUtils.isAir(blockMap.get(key1).type);
                boolean airAbove2 = !blockMap.containsKey(key2) || BlockMaterialUtils.isAir(blockMap.get(key2).type);

                if (airAbove1 && airAbove2) {
                    result.add(block);
                }
            }
        }

        return result;
    }

}
