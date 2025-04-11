package com.devone.bot.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BotGeoDataLoader {
    public static List<BotBlockData> blocks = new ArrayList<>();
    public static BotCoordinate3D botPosition;

    public static void loadFromFile(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));

        for (JsonNode node : root.get("blocks")) {
            BotBlockData block = mapper.treeToValue(node, BotBlockData.class);
            blocks.add(block);
        }

        JsonNode botNode = root.get("botLocation");
        botPosition = mapper.treeToValue(botNode, BotCoordinate3D.class);
    }
}