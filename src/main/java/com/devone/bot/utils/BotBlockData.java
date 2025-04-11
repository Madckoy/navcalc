package com.devone.bot.utils;

public class BotBlockData extends BotCoordinate3D {


    public String type;
    public boolean bot;  // из JSON  

    public boolean isAir() {
        return type != null && BlockMaterialUtils.AIR_TYPES.contains(type.toUpperCase());
    } 

    public boolean isCover() {
        return type != null && BlockMaterialUtils.COVER_TYPES.contains(type.toUpperCase());
    }   
    
    public boolean isDangerous() {
        return type != null && BlockMaterialUtils.UNSAFE_TYPES.contains(type.toUpperCase());  
    } 

    public boolean isBot() {
        return bot;
    }

    public boolean isUnknown() {
        return type != null && BlockMaterialUtils.UNSAFE_TYPES.contains(type.toUpperCase());
    }

    public boolean isPassable() {
        return type != null && !type.isBlank()
               && !BlockMaterialUtils.NON_PASSABLE_BLOCKS.contains(type.toUpperCase());
    }

    @Override
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", x, y, z, type, bot);
    }
}