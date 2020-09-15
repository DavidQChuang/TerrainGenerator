package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.HeightmapNode;

public abstract class Algorithm<TParam> implements IAlgorithm<TParam> {

	public Algorithm() {
	}

	@Override
	public abstract HeightmapNode[][] Execute(HeightmapNode[][] nodes, TParam param);
}