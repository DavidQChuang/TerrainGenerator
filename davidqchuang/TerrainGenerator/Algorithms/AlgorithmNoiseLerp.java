package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.HeightmapNode;
import davidqchuang.TerrainGenerator.NoiseGenerators.INoiseGenerator;
import davidqchuang.TerrainGenerator.Tools.float2;

public class AlgorithmNoiseLerp extends LocalAlgorithm<NoiseLerpParams> {

	public AlgorithmNoiseLerp() {
	}

	@Override
	public HeightmapNode[][] Execute(HeightmapNode[][] nodes, NoiseLerpParams param) {
		float2 original = param.Coordinates;

		for (int x = 0; x < nodes.length; x++) {
			for (int y = 0; y < nodes[x].length; y++) {
				param.Coordinates = new float2(original.x + x, original.y + y);
				nodes[x][y] = Execute(nodes[x][y], param);
			}
		}

		return nodes;
	}

	@Override
	public HeightmapNode Execute(HeightmapNode node, NoiseLerpParams k) {
		node.height = node.height * k.Weight + k.NoiseGen.Generate(k.Coordinates) * (1 - k.Weight);

		return node;
	}
}