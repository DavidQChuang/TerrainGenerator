package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.HeightmapNode;

public class AlgorithmRound extends LocalAlgorithm<Float> {
	@Override
	public HeightmapNode Execute(HeightmapNode node, Float param) {
		node.height = Math.round(node.height * param) / param;

		return node;
	}
}
