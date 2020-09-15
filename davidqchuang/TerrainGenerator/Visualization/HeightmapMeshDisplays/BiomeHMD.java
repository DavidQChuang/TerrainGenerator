package davidqchuang.TerrainGenerator.Visualization.HeightmapMeshDisplays;

import java.awt.Color;

import davidqchuang.TerrainGenerator.Biomes;
import davidqchuang.TerrainGenerator.HeightmapNode;

public class BiomeHMD extends HeightmapMeshDisplay {
	@Override
	protected Color GetColor(HeightmapNode node) {
		return Biomes.GetBiomeColor(node.height, node.moisture);
	}
}
