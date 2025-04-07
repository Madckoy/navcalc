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

    public static void generateExplorationPlot(List<BotBlockData> allBlocks,  List<BotBlockData> safe,  List<BotBlockData> walkable, List<BotBlockData> walkableByThread,
                                               BotCoordinate3D bot,
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

        // walkableByThread
        html.append("var walkableByThread = { x:[], y:[], z:[], mode:'markers', marker:{size:10, color:'orange'}, type:'scatter3d', name:'walkableByThread' };");
        for (BotBlockData p : walkableByThread) {
            html.append("walkableByThread.x.push(").append(p.x).append(");");
            html.append("walkableByThread.y.push(").append(p.y).append(");");
            html.append("walkableByThread.z.push(").append(p.z).append(");");
        }


        // Bot
        html.append("var bot = { x:[").append(bot.x).append("], y:[").append(bot.y).append("], z:[").append(bot.z)
            .append("], mode:'markers', marker:{size:10, color:'red'}, type:'scatter3d', name:'Bot' };");

        // Final plot
        html.append("Plotly.newPlot('plot', [allBlocks, safe, walkable, walkableByThread, bot], {");
        html.append("margin:{l:0,r:0,b:0,t:30},");
        html.append("scene:{xaxis:{title:'X'},yaxis:{title:'Y'},zaxis:{title:'Z'}},");
        html.append("title:'Walkable Points — Pathfinder - navcalc v1.17'");
        html.append("});");        
        html.append("</script></body></html>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html.toString());
        }
    }

    public static void generateExplorationPlotEx(List<BotCoordinate3D> safeBlocks,
                                               List<BotCoordinate3D> unsafeBlocks,
                                               List<BotCoordinate3D> reachableBlocks,
                                               List<List<BotCoordinate3D>> validPaths,
                                               List<BotCoordinate3D> selectedPath,
                                               BotCoordinate3D bot,
                                               String filePath) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>");
        html.append("<div id='plot' style='width:100%;height:100%;'></div><script>");

        // Unsafe
        html.append("var unsafe = { x:[], y:[], z:[], mode:'markers', marker:{size:3, color:'rgba(100,100,100,0.2)'}, type:'scatter3d', name:'Unsafe' };");
        for (BotCoordinate3D p : unsafeBlocks) {
            html.append("unsafe.x.push(").append(p.x).append(");");
            html.append("unsafe.y.push(").append(p.y).append(");");
            html.append("unsafe.z.push(").append(p.z).append(");");
        }

        // Safe non-reachable
        Set<String> reachableSet = new HashSet<>();
        for (BotCoordinate3D p : reachableBlocks) {
            reachableSet.add(p.x + "," + p.y + "," + p.z);
        }

        html.append("var safe = { x:[], y:[], z:[], mode:'markers', marker:{size:4, color:'green'}, type:'scatter3d', name:'Safe (non-reachable)' };");
        for (BotCoordinate3D p : safeBlocks) {
            if (!reachableSet.contains(p.x + "," + p.y + "," + p.z)) {
                html.append("safe.x.push(").append(p.x).append(");");
                html.append("safe.y.push(").append(p.y).append(");");
                html.append("safe.z.push(").append(p.z).append(");");
            }
        }

        // Reachable
        html.append("var reachable = { x:[], y:[], z:[], mode:'markers', marker:{size:6, color:'orange'}, type:'scatter3d', name:'Reachable' };");
        for (BotCoordinate3D p : reachableBlocks) {
            html.append("reachable.x.push(").append(p.x).append(");");
            html.append("reachable.y.push(").append(p.y).append(");");
            html.append("reachable.z.push(").append(p.z).append(");");
        }

        // Valid paths (blue lines)
        int pathIndex = 0;
        for (List<BotCoordinate3D> path : validPaths) {
            html.append("var path" + pathIndex + " = { x:[], y:[], z:[], mode:'lines', line:{width:4, color:'blue'}, type:'scatter3d', name:'Path " + pathIndex + "' };");
            for (BotCoordinate3D p : path) {
                html.append("path" + pathIndex + ".x.push(").append(p.x).append(");");
                html.append("path" + pathIndex + ".y.push(").append(p.y).append(");");
                html.append("path" + pathIndex + ".z.push(").append(p.z).append(");");
            }
            pathIndex++;
        }

        // Selected path (red)
        if (selectedPath != null && !selectedPath.isEmpty()) {
            html.append("var selected = { x:[], y:[], z:[], mode:'lines+markers', line:{width:5, color:'red'}, marker:{size:5, color:'red'}, type:'scatter3d', name:'Selected Path' };");
            for (BotCoordinate3D p : selectedPath) {
                html.append("selected.x.push(").append(p.x).append(");");
                html.append("selected.y.push(").append(p.y).append(");");
                html.append("selected.z.push(").append(p.z).append(");");
            }

            // Final goal point as a star
            BotCoordinate3D goal = selectedPath.get(selectedPath.size() - 1);
            html.append("var goalPoint = { x:[").append(goal.x)
                .append("], y:[").append(goal.y)
                .append("], z:[").append(goal.z)
                .append("], mode:'markers', marker:{size:10, color:'gold', symbol:'star'}, type:'scatter3d', name:'Goal'};");
        }

        // Bot
        html.append("var bot = { x:[").append(bot.x).append("], y:[").append(bot.y).append("], z:[").append(bot.z)
            .append("], mode:'markers', marker:{size:8, color:'red'}, type:'scatter3d', name:'Bot' };");

        // Final plot
        html.append("Plotly.newPlot('plot', [unsafe, safe, reachable");
        for (int i = 0; i < pathIndex; i++) {
            html.append(", path" + i);
        }
        html.append(", bot");
        if (selectedPath != null && !selectedPath.isEmpty()) {
            html.append(", selected, goalPoint");
        }
        html.append("], {");
        html.append("margin:{l:0,r:0,b:0,t:30},");
        html.append("scene:{xaxis:{title:'X'},yaxis:{title:'Y'},zaxis:{title:'Z'}},");
        html.append("title:'Reachable Paths to Distant Points — navcalc v1.17'");
        html.append("});");

        html.append("</script></body></html>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html.toString());
        }
    }
}
