package davidqchuang.TerrainGenerator.Visualization.HeightmapMeshDisplays;

import java.util.List;

import davidqchuang.TerrainGenerator.HeightmapNode;
import davidqchuang.TerrainGenerator.Visualization.Triangle;

public interface IHeightmapMeshDisplay {
	List<Triangle> GenerateMesh(HeightmapNode[][] nodes);
}
