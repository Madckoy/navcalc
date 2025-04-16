package com.devone.bot.utils.blocks;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BotBlockData extends BotLocation {

    private String type;
    private UUID   uuid;

    public void setType( String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public void setUUID( UUID id){
        this.uuid = id;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    @JsonIgnore
    private boolean bot;  // из JSON  
    
    @JsonIgnore
    public boolean isBot() {
        return bot;
    }

    @JsonIgnore
    public void setBot(boolean bot) {
        this.bot = bot;
    }

    @JsonIgnore
    public boolean isAir() {
        return type != null && BlockMaterialUtils.AIR_TYPES.contains(type.toUpperCase());
    } 

    @JsonIgnore
    public boolean isCover() {
        return type != null && BlockMaterialUtils.COVER_TYPES.contains(type.toUpperCase());
    }   

    @JsonIgnore   
    public boolean isDangerous() {
        return type != null && BlockMaterialUtils.UNSAFE_TYPES.contains(type.toUpperCase());  
    } 
    
    @JsonIgnore
    public String toString() {
        return String.format("Block[x=%d, y=%d, z=%d, type=%s, bot=%b]", this.getX(), this.getY(), this.getZ(), type, bot);
    }
}