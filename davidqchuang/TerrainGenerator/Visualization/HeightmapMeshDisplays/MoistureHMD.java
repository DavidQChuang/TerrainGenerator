package davidqchuang.TerrainGenerator.Visualization.HeightmapMeshDisplays;

import java.awt.Color;

import davidqchuang.TerrainGenerator.HeightmapNode;

public class MoistureHMD extends HeightmapMeshDisplay {
	@Override
	protected Color GetColor(HeightmapNode node) {
		Color c = new Color(Math.max(Math.min((int) (node.moisture / 32f * 255), 255), 0), 0, 0);
		
		return c;
	}
}
