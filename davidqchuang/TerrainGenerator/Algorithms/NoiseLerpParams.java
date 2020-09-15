package davidqchuang.TerrainGenerator.Algorithms;

import davidqchuang.TerrainGenerator.NoiseGenerators.INoiseGenerator;
import davidqchuang.TerrainGenerator.Tools.float2;

public class NoiseLerpParams {
	public float Weight;
	public INoiseGenerator NoiseGen;
	public float2 Coordinates;
}