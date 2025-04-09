package com.devone.bot.core.navigation.filters;

import com.devone.bot.utils.BlockMaterialUtils;
import com.devone.bot.utils.BotBlockData;

import java.util.*;

public class BotWalkableSurfaceFilter {

    /**
     * Фильтрация: оставляем только блоки, над которыми явно есть два уровня воздуха.
     */
    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();

        // Создаем карту блоков для быстрого поиска
        Map<String, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData b : blocks) {
            String key = b.x + "," + b.y + "," + b.z;
            blockMap.put(key, b);
        }

        // Проходим по всем блокам
        for (BotBlockData block : blocks) {

            if(block.isAir()) {
                //result.add(block); //
                continue;  // Пропускаем блок 
            }

            int x = block.x;
            int y = block.y;
            int z = block.z;

            // Проверяем два уровня воздуха сверху (y+1 и y+2)
            String key1 = x + "," + (y + 1) + "," + z;
            String key2 = x + "," + (y + 2) + "," + z;

            boolean airAbove1 = !blockMap.containsKey(key1) || BlockMaterialUtils.isAir(blockMap.get(key1).type);
            boolean airAbove2 = !blockMap.containsKey(key2) || BlockMaterialUtils.isAir(blockMap.get(key2).type);
            
            
            System.out.println(block.type + " " + airAbove1 + " : " + airAbove2);

            // Если над блоком два уровня воздуха, добавляем его в результат
            if (airAbove1 && airAbove2) {
                result.add(block);
            }
        }

        return result;
    }
}
