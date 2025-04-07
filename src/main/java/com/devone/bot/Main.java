package com.devone.bot;

import java.io.FileInputStream;
import java.util.List;

import java.util.Properties;

import com.devone.bot.core.navigation.filters.BotVerticalRangeFilter;
import com.devone.bot.core.navigation.BotExplorationTargetPlanner;
import com.devone.bot.core.navigation.filters.BotNavigablePointFilter;
import com.devone.bot.core.navigation.filters.BotSafeBlockFilter;
import com.devone.bot.core.navigation.filters.BotThreadSurfaceFilter;
import com.devone.bot.core.navigation.resolvers.BotReachabilityResolver;
import com.devone.bot.core.navigation.selectors.BotNavigationTargetSelector;
import com.devone.bot.core.navigation.selectors.BotNavigationTargetSelector.SelectionStrategy;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotGeoDataLoader;;

public class Main {
    public static void main(String[] args) {
        System.out.println("navcalc internal v1.17");

        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            String jsonFile = props.getProperty("data.file");

            BotGeoDataLoader.loadFromFile(jsonFile);

            System.out.println("Loaded blocks: " + BotGeoDataLoader.blocks.size());
            System.out.println("Bot position: " + BotGeoDataLoader.botPosition);

            List<BotBlockData> allBlocks = BotGeoDataLoader.blocks;
            List<BotBlockData> trimmedBlocks = BotVerticalRangeFilter.trimByYRange(BotGeoDataLoader.blocks, BotGeoDataLoader.botPosition.y, 2);//relative!!!
            List<BotBlockData> safe = BotSafeBlockFilter.extractSafeBlocks(trimmedBlocks);
            List<BotBlockData> walkable = BotThreadSurfaceFilter.filterWalkableSurfaceBlocks(safe);
            List<BotBlockData> navigable = BotNavigablePointFilter.filterNavigablePoints(walkable);
            List<BotBlockData> reachable = BotReachabilityResolver.findReachablePoints(BotGeoDataLoader.botPosition, navigable);

            List<BotBlockData> navTargets = BotExplorationTargetPlanner.selectTargets(BotGeoDataLoader.botPosition,
                                                                                            reachable,
                                                                                            BotExplorationTargetPlanner.Strategy.EVEN_DISTRIBUTED,
                                                                                            24,   // секторное деление
                                                                                            8,    // максимум целей
                                                                                            true, // предпочитать дальние
                                                                                            10    // минимальная дистанция (по scanRadius)
                                                                                        );

                
            
            //electNavigationTargets(BotGeoDataLoader.botPosition, reachable, 1.0, SelectionStrategy.RANDOM_FARTHEST);

            HtmlPlotGenerator.generateExplorationPlot(allBlocks, safe, walkable, navigable, reachable, navTargets, BotGeoDataLoader.botPosition, "nav_report.html");

            System.out.println("Saved visualization to nav_report.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}