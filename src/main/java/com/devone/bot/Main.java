package com.devone.bot;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.devone.bot.core.navigation.BotBotRouteSelector;
import com.devone.bot.core.navigation.BotExplorationPlanner;
import com.devone.bot.core.navigation.BotGeoDataLoader;
import com.devone.bot.core.navigation.BotNavigablePointFilter;
import com.devone.bot.core.navigation.BotReachabilityResolver;
import com.devone.bot.core.navigation.BotSafeBlockFilter;
import com.devone.bot.core.navigation.ThreadSurfaceFilter;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.core.navigation.BotBotVerticalRangeFilter;;

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
            List<BotBlockData> trimmedBlocks = BotBotVerticalRangeFilter.trimByYRange(BotGeoDataLoader.blocks, BotGeoDataLoader.botPosition.y, 2);//relative!!!
            List<BotBlockData> safe = BotSafeBlockFilter.extractSafeBlocks(trimmedBlocks);
            List<BotBlockData> walkable = ThreadSurfaceFilter.filterWalkableSurfaceBlocks(safe);
            List<BotBlockData> navigable = BotNavigablePointFilter.filterNavigablePoints(walkable);
            List<BotBlockData> reachable = BotReachabilityResolver.findReachablePoints(BotGeoDataLoader.botPosition, navigable);

            HtmlPlotGenerator.generateExplorationPlot(allBlocks, safe, walkable, navigable, reachable, BotGeoDataLoader.botPosition, "nav_report.html");

            System.out.println("Saved visualization to nav_report.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}