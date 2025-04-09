package com.devone.bot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.devone.bot.utils.BlockMaterialUtils;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;

public class HtmlPlotGenerator {

    private static final double HALF = 0.5;

    private static int addCube(StringBuilder js, BotBlockData block, int vertexOffset, String color) {
        double x = block.x;
        double y = block.y;
        double z = block.z;

        double x0 = x - HALF, x1 = x + HALF;
        double y0 = y - HALF, y1 = y + HALF;
        double z0 = z - HALF, z1 = z + HALF;

        // Вершины
        js.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");
        js.append("x.push(").append(x0).append("); x.push(").append(x1).append("); x.push(").append(x1).append("); x.push(").append(x0).append(");");

        js.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");
        js.append("y.push(").append(y0).append("); y.push(").append(y0).append("); y.push(").append(y1).append("); y.push(").append(y1).append(");");

        js.append("z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append("); z.push(").append(z0).append(");");
        js.append("z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append("); z.push(").append(z1).append(");");

        // Треугольники (каждая грань состоит из 2 треугольников)
        int[][] faces = {
            {0, 1, 2}, {0, 2, 3}, // bottom
            {4, 5, 6}, {4, 6, 7}, // top
            {0, 1, 5}, {0, 5, 4}, // front
            {2, 3, 7}, {2, 7, 6}, // back
            {1, 2, 6}, {1, 6, 5}, // right
            {3, 0, 4}, {3, 4, 7}  // left
        };

        for (int[] face : faces) {
            js.append("i.push(").append(vertexOffset + face[0]).append(");");
            js.append("j.push(").append(vertexOffset + face[1]).append(");");
            js.append("k.push(").append(vertexOffset + face[2]).append(");");
        }

        // Цвет для куба
        for (int f = 0; f < faces.length; f++) {
            js.append("facecolor.push('").append(color).append("');");
        }

        return vertexOffset + 8;
    }

    private static void addMesh3dSection(StringBuilder html, List<BotBlockData> blocks, String varName, String colorSource, boolean useMaterialColors) {
        //html.append("var ").append(varName).append(" = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], facecolor:[], opacity:0.7, name:'").append(varName).append("'};\n");

        html.append("var ").append(varName).append(" = {type:'mesh3d', x:[], y:[], z:[], i:[], j:[], k:[], facecolor:[], opacity:0.7, name:'")
                           .append(varName).append("', showlegend:true};\n");
        html.append("var x = ").append(varName).append(".x, y = ").append(varName).append(".y, z = ").append(varName).append(".z;\n");
        html.append("var i = ").append(varName).append(".i, j = ").append(varName).append(".j, k = ").append(varName).append(".k;\n");
        html.append("var facecolor = ").append(varName).append(".facecolor;\n");

        int vertexOffset = 0;
        for (BotBlockData block : blocks) {
            String color = useMaterialColors ? BlockMaterialUtils.getColorCodeForType(block.type) : colorSource;
            vertexOffset = addCube(html, block, vertexOffset, color);
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

        // Бот (как точка)
        html.append("var bot = { x:[").append(bot.x).append("], y:[").append(bot.y).append("], z:[").append(bot.z)
            .append("], mode:'markers', marker:{size:8, color:'red'}, type:'scatter3d', name:'Bot' };\n");

        html.append("Plotly.newPlot('plot', [allBlocks, safe, walkable, navigable, reachable, navTargets, bot], {");
        html.append("margin:{l:0,r:0,b:0,t:30},");
        html.append("scene:{xaxis:{title:'X'}, yaxis:{title:'Y'}, zaxis:{title:'Z'}},");
        html.append("title:'3D Navigation Map — Blocks as Cubes'");
        html.append("});\n");

        html.append("</script></body></html>");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(html.toString());
        }
    }
}
