package com.devone.bot.core.logic.navigation.filters;


import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;

import java.util.*;

public class BotBlocksWalkableFilter {

    /**
     * Фильтрация: оставляем только блоки, над которыми явно есть два уровня воздуха.
     */
    public static List<BotBlockData> filter(List<BotBlockData> blocks) {
        List<BotBlockData> result = new ArrayList<>();

        // Создаем карту блоков по координатам
        Map<BotLocation, BotBlockData> blockMap = new HashMap<>();
        for (BotBlockData b : blocks) {
            blockMap.put(new BotLocation(b.getX(), b.getY(), b.getZ()), b);
        }

        for (BotBlockData block : blocks) {

            BotLocation above1 = new BotLocation(block.getX(), block.getY() + 1, block.getZ());
            BotLocation above2 = new BotLocation(block.getX(), block.getY() + 2, block.getZ());

            BotBlockData blockAbove1 = blockMap.get(above1);
            BotBlockData blockAbove2 = blockMap.get(above2);

            // Проверяем, что блоки над текущим блоком существуют
            if (blockAbove1 == null || blockAbove2 == null) { 
                
                continue; // Пропускаем, если блоков нет
            }

            // Если над блоком два уровня воздуха или длинные проходимые растения — он пригоден для ходьбы
            if(blockAbove1.isAir() || blockAbove1.isBot() || blockAbove1.isCover() ) {
                if(blockAbove2.isAir() || blockAbove2.isBot()) {
                    if(!block.isAir()){
                        System.out.println("block: " + block + " blockAbove1: " + blockAbove1 + " blockAbove2: " + blockAbove2);
                        result.add(block); // Добавляем блок в результат, если он прошел все проверки
                    }
                }
            }
        }

        return result;
    }
}
