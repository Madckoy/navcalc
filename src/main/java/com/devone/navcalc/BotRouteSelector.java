package com.devone.navcalc;

import java.util.List;
import java.util.Random;

public class BotRouteSelector {

    private static final Random RANDOM = new Random();

    /**
     * Выбирает один маршрут для бота из списка валидных маршрутов.
     * На текущем этапе — случайным образом.
     */
    public static List<NavigablePoint> choosePath(List<List<NavigablePoint>> validPaths) {
        if (validPaths == null || validPaths.isEmpty()) {
            return null;
        }

        int index = RANDOM.nextInt(validPaths.size());
        return validPaths.get(index);
    }
}