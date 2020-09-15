package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.HeightmapNode;

public interface ILocalAlgorithm<TParam> {
	HeightmapNode Execute(HeightmapNode node, TParam param);
}
