package com.devone.navcalc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HtmlPlotGenerator {


    public static void generateExplorationPlot(List<NavigablePoint> safeBlocks,
                                               List<NavigablePoint> unsafeBlocks,
                                               List<NavigablePoint> reachableBlocks,
                                               List<List<NavigablePoint>> validPaths,
                                               List<NavigablePoint> selectedPath,
                                               BotPosition bot,
                                               String filePath) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>");
        html.append("<div id='plot' style='width:100%;height:100%;'></div><script>");

        // Unsafe
        html.append("var unsafe = { x:[], y:[], z:[], mode:'markers', marker:{size:3, color:'rgba(100,100,100,0.2)'}, type:'scatter3d', name:'Unsafe' };");
        for (NavigablePoint p : unsafeBlocks) {
            html.append("unsafe.x.push(").append(p.x).append(");");
            html.append("unsafe.y.push(").append(p.y).append(");");
            html.append("unsafe.z.push(").append(p.z).append(");");
        }

        // Safe non-reachable
        Set<String> reachableSet = new HashSet<>();
        for (NavigablePoint p : reachableBlocks) {
            reachableSet.add(p.x + "," + p.y + "," + p.z);
        }

        html.append("var safe = { x:[], y:[], z:[], mode:'markers', marker:{size:4, color:'green'}, type:'scatter3d', name:'Safe (non-reachable)' };");
        for (NavigablePoint p : safeBlocks) {
            if (!reachableSet.contains(p.x + "," + p.y + "," + p.z)) {
                html.append("safe.x.push(").append(p.x).append(");");
                html.append("safe.y.push(").append(p.y).append(");");
                html.append("safe.z.push(").append(p.z).append(");");
            }
        }

        // Reachable
        html.append("var reachable = { x:[], y:[], z:[], mode:'markers', marker:{size:6, color:'orange'}, type:'scatter3d', name:'Reachable' };");
        for (NavigablePoint p : reachableBlocks) {
            html.append("reachable.x.push(").append(p.x).append(");");
            html.append("reachable.y.push(").append(p.y).append(");");
            html.append("reachable.z.push(").append(p.z).append(");");
        }

        // Valid paths (blue lines)
        int pathIndex = 0;
        for (List<NavigablePoint> path : validPaths) {
            html.append("var path" + pathIndex + " = { x:[], y:[], z:[], mode:'lines', line:{width:4, color:'blue'}, type:'scatter3d', name:'Path " + pathIndex + "' };");
            for (NavigablePoint p : path) {
                html.append("path" + pathIndex + ".x.push(").append(p.x).append(");");
                html.append("path" + pathIndex + ".y.push(").append(p.y).append(");");
                html.append("path" + pathIndex + ".z.push(").append(p.z).append(");");
            }
            pathIndex++;
        }

        // Selected path (red)
        if (selectedPath != null && !selectedPath.isEmpty()) {
            html.append("var selected = { x:[], y:[], z:[], mode:'lines+markers', line:{width:5, color:'red'}, marker:{size:5, color:'red'}, type:'scatter3d', name:'Selected Path' };");
            for (NavigablePoint p : selectedPath) {
                html.append("selected.x.push(").append(p.x).append(");");
                html.append("selected.y.push(").append(p.y).append(");");
                html.append("selected.z.push(").append(p.z).append(");");
            }
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
            html.append(", selected");
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