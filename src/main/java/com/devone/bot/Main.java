package com.devone.bot;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.filters.BotBlockNoAirFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNavigableFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNoCoverFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNoDangerousFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksVerticalSliceFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksWalkableFilter;
import com.devone.bot.core.logic.navigation.resolvers.BotReachabilityResolver;
import com.devone.bot.utils.BotGeoDataLoader;
import com.devone.bot.utils.blocks.BotBlockData;;

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
            List<BotBlockData> trimmed       = BotBlocksVerticalSliceFilter.filter(BotGeoDataLoader.blocks, BotGeoDataLoader.bot.getY(), 4);//relative!!!

            List<BotBlockData> safe          = BotBlocksNoDangerousFilter.filter(trimmed);
                               //safe          = BotBlocksNoCoverFilter.filter(safe);

            List<BotBlockData> walkable      = BotBlocksWalkableFilter.filter(safe);

            List<BotBlockData> navigable     = BotBlocksNavigableFilter.filter(walkable);

            BotBlockData fakeBlockDirt1 = new BotBlockData();
            fakeBlockDirt1.setX(BotGeoDataLoader.bot.getX());
            fakeBlockDirt1.setY(BotGeoDataLoader.bot.getY()-1);
            fakeBlockDirt1.setZ(BotGeoDataLoader.bot.getZ()); 
            fakeBlockDirt1.setType("DIRT");
                
            navigable.add(fakeBlockDirt1);

            List<BotBlockData> reachable     = BotReachabilityResolver.resolve(BotGeoDataLoader.bot, navigable);

            if (reachable == null || reachable.isEmpty()) {
                System.out.println("No reachable blocks found.");

                reachable = new ArrayList<BotBlockData>();

                BotBlockData fakeBlock = new BotBlockData();
                fakeBlock.setX(BotGeoDataLoader.bot.getX());
                fakeBlock.setY(BotGeoDataLoader.bot.getY()+20);
                fakeBlock.setZ(BotGeoDataLoader.bot.getZ());   

                reachable.add(fakeBlock);    

            }

            List<BotBlockData> navTargets    = BotNavigationPlannerWrapper.getNextExplorationTargets(BotGeoDataLoader.bot, reachable);

            if (navTargets == null || navTargets.isEmpty()) {
                System.out.println("No navigation targets found.");
                navTargets = new ArrayList<BotBlockData>();

                BotBlockData fakeBlock = new BotBlockData();
                fakeBlock.setX(BotGeoDataLoader.bot.getX());
                fakeBlock.setY(BotGeoDataLoader.bot.getY()+20);
                fakeBlock.setZ(BotGeoDataLoader.bot.getZ());  

                navTargets.add(fakeBlock);
            }

            HtmlPlotGenerator.generateExplorationPlot(BotBlockNoAirFilter.filter(allBlocks), 
                                                     BotBlockNoAirFilter.filter(trimmed), 
                                                     BotBlockNoAirFilter.filter(safe), 
                                                     BotBlockNoAirFilter.filter(walkable), 
                                                     BotBlockNoAirFilter.filter(navigable), 
                                                     BotBlockNoAirFilter.filter(reachable), 
                                                     BotBlockNoAirFilter.filter(navTargets), 
                                                     BotGeoDataLoader.bot, 
                                                     "nav_vis.html");

            System.out.println("Saved visualization to nav_vis.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}