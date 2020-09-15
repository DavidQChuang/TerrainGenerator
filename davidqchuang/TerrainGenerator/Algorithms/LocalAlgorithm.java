package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.HeightmapNode;

public abstract class LocalAlgorithm<TParam> implements IAlgorithm<TParam>, ILocalAlgorithm<TParam> {

	public LocalAlgorithm() {
	}

	@Override
	public HeightmapNode[][] Execute(HeightmapNode[][] nodes, TParam param) {
		for (int x = 0; x < nodes.length; x++) {
			for (int y = 0; y < nodes[x].length; y++) {
				nodes[x][y] = Execute(nodes[x][y], param);
			}
		}
		return nodes;
	}

	@Override
	public abstract HeightmapNode Execute(HeightmapNode node, TParam param);
}