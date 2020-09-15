package davidqchuang.TerrainGenerator.Visualization.HeightmapMeshDisplays;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import davidqchuang.TerrainGenerator.HeightmapNode;
import davidqchuang.TerrainGenerator.Visualization.MeshGenerators;
import davidqchuang.TerrainGenerator.Visualization.Triangle;
import davidqchuang.TerrainGenerator.Visualization.Vertex;

public abstract class HeightmapMeshDisplay implements IHeightmapMeshDisplay {

	@Override
	public List<Triangle> GenerateMesh(HeightmapNode[][] heightMap) {
		List<Triangle> tris = new ArrayList<>();

		int mapSizeX = heightMap.length;
		int mapSizeY = heightMap[0].length;

		int boundX = mapSizeX - 1;
		int boundY = mapSizeY - 1;

		for (int x = 0; x < boundX; x++) {
			for (int y = 0; y < boundY; y++) {
				Color c = GetColor(heightMap[x][y]);

				Triangle t1 = new Triangle(
						new Vertex(x + 0 - mapSizeX / 2, -heightMap[x + 0][y + 0].height, y + 0 - mapSizeY / 2),
						new Vertex(x + 1 - mapSizeX / 2, -heightMap[x + 1][y + 0].height, y + 0 - mapSizeY / 2),
						new Vertex(x + 1 - mapSizeX / 2, -heightMap[x + 1][y + 1].height, y + 1 - mapSizeY / 2), c);
				t1.scale(MeshGenerators.scale);

				Triangle t2 = new Triangle(
						new Vertex(x + 0 - mapSizeX / 2, -heightMap[x + 0][y + 0].height, y + 0 - mapSizeY / 2),
						new Vertex(x + 0 - mapSizeX / 2, -heightMap[x + 0][y + 1].height, y + 1 - mapSizeY / 2),
						new Vertex(x + 1 - mapSizeX / 2, -heightMap[x + 1][y + 1].height, y + 1 - mapSizeY / 2), c);
				t2.scale(MeshGenerators.scale);
				
				tris.add(t1); tris.add(t2);
			}
		}

		return tris;
	}
	
	protected abstract Color GetColor(HeightmapNode node);
}
