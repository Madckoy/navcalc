package com.devone.bot;

import java.io.FileInputStream;

import java.util.List;

import java.util.Properties;

import com.devone.bot.core.navigation.filters.BotVerticalRangeFilter;
import com.devone.bot.core.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.navigation.filters.BotNavigablePointFilter;
import com.devone.bot.core.navigation.filters.BotRemoveAirFilter;
import com.devone.bot.core.navigation.filters.BotSolidBlockFilter;
import com.devone.bot.core.navigation.filters.BotWalkableSurfaceFilter;
import com.devone.bot.core.navigation.resolvers.BotReachabilityResolver;
import com.devone.bot.utils.BotBlockData;
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
            System.out.println("Bot position: " + BotGeoDataLoader.bot);

            List<BotBlockData> allBlocks = BotGeoDataLoader.blocks;

            // Фильтруем блоки по высоте, чтобы оставить только те, которые находятся в пределах 2 блоков от бота
            List<BotBlockData> trimmed       = BotVerticalRangeFilter.filter(BotGeoDataLoader.blocks, BotGeoDataLoader.bot.y, 2);//relative!!!

            List<BotBlockData> solid         = BotSolidBlockFilter.filter(trimmed);

            List<BotBlockData> walkable      = BotWalkableSurfaceFilter.filter(solid);

            List<BotBlockData> navigable     = BotNavigablePointFilter.filter(BotRemoveAirFilter.filter(walkable));

            List<BotBlockData> reachable     = BotReachabilityResolver.resolve(BotGeoDataLoader.bot, navigable);

            List<BotBlockData> navTargets    = BotNavigationPlannerWrapper.getNextExplorationTargets(BotGeoDataLoader.bot, reachable);

            HtmlPlotGenerator.generateExplorationPlot(BotRemoveAirFilter.filter(allBlocks), 
                                                     BotRemoveAirFilter.filter(trimmed), 
                                                     BotRemoveAirFilter.filter(solid), 
                                                     BotRemoveAirFilter.filter(walkable), 
                                                     navigable, 
                                                     reachable, 
                                                     navTargets, 
                                                     BotGeoDataLoader.bot, 
                                                     "nav_vis.html");

            System.out.println("Saved visualization to nav_vis.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}