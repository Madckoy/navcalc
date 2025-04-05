package com.devone.navcalc;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar navcalc.jar <path_to_geo_data.json>");
            return;
        }

        try {
            GeoDataLoader.loadFromFile(args[0]);
            System.out.println("Loaded blocks: " + GeoDataLoader.blocks.size());
            System.out.println("Bot position: " + GeoDataLoader.botPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}