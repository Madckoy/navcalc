package com.devone.navcalc;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("navcalc internal v1.17");

        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            String jsonFile = props.getProperty("data.file");

            GeoDataLoader.loadFromFile(jsonFile);
            System.out.println("Loaded blocks: " + GeoDataLoader.blocks.size());
            System.out.println("Bot position: " + GeoDataLoader.botPosition);

            List<BlockData> trimmedBlocks = VerticalRangeFilter.trimByYRange(GeoDataLoader.blocks, GeoDataLoader.botPosition.y, 2);
            List<NavigablePoint> safe = SafeBlockFilter.extractSafeBlocks(trimmedBlocks);
            List<NavigablePoint> unsafe = SafeBlockFilter.extractUnsafeBlocks(trimmedBlocks);
            List<NavigablePoint> reachable = SafeBlockFilter.extractReachableBlocksFromBot(
                    GeoDataLoader.blocks, GeoDataLoader.botPosition);

            List<List<NavigablePoint>> validPaths = ExplorationPlanner.findPathsToDistantTargets(
                    new NavigablePoint(GeoDataLoader.botPosition.x,
                                       GeoDataLoader.botPosition.y,
                                       GeoDataLoader.botPosition.z),
                    reachable,
                    20 // max paths to draw
            );

            List<NavigablePoint> selectedPath = BotRouteSelector.choosePath(validPaths);

            HtmlPlotGenerator.generateExplorationPlot(
                    safe, unsafe, reachable, validPaths, selectedPath,
                    GeoDataLoader.botPosition, "nav_report.html");

            System.out.println("Saved visualization to nav_report.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}