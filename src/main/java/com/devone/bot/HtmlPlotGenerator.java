package com.devone.bot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.devone.bot.utils.blocks.BlockMaterialUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotLocation;

public class HtmlPlotGenerator {

    private static final double HALF = 0.5;

    private static int addCube(StringBuilder js, BotBlockData block, int vertexOffset, String color, String tooltip) {

        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();
    
        double heightFactor = block.getType() != null && block.isCover() ? 0.25 : 1.0;
    
        double x0 = x - HALF, x1 = x + HALF;
        double y0 = y - HALF, y1 = y0 + heightFactor;
        double z0 = z - HALF, z1 = z + HALF;
    
        // Вершины куба
        js.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");
        js.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");
    
        js.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");
        js.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");
    
        js.append("z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append(");");
        js.append("z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append(");");
    
        // Треугольники
        int[][] faces = {
            {0, 1, 2}, {0, 2, 3},
            {4, 5, 6}, {4, 6, 7},
            {0, 1, 5}, {0, 5, 4},
            {2, 3, 7}, {2, 7, 6},
            {1, 2, 6}, {1, 6, 5},
            {3, 0, 4}, {3, 4, 7}
        };
    
        for (int[] face : faces) {
            js.append("i.push(").append(vertexOffset + face[0]).append(");");
            js.append("j.push(").append(vertexOffset + face[1]).append(");");
            js.append("k.push(").append(vertexOffset + face[2]).append(");");
        }
    
        for (int f = 0; f < faces.length; f++) {
            js.append("facecolor.push('").append(color).append("');");
            js.append("text.push('").append(tooltip.replace("'", "\\'")).append("');");
        }
    
        return vertexOffset + 8;
    }

    private static void addMesh3dSection(StringBuilder html, List<BotBlockData> blocks, String varName, String fallbackColor, boolean useMaterialColors) {

        if (blocks == null || blocks.isEmpty()) {
            System.err.println("Warning: No blocks to plot for " + varName);
            return;
        }

        html.append("var ").append(varName).append(" = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], ")
            .append("facecolor:[], text:[], opacity:0.5, name:'").append(varName)
            .append("', showlegend:true, hoverinfo:'text'};\n");
    
        html.append("var x = ").append(varName).append(".x, y = ").append(varName).append(".y, z = ").append(varName).append(".z;\n");
        html.append("var i = ").append(varName).append(".i, j = ").append(varName).append(".j, k = ").append(varName).append(".k;\n");
        html.append("var facecolor = ").append(varName).append(".facecolor;\n");
        html.append("var text = ").append(varName).append(".text;\n");
    
        int vertexOffset = 0;
        for (BotBlockData block : blocks) {
            String color = useMaterialColors ? BlockMaterialUtils.getColorCodeForType(block.getType()) : fallbackColor;
            String tooltip = String.format("Type: %s<br>X: %d<br>Y: %d<br>Z: %d",
                    block.getType() != null ? block.getType() : "UNKNOWN", block.getX(), block.getY(), block.getZ());

                vertexOffset = addCube(html, block, vertexOffset, color, tooltip);
        }
    }

    public static void generateExplorationPlot(List<BotBlockData> allBlocks, List<BotBlockData> trimmed, List<BotBlockData> safe, List<BotBlockData> walkable,
                                               List<BotBlockData> navigable, List<BotBlockData> reachable,
                                               List<BotBlockData> navTargets, BotLocation bot,
                                               String filePath) throws IOException {

        StringBuilder html = new StringBuilder();
        html.append("<html><head><script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>");
        html.append("<div id='plot' style='width:100%;height:100vh;'></div><script>\n");

        addMesh3dSection(html, allBlocks, "allBlocks", "#AAAAAA", true);
        addMesh3dSection(html, trimmed, "trimmed", "#BBBBBB", true);
        addMesh3dSection(html, safe, "safe", "green", false);
        addMesh3dSection(html, walkable, "walkable", "blue", false);
        addMesh3dSection(html, navigable, "navigable", "purple", false);
        addMesh3dSection(html, reachable, "reachable", "orange", false);
        addMesh3dSection(html, navTargets, "navTargets", "red", false);
    

        // Bot as two vertically stacked blocks (bottom + head)
        html.append("var bot = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], facecolor:[], text:[], opacity:1.0, name:'Bot', showlegend:true, hoverinfo:'text'};\n");
        html.append("var x = bot.x, y = bot.y, z = bot.z;\n");
        html.append("var i = bot.i, j = bot.j, k = bot.k;\n");
        html.append("var facecolor = bot.facecolor;\n");
        html.append("var text = bot.text;\n");

        int vertexOffset = 0;
        for (int dy = 0; dy <= 1; dy++) {
            BotBlockData botBlock = new BotBlockData();
            botBlock.setX(bot.getX());
            botBlock.setY(bot.getY() + dy);
            botBlock.setZ(bot.getZ());

            String label = (dy == 0) ? "Bot Base" : "Bot Head";
            String tooltip = String.format("%s<br>X: %d<br>Y: %d<br>Z: %d", label, botBlock.getX(), botBlock.getY(), botBlock.getZ());

            vertexOffset = addCube(html, botBlock, vertexOffset, "#FF0000", tooltip);
        }

        // Final plot
        html.append("Plotly.newPlot('plot', [allBlocks, trimmed, safe, walkable, navigable, reachable, navTargets, bot], {");
        html.append("margin:{l:0,r:0,b:0,t:30},");
        html.append("scene:{xaxis:{title:'X'}, yaxis:{title:'Y'}, zaxis:{title:'Z'}},");
        html.append("title:'3D Navigation Map — Blocks as Cubes'});\n");

        html.append("</script></body></html>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html.toString());
        }
    }
}
