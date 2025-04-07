package com.devone.bot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.devone.bot.utils.BlockMaterialUtils;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class HtmlPlotGenerator {

    public static void generateExplorationPlot(List<BotBlockData> allBlocks,  List<BotBlockData> safe,  List<BotBlockData> walkable, List<BotBlockData> navigable,
                                               List<BotBlockData> reachable, List<BotBlockData> navTargets, BotCoordinate3D bot,
                                               String filePath) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>");
        html.append("<div id='plot' style='width:100%;height:100%;'></div><script>");

        // All blocks
        html.append("var allBlocks = { x:[], y:[], z:[], mode:'markers', ");
        html.append("marker:{size:10, color:[], colorscale:'Viridis'}, "); // color как массив!
        html.append("type:'scatter3d', name:'All blocks' };");

        for (BotBlockData block : allBlocks) {
            html.append("allBlocks.x.push(").append(block.x).append(");");
            html.append("allBlocks.y.push(").append(block.y).append(");");
            html.append("allBlocks.z.push(").append(block.z).append(");");
            html.append("allBlocks.marker.color.push(")
                .append("\"").append(BlockMaterialUtils.getColorCodeForType(block.type)).append("\"").append(");");
        }

        // safe
        html.append("var safe = { x:[], y:[], z:[], mode:'markers', marker:{size:10, color:'gray'}, type:'scatter3d', name:'safe' };");
        for (BotBlockData p : safe) {
            html.append("safe.x.push(").append(p.x).append(");");
            html.append("safe.y.push(").append(p.y).append(");");
            html.append("safe.z.push(").append(p.z).append(");");
        }

        // waklable
        html.append("var walkable = { x:[], y:[], z:[], mode:'markers', marker:{size:10, color:'green'}, type:'scatter3d', name:'walkable' };");
        for (BotBlockData p : walkable) {
            html.append("walkable.x.push(").append(p.x).append(");");
            html.append("walkable.y.push(").append(p.y).append(");");
            html.append("walkable.z.push(").append(p.z).append(");");
        }

        // navigable
        html.append("var navigable = { x:[], y:[], z:[], mode:'markers', marker:{size:10, color:'blue'}, type:'scatter3d', name:'navigable' };");
        for (BotBlockData p : navigable) {
            html.append("navigable.x.push(").append(p.x).append(");");
            html.append("navigable.y.push(").append(p.y).append(");");
            html.append("navigable.z.push(").append(p.z).append(");");
        }

        // reachable
        html.append("var reachable = { x:[], y:[], z:[], mode:'markers', marker:{size:10, color:'orange'}, type:'scatter3d', name:'reachable' };");
        for (BotBlockData p : reachable) {
            html.append("reachable.x.push(").append(p.x).append(");");
            html.append("reachable.y.push(").append(p.y).append(");");
            html.append("reachable.z.push(").append(p.z).append(");");
        }

        // navTargets
        html.append("var navTargets = { x:[], y:[], z:[], mode:'markers', marker:{size:10, color:'purple'}, type:'scatter3d', name:'navTargets' };");
        for (BotBlockData p : navTargets) {
            html.append("navTargets.x.push(").append(p.x).append(");");
            html.append("navTargets.y.push(").append(p.y).append(");");
            html.append("navTargets.z.push(").append(p.z).append(");");
        }        

        // Bot
        html.append("var bot = { x:[").append(bot.x).append("], y:[").append(bot.y).append("], z:[").append(bot.z)
            .append("], mode:'markers', marker:{size:10, color:'red'}, type:'scatter3d', name:'Bot' };");

        // Final plot
        html.append("Plotly.newPlot('plot', [allBlocks, safe, walkable, navigable, reachable, navTargets, bot], {");
        html.append("margin:{l:0,r:0,b:0,t:30},");
        html.append("scene:{xaxis:{title:'X'},yaxis:{title:'Y'},zaxis:{title:'Z'}},");
        html.append("title:'Walkable Points — Pathfinder - navcalc v1.17'");
        html.append("});");        
        html.append("</script></body></html>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html.toString());
        }
    }

    
}
