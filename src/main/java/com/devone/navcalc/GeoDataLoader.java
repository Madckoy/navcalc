package com.devone.navcalc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GeoDataLoader {
    public static List<BlockData> blocks = new ArrayList<>();
    public static BotPosition botPosition;

    public static void loadFromFile(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));

        for (JsonNode node : root.get("blocks")) {
            BlockData block = mapper.treeToValue(node, BlockData.class);
            blocks.add(block);
        }

        JsonNode botNode = root.get("bot_position");
        botPosition = mapper.treeToValue(botNode, BotPosition.class);
    }
}