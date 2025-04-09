package com.devone.bot.core.navigation.filters;

import com.devone.bot.utils.BlockMaterialUtils;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

import java.util.*;

public class BotWalkableSurfaceFilter {

    /**
     * Фильтрация: оставляем только блоки, над которыми явно есть два уровня воздуха.
     */
    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();

        // Создаем карту блоков по координатам
        Map<BotCoordinate3D, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData b : blocks) {
            blockMap.put(new BotCoordinate3D(b.x, b.y, b.z), b);
        }

        for (BotBlockData block : blocks) {
            if (block.isAir()) continue; // Базовые блоки не должны быть воздухом

            BotCoordinate3D above1 = new BotCoordinate3D(block.x, block.y + 1, block.z);
            BotCoordinate3D above2 = new BotCoordinate3D(block.x, block.y + 2, block.z);

            boolean airAbove1 = !blockMap.containsKey(above1) || blockMap.get(above1).isAir();
            boolean airAbove2 = !blockMap.containsKey(above2) || blockMap.get(above2).isAir();

            // Если над блоком два уровня воздуха — он пригоден для ходьбы
            if (airAbove1 && airAbove2) {
                result.add(block);
            }
        }

        return result;
    }
}
