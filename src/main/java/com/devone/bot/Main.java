package com.devone.bot;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import com.devone.bot.core.navigation.BotBotRouteSelector;
import com.devone.bot.core.navigation.BotExplorationPlanner;
import com.devone.bot.core.navigation.BotGeoDataLoader;
import com.devone.bot.core.navigation.BotSafeBlockFilter;
import com.devone.bot.util.BotBlockData;
import com.devone.bot.util.BotCoordinate3D;
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

            List<BotBlockData> trimmedBlocks = BotBotVerticalRangeFilter.trimByYRange(BotGeoDataLoader.blocks, BotGeoDataLoader.botPosition.y, 2);
            List<BotCoordinate3D> safe = BotSafeBlockFilter.extractSafeBlocks(trimmedBlocks);
            List<BotCoordinate3D> unsafe = BotSafeBlockFilter.extractUnsafeBlocks(trimmedBlocks);
            List<BotCoordinate3D> reachable = BotSafeBlockFilter.extractReachableBlocksFromBot(
                    BotGeoDataLoader.blocks, BotGeoDataLoader.botPosition);

            List<List<BotCoordinate3D>> validPaths = BotExplorationPlanner.findPathsToDistantTargets(
                    new BotCoordinate3D(BotGeoDataLoader.botPosition), reachable);

            List<BotCoordinate3D> selectedPath = BotBotRouteSelector.choosePath(validPaths);

            HtmlPlotGenerator.generateExplorationPlot(
                    safe, unsafe, reachable, validPaths, selectedPath,
                    BotGeoDataLoader.botPosition, "nav_report.html");

            System.out.println("Saved visualization to nav_report.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}