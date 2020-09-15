package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.HeightmapNode;

public interface IAlgorithm<TParam> {
	HeightmapNode[][] Execute(HeightmapNode[][] nodes, TParam param);
}