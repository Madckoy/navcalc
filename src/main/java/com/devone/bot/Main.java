package com.devone.bot;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import com.devone.bot.core.navigation.filters.BotVerticalRangeFilter;
import com.devone.bot.core.navigation.BotExplorationTargetPlanner;
import com.devone.bot.core.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.navigation.filters.BotNavigablePointFilter;
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
            System.out.println("Bot position: " + BotGeoDataLoader.botPosition);

            List<BotBlockData> allBlocks = BotGeoDataLoader.blocks;

            // Фильтруем блоки по высоте, чтобы оставить только те, которые находятся в пределах 2 блоков от бота
            List<BotBlockData> trimmed       = BotVerticalRangeFilter.filter(BotGeoDataLoader.blocks, BotGeoDataLoader.botPosition.y, 2);//relative!!!

            List<BotBlockData> solid          = BotSolidBlockFilter.filter(trimmed);

            List<BotBlockData> walkable      = BotWalkableSurfaceFilter.filter(solid);

            List<BotBlockData> navigable     = BotNavigablePointFilter.filter(walkable);

            List<BotBlockData> reachable     = BotReachabilityResolver.resolve(BotGeoDataLoader.botPosition, navigable);

            List<BotBlockData> navTargets    = BotNavigationPlannerWrapper.getNextExplorationTargets(BotGeoDataLoader.botPosition, reachable);

            HtmlPlotGenerator.generateExplorationPlot(allBlocks, trimmed, solid, walkable, navigable, reachable, navTargets, BotGeoDataLoader.botPosition, "nav_report.html");

            System.out.println("Saved visualization to nav_report.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}