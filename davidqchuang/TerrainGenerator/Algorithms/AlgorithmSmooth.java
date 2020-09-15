package davidqchuang.TerrainGenerator.Algorithms;

import java.awt.Point;

import davidqchuang.TerrainGenerator.HeightmapNode;

public class AlgorithmSmooth extends Algorithm<Float> {
	@Override
    public HeightmapNode[][] Execute(HeightmapNode[][] nodes, Float k) {
        Point mapSize = new Point(nodes.length, nodes[0].length);

        float ik = 1 - k;

        /* Rows][left to right */
        for (int x = 1; x < mapSize.x; x++) {
            for (int y = 0; y < mapSize.y; y++) {
                float h = nodes[x - 1][y].height * k + nodes[x][y].height * ik;
                nodes[x][y].height = h;
            }
        }

        /* Rows right to left */
        for (int x = mapSize.x - 2; x > -1; x--) {
            for (int y = 0; y < mapSize.y; y++) {
                float h = nodes[x + 1][y].height * k + nodes[x][y].height * ik;
                nodes[x][y].height = h;
            }
        }

        /* Columns][bottom to top */
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = 1; y < mapSize.y; y++) {
                float h = nodes[x][y - 1].height * k + nodes[x][y].height * ik;
                nodes[x][y].height = h;
            }
        }

        /* Columns][top to bottom */
        for (int x = 0; x < mapSize.x; x++) {
            for (int y = mapSize.y - 2; y > -1; y--) {
                float h = nodes[x][y + 1].height * k + nodes[x][y].height * ik;
                nodes[x][y].height = h;
            }
        }
        
        return nodes;
    }
}