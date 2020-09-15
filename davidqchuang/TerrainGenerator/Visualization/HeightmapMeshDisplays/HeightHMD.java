package davidqchuang.TerrainGenerator.Visualization.HeightmapMeshDisplays;

import java.awt.Color;
import davidqchuang.TerrainGenerator.HeightmapNode;

public class HeightHMD extends HeightmapMeshDisplay {
	@Override
	protected Color GetColor(HeightmapNode node) {
		// weight height
		int weightedHeight = (int) (node.height / 18f * 255);
		
		int green = 0;
		int red = 0;
		// System.out.println((int)(heightMap[x][z]/10 * 255));
		if (weightedHeight < 127) {
			green = (int) (weightedHeight * 2);
			red = 0;
		} else {
			green = 255;
			red = (int) ((weightedHeight - 127) * 2);
		}
		green = Math.min(Math.max(green, 0), 255);
		red = Math.min(Math.max(red, 0), 255);
		Color c = new Color(red, green, 0);
		if (node.moisture < 0) {
			c = Color.BLUE;
		}
		
		return c;
	}
}
