package com.devone.bot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.devone.bot.utils.BlockMaterialUtils;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class HtmlPlotGenerator {

    private static final double HALF = 0.5;

    private static int addCube(StringBuilder js, BotBlockData block, int vertexOffset, String color, String tooltip) {
        double x = block.x;
        double y = block.y;
        double z = block.z;
    
        double heightFactor = block.type != null && block.isCover() ? 0.25 : 1.0;
    
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
        html.append("var ").append(varName).append(" = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], ")
            .append("facecolor:[], text:[], opacity:0.5, name:'").append(varName)
            .append("', showlegend:true, hoverinfo:'text'};\n");
    
        html.append("var x = ").append(varName).append(".x, y = ").append(varName).append(".y, z = ").append(varName).append(".z;\n");
        html.append("var i = ").append(varName).append(".i, j = ").append(varName).append(".j, k = ").append(varName).append(".k;\n");
        html.append("var facecolor = ").append(varName).append(".facecolor;\n");
        html.append("var text = ").append(varName).append(".text;\n");
    
        int vertexOffset = 0;
        for (BotBlockData block : blocks) {
            String color = useMaterialColors ? BlockMaterialUtils.getColorCodeForType(block.type) : fallbackColor;
            String tooltip = String.format("Type: %s<br>X: %d<br>Y: %d<br>Z: %d",
                    block.type != null ? block.type : "UNKNOWN", block.x, block.y, block.z);
            vertexOffset = addCube(html, block, vertexOffset, color, tooltip);
        }
    }

    public static void generateExplorationPlot(List<BotBlockData> allBlocks, List<BotBlockData> safe, List<BotBlockData> walkable,
                                               List<BotBlockData> navigable, List<BotBlockData> reachable,
                                               List<BotBlockData> navTargets, BotCoordinate3D bot,
                                               String filePath) throws IOException {

        StringBuilder html = new StringBuilder();
        html.append("<html><head><script src='https://cdn.plot.ly/plotly-latest.min.js'></script></head><body>");
        html.append("<div id='plot' style='width:100%;height:100vh;'></div><script>\n");

        addMesh3dSection(html, allBlocks, "allBlocks", "#AAAAAA", true);
        addMesh3dSection(html, safe, "safe", "gray", false);
        addMesh3dSection(html, walkable, "walkable", "green", false);
        addMesh3dSection(html, navigable, "navigable", "blue", false);
        addMesh3dSection(html, reachable, "reachable", "orange", false);
        addMesh3dSection(html, navTargets, "navTargets", "purple", false);
    

        // Bot as two vertically stacked blocks (bottom + head)
        html.append("var bot = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], facecolor:[], text:[], opacity:1.0, name:'Bot', showlegend:true, hoverinfo:'text'};\n");
        html.append("var x = bot.x, y = bot.y, z = bot.z;\n");
        html.append("var i = bot.i, j = bot.j, k = bot.k;\n");
        html.append("var facecolor = bot.facecolor;\n");
        html.append("var text = bot.text;\n");

        int vertexOffset = 0;
        for (int dy = 0; dy <= 1; dy++) {
            BotBlockData botBlock = new BotBlockData();
            botBlock.x = bot.x;
            botBlock.y = bot.y + dy;
            botBlock.z = bot.z;

            String label = (dy == 0) ? "Bot Base" : "Bot Head";
            String tooltip = String.format("%s<br>X: %d<br>Y: %d<br>Z: %d", label, botBlock.x, botBlock.y, botBlock.z);

            vertexOffset = addCube(html, botBlock, vertexOffset, "#FF0000", tooltip);
        }

        // Final plot
        html.append("Plotly.newPlot('plot', [allBlocks, safe, walkable, navigable, reachable, navTargets, bot], {");
        html.append("margin:{l:0,r:0,b:0,t:30},");
        html.append("scene:{xaxis:{title:'X'}, yaxis:{title:'Y'}, zaxis:{title:'Z'}},");
        html.append("title:'3D Navigation Map — Blocks as Cubes'});\n");

        html.append("</script></body></html>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html.toString());
        }
    }
}
