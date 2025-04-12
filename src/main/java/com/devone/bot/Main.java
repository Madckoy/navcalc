package com.devone.bot;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import com.devone.bot.core.logic.navigation.BotNavigationPlannerWrapper;
import com.devone.bot.core.logic.navigation.filters.BotBlockNoAirFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlockNoUnknownFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNavigableFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNoCoverFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksNoDangerousFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksVerticalSliceFilter;
import com.devone.bot.core.logic.navigation.filters.BotBlocksWalkableFilter;
import com.devone.bot.core.logic.navigation.resolvers.BotReachabilityResolver;
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
            List<BotBlockData> trimmed       = BotBlocksVerticalSliceFilter.filter(BotGeoDataLoader.blocks, BotGeoDataLoader.bot.y, 2);//relative!!!

            List<BotBlockData> safe          = BotBlocksNoDangerousFilter.filter(trimmed);
                               //safe          = BotBlocksNoCoverFilter.filter(safe);
                               //safe          = BotBlockNoUnknownFilter.filter(safe);

            List<BotBlockData> walkable      = BotBlocksWalkableFilter.filter(safe);

            List<BotBlockData> navigable     = BotBlocksNavigableFilter.filter(walkable);

            BotBlockData fakeBlockDirt = new BotBlockData();
            fakeBlockDirt.x = BotGeoDataLoader.bot.x;
            fakeBlockDirt.y = BotGeoDataLoader.bot.y-1;
            fakeBlockDirt.z = BotGeoDataLoader.bot.z; 
            fakeBlockDirt.type = "DIRT";
                
            navigable.add(fakeBlockDirt);

            List<BotBlockData> reachable     = BotReachabilityResolver.resolve(BotGeoDataLoader.bot, navigable);

            if (reachable == null || reachable.isEmpty()) {
                System.out.println("No reachable blocks found.");

                reachable = new ArrayList<BotBlockData>();

                BotBlockData fakeBlock = new BotBlockData();
                fakeBlock.x = BotGeoDataLoader.bot.x;
                fakeBlock.y = BotGeoDataLoader.bot.y+20;
                fakeBlock.z = BotGeoDataLoader.bot.z;   

                reachable.add(fakeBlock);    

            }

            List<BotBlockData> navTargets    = BotNavigationPlannerWrapper.getNextExplorationTargets(BotGeoDataLoader.bot, reachable);

            if (navTargets == null || navTargets.isEmpty()) {
                System.out.println("No navigation targets found.");
                navTargets = new ArrayList<BotBlockData>();

                BotBlockData fakeBlock = new BotBlockData();
                fakeBlock.x = BotGeoDataLoader.bot.x;
                fakeBlock.y = BotGeoDataLoader.bot.y + 20;
                fakeBlock.z = BotGeoDataLoader.bot.z;

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